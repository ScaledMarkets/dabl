package com.scaledmarkets.dabl.exec;

import com.scaledmarkets.dabl.analyzer.*;
import com.scaledmarkets.dabl.lexer.*;
import com.scaledmarkets.dabl.node.*;
import com.scaledmarkets.dabl.parser.*;
import com.scaledmarkets.dabl.util.Utilities;
import com.scaledmarkets.dabl.helper.*;

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

	private ExecHelper helper;
	private Map<AOtaskDeclaration, Task> tasks = new HashMap<AOtaskDeclaration, Task>();
	private Map<POartifactSet, Artifact> artifacts = new HashMap<POartifactSet, Artifact>();
	private Set<Task> rootTasks = new HashSet<Task>();
	
	/**
	 * Create a dependency graph that explicitly models the producer/consumer
	 * relationships between tasks.
	 */
	public static DependencyGraph genDependencySet(ExecHelper helper) {
		
		DependencyGraph graph = new DependencyGraph(helper);
		graph.genDependencies();
		return graph;
	}
	
	public Set<Task> getRootTasks() { return rootTasks; }
	
	public Task getTaskForDeclaration(AOtaskDeclaration taskDecl) {
		return tasks.get(taskDecl);
	}
	
	public Set<Task> getAllTasks() {
		return new HashSet<Task>(this.tasks.values());
	}
	
	public Set<Artifact> getAllArtifacts() {
		return new HashSet<Artifact>(this.artifacts.values());
	}
	
	public Artifact getArtifactForSet(POartifactSet a) {
		return artifacts.get(a);
	}
	
	protected DependencyGraph(ExecHelper helper) {
		this.helper = helper;
	}
	
	/**
	 * Build a dependency graph consisting of Artifacts and Tasks, based on
	 * the task inputs and outputs that are defined in the AST.
	 * See https://github.com/ScaledMarkets/dabl/tree/master/java/scaledmarkets/dabl#dependency-graph
	 */
	protected void genDependencies() {
		
		// 1. Create a graph of the artifact/task flow relationships:
		List<AOtaskDeclaration> taskDecls;
		try { taskDecls = this.helper.getTaskDeclarations(); } catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		for (AOtaskDeclaration taskDecl : taskDecls) { // each task declaration
			// a. Add a new Task to the set of tasks.
			Task task = tasks.get(taskDecl);
			if (task == null) task = createTask(taskDecl);
			
			// b. For each of the task’s inputs,
			LinkedList<POnamedArtifactSet> inputs = taskDecl.getInput();
			for (POnamedArtifactSet p : inputs) {
				
				POartifactSet pas = this.helper.getArtifactSet(p);
				
				// 1. If the input Artifact does not exist, then create a new Artifact.
				Artifact artifact = artifacts.get(pas);
				if (artifact == null) artifact = createArtifact(pas);
				
				// 2. Add the task to the input Artifact’s list of “IsReadBy”.
				artifact.addConsumer(task);
				
				// 3. Add the input Artifact to the task’s list of inputs.
				task.addInput(artifact);
			}
			
			// c. For each of the task’s outputs,
			LinkedList<POnamedArtifactSet> outputs = taskDecl.getOutput();
			for (POnamedArtifactSet p : outputs) {
				
				POartifactSpec spec;
				if (p instanceof AAnonymousOnamedArtifactSet) {
					spec = ((AAnonymousOnamedArtifactSet)p).getOartifactSpec();
				} else if (p instanceof ANamedOnamedArtifactSet) {
					spec = ((ANamedOnamedArtifactSet)p).getOartifactSpec();
				} else throw new RuntimeException(
					"Unexpected Node type: " + p.getClass().getName());
				
				POartifactSet pas;
				if (spec instanceof AInlineOartifactSpec) {
					pas = ((AInlineOartifactSpec)spec).getOartifactSet();
				} else if (spec instanceof AFilesRefOartifactSpec) {
					// 
					POidRef pr = ((AFilesRefOartifactSpec)spec).getOidRef();
					AOidRef oidRef = (AOidRef)pr;
					DeclaredEntry refEntry = this.helper.getDeclaredEntryForIdRef(oidRef);
					Utilities.assertIsA(refEntry, FilesRefEntry.class);
					FilesRefEntry filesRefEntry = (FilesRefEntry)refEntry;
					DeclaredEntry filesDeclEntry = filesRefEntry.getFilesDeclEntry();
					Node definingNode = filesDeclEntry.getDefiningNode();
					Utilities.assertIsA(definingNode, POartifactSet.class);
					pas = (POartifactSet)definingNode;
					
				} else throw new RuntimeException(
					"Unexpected Node type: " + spec.getClass().getName());
				
				// 1. If the output Artifact does not exist, then create a new Artifact.
				Artifact artifact = artifacts.get(pas);
				if (artifact == null) artifact = createArtifact(pas);
				
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
		Task t = tasks.get(taskDecl);
		if (t != null) throw new RuntimeException("Task " + t.getName() + " exists");
		Task task = new Task(taskDecl, new DablContext(this.helper));
		tasks.put(taskDecl, task);
		return task;
	}
	
	/**
	 * 
	 */
	protected Artifact createArtifact(POartifactSet aset) {
		Artifact a = artifacts.get(aset);
		if (a != null) throw new RuntimeException("Artifact exists");
		Artifact artifact = new Artifact(aset);
		artifacts.put(aset, artifact);
		return artifact;
	}
}
