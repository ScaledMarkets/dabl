package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.Config;
import scaledmarkets.dabl.analyzer.TimeUnit;
import scaledmarkets.dabl.analyzer.NameScopeEntry;
import scaledmarkets.dabl.analyzer.ExpressionContext;
import scaledmarkets.dabl.analyzer.ExpressionEvaluator;
import scaledmarkets.dabl.util.Utilities;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

public class Task {
	
	public Task(AOtaskDeclaration taskDecl, DablContext dablContext) {
		this.taskDecl = taskDecl;
		this.dablContext = dablContext;
		this.timeUnit = retrieveTimeUnit();
		this.timeout = retrieveTimeout();
	}
	
	public String getName() { return taskDecl.getName().getText(); }
		
	private AOtaskDeclaration taskDecl;
	private DablContext dablContext;
	private Set<Artifact> inputs = new HashSet<Artifact>();
	private Set<Artifact> outputs = new HashSet<Artifact>();
	
	private Set<Task> isConsumerOf = new HashSet<Task>();
	private Set<Task> isProducerFor = new HashSet<Task>();
	
	private TimeUnit timeUnit;
	private Double timeout;
	
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
	
	public Set<Artifact> getInputs() {
		return inputs;
	}
	
	public Set<Artifact> getOutputs() {
		return outputs;
	}
	
	public Set<Task> getProducers() {
		return isConsumerOf;
	}
	
	public Set<Task> getConsumers() {
		return isProducerFor;
	}
	
	public boolean isOpen() {
		return (this.taskDecl.getOopen() instanceof AOpenOopen);
	}
	
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}
	
	public double getTimeout() throws Exception {
		if (this.timeout == null) throw new Exception("No timeout");
		return this.timeout.doubleValue();
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
	 * Return the task's statement sequence as a legal DABL task declaration,
	 * but omitting all non-executable statements, and prepended with the
	 * namespace declaration. This is the program that the task will execute
	 * in the container.
	 */
	public String getTaskProgram() {
		
		String taskProgram = "";

		// Write the namespace declaration.
		NameScopeEntry entry = this.dablContext.getExecHelper()
			.getState().getPrimaryNamespaceSymbolEntry();
		taskProgram += ("namespace " + entry.getName() + "\n");
		
		// Write a task keyword.
		taskProgram += ("task " + getName() + " when true\n");
		
		LinkedList<POprocStmt> plist = this.taskDecl.getOprocStmt();
		for (POprocStmt p : plist) {
			
			taskProgram += SyntaxWriter.procStmtToString(p);
			taskProgram += "\n";
		}
		
		return taskProgram;
	}
	
	protected TimeUnit retrieveTimeUnit() {
		POtimeout pt = this.taskDecl.getOtimeout();
		if (pt instanceof ASpecifiedOtimeout) {
			ASpecifiedOtimeout spect = (ASpecifiedOtimeout)pt;
			POtimeUnit pu = spect.getOtimeUnit();
			return getTimeUnit(pu);
		} else if (pt instanceof AUnspecifiedOtimeout) {
			return null;
		} else throw new RuntimeException(
			"Unexpected type for timeout: " + pt.getClass().getName());
	}
	
	protected Double retrieveTimeout() {
		POtimeout pt = this.taskDecl.getOtimeout();
		if (pt instanceof ASpecifiedOtimeout) {
			ASpecifiedOtimeout spect = (ASpecifiedOtimeout)pt;
			POexpr expr = spect.getOexpr();
			Object result = (new ExpressionEvaluator(this.dablContext)).evaluateExpr(expr);
			Utilities.assertThat(result instanceof Number,
				"Timeout expression is not numeric: it is " + result.getClass().getName());
			if (result instanceof Double) return (Double)result;
			return new Double(((Double)result).doubleValue());
		} else if (pt instanceof AUnspecifiedOtimeout) {
			return null;
		} else throw new RuntimeException(
			"Unexpected type for timeout: " + pt.getClass().getName());
	}
	
	protected TimeUnit getTimeUnit(POtimeUnit pu) {
		if (pu instanceof ADaysOtimeUnit)
			return TimeUnit.days;
		if (pu instanceof AHoursOtimeUnit)
			return TimeUnit.hours;
		if (pu instanceof AMinOtimeUnit)
			return TimeUnit.min;
		if (pu instanceof ASecOtimeUnit)
			return TimeUnit.sec;
		if (pu instanceof AMsOtimeUnit)
			return TimeUnit.ms;
		throw new RuntimeException(
			"Unexpected POtimeUnit kind: " + pu.getClass().getName());
	}
}
