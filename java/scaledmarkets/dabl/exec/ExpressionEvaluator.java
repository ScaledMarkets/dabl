package scaledmarkets.dabl.exec;

import scaledmarkets.dabl.node.*;
import scaledmarkets.dabl.util.Utilities;
import java.util.LinkedList;
import java.util.Date;

/**
 * 
 */
public class ExpressionEvaluator {
	
	private ExpressionContext context;
	
	public ExpressionEvaluator(ExpressionContext context) {
		this.context = context;
	}
	
	public Object evaluateExpr(POexpr expr) {
		
		if (expr instanceof ALiteralOexpr) {
			return evaluateLiteral(((ALiteralOexpr)expr).getOliteral());
		} else if (expr instanceof AVariableOexpr) {
			return evaluateVariable(((AVariableOexpr)expr).getOvariable());
		} else if (expr instanceof AUnaryOexpr) {
			return evaluateUnaryExpr((AUnaryOexpr)expr);
		} else if (expr instanceof ABinaryOexpr) {
			return evaluateBinaryExpr((ABinaryOexpr)expr);
		} else if (expr instanceof ASuccessOexpr) {
			return evaluateSuccessExpr((ASuccessOexpr)expr);
		} else if (expr instanceof AAgeOexpr) {
			return evaluateAgeExpr((AAgeOexpr)expr);
		} else throw new RuntimeException("Unexpected expr type: " + expr.getClass().getName());
	}
	
	Object evaluateLiteral(POliteral literal) {
		
		if (literal instanceof ALogicOliteral) {
			POlogicLiteral plit = ((ALogicOliteral)literal).getOlogicLiteral();
			if (plit instanceof ATrueOlogicLiteral) return new Boolean(true);
			else return new Boolean(false);
		} else if (literal instanceof AStringOliteral) {
			AStringOliteral stringLit = (AStringOliteral)literal;
			POstringLiteral plit = stringLit.getOstringLiteral();
			return evaluateStringLiteral(plit);
		} else if (literal instanceof ANumericOliteral) {
			ANumericOliteral numLit = (ANumericOliteral)literal;
			POnumericLiteral p = numLit.getOnumericLiteral();
			if (p instanceof AIntOnumericLiteral) {
				AIntOnumericLiteral intNumLit = (AIntOnumericLiteral)p;
				POsign ps = intNumLit.getOsign();
				boolean isPositive;
				if (ps instanceof ANegativeOsign) {
					isPositive = false;
				} else if (ps instanceof APositiveOsign) {
					isPositive = true;
				} else throw new RuntimeException(
					"Unexpected sign type: " + ps.getClass().getName());
				
				TWholeNumber wholeNum = intNumLit.getWholeNumber();
				String numString = wholeNum.getText();
				long value;
				try {
					value = Long.parseLong(numString);
					if (! isPositive) {
						value = -value;
					}
				} catch (NumberFormatException ex) { throw new RuntimeException(ex); }
				
				return new Long(value);
				
			} else if (p instanceof APatternOnumericLiteral) {
				APatternOnumericLiteral pattern = (APatternOnumericLiteral)p;
				
				POsign ps = pattern.getOsign();
				boolean isPositive;
				if (ps instanceof ANegativeOsign) {
					isPositive = false;
				} else if (ps instanceof APositiveOsign) {
					isPositive = true;
				} else throw new RuntimeException(
					"Unexpected sign type: " + ps.getClass().getName());
			
				POnumSlot pMagnitude = pattern.getMagnitude();
				String magnitudeStr = getNumSlotValueString(pMagnitude);
				
				LinkedList<POnumSlot> pMantissas = pattern.getMantissa();
				if (pMantissas.size() > 1) throw new RuntimeException(
					"A float value cannot have more than one mantissa");
				if (pMantissas.size() == 0) {
					long value = Long.parseLong(magnitudeStr);
					if (isPositive) return new Long(value);
					else return new Long(-value);
				}
				POnumSlot pMantissa = pMantissas.get(0);
				String mantissaStr = getNumSlotValueString(pMantissa);
				double value = Double.parseDouble(magnitudeStr + "." + mantissaStr);
				if (isPositive) return new Double(value);
				else return new Double(-value);
			} else throw new RuntimeException(
				"Unexpected numeric literal type: " + p.getClass().getName());
		} else throw new RuntimeException(
			"Unexpected literal type: " + literal.getClass().getName());
	}
	
