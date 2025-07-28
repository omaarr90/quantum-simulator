package com.omaarr90;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Integration tests for RunCommand histogram formatting and execution flow. */
class RunCommandHistogramTest {

    @TempDir Path tempDir;

    @Test
    void testHistogramFormatting() throws Exception {
        // Create a temporary QASM file
        Path qasmFile = tempDir.resolve("test.qasm");
        String qasmContent =
                """
                OPENQASM 3.0;
                qreg q[2];
                creg c[2];
                h q[0];
                cx q[0], q[1];
                measure q[0] -> c[0];
                measure q[1] -> c[1];
                """;
        Files.writeString(qasmFile, qasmContent);

        // Capture stdout
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Create and execute RunCommand
            RunCommand command = new RunCommand();

            // Use reflection to set private fields for testing
            java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
            java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
            java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

            engineField.setAccessible(true);
            shotsField.setAccessible(true);
            fileField.setAccessible(true);

            engineField.set(command, "statevector");
            shotsField.set(command, 100);
            fileField.set(command, qasmFile);

            // Execute the command
            Integer exitCode = command.call();
            assertEquals(0, exitCode, "Command should execute successfully");

            // Verify output format
            String output = outputStream.toString();

            // Check for table structure
            assertTrue(output.contains("┌"), "Output should contain table top border");
            assertTrue(output.contains("└"), "Output should contain table bottom border");
            assertTrue(
                    output.contains("│ State │ Count │ Probability │"),
                    "Output should contain table header");
            assertTrue(output.contains("├"), "Output should contain table separator");

            // Check for expected Bell state outcomes
            assertTrue(
                    output.contains("00") || output.contains("11"),
                    "Output should contain Bell state outcomes");

            // Check probability format (3 decimal places)
            assertTrue(
                    output.matches("(?s).*│\\s+\\d+\\s+│\\s+\\d+\\.\\d{3}\\s+│.*"),
                    "Output should contain properly formatted probabilities");

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testSingleShotHistogram() throws Exception {
        // Create a temporary QASM file
        Path qasmFile = tempDir.resolve("single.qasm");
        String qasmContent =
                """
                OPENQASM 3.0;
                qreg q[1];
                creg c[1];
                measure q[0] -> c[0];
                """;
        Files.writeString(qasmFile, qasmContent);

        // Capture stdout
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Create and execute RunCommand with 1 shot
            RunCommand command = new RunCommand();

            // Use reflection to set private fields
            java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
            java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
            java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

            engineField.setAccessible(true);
            shotsField.setAccessible(true);
            fileField.setAccessible(true);

            engineField.set(command, "statevector");
            shotsField.set(command, 1);
            fileField.set(command, qasmFile);

            // Execute the command
            Integer exitCode = command.call();
            assertEquals(0, exitCode, "Command should execute successfully");

            // Verify output format for single shot
            String output = outputStream.toString();

            // Should contain probability 1.000 for single measurement
            assertTrue(output.contains("1.000"), "Single shot should show probability 1.000");

            // Should contain count of 1
            assertTrue(output.contains("│ 1     │"), "Single shot should show count of 1");

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testNoMeasurementsCircuit() throws Exception {
        // Create a temporary QASM file without measurements
        Path qasmFile = tempDir.resolve("nomeasure.qasm");
        String qasmContent =
                """
                OPENQASM 3.0;
                qreg q[1];
                h q[0];
                """;
        Files.writeString(qasmFile, qasmContent);

        // Capture stdout
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputStream));

        try {
            // Create and execute RunCommand
            RunCommand command = new RunCommand();

            // Use reflection to set private fields
            java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
            java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
            java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

            engineField.setAccessible(true);
            shotsField.setAccessible(true);
            fileField.setAccessible(true);

            engineField.set(command, "statevector");
            shotsField.set(command, 10);
            fileField.set(command, qasmFile);

            // Execute the command
            Integer exitCode = command.call();
            assertEquals(0, exitCode, "Command should execute successfully");

            // Verify output for no measurements
            String output = outputStream.toString();
            assertTrue(
                    output.contains("No measurements performed."),
                    "Should indicate no measurements were performed");

        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testInvalidShotsValidation() throws Exception {
        // Create a temporary QASM file
        Path qasmFile = tempDir.resolve("test.qasm");
        String qasmContent =
                """
                OPENQASM 3.0;
                qreg q[1];
                creg c[1];
                measure q[0] -> c[0];
                """;
        Files.writeString(qasmFile, qasmContent);

        // Test negative shots
        RunCommand command = new RunCommand();

        // Use reflection to set private fields
        java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
        java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
        java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

        engineField.setAccessible(true);
        shotsField.setAccessible(true);
        fileField.setAccessible(true);

        engineField.set(command, "statevector");
        shotsField.set(command, -5);
        fileField.set(command, qasmFile);

        // Execute should return error code 1
        Integer exitCode = command.call();
        assertEquals(1, exitCode, "Negative shots should return exit code 1");
    }

    @Test
    void testUnknownEngineError() throws Exception {
        // Create a temporary QASM file
        Path qasmFile = tempDir.resolve("test.qasm");
        String qasmContent =
                """
                OPENQASM 3.0;
                qreg q[1];
                creg c[1];
                measure q[0] -> c[0];
                """;
        Files.writeString(qasmFile, qasmContent);

        // Capture stderr
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStream));

        try {
            // Test unknown engine
            RunCommand command = new RunCommand();

            // Use reflection to set private fields
            java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
            java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
            java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

            engineField.setAccessible(true);
            shotsField.setAccessible(true);
            fileField.setAccessible(true);

            engineField.set(command, "unknown");
            shotsField.set(command, 10);
            fileField.set(command, qasmFile);

            // Execute should return error code 2
            Integer exitCode = command.call();
            assertEquals(2, exitCode, "Unknown engine should return exit code 2");

            // Verify error message
            String errorOutput = errorStream.toString();
            assertTrue(
                    errorOutput.contains("Unknown engine id 'unknown'"),
                    "Should contain unknown engine error message");
            assertTrue(errorOutput.contains("Available engines:"), "Should list available engines");

        } finally {
            System.setErr(originalErr);
        }
    }

    @Test
    void testFileNotFoundError() throws Exception {
        // Test with non-existent file
        Path nonExistentFile = tempDir.resolve("nonexistent.qasm");

        // Capture stderr
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        PrintStream originalErr = System.err;
        System.setErr(new PrintStream(errorStream));

        try {
            RunCommand command = new RunCommand();

            // Use reflection to set private fields
            java.lang.reflect.Field engineField = RunCommand.class.getDeclaredField("engineId");
            java.lang.reflect.Field shotsField = RunCommand.class.getDeclaredField("shots");
            java.lang.reflect.Field fileField = RunCommand.class.getDeclaredField("qasmFile");

            engineField.setAccessible(true);
            shotsField.setAccessible(true);
            fileField.setAccessible(true);

            engineField.set(command, "statevector");
            shotsField.set(command, 10);
            fileField.set(command, nonExistentFile);

            // Execute should return error code 3
            Integer exitCode = command.call();
            assertEquals(3, exitCode, "Non-existent file should return exit code 3");

            // Verify error message
            String errorOutput = errorStream.toString();
            assertTrue(
                    errorOutput.contains("QASM file does not exist"),
                    "Should contain file not found error message");

        } finally {
            System.setErr(originalErr);
        }
    }
}
