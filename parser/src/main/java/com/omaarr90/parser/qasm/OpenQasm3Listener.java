// Generated from OpenQasm3.g4 by ANTLR 4.13.1
package com.omaarr90.parser.qasm;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link OpenQasm3Parser}.
 */
public interface OpenQasm3Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(OpenQasm3Parser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(OpenQasm3Parser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#statementLine}.
	 * @param ctx the parse tree
	 */
	void enterStatementLine(OpenQasm3Parser.StatementLineContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#statementLine}.
	 * @param ctx the parse tree
	 */
	void exitStatementLine(OpenQasm3Parser.StatementLineContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#version}.
	 * @param ctx the parse tree
	 */
	void enterVersion(OpenQasm3Parser.VersionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#version}.
	 * @param ctx the parse tree
	 */
	void exitVersion(OpenQasm3Parser.VersionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(OpenQasm3Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(OpenQasm3Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#qubitDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterQubitDeclaration(OpenQasm3Parser.QubitDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#qubitDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitQubitDeclaration(OpenQasm3Parser.QubitDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#classicalDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassicalDeclaration(OpenQasm3Parser.ClassicalDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#classicalDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassicalDeclaration(OpenQasm3Parser.ClassicalDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#gateApplication}.
	 * @param ctx the parse tree
	 */
	void enterGateApplication(OpenQasm3Parser.GateApplicationContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#gateApplication}.
	 * @param ctx the parse tree
	 */
	void exitGateApplication(OpenQasm3Parser.GateApplicationContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#gateCall}.
	 * @param ctx the parse tree
	 */
	void enterGateCall(OpenQasm3Parser.GateCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#gateCall}.
	 * @param ctx the parse tree
	 */
	void exitGateCall(OpenQasm3Parser.GateCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#gateName}.
	 * @param ctx the parse tree
	 */
	void enterGateName(OpenQasm3Parser.GateNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#gateName}.
	 * @param ctx the parse tree
	 */
	void exitGateName(OpenQasm3Parser.GateNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#qubitArguments}.
	 * @param ctx the parse tree
	 */
	void enterQubitArguments(OpenQasm3Parser.QubitArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#qubitArguments}.
	 * @param ctx the parse tree
	 */
	void exitQubitArguments(OpenQasm3Parser.QubitArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#qubitReference}.
	 * @param ctx the parse tree
	 */
	void enterQubitReference(OpenQasm3Parser.QubitReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#qubitReference}.
	 * @param ctx the parse tree
	 */
	void exitQubitReference(OpenQasm3Parser.QubitReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#measureStatement}.
	 * @param ctx the parse tree
	 */
	void enterMeasureStatement(OpenQasm3Parser.MeasureStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#measureStatement}.
	 * @param ctx the parse tree
	 */
	void exitMeasureStatement(OpenQasm3Parser.MeasureStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#classicalReference}.
	 * @param ctx the parse tree
	 */
	void enterClassicalReference(OpenQasm3Parser.ClassicalReferenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#classicalReference}.
	 * @param ctx the parse tree
	 */
	void exitClassicalReference(OpenQasm3Parser.ClassicalReferenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#barrier}.
	 * @param ctx the parse tree
	 */
	void enterBarrier(OpenQasm3Parser.BarrierContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#barrier}.
	 * @param ctx the parse tree
	 */
	void exitBarrier(OpenQasm3Parser.BarrierContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenQasm3Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(OpenQasm3Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenQasm3Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(OpenQasm3Parser.ExpressionContext ctx);
}