	Object evaluateVariable(POvariable pv) {
		AOvariable variable = (AOvariable)pv;
		POidRef pref = variable.getOidRef();
		AOidRef vref = (AOidRef)pref;
		LinkedList<TId> ids = vref.getId();
		if (ids.size() != 1) throw new RuntimeException(
			"Variable name has " + ids.size() + " parts - expected one part");
		String variableName = ids.get(0).getText();
		Object value = context.getValueForVariable(variableName);
		if (value == null) throw new RuntimeException(
			"No value found for variable " + variableName);
		return value;
	}
	
	Object evaluateUnaryExpr(AUnaryOexpr uexpr) {
		
		POunaryOp op = uexpr.getOunaryOp();
		POexpr expr = uexpr.getOexpr();
		Object exprValue = evaluateExpr(expr);
		if (op instanceof ANegationOunaryOp) {
			if (exprValue instanceof Number) {
				if ((exprValue instanceof Integer) || (exprValue instanceof Long)) {
					long value = ((Number)exprValue).longValue();
					return new Long(-value);
				} else if ((exprValue instanceof Float) || (exprValue instanceof Double)) {
					double value = ((Number)exprValue).doubleValue();
					return new Double(-value);
				}
				else throw new RuntimeException(
					"Unexpected numeric type: " + exprValue.getClass().getName());
			} else throw new RuntimeException(
				"Negation operator can only be used on a numeric type");
		} else throw new RuntimeException(
			"Unexpected unary operator type: " + op.getClass().getName());
	}
	
	Object evaluateBinaryExpr(ABinaryOexpr bexpr) {
		
		Object left = evaluateExpr(bexpr.getOperand1());
		Object right = evaluateExpr(bexpr.getOperand2());
		
		PObinaryOp pOp = bexpr.getObinaryOp();
		
		if (pOp instanceof AAndObinaryOp) {
			if (! ((left instanceof Boolean) && (right instanceof Boolean)))
				throw new RuntimeException("Expected two boolean arguments");
			return (Boolean)left && (Boolean)right;
		} else if (pOp instanceof ADivideObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Double(((Number)left).doubleValue() / ((Number)right).doubleValue());
		} else if (pOp instanceof AEqObinaryOp) {
			return left.equals(right);
		} else if (pOp instanceof AGeObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Boolean(((Number)left).doubleValue() >= ((Number)right).doubleValue());
		} else if (pOp instanceof AGtObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Boolean(((Number)left).doubleValue() > ((Number)right).doubleValue());
		} else if (pOp instanceof ALeObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Boolean(((Number)left).doubleValue() <= ((Number)right).doubleValue());
		} else if (pOp instanceof ALtObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Boolean(((Number)left).doubleValue() < ((Number)right).doubleValue());
		} else if (pOp instanceof APlusObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Double(((Number)left).doubleValue() + ((Number)right).doubleValue());
		} else if (pOp instanceof AMinusObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Double(((Number)left).doubleValue() - ((Number)right).doubleValue());
		} else if (pOp instanceof AMultiplyObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Double(((Number)left).doubleValue() * ((Number)right).doubleValue());
		} else if (pOp instanceof ANeObinaryOp) {
			if (! ((left instanceof Number) && (right instanceof Number)))
				throw new RuntimeException("Expected two boolean arguments");
			return new Boolean(((Number)left).doubleValue() != ((Number)right).doubleValue());
		} else if (pOp instanceof AOrObinaryOp) {
			if (! ((left instanceof Boolean) && (right instanceof Boolean)))
				throw new RuntimeException("Expected two boolean arguments");
			return (Boolean)left || (Boolean)right;
		} else throw new RuntimeException(
			"Unexpected binary operator type: " + pOp.getClass().getName());
	}
	
