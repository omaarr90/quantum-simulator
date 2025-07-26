package com.omaarr90.parser;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.core.gate.GateType;
import com.omaarr90.parser.qasm.OpenQasm3BaseVisitor;
import com.omaarr90.parser.qasm.OpenQasm3Parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Visitor that converts OpenQASM 3 parse tree into Circuit objects.
 * Maintains state for qubit and classical bit mappings during traversal.
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
        
        // Process all statements
        for (OpenQasm3Parser.StatementContext stmt : ctx.statement()) {
            visit(stmt);
        }
        
        return builder.build();
    }
    
    @Override
    public Circuit visitQubitDeclaration(OpenQasm3Parser.QubitDeclarationContext ctx) {
        String qubitName = ctx.IDENTIFIER().getText();
        int size = Integer.parseInt(ctx.INT().getText());
        
        // Update qubit count and create new builder if needed
        if (qubitCount == 0) {
            qubitCount = size;
            builder = CircuitBuilder.of(size);
        } else {
            // For now, we only support one qubit declaration
            throw new RuntimeException("Multiple qubit declarations not supported yet");
        }
        
        // Map qubit name to indices
        qubitMap.put(qubitName, 0); // Base index for the qubit array
        
        return null;
    }
    
    @Override
    public Circuit visitGateApplication(OpenQasm3Parser.GateApplicationContext ctx) {
        String gateName = ctx.gateCall().IDENTIFIER().getText();
        
        // Get qubit arguments
        int[] qubits = getQubitIndices(ctx.qubitArguments());
        
        // Handle different gate types
        switch (gateName.toLowerCase()) {
            case "h":
                if (qubits.length != 1) throw new RuntimeException("H gate requires exactly 1 qubit");
                builder.h(qubits[0]);
                break;
            case "x":
                if (qubits.length != 1) throw new RuntimeException("X gate requires exactly 1 qubit");
                builder.x(qubits[0]);
                break;
            case "y":
                if (qubits.length != 1) throw new RuntimeException("Y gate requires exactly 1 qubit");
                builder.y(qubits[0]);
                break;
            case "z":
                if (qubits.length != 1) throw new RuntimeException("Z gate requires exactly 1 qubit");
                builder.z(qubits[0]);
                break;
            case "cx":
                if (qubits.length != 2) throw new RuntimeException("CX gate requires exactly 2 qubits");
                builder.cx(qubits[0], qubits[1]);
                break;
            case "cz":
                if (qubits.length != 2) throw new RuntimeException("CZ gate requires exactly 2 qubits");
                builder.cz(qubits[0], qubits[1]);
                break;
            case "swap":
                if (qubits.length != 2) throw new RuntimeException("SWAP gate requires exactly 2 qubits");
                builder.swap(qubits[0], qubits[1]);
                break;
            case "rx":
                if (qubits.length != 1) throw new RuntimeException("RX gate requires exactly 1 qubit");
                double rxAngle = evaluateExpression(ctx.gateCall().expression());
                builder.rx(qubits[0], rxAngle);
                break;
            case "ry":
                if (qubits.length != 1) throw new RuntimeException("RY gate requires exactly 1 qubit");
                double ryAngle = evaluateExpression(ctx.gateCall().expression());
                builder.ry(qubits[0], ryAngle);
                break;
            case "rz":
                if (qubits.length != 1) throw new RuntimeException("RZ gate requires exactly 1 qubit");
                double rzAngle = evaluateExpression(ctx.gateCall().expression());
                builder.rz(qubits[0], rzAngle);
                break;
            default:
                throw new RuntimeException("Unsupported gate: " + gateName);
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
        return ctx.qubitReference().stream()
                .mapToInt(this::getQubitIndex)
                .toArray();
    }
    
    private int getQubitIndex(OpenQasm3Parser.QubitReferenceContext ctx) {
        String qubitName = ctx.IDENTIFIER().getText();
        int index = Integer.parseInt(ctx.INT().getText());
        
        if (!qubitMap.containsKey(qubitName)) {
            throw new RuntimeException("Undefined qubit: " + qubitName);
        }
        
        return qubitMap.get(qubitName) + index;
    }
    
    private int getClassicalIndex(OpenQasm3Parser.ClassicalReferenceContext ctx) {
        String cbitName = ctx.IDENTIFIER().getText();
        int index = Integer.parseInt(ctx.INT().getText());
        
        // For now, assume classical bits are indexed directly
        return index;
    }
    
    private double evaluateExpression(OpenQasm3Parser.ExpressionContext ctx) {
        if (ctx == null) {
            throw new RuntimeException("Missing expression for parameterized gate");
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
                case "+": return left + right;
                case "-": return left - right;
                case "*": return left * right;
                case "/": return left / right;
                default: throw new RuntimeException("Unsupported operator: " + op);
            }
        } else if (ctx.expression().size() == 1) {
            // Parenthesized expression
            return evaluateExpression(ctx.expression(0));
        }
        
        throw new RuntimeException("Unable to evaluate expression: " + ctx.getText());
    }
}