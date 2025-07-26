package com.omaarr90;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.parser.OpenQasmParser;
import com.omaarr90.parser.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main entry point for the quantum simulator CLI.
 * Supports running circuits from OpenQASM files or using built-in examples.
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            runDefaultDemo();
        } else if (args.length >= 2 && "run".equals(args[0]) && "-f".equals(args[1])) {
            if (args.length < 3) {
                System.err.println("Error: Missing file path after -f flag");
                printUsage();
                System.exit(1);
            }
            runQasmFile(args[2]);
        } else if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printUsage();
        } else {
            System.err.println("Error: Invalid arguments");
            printUsage();
            System.exit(1);
        }
    }
    
    private static void runDefaultDemo() {
        System.out.println("Running default demo circuit...\n");
        
        var circuit = CircuitBuilder.of(3)
                                    .h(0)
                                    .cx(0, 1)
                                    .rz(1, Math.PI / 2)
                                    .barrier()          // full barrier
                                    .measureAll()
                                    .build();

        displayCircuitInfo(circuit);
    }
    
    private static void runQasmFile(String filePath) {
        try {
            Path qasmFile = Paths.get(filePath);
            System.out.println("Parsing OpenQASM file: " + qasmFile.toAbsolutePath());
            
            Circuit circuit = OpenQasmParser.parse(qasmFile);
            
            System.out.println("Successfully parsed OpenQASM circuit!\n");
            displayCircuitInfo(circuit);
            
        } catch (ParseException e) {
            System.err.println("Parse error: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static void displayCircuitInfo(Circuit circuit) {
        System.out.println("Circuit Information:");
        System.out.println("==================");
        System.out.println("Qubits: " + circuit.qubitCount());
        System.out.println("Operations: " + circuit.operationCount());
        System.out.println("Measurements: " + circuit.measurementCount());
        System.out.println("Classical bits: " + circuit.classicalBits());
        System.out.println("Created at: " + circuit.createdAt());

        // Show operations
        if (circuit.operationCount() > 0) {
            System.out.println("\nOperations:");
            for (int i = 0; i < circuit.ops().size(); i++) {
                var op = circuit.ops().get(i);
                System.out.println("  " + (i + 1) + ". " + op);
            }
        }

        // Show measurements
        if (circuit.hasMeasurements()) {
            System.out.println("\nMeasurements:");
            circuit.measureMap().forEach((qubit, cbit) ->
                System.out.println("  Qubit " + qubit + " -> Classical bit " + cbit));
        }
    }
    
    private static void printUsage() {
        System.out.println("Quantum Simulator CLI");
        System.out.println("====================");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  qsim                    Run default demo circuit");
        System.out.println("  qsim run -f <file>      Run OpenQASM file");
        System.out.println("  qsim --help, -h         Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  qsim run -f bell.qasm   Parse and display Bell state circuit");
        System.out.println("  qsim run -f ghz.qasm    Parse and display GHZ state circuit");
        System.out.println();
        System.out.println("Supported OpenQASM 3 features:");
        System.out.println("  - Qubit declarations: qubit[N] q;");
        System.out.println("  - Single-qubit gates: h, x, y, z");
        System.out.println("  - Two-qubit gates: cx, cz, swap");
        System.out.println("  - Parametrized rotations: rx(θ), ry(θ), rz(θ)");
        System.out.println("  - Measurements: measure q[i] -> c[i];");
        System.out.println("  - Barriers: barrier;");
    }
}