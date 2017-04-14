package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.CompilerState;
import scaledmarkets.dabl.analysis.Utilities;
import java.util.Set;
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
				FileAttribute<PosixFilePermission>[] attrs = 
					permSet.toArray(new PosixFilePermission[permSet.size()]);
				File workspace = Files.createTempDirectory("dabl", attrs).toFile();
				
				// Obtain inputs and copy them into the workspace.
				copyArtifactsTo(inputs, workspace);
		
				// Create a container.
				TaskContainer taskContainer =
					this.taskContainerFactory.createTaskContainer(task, workspace);
				
				// Execute the task in the container.
				taskContainer.execute();
				
				// Write the outputs from the workspace to the output directories.
				copyToArtifacts(workspace, outputs);
				
				// Destroy the container, if desired.
				this.dockerContainer.destroy();
				this.taskContainerFactory.containerWasDestroyed(this);
				
				// Clean up files.
				Utilities.deleteDirectoryTree(workspace);
			}
			
			for (Task t_o : task.getConsumers()) {
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
	
	private interface ArtifactOperator {
		void operation(String project, List<String> includePatterns,
			List<String> excludePatterns) throws Exception;
	}
	
	/**
	 * Obtain the specified artifacts from their repositories, and copy them
	 * into the specified host directory.
	 */
	protected copyArtifactsTo(Set<Artifact> artifacts, File dir) throws Exception {
		
		operateOnArtifacts(artifacts, dir,
			(String project, List<String> includePatterns, List<String> excludePatterns) ->
				repo.getFiles(project, includePatterns, excludePatterns));
	}
	
	/**
	 * Push the specified artifacts - and only those artifacts - from the host
	 * directory into the repository for each artifact.
	 */
	protected copyToArtifacts(File dir, Set<Artifact> artifacts) throws Exception {
		
		operateOnArtifacts(artifacts, dir,
			(String project, List<String> includePatterns, List<String> excludePatterns) ->
				repo.putFiles(project, includePatterns, excludePatterns));
	}
	
	protected void operateOnArtifacts(File dir, Set<Artifact> artifacts,
		ArtifactOperator operator) throws Exception {
		
		List<String> includePatterns = new LinkedList<String>();
		List<String> excludePatterns = new LinkedList<String>();

		for (Artifact artifact : artifacts) {
			AOartifactSet artifactSet = artifact.getArtifactSet();
			AOidRef reposIdRef = (AOidRef)(artifactSet.getRepositoryId());
			
			// Identify the repo declaration.
			AOrepoDeclaration repoDecl = this.helper.getRepoDeclFromRepoRef(reposIdRef);
			
			// Obtain the repo information.
			String scheme = this.helper.getStringValueOpt(repoDecl.getScheme());
			String userid = this.helper.getStringValueOpt(repoDecl.getUserid());
			String password = this.helper.getStringValueOpt(repoDecl.getPassword());
			String repoType = this.helper.getStringLiteralValue(repoDecl.getType());
			String path = this.helper.getStringLiteralValue(repoDecl.getPath());
			
			// Use the repo info to construct a Repo object.
			Repo repo = Repo.getRepo(repoType, scheme, path, userid, password);

			// Construct a set of include patterns and a set of exclude patterns.
			LinkedList<POfilesetOperation> filesetOps = artifactSet.getOfilesetOperation();
			assembleIncludesAndExcludes(filesetOps, includePatterns, excludePatterns);
			
			// Use the Repo object to pull the files from the repo.
			String project = this.helper.getStringLiteralValue(artifactSet.getProject());
		}
		
		operator(project, includePatterns, excludePatterns);
	}
	
	protected void assembleIncludesAndExcludes(List<POfilesetOperation>filesetOps,
		List<String> includePatterns, List<String> excludePatterns) {
	
		for (POfilesetOperation op : filesetOps) {
			
			if (op instanceof AIncludeOfilesetOperation) {
				AIncludeOfilesetOperation includeOp = (AIncludeOfilesetOperation)op;
				POstringLiteral lit = includeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				includePatterns.add(pattern);
			} else if (op instanceof AExcludeOfilesetOperation) {
				AExcludeOfilesetOperation excludeOp = (AExcludeOfilesetOperation)op;
				POstringLiteral lit = excludeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				excludePatterns.add(pattern);
			} else throw new RuntimeException(
				"Unexpected POfilesetOperation type: " + op.getClass().getName());
		}
	}
}
