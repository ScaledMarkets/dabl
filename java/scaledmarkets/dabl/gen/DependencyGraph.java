package scaledmarkets.dabl.gen;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.lexer.*;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.parser.*;

import scaledmarkets.dabl.Config;

import scaledmarkets.dabl.analysis.*;
import scaledmarkets.dabl.helper.*;

import java.io.IOException;
import java.util.logging.Logger;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * 
 */
public class DependencyGraph {

	private CompilerState state;
	private TaskContextFactory taskContextFactory;
	private Map<AOtaskDeclaration, Task> tasks = new HashMap<AOtaskDeclaration, Task>();
	private Map<AOartifactSet, Artifact> artifacts = new HashMap<AOartifactSet, Artifact>();
	private Set<Task> rootTasks = new TreeSet<Task>();
	
	/**
	 * Create a dependency graph that explicitly models the producer/consumer
	 * relationships between tasks.
	 */
	public static DependencyGraph genDependencySet(CompilerState state,
		TaskContextFactory contextFactory) {
		
		DependencyGraph graph = new DependencyGraph(state, contextFactory);
		graph.genDependencies();
		return graph;
	}
	
	/**
	 * 
	 */
	public void executeAll() {
		for (Task task : rootTasks) { // each root task tr,
			executeTaskTree(task);
		}
	}
	
	DependencyGraph(CompilerState state, TaskContextFactory contextFactory) {
		this.state = state;
		this.taskContextFactory = contextFactory;
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
				
				LinkedList<POartifactSet> artifactSet;
				
				if (p instanceof AAnonymousOnamedArtifactSet) {
					artifactSet = ((AAnonymousOnamedArtifactSet)p).getOartifactSet();
				} else if (p instanceof ANamedOnamedArtifactSet) {
					artifactSet = ((ANamedOnamedArtifactSet)p).getOartifactSet();
				} else throw new RuntimeException(
					"Unexpected Node type: " + p.getClass().getName());
				
				for (POartifactSet a : artifactSet) {
					// 1. If the input Artifact does not exist, then create a new Artifact.
					Artifact artifact = createArtifact((AOartifactSet)a);
					
					// 2. Add the task to the input Artifact’s list of “IsReadBy”.
					artifact.addConsumer(task);
					
					// 3. Add the input Artifact to the task’s list of inputs.
					task.addInput(artifact);
				}
			}
			
			// c. For each of the task’s outputs,
			LinkedList<POnamedArtifactSet> outputs = taskDecl.getOutput();
			for (POnamedArtifactSet p : outputs) {
				
				LinkedList<POartifactSet> artifactSet;
				
				if (p instanceof AAnonymousOnamedArtifactSet) {
					artifactSet = ((AAnonymousOnamedArtifactSet)p).getOartifactSet();
				} else if (p instanceof ANamedOnamedArtifactSet) {
					artifactSet = ((ANamedOnamedArtifactSet)p).getOartifactSet();
				} else throw new RuntimeException(
					"Unexpected Node type: " + p.getClass().getName());
				
				for (POartifactSet a : artifactSet) {
					// 1. If the output Artifact does not exist, then create a new Artifact.
					Artifact artifact = createArtifact((AOartifactSet)a);
					
					// 2. Add the task to the output Artifact’s list of “IsWrittenBy”.
					artifact.addProducer(task);
					
					// 3. Add the output Artifact to the task’s list of outputs.
					task.addOutput(artifact);
				}
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
		Task task = new Task(taskDecl, this.taskContext);
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
	 * 
	 */
	protected void executeTaskTree(Task task) {
		if (isDownstreamFromUnvisitedTask(task)) {

			if (task.hasBeenVisited()) throw new RuntimeException(
				"Task has already been executed");
			
			task.visit();  // mark that we have been here.
			
			TaskContext taskContext = this.taskContextFactory.createTaskContext();
			if (task.taskWhenConditionIsTrue(taskContext)) {
				executeTask(task, taskContext);
			}
			
			for (Task t_o : task.getConsumers()) {
				// for each task t_o that is immediately downstream of task,
				executeTaskTree(t_o);
			}
		}
	}
	
	/**
	 * Perform a task's procedural statements. This should be done in isolation.
	 * Therefore, launch a Linux container and run a TaskExecutor.
	 */
	protected void executeTask(Task task, TaskContext taskContext) {
		
		// Launch a task execution container, containing the task interpreter, and
		// provide the task procedural statements as a file parameter.
		// For now, just print that we are performing the task.
		System.out.println("Performing task " + task.getName());
		
	}
	
	/**
	 * 
	 */
	protected boolean isDownstreamFromUnvisitedTask(Task task) {
		
		for (Task p : task.getProducers()) {
			if (! p.hasBeenVisited()) return true;
			if (isDownstreamFromUnvisitedTask(p)) return true;
		}
		return false;
	}
}
