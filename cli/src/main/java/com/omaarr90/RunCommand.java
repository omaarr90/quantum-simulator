package com.omaarr90;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.SimulatorEngineRegistry;
import com.omaarr90.core.engine.result.SimulationResult;
import com.omaarr90.parser.OpenQasmParser;
import com.omaarr90.parser.ParseException;
import com.omaarr90.qsim.statevector.StateVectorEngine;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;

/**
 * CLI command for executing quantum circuits from OpenQASM files.
 *
 * <p>This command parses a quantum circuit from an OpenQASM 3 file, executes it using the specified
 * simulation engine, and outputs measurement statistics in a formatted histogram.
 *
 * <p>Usage example:
 *
 * <pre>
 * qsim run --engine state --shots 1024 --in bell.qasm
 * </pre>
 */
@Command(
        name = "run",
        description = "Execute a quantum circuit from an OpenQASM file",
        mixinStandardHelpOptions = true)
public class RunCommand implements Callable<Integer> {

    @Option(
            names = {"-e", "--engine"},
            defaultValue = "statevector",
            paramLabel = "<id>",
            description = "Simulation engine to use (default: ${DEFAULT-VALUE})")
    private String engineId;

    @Option(
            names = {"-n", "--shots"},
            defaultValue = "1",
            paramLabel = "<N>",
            description = "Number of measurement shots (default: ${DEFAULT-VALUE})")
    private int shots;

    @Option(
            names = {"-i", "--in"},
            required = true,
            paramLabel = "<file.qasm>",
            description = "Path to OpenQASM 3 file (required)")
    private Path qasmFile;

    @Override
    public Integer call() {
        try {
            // Validate shots parameter
            if (shots <= 0) {
                throw new ParameterException(null, "Number of shots must be positive: " + shots);
            }

            // Validate and read QASM file
            if (!Files.exists(qasmFile)) {
                System.err.println("Error: QASM file does not exist: " + qasmFile);
                return 3;
            }

            if (!Files.isReadable(qasmFile)) {
                System.err.println("Error: QASM file is not readable: " + qasmFile);
                return 3;
            }

            // Parse the circuit
            Circuit circuit;
            try {
                circuit = OpenQasmParser.parse(qasmFile);
            } catch (ParseException e) {
                System.err.println("Error: Failed to parse QASM file: " + e.getMessage());
                return 3;
            } catch (IOException e) {
                System.err.println("Error: Failed to read QASM file: " + e.getMessage());
                return 3;
            }

            // Get the simulation engine
            SimulatorEngine engine;
            try {
                engine = SimulatorEngineRegistry.get(engineId);
            } catch (NoSuchElementException e) {
                System.err.println(
                        "Error: Unknown engine id '" + engineId + "'. " + e.getMessage());
                return 2;
            }

            // Execute the circuit
            SimulationResult result;
            if (engine instanceof StateVectorEngine stateEngine && shots > 1) {
                // Use shots-aware method for StateVectorEngine
                result = stateEngine.run(circuit, shots);
            } else {
                // For other engines or single shot, use standard method
                result = engine.run(circuit);
            }

            // Format and print histogram
            printHistogram(result);

            return 0;

        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Prints a formatted histogram of measurement results.
     *
     * @param result the simulation result containing measurement counts
     */
    private void printHistogram(SimulationResult result) {
        Map<String, Long> counts = result.counts();
        int totalShots = result.totalShots();

        if (counts.isEmpty()) {
            System.out.println("No measurements performed.");
            return;
        }

        // Calculate column widths
        int stateWidth =
                Math.max(5, counts.keySet().stream().mapToInt(String::length).max().orElse(5));
        int countWidth = Math.max(5, String.valueOf(totalShots).length());
        int probWidth = 11; // "Probability" length

        // Print header
        String headerFormat =
                "┌─"
                        + "─".repeat(stateWidth)
                        + "─┬─"
                        + "─".repeat(countWidth)
                        + "─┬─"
                        + "─".repeat(probWidth)
                        + "─┐%n";
        System.out.printf(headerFormat);

        String titleFormat =
                "│ %-" + stateWidth + "s │ %-" + countWidth + "s │ %-" + probWidth + "s │%n";
        System.out.printf(titleFormat, "State", "Count", "Probability");

        String separatorFormat =
                "├─"
                        + "─".repeat(stateWidth)
                        + "─┼─"
                        + "─".repeat(countWidth)
                        + "─┼─"
                        + "─".repeat(probWidth)
                        + "─┤%n";
        System.out.printf(separatorFormat);

        // Sort by count (descending), then by state (ascending)
        counts.entrySet().stream()
                .sorted(
                        (e1, e2) -> {
                            int countCompare = Long.compare(e2.getValue(), e1.getValue());
                            return countCompare != 0
                                    ? countCompare
                                    : e1.getKey().compareTo(e2.getKey());
                        })
                .forEach(
                        entry -> {
                            String state = entry.getKey();
                            long count = entry.getValue();
                            double probability = (double) count / totalShots;

                            String rowFormat =
                                    "│ %-"
                                            + stateWidth
                                            + "s │ %-"
                                            + countWidth
                                            + "d │ %-"
                                            + probWidth
                                            + ".3f │%n";
                            System.out.printf(rowFormat, state, count, probability);
                        });

        // Print footer
        String footerFormat =
                "└─"
                        + "─".repeat(stateWidth)
                        + "─┴─"
                        + "─".repeat(countWidth)
                        + "─┴─"
                        + "─".repeat(probWidth)
                        + "─┘%n";
        System.out.printf(footerFormat);
    }
}
