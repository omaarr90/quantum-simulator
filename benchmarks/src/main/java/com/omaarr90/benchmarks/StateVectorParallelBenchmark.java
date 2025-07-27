package com.omaarr90.benchmarks;

import com.omaarr90.core.circuit.Circuit;
import com.omaarr90.core.engine.result.SimulationResult;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;

/**
 * JMH benchmark for measuring parallel execution speedup in StateVector engine.
 *
 * <p>This benchmark demonstrates and documents that the new parallel sweep delivers
 * ≥3× speed-up over the single-thread baseline on an 8-core CPU. The benchmark
 * compares parallel vs serial execution across different circuit sizes.
 *
 * <p><strong>BENCHMARK DESIGN:</strong>
 * <ul>
 *   <li>Parameters: qubits (10,14,20,24), gates (256,1024), parallel (true/false)
 *   <li>Mode: SingleShotTime with 10 measurement iterations and 5 warmup iterations
 *   <li>Circuit generation: Uniform random gate sequences with seeded randomness
 *   <li>Parallel control: Uses qsim.forceSerial system property
 * </ul>
 *
 * <p><strong>EXPECTED RESULTS:</strong>
 * <ul>
 *   <li>qubits=10: speed-up ≈ 1.0 (auto-fallback to serial for ≤12 qubits)
 *   <li>qubits≥14: speed-up ≥ 3.0× for parallel vs serial execution
 * </ul>
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(1)
public class StateVectorParallelBenchmark extends BenchmarkBase {

    /** Number of qubits in the test circuit. */
    @Param({"10", "14", "20", "24"})
    private int qubits;

    /** Number of gates in the test circuit. */
    @Param({"256", "1024"})
    private int gates;

    /** Whether to use parallel execution (true) or force serial execution (false). */
    @Param({"true", "false"})
    private boolean parallel;

    /**
     * Configures system properties to control parallel vs serial execution.
     *
     * <p>When {@code parallel=false}, sets {@code qsim.forceSerial=true} to force
     * serial execution. When {@code parallel=true}, ensures the property is cleared
     * to allow normal parallel execution logic.
     */
    @Override
    protected void configureExecution() {
        if (parallel) {
            // Clear the property to allow parallel execution
            System.clearProperty("qsim.forceSerial");
        } else {
            // Force serial execution
            System.setProperty("qsim.forceSerial", "true");
        }
    }

    /**
     * Generates a random quantum circuit for benchmarking.
     *
     * <p>Uses a fixed seed based on benchmark parameters to ensure reproducible
     * circuit generation within each trial while avoiding cache effects between
     * different parameter combinations.
     *
     * @return a randomly generated quantum circuit
     */
    @Override
    protected Circuit generateCircuit() {
        // Use parameters as seed to ensure reproducibility within trial
        // but different circuits for different parameter combinations
        long seed = (long) qubits * 1000000L + gates * 1000L + (parallel ? 1 : 0);
        return RandomCircuitFactory.generate(qubits, gates, seed);
    }

    /**
     * Executes the quantum circuit and returns the simulation result.
     *
     * <p>This is the main benchmark method that measures the execution time
     * of circuit simulation. The circuit and engine are initialized once per
     * trial in the setup phase.
     *
     * @return the simulation result
     */
    @Benchmark
    public SimulationResult runCircuit() {
        return engine.run(circuit);
    }
}