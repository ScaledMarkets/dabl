package scaledmarkets.dabl.gen;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analysis.*;

import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.LinkedList;

public class Task {
	
	public Task(AOtaskDeclaration taskDecl) {
		this.taskDecl = taskDecl;
	}
	
	public Task(AOtaskDeclaration taskDecl, TaskContext context) {
		this(taskDecl);
		this.context = context;
	}
	
	public String getName() { return taskDecl.getName().getText(); }
	
	public TaskContext getContext() { return this.context; }
	
	private TaskContext context = null;
	private AOtaskDeclaration taskDecl;
	private Set<Artifact> inputs = new TreeSet<Artifact>();
	private Set<Artifact> outputs = new TreeSet<Artifact>();
	
	private Set<Task> isConsumerOf = new TreeSet<Task>();
	private Set<Task> isProducerFor = new TreeSet<Task>();
	
	private boolean visited = false;
	
	void addInput(Artifact a) {
		inputs.add(a);
	}
	
	void addOutput(Artifact a) {
		outputs.add(a);
	}
	
	void addProducer(Task t) {
		isConsumerOf.add(t);
	}
	
	void addConsumer(Task t) {
		isProducerFor.add(t);
	}
	
	Set<Task> getProducers() {
		return isConsumerOf;
	}
	
	Set<Task> getConsumers() {
		return isProducerFor;
	}
	
	void visit() {
		visited = true;
	}
	
	boolean hasBeenVisited() {
		return visited;
	}
	
	boolean taskWhenConditionIsTrue() {
		
		LinkedList<POexpr> exprs = taskDecl.getWhen();
		for (POexpr expr : exprs) {
			Object result = (new ExpressionEvaluator(getContext())).evaluateExpr(expr);
			if (result instanceof Boolean) {
				if ((Boolean)result) return true;
			}
		}
		return false;
	}
}
