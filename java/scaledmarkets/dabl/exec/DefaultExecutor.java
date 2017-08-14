package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.Executor;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analyzer.CompilerState;
import scaledmarkets.dabl.analyzer.TimeUnit;
import scaledmarkets.dabl.util.Utilities;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
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
	private DablContext dablContext;
	private boolean verbose;
	private ExecHelper helper;

	/**
	 * If 'verbose' is true, then print the actions
	 * that are performed as they are performed.
	 */
	public DefaultExecutor(CompilerState state, TaskContainerFactory taskContainerFactory,
		boolean verbose) {
		this.state = state;
		this.taskContainerFactory = taskContainerFactory;
		this.verbose = verbose;
		this.helper = new ExecHelper(state);
		this.dablContext = new DablContext(this.helper);
	}
	
	public void execute() throws Exception {
		System.out.println("Determining dependency graph...");
		DependencyGraph graph = DependencyGraph.genDependencySet(new ExecHelper(state));

		System.out.println("Executing tasks...");
		executeAll(graph);
		
		System.out.println("...done executing tasks.");
	}

	/**
	 * If the specified task has finished executing, return its final process
	 * status. If it has not yet executed, or is still executing, throw
	 * an exception. This method must be thread-safe, and able to be called
	 * from different thread from the one in which this Executor initiated
	 * execution. If 'block' is true, then this method blocks until the
	 * task completes.
	 */
	public synchronized int getTaskStatus(String taskName, boolean block) throws Exception {
		
		if (block) {
			wait();
		}
		
		return dablContext.getTaskStatus(taskName);
	}
	
	private synchronized void setTaskStatus(String taskName, int status) {
		this.dablContext.setTaskStatus(taskName, status);
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
	 * See https://github.com/ScaledMarkets/dabl/tree/master/java/scaledmarkets/dabl#execution-phase
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
				if (verbose) System.out.print("Creating private temp directory for workspace...");
				Set<PosixFilePermission> permSet = PosixFilePermissions.fromString("rwx------");
				FileAttribute<Set<PosixFilePermission>> attr =
					PosixFilePermissions.asFileAttribute(permSet);
				File workspace = Files.createTempDirectory("dabl", attr).toFile();
				if (verbose) System.out.println("created workspace.");
				
				// Obtain inputs and copy them into the workspace.
				if (verbose) System.out.print("Copying inputs to workspace...");
				(new ArtifactOperator(this.helper) {
					protected void operation(PatternSets patternSets) throws Exception {
						patternSets.getRepo().getFiles(patternSets, workspace);
					}
				}).operateOnArtifacts(helper.getPrimaryNamespaceFullName(), task.getName(), inputs);
				if (verbose) System.out.println("copied inputs.");
				
				// Create a container.
				if (verbose) System.out.print("Creating container for task " + task.getName() + "...");
				TaskContainer taskContainer =
					this.taskContainerFactory.createTaskContainer(
						task, workspace, Utilities.getContainerProperties());
				if (verbose) System.out.println("created container.");
				
				// Set a timer to interrupt the task after the timeout period.
				Timer timer = null;
				if (task.getTimeUnit() != null) {  // there is a timeout
					double timeout = task.getTimeout();
					TimeUnit timeUnit = task.getTimeUnit();
					long ms = timeUnit.convertToMs(timeout);
					
					// Create timer.
					timer = new Timer();
					TimerTask timerTask = new TimerTask() {
						public void run() {
							// Interrupt the DABL task.
							try { taskContainer.stop(); }
							catch (Exception ex) {
								System.err.println(ex.getMessage());
							}
						}
					};
					timer.schedule(timerTask, ms);
				}
				
				// Execute the task in the container.
				if (verbose) System.out.print("Executing container...");
				try {
					InputStream containerOutput = taskContainer.execute();
					
					// Send the container's output to this process's stdout.
					Utilities.pipeInputStreamToOutputStream(containerOutput, System.out);
					containerOutput.close();
				} finally {
					if (verbose) System.out.println("container exited.");
					if (timer != null) timer.cancel();
					// Obtain the container's exit status.
					int exitStatus = taskContainer.getExitStatus();
					setTaskStatus(task.getName(), exitStatus);
					
					// notify any threads that called getTaskStatus with block set to true.
					try { notify(); } catch (IllegalMonitorStateException ex) {
						// This error only occurs if there is no thread waiting
						// on a call to getTaskStatus.
					}
				}
				
				// Write the outputs from the workspace to the output directories.
				if (verbose) System.out.print("Transferring container outputs...");
				(new ArtifactOperator(this.helper) {
					protected void operation(PatternSets patternSets) throws Exception {
						patternSets.getRepo().putFiles(workspace, patternSets);
					}
				}).operateOnArtifacts(helper.getPrimaryNamespaceFullName(), task.getName(), outputs);
				if (verbose) System.out.println("outputs transferred.");
				
				// Destroy the container, if desired.
				if (verbose) System.out.print("Destroying container...");
				taskContainer.destroy();
				this.taskContainerFactory.containerWasDestroyed(taskContainer);
				if (verbose) System.out.println("container destroyed.");
				
				// Clean up files.
				if (verbose) System.out.print("Cleaning up...");
				Utilities.deleteDirectoryTree(workspace.toPath());
				if (verbose) System.out.println("cleaned up.");
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
