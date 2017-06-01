package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analyzer.CompilerState;
import scaledmarkets.dabl.util.Utilities;
import java.util.Set;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

/**
 * Determine which tasks should be executed, based on their 'when' conditions,
 * and execute them.
 */
public class DefaultExecutor implements Executor {
	
	private CompilerState state;
	private TaskContainerFactory taskContainerFactory;
	private DablContext dablContext = new DablContext();
	private boolean verbose;
	private Helper helper;

	/**
	 * If 'verbose' is true, then print the actions
	 * that are performed as they are performed.
	 */
	public DefaultExecutor(CompilerState state, TaskContainerFactory taskContainerFactory,
		boolean verbose) {
		this.state = state;
		this.taskContainerFactory = taskContainerFactory;
		this.verbose = verbose;
		this.helper = new Helper(state);
	}
	
	public void execute() throws Exception {
		System.out.println("Determining dependency graph...");
		DependencyGraph graph = DependencyGraph.genDependencySet(this.state);

		System.out.println("Executing tasks...");
		executeAll(graph);
		
		System.out.println("...done executing tasks.");
	}

	/**
	 * 
	 */
	protected void executeAll(DependencyGraph graph) throws Exception {
		
		// To do: convert this to use streams.
		
		for (Task task : graph.getRootTasks()) { // each root task tr,
			executeTaskTree(graph, task);
		}
	}
	
	/**
	 * 
	 */
	protected void executeTaskTree(DependencyGraph graph, Task task) throws Exception {
		
		if (! isDownstreamFromUnvisitedTask(task)) {
			
			if (task.hasBeenVisited()) throw new RuntimeException(
				"Task has already been executed");
			
			task.visit();  // mark that we have been here.
			if (verbose) System.out.println("Visiting task " + task.getName());
			
			/*
				Execute the task tree beginning at task if either,
					A. the task is a simulation and it is downstream from a task that
					has been visited and has outputs; or,
					B. the task's 'when' condition is true.
			 */
			if (
				(taskContainerFactory.isASimulation() && isDownstreamFromOutputtingTask(task))
				||
				task.taskWhenConditionIsTrue(dablContext)
			   ) {
				
				if (verbose) System.out.println("\ttask 'when' condition is true");
				
				// Identify the input and output paths.
				Set<Artifact> inputs = task.getInputs();
				Set<Artifact> outputs = task.getOutputs();
				
				// Create a new private temp directory.
				Set<PosixFilePermission> permSet = PosixFilePermissions.fromString("rwx------");
				FileAttribute<Set<PosixFilePermission>> attr =
					PosixFilePermissions.asFileAttribute(permSet);
				File workspace = Files.createTempDirectory("dabl", attr).toFile();
				
				// Obtain inputs and copy them into the workspace.
				(new ArtifactOperator(this.helper) {
					protected void operation(PatternSets patternSets) throws Exception {
						patternSets.getRepo().getFiles(patternSets, workspace);
					}
				}).operateOnArtifacts(helper.getPrimaryNamespaceFullName(), task.getName(), inputs);
				
				// Create a container.
				TaskContainer taskContainer =
					this.taskContainerFactory.createTaskContainer(task, workspace);
				
				// Execute the task in the container.
				InputStream containerOutput = taskContainer.execute(3600000);
				
				// Send the container's output to this process's stdout.
				Utilities.pipeInputStreamToOutputStream(containerOutput, System.out);
				
				// Obtain the container's exit status.
				int exitStatus = taskContainer.getExitStatus();
				this.dablContext.setTaskStatus(task.getName(), exitStatus);
				
				// Write the outputs from the workspace to the output directories.
				(new ArtifactOperator(this.helper) {
					protected void operation(PatternSets patternSets) throws Exception {
						patternSets.getRepo().putFiles(workspace, patternSets);
					}
				}).operateOnArtifacts(helper.getPrimaryNamespaceFullName(), task.getName(), outputs);
				
				// Destroy the container, if desired.
				taskContainer.destroy();
				this.taskContainerFactory.containerWasDestroyed(taskContainer);
				
				// Clean up files.
				Utilities.deleteDirectoryTree(workspace.toPath());
			}
			
			for (Task t_o : task.getConsumers()) {
				// To do: convert this to use streams.
				
				// for each task t_o that is immediately downstream of task,
				executeTaskTree(graph, t_o);
			}
		}
	}
	
	/**
	 * Return true iff. task is downstream from a task that has not been visited
	 * yet by the executeTaskTree method.
	 */
	protected boolean isDownstreamFromUnvisitedTask(Task task) {
		
		for (Task p : task.getProducers()) {
			if (! p.hasBeenVisited()) return true;
			if (isDownstreamFromUnvisitedTask(p)) return true;
		}
		return false;
	}
	
	/**
	 * Return true iff task is downstream from a task that,
	 *	1. has been visited, and,
	 *	2. has outputs.
	 */
	protected boolean isDownstreamFromOutputtingTask(Task task) {
		
		for (Task p : task.getProducers()) {
			if (p.hasBeenVisited() && p.hasOutputs()) return true;
			if (isDownstreamFromOutputtingTask(p)) return true;
		}
		return false;
	}
}
