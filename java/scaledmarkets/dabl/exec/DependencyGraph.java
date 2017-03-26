package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import scaledmarkets.dabl.Config;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.helper.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * Construct a dependency graph, that represents the input/output dependencies
 * among the tasks.
 */
public class DependencyGraph {

	private CompilerState state;
	private Map<AOtaskDeclaration, Task> tasks = new HashMap<AOtaskDeclaration, Task>();
	private Map<AOartifactSet, Artifact> artifacts = new HashMap<AOartifactSet, Artifact>();
	private Set<Task> rootTasks = new HashSet<Task>();
	
	/**
	 * Create a dependency graph that explicitly models the producer/consumer
	 * relationships between tasks.
	 */
	public static DependencyGraph genDependencySet(CompilerState state) {
		
		DependencyGraph graph = new DependencyGraph(state);
		graph.genDependencies();
		return graph;
	}
	
	public Set<Task> getRootTasks() { return rootTasks; }
	
	public Task getTaskForDeclaration(AOtaskDeclaration taskDecl) {
		return tasks.get(taskDecl);
	}
	
	public Artifact getArtifactForSet(AOartifactSet a) {
		return artifacts.get(a);
	}
	
	protected DependencyGraph(CompilerState state) {
		this.state = state;
	}
	
	/**
	 * 
	 */
	protected void genDependencies() {
		
		// 1. Create a graph of the artifact/task flow relationships:
		Helper helper = new Helper(state);
		List<AOtaskDeclaration> taskDecls;
		try { taskDecls = helper.getTaskDeclarations(); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		for (AOtaskDeclaration taskDecl : taskDecls) { // each task declaration
			// a. Add a new Task to the set of tasks.
			Task task = createTask(taskDecl);
			
			// b. For each of the task’s inputs,
			LinkedList<POnamedArtifactSet> inputs = taskDecl.getInput();
			for (POnamedArtifactSet p : inputs) {
				
				POartifactSet artifactSet;
				
				if (p instanceof AAnonymousOnamedArtifactSet) {
					artifactSet = ((AAnonymousOnamedArtifactSet)p).getOartifactSet();
				} else if (p instanceof ANamedOnamedArtifactSet) {
					artifactSet = ((ANamedOnamedArtifactSet)p).getOartifactSet();
				} else if (p instanceof AFilesOnamedArtifactSet) {
					artifactSet = getArtifactSet((AFilesOnamedArtifactSet)p);
				} else if (p instanceof ATaskOnamedArtifactSet) {
					artifactSet = getArtifactSet((ATaskOnamedArtifactSet)p);
				} else throw new RuntimeException(
					"Unexpected Node type: " + p.getClass().getName());
				
				// 1. If the input Artifact does not exist, then create a new Artifact.
				Artifact artifact = createArtifact((AOartifactSet)artifactSet);
				
				// 2. Add the task to the input Artifact’s list of “IsReadBy”.
				artifact.addConsumer(task);
				
				// 3. Add the input Artifact to the task’s list of inputs.
				task.addInput(artifact);
			}
			
			// c. For each of the task’s outputs,
			LinkedList<POnamedArtifactSet> outputs = taskDecl.getOutput();
			for (POnamedArtifactSet p : outputs) {
				
				POartifactSet artifactSet;
				
				if (p instanceof AAnonymousOnamedArtifactSet) {
					artifactSet = ((AAnonymousOnamedArtifactSet)p).getOartifactSet();
				} else if (p instanceof ANamedOnamedArtifactSet) {
					artifactSet = ((ANamedOnamedArtifactSet)p).getOartifactSet();
				} else throw new RuntimeException(
					"Unexpected Node type: " + p.getClass().getName());
				
				// 1. If the output Artifact does not exist, then create a new Artifact.
				Artifact artifact = createArtifact((AOartifactSet)artifactSet);
				
				// 2. Add the task to the output Artifact’s list of “IsWrittenBy”.
				artifact.addProducer(task);
				
				// 3. Add the output Artifact to the task’s list of outputs.
				task.addOutput(artifact);
			}
		}
		
		// 2. Determine task dependency graph:
		for (Artifact artifact : artifacts.values()) {
			// Add a Producer and Consumer relation between each input task and
			// each output task.
			
			for (Task producer : artifact.getProducers()) {
				for (Task consumer : artifact.getConsumers()) {
					consumer.addProducer(producer);
					producer.addConsumer(consumer);
				}
			}
		}
		
		// 3. Identify root tasks:
		for (Task task : tasks.values()) {
			if (task.getProducers().size() == 0) {// task has no producer, then
				// Add it to the set of root tasks.
				rootTasks.add(task);
			}
		}
	}
	
	/**
	 * 
	 */
	protected Task createTask(AOtaskDeclaration taskDecl) {
		if (tasks.get(taskDecl) != null) return null;
		Task task = new Task(taskDecl);
		tasks.put(taskDecl, task);
		return task;
	}
	
	/**
	 * 
	 */
	protected Artifact createArtifact(AOartifactSet a) {
		if (artifacts.get(a) != null) return null;
		Artifact artifact = new Artifact(a);
		artifacts.put(a, artifact);
		return artifact;
	}
	
	/**
	 * Retrieve the artifact set referenced by the oid_ref.
	 */
	protected AOartifactSet getArtifactSet(AFilesOnamedArtifactSet node) {
		
		POidRef p = node.getOidRef();
		AOidRef oidRef = (AOidRef)p;
		
		TId id = oidRef.getId();
		Annotation a = state.getIn(id);
		if (a == null) throw new RuntimeException("Unable to identify " + id.getText());
		DeclaredEntry entry = null;
		if (a instanceof DeclaredEntry) {
			entry = (DeclaredEntry)a;
		} else throw new RuntimeException("Unexpected Node type: " + a.getClass().getName());
		
		// The defining node should be a files declaration.
			
		Node n = entry.getDefiningNode();
		AOfilesDeclaration filesDecl;
		if (n instanceof AOfilesDeclaration) {
			filesDecl = (AOfilesDeclaration)n;
		} else throw new RuntimeException(
			id.getText() + " was expected to refer to a files declaration");
		
		POartifactSet b = filesDecl.getOartifactSet();
		Utilities.assertThat(b instanceof AOartifactSet,
			"Unexpected Node type: " + b.getClass().getName());
		
		return (AOartifactSet)b;
	}
	
	/**
	 * Retrieve the artifact set referenced by the qualified name.
	 */
	protected AOartifactSet getArtifactSet(ATaskOnamedArtifactSet node) {
		
		POqualifiedNameRef p = node.getOqualifiedNameRef();
		Utilities.assertThat(p instanceof AOqualifiedNameRef,
			"Unexpected Node type: " + p.getClass().getName());
		AOqualifiedNameRef nameRef = (AOqualifiedNameRef)p;
		
		Annotation a = state.getOut(nameRef);
		if (a == null) throw new RuntimeException(
			"Unable to identify " + Utilities.createNameFromPath(nameRef.getId()));
		DeclaredEntry entry = null;
		if (a instanceof DeclaredEntry) {
			entry = (DeclaredEntry)a;
		} else throw new RuntimeException("Unexpected Node type: " + a.getClass().getName());
		
		// The defining node should be a ANamedOnamedArtifactSet.
		
		Node n = entry.getDefiningNode();
		ANamedOnamedArtifactSet namedArtifactSet;
		if (n instanceof ANamedOnamedArtifactSet) {
			namedArtifactSet = (ANamedOnamedArtifactSet)n;
		} else throw new RuntimeException(
			Utilities.createNameFromPath(nameRef.getId()) +
			" was expected to refer to an output in a task declaration");
		
		POartifactSet b = namedArtifactSet.getOartifactSet();
		Utilities.assertThat(b instanceof AOartifactSet,
			"Unexpected Node type: " + b.getClass().getName());
		
		return (AOartifactSet)b;
	}
}
