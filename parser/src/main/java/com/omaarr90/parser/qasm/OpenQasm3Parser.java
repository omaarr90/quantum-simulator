// Generated from OpenQasm3.g4 by ANTLR 4.13.1
package com.omaarr90.parser.qasm;

import java.util.List;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class OpenQasm3Parser extends Parser {
    static {
        RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION);
    }

    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache =
            new PredictionContextCache();
    public static final int T__0 = 1,
            T__1 = 2,
            T__2 = 3,
            T__3 = 4,
            T__4 = 5,
            T__5 = 6,
            T__6 = 7,
            T__7 = 8,
            T__8 = 9,
            T__9 = 10,
            T__10 = 11,
            T__11 = 12,
            T__12 = 13,
            T__13 = 14,
            T__14 = 15,
            T__15 = 16,
            T__16 = 17,
            T__17 = 18,
            T__18 = 19,
            IDENTIFIER = 20,
            INT = 21,
            REAL = 22,
            PI = 23,
            WS = 24,
            LINE_COMMENT = 25,
            BLOCK_COMMENT = 26;
    public static final int RULE_program = 0,
            RULE_statementLine = 1,
            RULE_version = 2,
            RULE_statement = 3,
            RULE_qubitDeclaration = 4,
            RULE_classicalDeclaration = 5,
            RULE_gateApplication = 6,
            RULE_gateCall = 7,
            RULE_gateName = 8,
            RULE_qubitArguments = 9,
            RULE_qubitReference = 10,
            RULE_measureStatement = 11,
            RULE_classicalReference = 12,
            RULE_barrier = 13,
            RULE_expression = 14;

    private static String[] makeRuleNames() {
        return new String[] {
            "program",
            "statementLine",
            "version",
            "statement",
            "qubitDeclaration",
            "classicalDeclaration",
            "gateApplication",
            "gateCall",
            "gateName",
            "qubitArguments",
            "qubitReference",
            "measureStatement",
            "classicalReference",
            "barrier",
            "expression"
        };
    }

    public static final String[] ruleNames = makeRuleNames();

    private static String[] makeLiteralNames() {
        return new String[] {
            null,
            "';'",
            "'OPENQASM'",
            "'qubit'",
            "'['",
            "']'",
            "'qreg'",
            "'creg'",
            "'('",
            "')'",
            "'cx'",
            "'cnot'",
            "','",
            "'measure'",
            "'->'",
            "'barrier'",
            "'*'",
            "'/'",
            "'+'",
            "'-'"
        };
    }

    private static final String[] _LITERAL_NAMES = makeLiteralNames();

    private static String[] makeSymbolicNames() {
        return new String[] {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "IDENTIFIER",
            "INT",
            "REAL",
            "PI",
            "WS",
            "LINE_COMMENT",
            "BLOCK_COMMENT"
        };
    }

    private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
    public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    @Deprecated public static final String[] tokenNames;

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
    public String getGrammarFileName() {
        return "OpenQasm3.g4";
    }

    @Override
    public String[] getRuleNames() {
        return ruleNames;
    }

    @Override
    public String getSerializedATN() {
        return _serializedATN;
    }

    @Override
    public ATN getATN() {
        return _ATN;
    }

    public OpenQasm3Parser(TokenStream input) {
        super(input);
        _interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ProgramContext extends ParserRuleContext {
        public TerminalNode EOF() {
            return getToken(OpenQasm3Parser.EOF, 0);
        }

        public VersionContext version() {
            return getRuleContext(VersionContext.class, 0);
        }

        public List<StatementLineContext> statementLine() {
            return getRuleContexts(StatementLineContext.class);
        }

        public StatementLineContext statementLine(int i) {
            return getRuleContext(StatementLineContext.class, i);
        }

        public ProgramContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_program;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterProgram(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitProgram(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitProgram(this);
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
                setState(31);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == T__1) {
                    {
                        setState(30);
                        version();
                    }
                }

                setState(36);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 1092808L) != 0)) {
                    {
                        {
                            setState(33);
                            statementLine();
                        }
                    }
                    setState(38);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
                setState(39);
                match(EOF);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class StatementLineContext extends ParserRuleContext {
        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return getRuleContext(StatementContext.class, i);
        }

        public StatementLineContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statementLine;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterStatementLine(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitStatementLine(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitStatementLine(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatementLineContext statementLine() throws RecognitionException {
        StatementLineContext _localctx = new StatementLineContext(_ctx, getState());
        enterRule(_localctx, 2, RULE_statementLine);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(41);
                statement();
                setState(46);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        {
                            {
                                setState(42);
                                match(T__0);
                                setState(43);
                                statement();
                            }
                        }
                    }
                    setState(48);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 2, _ctx);
                }
                setState(50);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == T__0) {
                    {
                        setState(49);
                        match(T__0);
                    }
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class VersionContext extends ParserRuleContext {
        public TerminalNode REAL() {
            return getToken(OpenQasm3Parser.REAL, 0);
        }

        public VersionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_version;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterVersion(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitVersion(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitVersion(this);
            else return visitor.visitChildren(this);
        }
    }

    public final VersionContext version() throws RecognitionException {
        VersionContext _localctx = new VersionContext(_ctx, getState());
        enterRule(_localctx, 4, RULE_version);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(52);
                match(T__1);
                setState(53);
                match(REAL);
                setState(54);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class StatementContext extends ParserRuleContext {
        public QubitDeclarationContext qubitDeclaration() {
            return getRuleContext(QubitDeclarationContext.class, 0);
        }

        public ClassicalDeclarationContext classicalDeclaration() {
            return getRuleContext(ClassicalDeclarationContext.class, 0);
        }

        public GateApplicationContext gateApplication() {
            return getRuleContext(GateApplicationContext.class, 0);
        }

        public MeasureStatementContext measureStatement() {
            return getRuleContext(MeasureStatementContext.class, 0);
        }

        public BarrierContext barrier() {
            return getRuleContext(BarrierContext.class, 0);
        }

        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_statement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(_ctx, getState());
        enterRule(_localctx, 6, RULE_statement);
        try {
            setState(61);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case T__2:
                case T__5:
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(56);
                        qubitDeclaration();
                    }
                    break;
                case T__6:
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(57);
                        classicalDeclaration();
                    }
                    break;
                case T__9:
                case T__10:
                case IDENTIFIER:
                    enterOuterAlt(_localctx, 3);
                    {
                        setState(58);
                        gateApplication();
                    }
                    break;
                case T__12:
                    enterOuterAlt(_localctx, 4);
                    {
                        setState(59);
                        measureStatement();
                    }
                    break;
                case T__14:
                    enterOuterAlt(_localctx, 5);
                    {
                        setState(60);
                        barrier();
                    }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class QubitDeclarationContext extends ParserRuleContext {
        public TerminalNode INT() {
            return getToken(OpenQasm3Parser.INT, 0);
        }

        public TerminalNode IDENTIFIER() {
            return getToken(OpenQasm3Parser.IDENTIFIER, 0);
        }

        public QubitDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_qubitDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterQubitDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitQubitDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitQubitDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    public final QubitDeclarationContext qubitDeclaration() throws RecognitionException {
        QubitDeclarationContext _localctx = new QubitDeclarationContext(_ctx, getState());
        enterRule(_localctx, 8, RULE_qubitDeclaration);
        try {
            setState(75);
            _errHandler.sync(this);
            switch (_input.LA(1)) {
                case T__2:
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(63);
                        match(T__2);
                        setState(64);
                        match(T__3);
                        setState(65);
                        match(INT);
                        setState(66);
                        match(T__4);
                        setState(67);
                        match(IDENTIFIER);
                        setState(68);
                        match(T__0);
                    }
                    break;
                case T__5:
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(69);
                        match(T__5);
                        setState(70);
                        match(IDENTIFIER);
                        setState(71);
                        match(T__3);
                        setState(72);
                        match(INT);
                        setState(73);
                        match(T__4);
                        setState(74);
                        match(T__0);
                    }
                    break;
                default:
                    throw new NoViableAltException(this);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ClassicalDeclarationContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(OpenQasm3Parser.IDENTIFIER, 0);
        }

        public TerminalNode INT() {
            return getToken(OpenQasm3Parser.INT, 0);
        }

        public ClassicalDeclarationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_classicalDeclaration;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterClassicalDeclaration(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitClassicalDeclaration(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitClassicalDeclaration(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ClassicalDeclarationContext classicalDeclaration() throws RecognitionException {
        ClassicalDeclarationContext _localctx = new ClassicalDeclarationContext(_ctx, getState());
        enterRule(_localctx, 10, RULE_classicalDeclaration);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(77);
                match(T__6);
                setState(78);
                match(IDENTIFIER);
                setState(79);
                match(T__3);
                setState(80);
                match(INT);
                setState(81);
                match(T__4);
                setState(82);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class GateApplicationContext extends ParserRuleContext {
        public GateCallContext gateCall() {
            return getRuleContext(GateCallContext.class, 0);
        }

        public QubitArgumentsContext qubitArguments() {
            return getRuleContext(QubitArgumentsContext.class, 0);
        }

        public GateApplicationContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_gateApplication;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterGateApplication(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitGateApplication(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitGateApplication(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GateApplicationContext gateApplication() throws RecognitionException {
        GateApplicationContext _localctx = new GateApplicationContext(_ctx, getState());
        enterRule(_localctx, 12, RULE_gateApplication);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(84);
                gateCall();
                setState(85);
                qubitArguments();
                setState(86);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class GateCallContext extends ParserRuleContext {
        public GateNameContext gateName() {
            return getRuleContext(GateNameContext.class, 0);
        }

        public ExpressionContext expression() {
            return getRuleContext(ExpressionContext.class, 0);
        }

        public GateCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_gateCall;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterGateCall(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitGateCall(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitGateCall(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GateCallContext gateCall() throws RecognitionException {
        GateCallContext _localctx = new GateCallContext(_ctx, getState());
        enterRule(_localctx, 14, RULE_gateCall);
        try {
            setState(94);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 6, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(88);
                        gateName();
                    }
                    break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(89);
                        gateName();
                        setState(90);
                        match(T__7);
                        setState(91);
                        expression(0);
                        setState(92);
                        match(T__8);
                    }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class GateNameContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(OpenQasm3Parser.IDENTIFIER, 0);
        }

        public GateNameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_gateName;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterGateName(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitGateName(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitGateName(this);
            else return visitor.visitChildren(this);
        }
    }

    public final GateNameContext gateName() throws RecognitionException {
        GateNameContext _localctx = new GateNameContext(_ctx, getState());
        enterRule(_localctx, 16, RULE_gateName);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(96);
                _la = _input.LA(1);
                if (!((((_la) & ~0x3f) == 0 && ((1L << _la) & 1051648L) != 0))) {
                    _errHandler.recoverInline(this);
                } else {
                    if (_input.LA(1) == Token.EOF) matchedEOF = true;
                    _errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
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
            return getRuleContext(QubitReferenceContext.class, i);
        }

        public QubitArgumentsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_qubitArguments;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterQubitArguments(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitQubitArguments(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitQubitArguments(this);
            else return visitor.visitChildren(this);
        }
    }

    public final QubitArgumentsContext qubitArguments() throws RecognitionException {
        QubitArgumentsContext _localctx = new QubitArgumentsContext(_ctx, getState());
        enterRule(_localctx, 18, RULE_qubitArguments);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(98);
                qubitReference();
                setState(103);
                _errHandler.sync(this);
                _la = _input.LA(1);
                while (_la == T__11) {
                    {
                        {
                            setState(99);
                            match(T__11);
                            setState(100);
                            qubitReference();
                        }
                    }
                    setState(105);
                    _errHandler.sync(this);
                    _la = _input.LA(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class QubitReferenceContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(OpenQasm3Parser.IDENTIFIER, 0);
        }

        public TerminalNode INT() {
            return getToken(OpenQasm3Parser.INT, 0);
        }

        public QubitReferenceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_qubitReference;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterQubitReference(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitQubitReference(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitQubitReference(this);
            else return visitor.visitChildren(this);
        }
    }

    public final QubitReferenceContext qubitReference() throws RecognitionException {
        QubitReferenceContext _localctx = new QubitReferenceContext(_ctx, getState());
        enterRule(_localctx, 20, RULE_qubitReference);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(106);
                match(IDENTIFIER);
                setState(107);
                match(T__3);
                setState(108);
                match(INT);
                setState(109);
                match(T__4);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class MeasureStatementContext extends ParserRuleContext {
        public QubitReferenceContext qubitReference() {
            return getRuleContext(QubitReferenceContext.class, 0);
        }

        public ClassicalReferenceContext classicalReference() {
            return getRuleContext(ClassicalReferenceContext.class, 0);
        }

        public List<TerminalNode> IDENTIFIER() {
            return getTokens(OpenQasm3Parser.IDENTIFIER);
        }

        public TerminalNode IDENTIFIER(int i) {
            return getToken(OpenQasm3Parser.IDENTIFIER, i);
        }

        public MeasureStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_measureStatement;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterMeasureStatement(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitMeasureStatement(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitMeasureStatement(this);
            else return visitor.visitChildren(this);
        }
    }

    public final MeasureStatementContext measureStatement() throws RecognitionException {
        MeasureStatementContext _localctx = new MeasureStatementContext(_ctx, getState());
        enterRule(_localctx, 22, RULE_measureStatement);
        try {
            setState(122);
            _errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(_input, 8, _ctx)) {
                case 1:
                    enterOuterAlt(_localctx, 1);
                    {
                        setState(111);
                        match(T__12);
                        setState(112);
                        qubitReference();
                        setState(113);
                        match(T__13);
                        setState(114);
                        classicalReference();
                        setState(115);
                        match(T__0);
                    }
                    break;
                case 2:
                    enterOuterAlt(_localctx, 2);
                    {
                        setState(117);
                        match(T__12);
                        setState(118);
                        match(IDENTIFIER);
                        setState(119);
                        match(T__13);
                        setState(120);
                        match(IDENTIFIER);
                        setState(121);
                        match(T__0);
                    }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ClassicalReferenceContext extends ParserRuleContext {
        public TerminalNode IDENTIFIER() {
            return getToken(OpenQasm3Parser.IDENTIFIER, 0);
        }

        public TerminalNode INT() {
            return getToken(OpenQasm3Parser.INT, 0);
        }

        public ClassicalReferenceContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_classicalReference;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterClassicalReference(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitClassicalReference(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitClassicalReference(this);
            else return visitor.visitChildren(this);
        }
    }

    public final ClassicalReferenceContext classicalReference() throws RecognitionException {
        ClassicalReferenceContext _localctx = new ClassicalReferenceContext(_ctx, getState());
        enterRule(_localctx, 24, RULE_classicalReference);
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(124);
                match(IDENTIFIER);
                setState(125);
                match(T__3);
                setState(126);
                match(INT);
                setState(127);
                match(T__4);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class BarrierContext extends ParserRuleContext {
        public QubitArgumentsContext qubitArguments() {
            return getRuleContext(QubitArgumentsContext.class, 0);
        }

        public BarrierContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_barrier;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterBarrier(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitBarrier(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitBarrier(this);
            else return visitor.visitChildren(this);
        }
    }

    public final BarrierContext barrier() throws RecognitionException {
        BarrierContext _localctx = new BarrierContext(_ctx, getState());
        enterRule(_localctx, 26, RULE_barrier);
        int _la;
        try {
            enterOuterAlt(_localctx, 1);
            {
                setState(129);
                match(T__14);
                setState(131);
                _errHandler.sync(this);
                _la = _input.LA(1);
                if (_la == IDENTIFIER) {
                    {
                        setState(130);
                        qubitArguments();
                    }
                }

                setState(133);
                match(T__0);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    @SuppressWarnings("CheckReturnValue")
    public static class ExpressionContext extends ParserRuleContext {
        public TerminalNode REAL() {
            return getToken(OpenQasm3Parser.REAL, 0);
        }

        public TerminalNode INT() {
            return getToken(OpenQasm3Parser.INT, 0);
        }

        public TerminalNode PI() {
            return getToken(OpenQasm3Parser.PI, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return getRuleContext(ExpressionContext.class, i);
        }

        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        @Override
        public int getRuleIndex() {
            return RULE_expression;
        }

        @Override
        public void enterRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).enterExpression(this);
        }

        @Override
        public void exitRule(ParseTreeListener listener) {
            if (listener instanceof OpenQasm3Listener)
                ((OpenQasm3Listener) listener).exitExpression(this);
        }

        @Override
        public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
            if (visitor instanceof OpenQasm3Visitor)
                return ((OpenQasm3Visitor<? extends T>) visitor).visitExpression(this);
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
        int _startState = 28;
        enterRecursionRule(_localctx, 28, RULE_expression, _p);
        int _la;
        try {
            int _alt;
            enterOuterAlt(_localctx, 1);
            {
                setState(143);
                _errHandler.sync(this);
                switch (_input.LA(1)) {
                    case REAL:
                        {
                            setState(136);
                            match(REAL);
                        }
                        break;
                    case INT:
                        {
                            setState(137);
                            match(INT);
                        }
                        break;
                    case PI:
                        {
                            setState(138);
                            match(PI);
                        }
                        break;
                    case T__7:
                        {
                            setState(139);
                            match(T__7);
                            setState(140);
                            expression(0);
                            setState(141);
                            match(T__8);
                        }
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                _ctx.stop = _input.LT(-1);
                setState(153);
                _errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                while (_alt != 2 && _alt != org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER) {
                    if (_alt == 1) {
                        if (_parseListeners != null) triggerExitRuleEvent();
                        _prevctx = _localctx;
                        {
                            setState(151);
                            _errHandler.sync(this);
                            switch (getInterpreter().adaptivePredict(_input, 11, _ctx)) {
                                case 1:
                                    {
                                        _localctx = new ExpressionContext(_parentctx, _parentState);
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_expression);
                                        setState(145);
                                        if (!(precpred(_ctx, 3)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 3)");
                                        setState(146);
                                        _la = _input.LA(1);
                                        if (!(_la == T__15 || _la == T__16)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                        setState(147);
                                        expression(4);
                                    }
                                    break;
                                case 2:
                                    {
                                        _localctx = new ExpressionContext(_parentctx, _parentState);
                                        pushNewRecursionContext(
                                                _localctx, _startState, RULE_expression);
                                        setState(148);
                                        if (!(precpred(_ctx, 2)))
                                            throw new FailedPredicateException(
                                                    this, "precpred(_ctx, 2)");
                                        setState(149);
                                        _la = _input.LA(1);
                                        if (!(_la == T__17 || _la == T__18)) {
                                            _errHandler.recoverInline(this);
                                        } else {
                                            if (_input.LA(1) == Token.EOF) matchedEOF = true;
                                            _errHandler.reportMatch(this);
                                            consume();
                                        }
                                        setState(150);
                                        expression(3);
                                    }
                                    break;
                            }
                        }
                    }
                    setState(155);
                    _errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(_input, 12, _ctx);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            _errHandler.reportError(this, re);
            _errHandler.recover(this, re);
        } finally {
            unrollRecursionContexts(_parentctx);
        }
        return _localctx;
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 14:
                return expression_sempred((ExpressionContext) _localctx, predIndex);
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
            "\u0004\u0001\u001a\u009d\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"
                    + "\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"
                    + "\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"
                    + "\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"
                    + "\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0001\u0000\u0003"
                    + "\u0000 \b\u0000\u0001\u0000\u0005\u0000#\b\u0000\n\u0000\f\u0000&\t\u0000"
                    + "\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0005\u0001"
                    + "-\b\u0001\n\u0001\f\u00010\t\u0001\u0001\u0001\u0003\u00013\b\u0001\u0001"
                    + "\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001"
                    + "\u0003\u0001\u0003\u0001\u0003\u0003\u0003>\b\u0003\u0001\u0004\u0001"
                    + "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"
                    + "\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004L\b"
                    + "\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001"
                    + "\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001"
                    + "\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0003"
                    + "\u0007_\b\u0007\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\tf\b\t\n"
                    + "\t\f\ti\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\u000b\u0001"
                    + "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"
                    + "\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b{\b\u000b\u0001"
                    + "\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0003\r\u0084\b\r\u0001"
                    + "\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e"
                    + "\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u0090\b\u000e\u0001\u000e"
                    + "\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e"
                    + "\u0098\b\u000e\n\u000e\f\u000e\u009b\t\u000e\u0001\u000e\u0000\u0001\u001c"
                    + "\u000f\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018"
                    + "\u001a\u001c\u0000\u0003\u0002\u0000\n\u000b\u0014\u0014\u0001\u0000\u0010"
                    + "\u0011\u0001\u0000\u0012\u0013\u009f\u0000\u001f\u0001\u0000\u0000\u0000"
                    + "\u0002)\u0001\u0000\u0000\u0000\u00044\u0001\u0000\u0000\u0000\u0006="
                    + "\u0001\u0000\u0000\u0000\bK\u0001\u0000\u0000\u0000\nM\u0001\u0000\u0000"
                    + "\u0000\fT\u0001\u0000\u0000\u0000\u000e^\u0001\u0000\u0000\u0000\u0010"
                    + "`\u0001\u0000\u0000\u0000\u0012b\u0001\u0000\u0000\u0000\u0014j\u0001"
                    + "\u0000\u0000\u0000\u0016z\u0001\u0000\u0000\u0000\u0018|\u0001\u0000\u0000"
                    + "\u0000\u001a\u0081\u0001\u0000\u0000\u0000\u001c\u008f\u0001\u0000\u0000"
                    + "\u0000\u001e \u0003\u0004\u0002\u0000\u001f\u001e\u0001\u0000\u0000\u0000"
                    + "\u001f \u0001\u0000\u0000\u0000 $\u0001\u0000\u0000\u0000!#\u0003\u0002"
                    + "\u0001\u0000\"!\u0001\u0000\u0000\u0000#&\u0001\u0000\u0000\u0000$\"\u0001"
                    + "\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%\'\u0001\u0000\u0000\u0000"
                    + "&$\u0001\u0000\u0000\u0000\'(\u0005\u0000\u0000\u0001(\u0001\u0001\u0000"
                    + "\u0000\u0000).\u0003\u0006\u0003\u0000*+\u0005\u0001\u0000\u0000+-\u0003"
                    + "\u0006\u0003\u0000,*\u0001\u0000\u0000\u0000-0\u0001\u0000\u0000\u0000"
                    + ".,\u0001\u0000\u0000\u0000./\u0001\u0000\u0000\u0000/2\u0001\u0000\u0000"
                    + "\u00000.\u0001\u0000\u0000\u000013\u0005\u0001\u0000\u000021\u0001\u0000"
                    + "\u0000\u000023\u0001\u0000\u0000\u00003\u0003\u0001\u0000\u0000\u0000"
                    + "45\u0005\u0002\u0000\u000056\u0005\u0016\u0000\u000067\u0005\u0001\u0000"
                    + "\u00007\u0005\u0001\u0000\u0000\u00008>\u0003\b\u0004\u00009>\u0003\n"
                    + "\u0005\u0000:>\u0003\f\u0006\u0000;>\u0003\u0016\u000b\u0000<>\u0003\u001a"
                    + "\r\u0000=8\u0001\u0000\u0000\u0000=9\u0001\u0000\u0000\u0000=:\u0001\u0000"
                    + "\u0000\u0000=;\u0001\u0000\u0000\u0000=<\u0001\u0000\u0000\u0000>\u0007"
                    + "\u0001\u0000\u0000\u0000?@\u0005\u0003\u0000\u0000@A\u0005\u0004\u0000"
                    + "\u0000AB\u0005\u0015\u0000\u0000BC\u0005\u0005\u0000\u0000CD\u0005\u0014"
                    + "\u0000\u0000DL\u0005\u0001\u0000\u0000EF\u0005\u0006\u0000\u0000FG\u0005"
                    + "\u0014\u0000\u0000GH\u0005\u0004\u0000\u0000HI\u0005\u0015\u0000\u0000"
                    + "IJ\u0005\u0005\u0000\u0000JL\u0005\u0001\u0000\u0000K?\u0001\u0000\u0000"
                    + "\u0000KE\u0001\u0000\u0000\u0000L\t\u0001\u0000\u0000\u0000MN\u0005\u0007"
                    + "\u0000\u0000NO\u0005\u0014\u0000\u0000OP\u0005\u0004\u0000\u0000PQ\u0005"
                    + "\u0015\u0000\u0000QR\u0005\u0005\u0000\u0000RS\u0005\u0001\u0000\u0000"
                    + "S\u000b\u0001\u0000\u0000\u0000TU\u0003\u000e\u0007\u0000UV\u0003\u0012"
                    + "\t\u0000VW\u0005\u0001\u0000\u0000W\r\u0001\u0000\u0000\u0000X_\u0003"
                    + "\u0010\b\u0000YZ\u0003\u0010\b\u0000Z[\u0005\b\u0000\u0000[\\\u0003\u001c"
                    + "\u000e\u0000\\]\u0005\t\u0000\u0000]_\u0001\u0000\u0000\u0000^X\u0001"
                    + "\u0000\u0000\u0000^Y\u0001\u0000\u0000\u0000_\u000f\u0001\u0000\u0000"
                    + "\u0000`a\u0007\u0000\u0000\u0000a\u0011\u0001\u0000\u0000\u0000bg\u0003"
                    + "\u0014\n\u0000cd\u0005\f\u0000\u0000df\u0003\u0014\n\u0000ec\u0001\u0000"
                    + "\u0000\u0000fi\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001"
                    + "\u0000\u0000\u0000h\u0013\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000"
                    + "\u0000jk\u0005\u0014\u0000\u0000kl\u0005\u0004\u0000\u0000lm\u0005\u0015"
                    + "\u0000\u0000mn\u0005\u0005\u0000\u0000n\u0015\u0001\u0000\u0000\u0000"
                    + "op\u0005\r\u0000\u0000pq\u0003\u0014\n\u0000qr\u0005\u000e\u0000\u0000"
                    + "rs\u0003\u0018\f\u0000st\u0005\u0001\u0000\u0000t{\u0001\u0000\u0000\u0000"
                    + "uv\u0005\r\u0000\u0000vw\u0005\u0014\u0000\u0000wx\u0005\u000e\u0000\u0000"
                    + "xy\u0005\u0014\u0000\u0000y{\u0005\u0001\u0000\u0000zo\u0001\u0000\u0000"
                    + "\u0000zu\u0001\u0000\u0000\u0000{\u0017\u0001\u0000\u0000\u0000|}\u0005"
                    + "\u0014\u0000\u0000}~\u0005\u0004\u0000\u0000~\u007f\u0005\u0015\u0000"
                    + "\u0000\u007f\u0080\u0005\u0005\u0000\u0000\u0080\u0019\u0001\u0000\u0000"
                    + "\u0000\u0081\u0083\u0005\u000f\u0000\u0000\u0082\u0084\u0003\u0012\t\u0000"
                    + "\u0083\u0082\u0001\u0000\u0000\u0000\u0083\u0084\u0001\u0000\u0000\u0000"
                    + "\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0086\u0005\u0001\u0000\u0000"
                    + "\u0086\u001b\u0001\u0000\u0000\u0000\u0087\u0088\u0006\u000e\uffff\uffff"
                    + "\u0000\u0088\u0090\u0005\u0016\u0000\u0000\u0089\u0090\u0005\u0015\u0000"
                    + "\u0000\u008a\u0090\u0005\u0017\u0000\u0000\u008b\u008c\u0005\b\u0000\u0000"
                    + "\u008c\u008d\u0003\u001c\u000e\u0000\u008d\u008e\u0005\t\u0000\u0000\u008e"
                    + "\u0090\u0001\u0000\u0000\u0000\u008f\u0087\u0001\u0000\u0000\u0000\u008f"
                    + "\u0089\u0001\u0000\u0000\u0000\u008f\u008a\u0001\u0000\u0000\u0000\u008f"
                    + "\u008b\u0001\u0000\u0000\u0000\u0090\u0099\u0001\u0000\u0000\u0000\u0091"
                    + "\u0092\n\u0003\u0000\u0000\u0092\u0093\u0007\u0001\u0000\u0000\u0093\u0098"
                    + "\u0003\u001c\u000e\u0004\u0094\u0095\n\u0002\u0000\u0000\u0095\u0096\u0007"
                    + "\u0002\u0000\u0000\u0096\u0098\u0003\u001c\u000e\u0003\u0097\u0091\u0001"
                    + "\u0000\u0000\u0000\u0097\u0094\u0001\u0000\u0000\u0000\u0098\u009b\u0001"
                    + "\u0000\u0000\u0000\u0099\u0097\u0001\u0000\u0000\u0000\u0099\u009a\u0001"
                    + "\u0000\u0000\u0000\u009a\u001d\u0001\u0000\u0000\u0000\u009b\u0099\u0001"
                    + "\u0000\u0000\u0000\r\u001f$.2=K^gz\u0083\u008f\u0097\u0099";
    public static final ATN _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());

    static {
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
        }
    }
}
