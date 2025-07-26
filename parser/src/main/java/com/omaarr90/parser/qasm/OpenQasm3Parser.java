// Generated from OpenQasm3.g4 by ANTLR 4.13.1
package com.omaarr90.parser.qasm;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class OpenQasm3Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, IDENTIFIER=16, 
		INT=17, REAL=18, PI=19, WS=20, LINE_COMMENT=21, BLOCK_COMMENT=22;
	public static final int
		RULE_program = 0, RULE_version = 1, RULE_statement = 2, RULE_qubitDeclaration = 3, 
		RULE_gateApplication = 4, RULE_gateCall = 5, RULE_qubitArguments = 6, 
		RULE_qubitReference = 7, RULE_measureStatement = 8, RULE_classicalReference = 9, 
		RULE_barrier = 10, RULE_expression = 11;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "version", "statement", "qubitDeclaration", "gateApplication", 
			"gateCall", "qubitArguments", "qubitReference", "measureStatement", "classicalReference", 
			"barrier", "expression"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'OPENQASM'", "';'", "'qubit'", "'['", "']'", "'('", "')'", "','", 
			"'measure'", "'->'", "'barrier'", "'*'", "'/'", "'+'", "'-'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "IDENTIFIER", "INT", "REAL", "PI", "WS", "LINE_COMMENT", 
			"BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "OpenQasm3.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public OpenQasm3Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(OpenQasm3Parser.EOF, 0); }
		public VersionContext version() {
			return getRuleContext(VersionContext.class,0);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(24);
				version();
				}
			}

			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 68104L) != 0)) {
				{
				{
				setState(27);
				statement();
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(33);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class VersionContext extends ParserRuleContext {
		public TerminalNode REAL() { return getToken(OpenQasm3Parser.REAL, 0); }
		public VersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_version; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitVersion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitVersion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final VersionContext version() throws RecognitionException {
		VersionContext _localctx = new VersionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_version);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(T__0);
			setState(36);
			match(REAL);
			setState(37);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public QubitDeclarationContext qubitDeclaration() {
			return getRuleContext(QubitDeclarationContext.class,0);
		}
		public GateApplicationContext gateApplication() {
			return getRuleContext(GateApplicationContext.class,0);
		}
		public MeasureStatementContext measureStatement() {
			return getRuleContext(MeasureStatementContext.class,0);
		}
		public BarrierContext barrier() {
			return getRuleContext(BarrierContext.class,0);
		}
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_statement);
		try {
			setState(43);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__2:
				enterOuterAlt(_localctx, 1);
				{
				setState(39);
				qubitDeclaration();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(40);
				gateApplication();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(41);
				measureStatement();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(42);
				barrier();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QubitDeclarationContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(OpenQasm3Parser.INT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(OpenQasm3Parser.IDENTIFIER, 0); }
		public QubitDeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qubitDeclaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterQubitDeclaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitQubitDeclaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitQubitDeclaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QubitDeclarationContext qubitDeclaration() throws RecognitionException {
		QubitDeclarationContext _localctx = new QubitDeclarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_qubitDeclaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			match(T__2);
			setState(46);
			match(T__3);
			setState(47);
			match(INT);
			setState(48);
			match(T__4);
			setState(49);
			match(IDENTIFIER);
			setState(50);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GateApplicationContext extends ParserRuleContext {
		public GateCallContext gateCall() {
			return getRuleContext(GateCallContext.class,0);
		}
		public QubitArgumentsContext qubitArguments() {
			return getRuleContext(QubitArgumentsContext.class,0);
		}
		public GateApplicationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gateApplication; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterGateApplication(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitGateApplication(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitGateApplication(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GateApplicationContext gateApplication() throws RecognitionException {
		GateApplicationContext _localctx = new GateApplicationContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_gateApplication);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			gateCall();
			setState(53);
			qubitArguments();
			setState(54);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GateCallContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(OpenQasm3Parser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public GateCallContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_gateCall; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterGateCall(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitGateCall(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitGateCall(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GateCallContext gateCall() throws RecognitionException {
		GateCallContext _localctx = new GateCallContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_gateCall);
		try {
			setState(62);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(56);
				match(IDENTIFIER);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(57);
				match(IDENTIFIER);
				setState(58);
				match(T__5);
				setState(59);
				expression(0);
				setState(60);
				match(T__6);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QubitArgumentsContext extends ParserRuleContext {
		public List<QubitReferenceContext> qubitReference() {
			return getRuleContexts(QubitReferenceContext.class);
		}
		public QubitReferenceContext qubitReference(int i) {
			return getRuleContext(QubitReferenceContext.class,i);
		}
		public QubitArgumentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qubitArguments; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterQubitArguments(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitQubitArguments(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitQubitArguments(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QubitArgumentsContext qubitArguments() throws RecognitionException {
		QubitArgumentsContext _localctx = new QubitArgumentsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_qubitArguments);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(64);
			qubitReference();
			setState(69);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(65);
				match(T__7);
				setState(66);
				qubitReference();
				}
				}
				setState(71);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class QubitReferenceContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(OpenQasm3Parser.IDENTIFIER, 0); }
		public TerminalNode INT() { return getToken(OpenQasm3Parser.INT, 0); }
		public QubitReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qubitReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterQubitReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitQubitReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitQubitReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QubitReferenceContext qubitReference() throws RecognitionException {
		QubitReferenceContext _localctx = new QubitReferenceContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_qubitReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(IDENTIFIER);
			setState(73);
			match(T__3);
			setState(74);
			match(INT);
			setState(75);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MeasureStatementContext extends ParserRuleContext {
		public QubitReferenceContext qubitReference() {
			return getRuleContext(QubitReferenceContext.class,0);
		}
		public ClassicalReferenceContext classicalReference() {
			return getRuleContext(ClassicalReferenceContext.class,0);
		}
		public List<TerminalNode> IDENTIFIER() { return getTokens(OpenQasm3Parser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(OpenQasm3Parser.IDENTIFIER, i);
		}
		public MeasureStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_measureStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterMeasureStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitMeasureStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitMeasureStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MeasureStatementContext measureStatement() throws RecognitionException {
		MeasureStatementContext _localctx = new MeasureStatementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_measureStatement);
		try {
			setState(88);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(77);
				match(T__8);
				setState(78);
				qubitReference();
				setState(79);
				match(T__9);
				setState(80);
				classicalReference();
				setState(81);
				match(T__1);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(83);
				match(T__8);
				setState(84);
				match(IDENTIFIER);
				setState(85);
				match(T__9);
				setState(86);
				match(IDENTIFIER);
				setState(87);
				match(T__1);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ClassicalReferenceContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(OpenQasm3Parser.IDENTIFIER, 0); }
		public TerminalNode INT() { return getToken(OpenQasm3Parser.INT, 0); }
		public ClassicalReferenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_classicalReference; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterClassicalReference(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitClassicalReference(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitClassicalReference(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ClassicalReferenceContext classicalReference() throws RecognitionException {
		ClassicalReferenceContext _localctx = new ClassicalReferenceContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_classicalReference);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(IDENTIFIER);
			setState(91);
			match(T__3);
			setState(92);
			match(INT);
			setState(93);
			match(T__4);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BarrierContext extends ParserRuleContext {
		public QubitArgumentsContext qubitArguments() {
			return getRuleContext(QubitArgumentsContext.class,0);
		}
		public BarrierContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_barrier; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterBarrier(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitBarrier(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitBarrier(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BarrierContext barrier() throws RecognitionException {
		BarrierContext _localctx = new BarrierContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_barrier);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(T__10);
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(96);
				qubitArguments();
				}
			}

			setState(99);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public TerminalNode REAL() { return getToken(OpenQasm3Parser.REAL, 0); }
		public TerminalNode INT() { return getToken(OpenQasm3Parser.INT, 0); }
		public TerminalNode PI() { return getToken(OpenQasm3Parser.PI, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenQasm3Listener ) ((OpenQasm3Listener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenQasm3Visitor ) return ((OpenQasm3Visitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 22;
		enterRecursionRule(_localctx, 22, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case REAL:
				{
				setState(102);
				match(REAL);
				}
				break;
			case INT:
				{
				setState(103);
				match(INT);
				}
				break;
			case PI:
				{
				setState(104);
				match(PI);
				}
				break;
			case T__5:
				{
				setState(105);
				match(T__5);
				setState(106);
				expression(0);
				setState(107);
				match(T__6);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(119);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(117);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
					case 1:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(111);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(112);
						_la = _input.LA(1);
						if ( !(_la==T__11 || _la==T__12) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(113);
						expression(4);
						}
						break;
					case 2:
						{
						_localctx = new ExpressionContext(_parentctx, _parentState);
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(114);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(115);
						_la = _input.LA(1);
						if ( !(_la==T__13 || _la==T__14) ) {
						_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(116);
						expression(3);
						}
						break;
					}
					} 
				}
				setState(121);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 11:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 3);
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0016{\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0001"+
		"\u0000\u0003\u0000\u001a\b\u0000\u0001\u0000\u0005\u0000\u001d\b\u0000"+
		"\n\u0000\f\u0000 \t\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002,\b\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0001\u0005\u0003\u0005?\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006"+
		"\u0005\u0006D\b\u0006\n\u0006\f\u0006G\t\u0006\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\bY\b\b\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0003\nb\b\n\u0001"+
		"\n\u0001\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000bn\b\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0005\u000b"+
		"v\b\u000b\n\u000b\f\u000by\t\u000b\u0001\u000b\u0000\u0001\u0016\f\u0000"+
		"\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0000\u0002\u0001"+
		"\u0000\f\r\u0001\u0000\u000e\u000f|\u0000\u0019\u0001\u0000\u0000\u0000"+
		"\u0002#\u0001\u0000\u0000\u0000\u0004+\u0001\u0000\u0000\u0000\u0006-"+
		"\u0001\u0000\u0000\u0000\b4\u0001\u0000\u0000\u0000\n>\u0001\u0000\u0000"+
		"\u0000\f@\u0001\u0000\u0000\u0000\u000eH\u0001\u0000\u0000\u0000\u0010"+
		"X\u0001\u0000\u0000\u0000\u0012Z\u0001\u0000\u0000\u0000\u0014_\u0001"+
		"\u0000\u0000\u0000\u0016m\u0001\u0000\u0000\u0000\u0018\u001a\u0003\u0002"+
		"\u0001\u0000\u0019\u0018\u0001\u0000\u0000\u0000\u0019\u001a\u0001\u0000"+
		"\u0000\u0000\u001a\u001e\u0001\u0000\u0000\u0000\u001b\u001d\u0003\u0004"+
		"\u0002\u0000\u001c\u001b\u0001\u0000\u0000\u0000\u001d \u0001\u0000\u0000"+
		"\u0000\u001e\u001c\u0001\u0000\u0000\u0000\u001e\u001f\u0001\u0000\u0000"+
		"\u0000\u001f!\u0001\u0000\u0000\u0000 \u001e\u0001\u0000\u0000\u0000!"+
		"\"\u0005\u0000\u0000\u0001\"\u0001\u0001\u0000\u0000\u0000#$\u0005\u0001"+
		"\u0000\u0000$%\u0005\u0012\u0000\u0000%&\u0005\u0002\u0000\u0000&\u0003"+
		"\u0001\u0000\u0000\u0000\',\u0003\u0006\u0003\u0000(,\u0003\b\u0004\u0000"+
		"),\u0003\u0010\b\u0000*,\u0003\u0014\n\u0000+\'\u0001\u0000\u0000\u0000"+
		"+(\u0001\u0000\u0000\u0000+)\u0001\u0000\u0000\u0000+*\u0001\u0000\u0000"+
		"\u0000,\u0005\u0001\u0000\u0000\u0000-.\u0005\u0003\u0000\u0000./\u0005"+
		"\u0004\u0000\u0000/0\u0005\u0011\u0000\u000001\u0005\u0005\u0000\u0000"+
		"12\u0005\u0010\u0000\u000023\u0005\u0002\u0000\u00003\u0007\u0001\u0000"+
		"\u0000\u000045\u0003\n\u0005\u000056\u0003\f\u0006\u000067\u0005\u0002"+
		"\u0000\u00007\t\u0001\u0000\u0000\u00008?\u0005\u0010\u0000\u00009:\u0005"+
		"\u0010\u0000\u0000:;\u0005\u0006\u0000\u0000;<\u0003\u0016\u000b\u0000"+
		"<=\u0005\u0007\u0000\u0000=?\u0001\u0000\u0000\u0000>8\u0001\u0000\u0000"+
		"\u0000>9\u0001\u0000\u0000\u0000?\u000b\u0001\u0000\u0000\u0000@E\u0003"+
		"\u000e\u0007\u0000AB\u0005\b\u0000\u0000BD\u0003\u000e\u0007\u0000CA\u0001"+
		"\u0000\u0000\u0000DG\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000"+
		"EF\u0001\u0000\u0000\u0000F\r\u0001\u0000\u0000\u0000GE\u0001\u0000\u0000"+
		"\u0000HI\u0005\u0010\u0000\u0000IJ\u0005\u0004\u0000\u0000JK\u0005\u0011"+
		"\u0000\u0000KL\u0005\u0005\u0000\u0000L\u000f\u0001\u0000\u0000\u0000"+
		"MN\u0005\t\u0000\u0000NO\u0003\u000e\u0007\u0000OP\u0005\n\u0000\u0000"+
		"PQ\u0003\u0012\t\u0000QR\u0005\u0002\u0000\u0000RY\u0001\u0000\u0000\u0000"+
		"ST\u0005\t\u0000\u0000TU\u0005\u0010\u0000\u0000UV\u0005\n\u0000\u0000"+
		"VW\u0005\u0010\u0000\u0000WY\u0005\u0002\u0000\u0000XM\u0001\u0000\u0000"+
		"\u0000XS\u0001\u0000\u0000\u0000Y\u0011\u0001\u0000\u0000\u0000Z[\u0005"+
		"\u0010\u0000\u0000[\\\u0005\u0004\u0000\u0000\\]\u0005\u0011\u0000\u0000"+
		"]^\u0005\u0005\u0000\u0000^\u0013\u0001\u0000\u0000\u0000_a\u0005\u000b"+
		"\u0000\u0000`b\u0003\f\u0006\u0000a`\u0001\u0000\u0000\u0000ab\u0001\u0000"+
		"\u0000\u0000bc\u0001\u0000\u0000\u0000cd\u0005\u0002\u0000\u0000d\u0015"+
		"\u0001\u0000\u0000\u0000ef\u0006\u000b\uffff\uffff\u0000fn\u0005\u0012"+
		"\u0000\u0000gn\u0005\u0011\u0000\u0000hn\u0005\u0013\u0000\u0000ij\u0005"+
		"\u0006\u0000\u0000jk\u0003\u0016\u000b\u0000kl\u0005\u0007\u0000\u0000"+
		"ln\u0001\u0000\u0000\u0000me\u0001\u0000\u0000\u0000mg\u0001\u0000\u0000"+
		"\u0000mh\u0001\u0000\u0000\u0000mi\u0001\u0000\u0000\u0000nw\u0001\u0000"+
		"\u0000\u0000op\n\u0003\u0000\u0000pq\u0007\u0000\u0000\u0000qv\u0003\u0016"+
		"\u000b\u0004rs\n\u0002\u0000\u0000st\u0007\u0001\u0000\u0000tv\u0003\u0016"+
		"\u000b\u0003uo\u0001\u0000\u0000\u0000ur\u0001\u0000\u0000\u0000vy\u0001"+
		"\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000\u0000\u0000"+
		"x\u0017\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000\n\u0019\u001e"+
		"+>EXamuw";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}