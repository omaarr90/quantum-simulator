// Generated from OpenQasm3.g4 by ANTLR 4.13.1
package com.omaarr90.parser.qasm;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link OpenQasm3Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface OpenQasm3Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(OpenQasm3Parser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#statementLine}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementLine(OpenQasm3Parser.StatementLineContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#version}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVersion(OpenQasm3Parser.VersionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(OpenQasm3Parser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#qubitDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQubitDeclaration(OpenQasm3Parser.QubitDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#classicalDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassicalDeclaration(OpenQasm3Parser.ClassicalDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#gateApplication}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGateApplication(OpenQasm3Parser.GateApplicationContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#gateCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGateCall(OpenQasm3Parser.GateCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#gateName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGateName(OpenQasm3Parser.GateNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#qubitArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQubitArguments(OpenQasm3Parser.QubitArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#qubitReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQubitReference(OpenQasm3Parser.QubitReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#measureStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMeasureStatement(OpenQasm3Parser.MeasureStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#classicalReference}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassicalReference(OpenQasm3Parser.ClassicalReferenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#barrier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBarrier(OpenQasm3Parser.BarrierContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenQasm3Parser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(OpenQasm3Parser.ExpressionContext ctx);
}