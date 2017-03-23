package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analysis.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class Task {
	
	public Task(AOtaskDeclaration taskDecl) {
		this.taskDecl = taskDecl;
	}
	
	public String getName() { return taskDecl.getName().getText(); }
		
	private AOtaskDeclaration taskDecl;
	private Set<Artifact> inputs = new HashSet<Artifact>();
	private Set<Artifact> outputs = new HashSet<Artifact>();
	
	private Set<Task> isConsumerOf = new HashSet<Task>();
	private Set<Task> isProducerFor = new HashSet<Task>();
	
	private boolean visited = false;
	
	void addInput(Artifact a) {
		inputs.add(a);
	}
	
	void addOutput(Artifact a) {
		outputs.add(a);
	}
	
	boolean hasOutputs() {
		return (outputs.size() > 0);
	}
	
	void addProducer(Task t) {
		isConsumerOf.add(t);
	}
	
	void addConsumer(Task t) {
		isProducerFor.add(t);
	}
	
	public Set<Task> getProducers() {
		return isConsumerOf;
	}
	
	public Set<Task> getConsumers() {
		return isProducerFor;
	}
	
	void visit() {
		visited = true;
	}
	
	boolean hasBeenVisited() {
		return visited;
	}
	
	boolean taskWhenConditionIsTrue(DablContext dablContext) {
		
		LinkedList<POexpr> exprs = taskDecl.getWhen();
		for (POexpr expr : exprs) {
			Object result = (new ExpressionEvaluator(dablContext)).evaluateExpr(expr);
			if (result instanceof Boolean) {
				if ((Boolean)result) return true;
			}
		}
		return false;
	}
	
	/**
	 * Perform a task's procedural statements. This should be done in isolation.
	 * Therefore, launch a Linux container and run a TaskExecutor.
	 */
	public void executeTask(TaskContainer container) throws Exception {
		System.out.println("Performing task " + this.getName());
		container.executeTask(this);
	}
}
