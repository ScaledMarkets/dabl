package com.scaledmarkets.dabl.exec;

import com.scaledmarkets.dabl.node.*;
import java.util.LinkedList;

/**
 * Methods for writing DABL AST elements as DABL syntax.
 */
public class SyntaxWriter {

	/**
	 * Walk the expression and return its DABL syntax equivalent,
	 */
	public static String exprToString(POexpr pexpr) {
		
		if (pexpr instanceof AAgeOexpr) {
			POageExpr pe = ((AAgeOexpr)pexpr).getOageExpr();
			if (pe instanceof ANewerThanOageExpr) {
				// id newer than [older_id]:id
				POidRef pnewer = ((ANewerThanOageExpr)pe).getNewerId();
				POidRef polder = ((ANewerThanOageExpr)pe).getOlderId();
				return pnewer.toString() + " newer than " + polder.toString();
			} else if (pe instanceof AOlderThanOageExpr) {
				// id older than [older_id]:id
				POidRef polder = ((AOlderThanOageExpr)pe).getOlderId();
				POidRef pnewer = ((AOlderThanOageExpr)pe).getNewerId();
				return polder + " older than " + pnewer.toString();
			} else throw new RuntimeException(
				"age expr is not a known type of POageExpr: it is a " + pe.getClass().getName());
		} else if (pexpr instanceof ABinaryOexpr) {
			return pexpr.toString();
		} else if (pexpr instanceof ALiteralOexpr) {
			POliteral plit = ((ALiteralOexpr)pexpr).getOliteral();
			// logic, string lit, or numeric lit
			if (plit instanceof ALogicOliteral) {
				POlogicLiteral plog = ((ALogicOliteral)plit).getOlogicLiteral();
				if (plog instanceof ATrueOlogicLiteral) {
					return "true";
				} else if (plog instanceof AFalseOlogicLiteral) {
					return "false";
				} else throw new RuntimeException(
					"logic literal is not a known type of POlogicLiteral: it is a " + plog.getClass().getName());
			} else if (plit instanceof ANumericOliteral) {
				return plit.toString();
			} else if (plit instanceof AStringOliteral) {
				POstringLiteral pstrlit = ((AStringOliteral)plit).getOstringLiteral();
				return stringLiteralToString(pstrlit);
			} else throw new RuntimeException(
				"literal is not a known type of POliteral: it is a " + plit.getClass().getName());
		} else if (pexpr instanceof ASuccessOexpr) {
			POsuccessExpr sexpr = ((ASuccessOexpr)pexpr).getOsuccessExpr();
			if (sexpr instanceof ASucceededOsuccessExpr) {
				AOidRef idref = (AOidRef)(((ASucceededOsuccessExpr)sexpr).getOidRef());
				return idref.toString() + " succeeded";
			} else if (sexpr instanceof AFailedOsuccessExpr) {
				AOidRef idref = (AOidRef)(((AFailedOsuccessExpr)sexpr).getOidRef());
				return idref.toString() + " failed";
			} else throw new RuntimeException(
				"Success expression is not a known type: " + sexpr.getClass().getName());
		} else if (pexpr instanceof AUnaryOexpr) {
			return pexpr.toString();
		} else if (pexpr instanceof AVariableOexpr) {
			return pexpr.toString();
		} else throw new RuntimeException(
			"expr is not an known type of POexpr: it is a " + pexpr.getClass().getName());
	}
	
	/**
	 * Walk the proc statement and return its DABL syntax equivalent.
	 */
	public static String procStmtToString(POprocStmt p) {
		
		String taskProgram = "";
		
		if (p instanceof AFuncCallOprocStmt) {
			AFuncCallOprocStmt funcCallStmt = (AFuncCallOprocStmt)p;
			// oid_ref oexpr* otarget_opt
			
			// Obtain the elements of the statement.
			
			POidRef pfidref = funcCallStmt.getOidRef();
			
			LinkedList<POexpr> pexprs = funcCallStmt.getOexpr();
			
			POtargetOpt ptarget = funcCallStmt.getOtargetOpt();
			
			// Write the function call statement to the task program string.
			
			taskProgram += ptarget.toString();
			taskProgram += ( " = " + pfidref.toString());
			
			boolean firstTime = true;
			for (POexpr pexpr : pexprs) {
				if (firstTime) firstTime = false;
				else taskProgram += ", ";
				taskProgram += exprToString(pexpr);
			}
				
		} else if (p instanceof AIfErrorOprocStmt) {
			AIfErrorOprocStmt errorStmt = (AIfErrorOprocStmt)p;
			
			taskProgram += "if error\n";
			
			// oproc_stmt*
			LinkedList<POprocStmt> procStmts = errorStmt.getOprocStmt();
			for (POprocStmt procStmt : procStmts) {
				taskProgram += ("\t" + procStmtToString(procStmt) + "\n");
			}

			taskProgram += "end if";
		} else
			throw new RuntimeException(
				"Unexpected POprocStmt node kind: " + p.getClass().getName());
			
		return taskProgram;
	}
	
	/**
	 * Walk the string literal and return its DABL string syntax representation.
	 */
	public static String stringLiteralToString(POstringLiteral pstrlit) {
		
		if (pstrlit instanceof AStaticStringExprOstringLiteral) {
			POstringLiteral left = ((AStaticStringExprOstringLiteral)pstrlit).getLeft();
			POstringLiteral right = ((AStaticStringExprOstringLiteral)pstrlit).getRight();
			return stringLiteralToString(left) + "^" + stringLiteralToString(right);
		} else if (pstrlit instanceof AString2OstringLiteral) {
			TString2 str = ((AString2OstringLiteral)pstrlit).getString2();
			return "\"\"\"" + str.toString() + "\"\"\"";
		} else if (pstrlit instanceof AStringOstringLiteral) {
			TString str = ((AStringOstringLiteral)pstrlit).getString();
			return "\"" + str.toString() +  "\"";
		} else throw new RuntimeException(
			"string literal is not a known type of POstringLiteral: it is a " + pstrlit.getClass().getName());
	}
}