	Object evaluateSuccessExpr(ASuccessOexpr sexpr) {
		POsuccessExpr p = sexpr.getOsuccessExpr();
		POidRef idref;
		boolean wantSuccess;
		if (p instanceof ASucceededOsuccessExpr) {
			idref = ((ASucceededOsuccessExpr)p).getOidRef();
			wantSuccess = true;
		} else if (p instanceof AFailedOsuccessExpr) {
			idref = ((AFailedOsuccessExpr)p).getOidRef();
			wantSuccess = false;
		} else throw new RuntimeException("Unexpected success expr type: " + p.getClass().getName());
		LinkedList<TId> ids = ((AOidRef)idref).getId();
		String taskName = Utilities.createNameFromPath(ids);
		int taskStatus;
		try {
			taskStatus = context.getTaskStatus(taskName);
		} catch (Exception ex) { throw new RuntimeException(ex); }
		
		return wantSuccess == (taskStatus == 0);
	}
	
	Object evaluateAgeExpr(AAgeOexpr aexpr) {
		
		POageExpr pAgeExpr = aexpr.getOageExpr();
		boolean isTrue;
		try {
			if (pAgeExpr instanceof ANewerThanOageExpr) {
				ANewerThanOageExpr newExpr = (ANewerThanOageExpr)pAgeExpr;
				AOidRef newerIdRef = (AOidRef)(newExpr.getNewerId());
				AOidRef olderIdRef = (AOidRef)(newExpr.getOlderId());
				
				try {
					Date newerDate = this.context.getDateOfMostRecentChange(newerIdRef);
					Date olderDate = this.context.getDateOfMostRecentChange(olderIdRef);
					isTrue = (newerDate.compareTo(olderDate) > 0);
				} catch (Exception ex) { throw new RuntimeException(ex); }
				
			} else if (pAgeExpr instanceof AOlderThanOageExpr) {
				AOlderThanOageExpr olderExpr = (AOlderThanOageExpr)pAgeExpr;
				AOidRef olderId = (AOidRef)(olderExpr.getOlderId());
				AOidRef newerId = (AOidRef)(olderExpr.getNewerId());
				
				try {
					Date newerDate = this.context.getDateOfMostRecentChange(newerId);
					Date olderDate = this.context.getDateOfMostRecentChange(olderId);
					isTrue = (newerDate.compareTo(olderDate) > 0);
				} catch (Exception ex) { throw new RuntimeException(ex); }
				
			} else throw new RuntimeException(
				"Unexpected age expr type: " + pAgeExpr.getClass().getName());
		} catch (Exception ex) { throw new RuntimeException(ex); }
		
		return new Boolean(isTrue);
	}
	
	String evaluateStringLiteral(POstringLiteral plit) {
		if (plit instanceof AStringOstringLiteral) {
			String s = ((AStringOstringLiteral)plit).getString().getText();
			return s.substring(1, s.length()-2);  // Remove quotes
		} else if (plit instanceof AString2OstringLiteral) {
			String s = ((AString2OstringLiteral)plit).getString2().getText();
			return s.substring(3, s.length()-6);  // Remove triple quotes
		} else if (plit instanceof AStaticStringExprOstringLiteral) {
			AStaticStringExprOstringLiteral sexpr = (AStaticStringExprOstringLiteral)plit;
			return evaluateStringLiteral(sexpr.getLeft()) +
				evaluateStringLiteral(sexpr.getRight());
		} else throw new RuntimeException(
			"Unexpected literal type: " + plit.getClass().getName());
	}
	
	String getNumSlotValueString(POnumSlot pNumSlot) {
		if (pNumSlot instanceof ANumOnumSlot) {
			TWholeNumber number = ((ANumOnumSlot)pNumSlot).getWholeNumber();
			try {
				return number.getText();
			} catch (NumberFormatException ex) { throw new RuntimeException(ex); }
		} else if (pNumSlot instanceof AWildcardOnumSlot) {
			throw new RuntimeException(
				"A wildcard cannot appear in an expression");
		} else throw new RuntimeException(
			"Unexpected magnitude: " + pNumSlot.getClass().getName());
	}
}
