package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.helper.Helper;
import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.analysis.CompilerState;
import scaledmarkets.dabl.analysis.Utilities;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
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
				FileAttribute<Set<PosixFilePermission>> attr =
					PosixFilePermissions.asFileAttribute(permSet);
				File workspace = Files.createTempDirectory("dabl", attr).toFile();
				
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
				taskContainer.destroy();
				this.taskContainerFactory.containerWasDestroyed(taskContainer);
				
				// Clean up files.
				Utilities.deleteDirectoryTree(workspace.toPath());
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
	
	/**
	 * A operator for performing actions on selected files in a project in a repository.
	 * The files are selected by the PatternSets.
	 */
	private interface ArtifactOperator {
		void operation(PatternSets patternSets) throws Exception;
	}
	
	/**
	 * Obtain the specified artifacts from their repositories, and copy them
	 * into the specified host directory.
	 */
	protected void copyArtifactsTo(Set<Artifact> artifacts, File dir) throws Exception {
		
		operateOnArtifacts(artifacts, dir,
			(PatternSets patternSets) -> patternSets.getRepo().getFiles(patternSets, dir));
	}
	
	/**
	 * Push the specified artifacts - and only those artifacts - from the host
	 * directory into the repository for each artifact.
	 */
	protected void copyToArtifacts(File dir, Set<Artifact> artifacts) throws Exception {
		
		operateOnArtifacts(artifacts, dir,
			(PatternSets patternSets) -> patternSets.getRepo().putFiles(dir, patternSets));
	}
	
	class PatternSetsMap extends HashMap<String, PatternSets> {
		
		/**
		 * Return the PatternSets for the specified path and project. If it does
		 * not exist, create it.
		 */
		public PatternSets getPatternSets(Repo repo, String project) {
			String key = PatternSets.getKey(repo, project);
			PatternSets p = get(key);
			if (p == null) {
				p = new PatternSets(repo, project);
				put(key, p);
			}
			return p;
		}
	}
	
	protected void operateOnArtifacts(Set<Artifact> artifacts, File dir,
		ArtifactOperator operator) throws Exception {
		
		PatternSetsMap patternSetsMap = new PatternSetsMap();
		
		for (Artifact artifact : artifacts) {
			AOartifactSet artifactSet = artifact.getArtifactSet();
			AOidRef reposIdRef = (AOidRef)(artifactSet.getRepositoryId());
			
			// Use the Repo object to pull the files from the repo.
			String project = this.helper.getStringLiteralValue(artifactSet.getProject());
			
			// Identify the repo declaration.
			AOrepoDeclaration repoDecl = this.helper.getRepoDeclFromRepoRef(reposIdRef);
			String path = this.helper.getStringLiteralValue(repoDecl.getPath());
			
			// Obtain the repo information.
			String scheme = this.helper.getStringValueOpt(repoDecl.getScheme());
			String userid = this.helper.getStringValueOpt(repoDecl.getUserid());
			String password = this.helper.getStringValueOpt(repoDecl.getPassword());
			String repoType = this.helper.getStringLiteralValue(repoDecl.getType());
			
			// Use the repo info to construct a Repo object.
			Repo repo = Repo.getRepo(repoType, scheme, path, userid, password);

			PatternSets patternSets = patternSetsMap.getPatternSets(repo, project);
			
			// Construct a set of include patterns and a set of exclude patterns.
			LinkedList<POfilesetOperation> filesetOps = artifactSet.getOfilesetOperation();
			assembleIncludesAndExcludes(patternSets, filesetOps);
		}
		
		for (PatternSets patternSets : patternSetsMap.values()) {
			operator.operation(patternSets);
		}
	}

	protected void assembleIncludesAndExcludes(PatternSets patternSets,
		List<POfilesetOperation>filesetOps)  throws Exception {
	
		for (POfilesetOperation op : filesetOps) {
			
			if (op instanceof AIncludeOfilesetOperation) {
				AIncludeOfilesetOperation includeOp = (AIncludeOfilesetOperation)op;
				POstringLiteral lit = includeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				patternSets.getIncludePatterns().add(pattern);
			} else if (op instanceof AExcludeOfilesetOperation) {
				AExcludeOfilesetOperation excludeOp = (AExcludeOfilesetOperation)op;
				POstringLiteral lit = excludeOp.getOstringLiteral();
				String pattern = this.helper.getStringLiteralValue(lit);
				patternSets.getExcludePatterns().add(pattern);
			} else throw new RuntimeException(
				"Unexpected POfilesetOperation type: " + op.getClass().getName());
		}
	}
}
