package com.omaarr90.parser;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.parser.qasm.OpenQasm3BaseVisitor;
import com.omaarr90.parser.qasm.OpenQasm3Parser;
import java.util.HashMap;
import java.util.Map;

/**
 * Visitor that converts OpenQASM 3 parse tree into Circuit objects. Maintains state for qubit and
 * classical bit mappings during traversal.
 */
public class AstToCircuitVisitor extends OpenQasm3BaseVisitor<Circuit> {

    private CircuitBuilder builder;
    private Map<String, Integer> qubitMap = new HashMap<>();
    private Map<String, Integer> classicalMap = new HashMap<>();
    private int qubitCount = 0;
    private int classicalCount = 0;

    @Override
    public Circuit visitProgram(OpenQasm3Parser.ProgramContext ctx) {
        // Initialize with default qubit count, will be updated when we see qubit declarations
        builder = CircuitBuilder.of(1);

        // Process all statement lines
        for (OpenQasm3Parser.StatementLineContext stmtLine : ctx.statementLine()) {
            visit(stmtLine);
        }

        return builder.build();
    }

    @Override
    public Circuit visitStatementLine(OpenQasm3Parser.StatementLineContext ctx) {
        // Process all statements in this line
        for (OpenQasm3Parser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        return null;
    }

    @Override
    public Circuit visitQubitDeclaration(OpenQasm3Parser.QubitDeclarationContext ctx) {
        String qubitName;
        int size;

        // Handle both qubit[n] name and qreg name[n] syntax
        if (ctx.getText().startsWith("qubit")) {
            qubitName = ctx.IDENTIFIER().getText();
            size = Integer.parseInt(ctx.INT().getText());
        } else {
            // qreg syntax
            qubitName = ctx.IDENTIFIER().getText();
            size = Integer.parseInt(ctx.INT().getText());
        }

        // Check if this qubit register already exists
        if (qubitMap.containsKey(qubitName)) {
            throw createParseException("Qubit register '" + qubitName + "' already declared", ctx);
        }

        // Update total qubit count and create new builder if needed
        int newQubitCount = qubitCount + size;
        if (qubitCount == 0) {
            builder = CircuitBuilder.of(size);
        } else {
            // Create new builder with expanded qubit count
            builder = CircuitBuilder.of(newQubitCount);
        }

        // Map qubit name to starting index
        qubitMap.put(qubitName, qubitCount);
        qubitCount = newQubitCount;

        return null;
    }

    @Override
    public Circuit visitClassicalDeclaration(OpenQasm3Parser.ClassicalDeclarationContext ctx) {
        String cregName = ctx.IDENTIFIER().getText();
        int size = Integer.parseInt(ctx.INT().getText());

        // Check if this classical register already exists
        if (classicalMap.containsKey(cregName)) {
            throw createParseException(
                    "Classical register '" + cregName + "' already declared", ctx);
        }

        // Map classical register name to starting index
        classicalMap.put(cregName, classicalCount);
        classicalCount += size;

        return null;
    }

    @Override
    public Circuit visitGateApplication(OpenQasm3Parser.GateApplicationContext ctx) {
        String gateName = getGateName(ctx.gateCall());

        // Get qubit arguments
        int[] qubits = getQubitIndices(ctx.qubitArguments());

        // Handle different gate types (including aliases)
        switch (gateName.toLowerCase()) {
            case "h":
                if (qubits.length != 1)
                    throw createParseException("H gate requires exactly 1 qubit", ctx);
                builder.h(qubits[0]);
                break;
            case "x":
                if (qubits.length != 1)
                    throw createParseException("X gate requires exactly 1 qubit", ctx);
                builder.x(qubits[0]);
                break;
            case "y":
                if (qubits.length != 1)
                    throw createParseException("Y gate requires exactly 1 qubit", ctx);
                builder.y(qubits[0]);
                break;
            case "z":
                if (qubits.length != 1)
                    throw createParseException("Z gate requires exactly 1 qubit", ctx);
                builder.z(qubits[0]);
                break;
            case "cx":
            case "cnot": // Alias for cx
                if (qubits.length != 2)
                    throw createParseException("CX/CNOT gate requires exactly 2 qubits", ctx);
                builder.cx(qubits[0], qubits[1]);
                break;
            case "cz":
                if (qubits.length != 2)
                    throw createParseException("CZ gate requires exactly 2 qubits", ctx);
                builder.cz(qubits[0], qubits[1]);
                break;
            case "swap":
                if (qubits.length != 2)
                    throw createParseException("SWAP gate requires exactly 2 qubits", ctx);
                builder.swap(qubits[0], qubits[1]);
                break;
            case "rx":
                if (qubits.length != 1)
                    throw createParseException("RX gate requires exactly 1 qubit", ctx);
                double rxAngle = evaluateExpression(ctx.gateCall().expression());
                builder.rx(qubits[0], rxAngle);
                break;
            case "ry":
                if (qubits.length != 1)
                    throw createParseException("RY gate requires exactly 1 qubit", ctx);
                double ryAngle = evaluateExpression(ctx.gateCall().expression());
                builder.ry(qubits[0], ryAngle);
                break;
            case "rz":
                if (qubits.length != 1)
                    throw createParseException("RZ gate requires exactly 1 qubit", ctx);
                double rzAngle = evaluateExpression(ctx.gateCall().expression());
                builder.rz(qubits[0], rzAngle);
                break;
            default:
                throw createParseException("Unsupported gate: " + gateName, ctx);
        }

        return null;
    }

    @Override
    public Circuit visitMeasureStatement(OpenQasm3Parser.MeasureStatementContext ctx) {
        if (ctx.qubitReference() != null && ctx.classicalReference() != null) {
            // Single qubit measurement
            int qubit = getQubitIndex(ctx.qubitReference());
            int cbit = getClassicalIndex(ctx.classicalReference());
            builder.measure(qubit, cbit);
        } else {
            // Measure all qubits (simplified implementation)
            builder.measureAll();
        }
        return null;
    }

    @Override
    public Circuit visitBarrier(OpenQasm3Parser.BarrierContext ctx) {
        if (ctx.qubitArguments() != null) {
            int[] qubits = getQubitIndices(ctx.qubitArguments());
            builder.barrier(qubits);
        } else {
            builder.barrier();
        }
        return null;
    }

    private int[] getQubitIndices(OpenQasm3Parser.QubitArgumentsContext ctx) {
        return ctx.qubitReference().stream().mapToInt(this::getQubitIndex).toArray();
    }

    private int getQubitIndex(OpenQasm3Parser.QubitReferenceContext ctx) {
        String qubitName = ctx.IDENTIFIER().getText();
        int index = Integer.parseInt(ctx.INT().getText());

        if (!qubitMap.containsKey(qubitName)) {
            throw createParseException("Undefined qubit register: " + qubitName, ctx);
        }

        return qubitMap.get(qubitName) + index;
    }

    private int getClassicalIndex(OpenQasm3Parser.ClassicalReferenceContext ctx) {
        String cbitName = ctx.IDENTIFIER().getText();
        int index = Integer.parseInt(ctx.INT().getText());

        // Check if classical register is declared
        if (!classicalMap.containsKey(cbitName)) {
            // If not declared, assume it's an implicit classical register (for backward
            // compatibility)
            classicalMap.put(cbitName, classicalCount);
            classicalCount += index + 1; // Ensure we have enough classical bits
        }

        return classicalMap.get(cbitName) + index;
    }

    private double evaluateExpression(OpenQasm3Parser.ExpressionContext ctx) {
        if (ctx == null) {
            throw new RuntimeException(
                    new ParseException("Missing expression for parameterized gate"));
        }

        if (ctx.REAL() != null) {
            return Double.parseDouble(ctx.REAL().getText());
        } else if (ctx.INT() != null) {
            return Double.parseDouble(ctx.INT().getText());
        } else if (ctx.PI() != null) {
            return Math.PI;
        } else if (ctx.expression().size() == 2) {
            // Binary operation
            double left = evaluateExpression(ctx.expression(0));
            double right = evaluateExpression(ctx.expression(1));
            String op = ctx.getChild(1).getText();

            switch (op) {
                case "+":
                    return left + right;
                case "-":
                    return left - right;
                case "*":
                    return left * right;
                case "/":
                    return left / right;
                default:
                    throw createParseException("Unsupported operator: " + op, ctx);
            }
        } else if (ctx.expression().size() == 1) {
            // Parenthesized expression
            return evaluateExpression(ctx.expression(0));
        }

        throw createParseException("Unable to evaluate expression", ctx);
    }

    private String getGateName(OpenQasm3Parser.GateCallContext ctx) {
        if (ctx.gateName().IDENTIFIER() != null) {
            return ctx.gateName().IDENTIFIER().getText();
        } else {
            // Handle specific gate keywords (cx, cnot, ccx, toffoli)
            return ctx.gateName().getText();
        }
    }

    private RuntimeException createParseException(
            String message, org.antlr.v4.runtime.ParserRuleContext ctx) {
        int line = ctx.getStart().getLine();
        int column = ctx.getStart().getCharPositionInLine();
        String offendingSymbol = ctx.getStart().getText();

        String suggestion = getSuggestion(message, ctx);

        return new RuntimeException(
                new ParseException(message, line, column, offendingSymbol, suggestion));
    }

    private String getSuggestion(String message, org.antlr.v4.runtime.ParserRuleContext ctx) {
        if (message.contains("already declared")) {
            return "Use a different register name or remove the duplicate declaration";
        } else if (message.contains("Undefined qubit")) {
            return "Make sure the qubit register is declared before use";
        } else if (message.contains("requires exactly")) {
            return "Check the gate documentation for the correct number of qubits";
        } else if (message.contains("Unsupported gate")) {
            return "Check if the gate name is spelled correctly or if it's supported";
        } else if (message.contains("Missing expression")) {
            return "Add a parameter expression in parentheses, e.g., rz(π/2)";
        } else if (message.contains("Unable to evaluate expression")) {
            return "Check the arithmetic expression syntax";
        }
        return null;
    }
}
