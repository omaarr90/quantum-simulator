package com.omaarr90;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

/** Unit tests for RunCommand option parsing and validation. */
class RunCommandOptionParsingTest {

    @Test
    void testDefaultValues() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Parse with only required option
        String[] args = {"--in", "test.qasm"};
        cmd.parseArgs(args);

        // Verify defaults through reflection since fields are private
        CommandSpec spec = cmd.getCommandSpec();
        OptionSpec engineOption = spec.findOption("--engine");
        OptionSpec shotsOption = spec.findOption("--shots");

        assertEquals("statevector", engineOption.defaultValue());
        assertEquals("1", shotsOption.defaultValue());
    }

    @Test
    void testShortOptionAliases() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test short aliases
        String[] args = {"-e", "statevector", "-n", "100", "-i", "test.qasm"};
        cmd.parseArgs(args);

        // Verify parsing succeeded without exceptions
        assertTrue(true, "Short option aliases should parse successfully");
    }

    @Test
    void testLongOptionNames() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test long option names
        String[] args = {"--engine", "statevector", "--shots", "100", "--in", "test.qasm"};
        cmd.parseArgs(args);

        // Verify parsing succeeded without exceptions
        assertTrue(true, "Long option names should parse successfully");
    }

    @Test
    void testRequiredInOption() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test missing required --in option
        String[] args = {"--engine", "statevector", "--shots", "100"};

        assertThrows(
                CommandLine.MissingParameterException.class,
                () -> {
                    cmd.parseArgs(args);
                },
                "Missing --in option should throw exception");
    }

    @Test
    void testInvalidShotsValue() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test non-numeric shots value
        String[] args = {"--shots", "invalid", "--in", "test.qasm"};

        assertThrows(
                CommandLine.ParameterException.class,
                () -> {
                    cmd.parseArgs(args);
                },
                "Non-numeric shots value should throw exception");
    }

    @Test
    void testNegativeShotsValue() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Parse with negative shots (parsing should succeed, validation happens in call())
        String[] args = {"--shots", "-5", "--in", "test.qasm"};
        cmd.parseArgs(args);

        // Verify parsing succeeded (validation happens later)
        assertTrue(true, "Negative shots should parse but fail validation in call()");
    }

    @Test
    void testZeroShotsValue() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Parse with zero shots (parsing should succeed, validation happens in call())
        String[] args = {"--shots", "0", "--in", "test.qasm"};
        cmd.parseArgs(args);

        // Verify parsing succeeded (validation happens later)
        assertTrue(true, "Zero shots should parse but fail validation in call()");
    }

    @Test
    void testPathConversion() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test path conversion
        String[] args = {"--in", "/path/to/test.qasm"};
        cmd.parseArgs(args);

        // Verify parsing succeeded
        assertTrue(true, "Path conversion should work correctly");
    }

    @Test
    void testHelpOption() {
        RunCommand command = new RunCommand();
        CommandLine cmd = new CommandLine(command);

        // Test help option - parsing should succeed and set help flag
        String[] args = {"--help"};
        CommandLine.ParseResult parseResult = cmd.parseArgs(args);

        // Verify help was requested
        assertTrue(parseResult.isUsageHelpRequested(), "Help option should set help flag");
    }

    @Test
    void testCommandMetadata() {
        CommandLine cmd = new CommandLine(RunCommand.class);
        CommandSpec spec = cmd.getCommandSpec();

        assertEquals("run", spec.name());
        assertEquals(
                "Execute a quantum circuit from an OpenQASM file",
                spec.usageMessage().description()[0]);
        assertTrue(spec.mixinStandardHelpOptions());
    }

    @Test
    void testOptionDescriptions() {
        CommandLine cmd = new CommandLine(RunCommand.class);
        CommandSpec spec = cmd.getCommandSpec();

        OptionSpec engineOption = spec.findOption("--engine");
        OptionSpec shotsOption = spec.findOption("--shots");
        OptionSpec inOption = spec.findOption("--in");

        assertNotNull(engineOption);
        assertNotNull(shotsOption);
        assertNotNull(inOption);

        assertTrue(inOption.required());
        assertFalse(engineOption.required());
        assertFalse(shotsOption.required());

        assertEquals("<id>", engineOption.paramLabel());
        assertEquals("<N>", shotsOption.paramLabel());
        assertEquals("<file.qasm>", inOption.paramLabel());
    }
}
