package com.omaarr90;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.circuit.CircuitBuilder;
import com.omaarr90.parser.OpenQasmParser;
import com.omaarr90.parser.ParseException;
import com.omaarr90.parser.qasm.OpenQasm3Lexer;
import com.omaarr90.parser.qasm.OpenQasm3Parser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main entry point for the quantum simulator CLI.
 *
 * <p>This is the root command that supports both the legacy demo functionality and the new
 * subcommand-based interface for executing quantum circuits.
 */
@Command(
        name = "qsim",
        description = "Quantum Circuit Simulator",
        mixinStandardHelpOptions = true,
        subcommands = {RunCommand.class},
        version = "qsim 1.0")
public class Main implements Callable<Integer> {

    @Option(
            names = {"--demo"},
            description = "Run the default demo circuit")
    private boolean demo;

    @Option(
            names = {"-f", "--file"},
            paramLabel = "<file.qasm>",
            description = "Parse and display OpenQASM file (legacy mode)")
    private Path legacyFile;

    @Option(
            names = {"--debug-parse"},
            description = "Show parse tree output (legacy mode)")
    private boolean debugParse;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (demo) {
            runDefaultDemo();
            return 0;
        } else if (legacyFile != null) {
            runQasmFile(legacyFile.toString(), debugParse);
            return 0;
        } else {
            // No options specified, run default demo for backward compatibility
            runDefaultDemo();
            return 0;
        }
    }

    private static void runDefaultDemo() {
        System.out.println("Running default demo circuit...\n");

        var circuit =
                CircuitBuilder.of(3)
                        .h(0)
                        .cx(0, 1)
                        .rz(1, Math.PI / 2)
                        .barrier() // full barrier
                        .measureAll()
                        .build();

        displayCircuitInfo(circuit);
    }

    private static void runQasmFile(String filePath, boolean debugParse) {
        try {
            Path qasmFile = Paths.get(filePath);
            System.out.println("Parsing OpenQASM file: " + qasmFile.toAbsolutePath());

            if (debugParse) {
                printParseTree(qasmFile);
                System.out.println();
            }

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

    private static void printParseTree(Path qasmFile) throws IOException {
        String source = Files.readString(qasmFile);

        // Create input stream from source
        CharStream input = CharStreams.fromString(source);

        // Create lexer
        OpenQasm3Lexer lexer = new OpenQasm3Lexer(input);

        // Create token stream
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Create parser
        OpenQasm3Parser parser = new OpenQasm3Parser(tokens);

        // Parse the program
        ParseTree tree = parser.program();

        // Print the parse tree
        System.out.println("Parse Tree:");
        System.out.println("===========");
        System.out.println(tree.toStringTree(parser));
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
            circuit.measureMap()
                    .forEach(
                            (qubit, cbit) ->
                                    System.out.println(
                                            "  Qubit " + qubit + " -> Classical bit " + cbit));
        }
    }
}
