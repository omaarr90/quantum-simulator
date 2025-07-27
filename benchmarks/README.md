# Quantum Simulator Benchmarks

This module contains JMH (Java Microbenchmark Harness) benchmarks for measuring and validating the performance characteristics of the quantum simulator, with a focus on parallel execution speedup in the StateVector engine.

## Overview

The benchmarks are designed to:
- Demonstrate ≥3× speedup from parallel execution on 8-core CPUs
- Validate performance across different circuit sizes
- Provide CI/CD integration for performance regression detection
- Generate detailed reports for performance analysis

## Benchmark Structure

### StateVectorParallelBenchmark

The main benchmark class that measures parallel vs serial execution performance:

**Parameters:**
- `qubits`: Number of qubits (10, 14, 20, 24)
- `gates`: Number of gates (256, 1024)  
- `parallel`: Execution mode (true/false)

**Configuration:**
- **Mode**: SingleShotTime (measures single execution time)
- **Warmup**: 5 iterations
- **Measurement**: 10 iterations
- **JVM Args**: `--enable-preview -Djdk.incubator.concurrent.enablePreview -Xms4g -Xmx4g`

### Expected Results

| Circuit Size | Expected Behavior |
|--------------|-------------------|
| ≤12 qubits   | Auto-fallback to serial (speedup ≈ 1.0×) |
| ≥14 qubits   | Parallel execution (speedup ≥ 3.0×) |

## Running Benchmarks

### Local Execution

#### Full Benchmark Suite
```bash
# Run all parameter combinations
./gradlew :benchmarks:jmhParallel

# Results saved to: build/reports/jmh/statevector-parallel.json
```

#### CI-Optimized Benchmark
```bash
# Run focused benchmark (qubits=20, gates=1024)
./gradlew :benchmarks:jmhCI
```

#### Custom Parameters
```bash
# Run with specific JMH parameters
./gradlew :benchmarks:jmh -PjmhIncludes=StateVectorParallelBenchmark \
  -PjmhParams="-p qubits=20 -p gates=1024"
```

### Result Analysis

#### Generate Speedup Report
```bash
cd benchmarks
python3 scripts/speedup.py \
  ../build/reports/jmh/statevector-parallel.json \
  --output reports/parallel-speedup.md \
  --min-speedup 2.8
```

#### Console Output Example
```
================================================================================
STATEVECTOR PARALLEL EXECUTION SPEEDUP RESULTS
================================================================================
Qubits   Gates    Serial (ms)  Parallel (ms)  Speedup   
--------------------------------------------------------------------------------
10       256      12.34        12.45          0.99      
10       1024     45.67        46.12          0.99      
14       256      89.23        28.41          3.14      
14       1024     356.78       112.34         3.18      
20       256      1234.56      387.65         3.18      
20       1024     4567.89      1432.10        3.19      
24       256      5678.90      1789.23        3.17      
24       1024     12345.67     3876.54        3.18      
--------------------------------------------------------------------------------
Geometric mean speedup (qubits ≥ 14): 3.17×
✅ PASS: Speedup target (≥3.0×) achieved!
================================================================================
```

## CI/CD Integration

### GitHub Actions Workflow

The benchmark runs automatically on:
- Push to `main`/`develop` branches
- Pull requests affecting benchmark code
- Manual workflow dispatch

**Workflow Features:**
- CPU governor optimization for stable results
- Automatic result analysis and reporting
- PR comments with benchmark results
- Artifact upload for result preservation
- CI failure on insufficient speedup (<2.8×)

### Manual Trigger
```bash
# Trigger via GitHub CLI
gh workflow run benchmarks.yml -f qubits=20 -f gates=1024
```

## Performance Optimization Tips

### Local Environment Setup

#### CPU Governor (Linux)
```bash
# Set CPU to performance mode for stable benchmarks
sudo cpupower frequency-set --governor performance

# Verify setting
cpupower frequency-info
```

#### JVM Tuning
```bash
# Additional JVM flags for optimal performance
export JMH_OPTS="-XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+UseLargePages"
```

### Profiling

#### GC Analysis
```bash
./gradlew :benchmarks:jmh -PjmhParams="-prof gc"
```

#### Stack Profiling
```bash
./gradlew :benchmarks:jmh -PjmhParams="-prof stack"
```

## Understanding Results

### Speedup Calculation
```
Speedup = Serial Execution Time / Parallel Execution Time
```

### Geometric Mean
The benchmark uses geometric mean for aggregating speedups across different circuit sizes, which is more appropriate for ratio data than arithmetic mean.

### Variance Factors
- **CPU Architecture**: Results vary between Intel/AMD and core counts
- **System Load**: Background processes affect measurements
- **JIT Compilation**: Warmup iterations mitigate but don't eliminate effects
- **Memory Pressure**: Large circuits (24+ qubits) may hit memory bandwidth limits

### Troubleshooting

#### Low Speedup (<2.8×)
1. **Check CPU cores**: `nproc` should show ≥4 cores
2. **Verify parallel execution**: Look for "parallel execution using StructuredTaskScope" in logs
3. **System load**: Close unnecessary applications
4. **Memory**: Ensure sufficient RAM (≥8GB recommended)

#### Benchmark Failures
1. **OutOfMemoryError**: Increase heap size (`-Xmx8g`)
2. **Compilation errors**: Ensure Java 24 with preview features
3. **Missing engines**: Verify StateVector engine is on classpath

## File Structure

```
benchmarks/
├── README.md                           # This file
├── build.gradle.kts                    # Build configuration
├── scripts/
│   └── speedup.py                      # Result analysis script
├── reports/                            # Generated reports (gitignored)
│   └── parallel-speedup.md
└── src/main/java/com/omaarr90/benchmarks/
    ├── BenchmarkBase.java              # Abstract benchmark base
    ├── RandomCircuitFactory.java       # Circuit generation utility
    ├── StateVectorParallelBenchmark.java # Main parallel benchmark
    └── ComplexArrayBenchmark.java      # SIMD math benchmarks
```

## Development Guidelines

### Adding New Benchmarks

1. **Extend BenchmarkBase** for common infrastructure
2. **Use @Param** for parameterized testing
3. **Implement proper setup/teardown** with JMH annotations
4. **Document expected results** and performance characteristics

### Best Practices

- **Reproducible circuits**: Use seeded random generation
- **Avoid allocation in hot paths**: Pre-allocate in setup
- **Blackhole results**: Prevent dead code elimination
- **Consistent naming**: Follow `*Benchmark` convention

## References

- [JMH Documentation](https://openjdk.org/projects/code-tools/jmh/)
- [Java 24 Preview Features](https://openjdk.org/projects/jdk/24/)
- [StructuredTaskScope Documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html)