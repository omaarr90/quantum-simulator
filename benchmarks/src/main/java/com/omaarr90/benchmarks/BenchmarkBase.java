package com.omaarr90.benchmarks;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.engine.SimulatorEngine;
import com.omaarr90.core.engine.SimulatorEngineRegistry;
import org.openjdk.jmh.annotations.*;

/**
 * Base class for quantum circuit benchmarks.
 *
 * <p>This abstract class provides common benchmark infrastructure including:
 *
 * <ul>
 *   <li>Engine initialization and configuration per trial
 *   <li>Circuit generation with seeded randomness
 *   <li>Proper JMH state management
 *   <li>System property configuration for parallel vs serial execution
 * </ul>
 *
 * <p><strong>BENCHMARK STATE:</strong> This class uses {@code @State(Scope.Benchmark)} to ensure
 * engine and circuit are initialized once per benchmark trial, not per iteration.
 */
@State(Scope.Benchmark)
public abstract class BenchmarkBase {

    /** The quantum simulator engine used for circuit execution. */
    protected SimulatorEngine engine;

    /** The quantum circuit to be executed in the benchmark. */
    protected Circuit circuit;

    /**
     * Sets up the benchmark state before each trial.
     *
     * <p>This method is called once per benchmark trial (not per iteration) and:
     *
     * <ul>
     *   <li>Configures system properties for parallel vs serial execution
     *   <li>Initializes the appropriate simulator engine
     *   <li>Generates the test circuit with the specified parameters
     * </ul>
     *
     * <p><strong>PARALLEL CONTROL:</strong> Uses the {@code qsim.forceSerial} system property to
     * control execution mode. When {@code parallel=false}, this property is set to {@code true} to
     * force serial execution.
     */
    @Setup(Level.Trial)
    public void setup() {
        // Configure parallel vs serial execution
        configureExecution();

        // Initialize the state vector engine
        engine = SimulatorEngineRegistry.get("statevector");

        // Generate the test circuit
        circuit = generateCircuit();
    }

    /**
     * Configures system properties to control parallel vs serial execution.
     *
     * <p>This method sets the {@code qsim.forceSerial} system property based on the benchmark
     * parameters. Subclasses should override this method to provide the appropriate configuration
     * logic.
     */
    protected abstract void configureExecution();

    /**
     * Generates the quantum circuit for benchmarking.
     *
     * <p>Subclasses should implement this method to generate circuits with the appropriate
     * parameters (number of qubits, gates, etc.) using a consistent seed for reproducibility within
     * each trial.
     *
     * @return the quantum circuit to benchmark
     */
    protected abstract Circuit generateCircuit();

    /**
     * Cleans up benchmark state after each trial.
     *
     * <p>This method clears system properties and releases resources to ensure clean state between
     * benchmark trials.
     */
    @TearDown(Level.Trial)
    public void tearDown() {
        // Clear system properties to avoid interference between trials
        System.clearProperty("qsim.forceSerial");

        // Clear references
        engine = null;
        circuit = null;
    }
}
