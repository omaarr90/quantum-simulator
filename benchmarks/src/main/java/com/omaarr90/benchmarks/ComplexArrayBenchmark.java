package com.omaarr90.benchmarks;

import com.omaarr90.core.math.Complex;
import com.omaarr90.core.math.ComplexArray;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ComplexArrayBenchmark {

    @Param({"128", "256", "512", "1024", "2048", "4096"})
    private int size;

    private ComplexArray array1;
    private ComplexArray array2;

    @Setup(Level.Trial)
    public void setup() {
        // Create aligned arrays for optimal SIMD performance
        array1 = ComplexArray.createAligned(size);
        array2 = ComplexArray.createAligned(size);

        // Initialize with random complex numbers
        for (int i = 0; i < size; i++) {
            array1.set(i, new Complex(Math.random(), Math.random()));
            array2.set(i, new Complex(Math.random(), Math.random()));
        }
    }

    // Addition benchmark - uses SIMD when available
    @Benchmark
    public void addInPlace(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.addInPlace(array2);
        bh.consume(result);
    }

    // Multiplication benchmark - uses SIMD when available
    @Benchmark
    public void multiplyInPlace(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.multiplyInPlace(array2);
        bh.consume(result);
    }

    // Division benchmark - uses SIMD when available
    @Benchmark
    public void divideInPlace(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.divideInPlace(array2);
        bh.consume(result);
    }

    // Dot product benchmark - uses SIMD when available
    @Benchmark
    public void dotProduct(Blackhole bh) {
        Complex result = array1.dotProduct(array2);
        bh.consume(result);
    }

    // Norms benchmark - uses SIMD when available
    @Benchmark
    public void norms(Blackhole bh) {
        double[] result = array1.norms();
        bh.consume(result);
    }

    // Norms squared benchmark - uses SIMD when available
    @Benchmark
    public void norms2(Blackhole bh) {
        double[] result = array1.norms2();
        bh.consume(result);
    }

    // Scalar multiplication benchmark - uses SIMD when available
    @Benchmark
    public void scaleInPlace(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.scaleInPlace(2.5);
        bh.consume(result);
    }

    // Complex scalar multiplication benchmark - uses SIMD when available
    @Benchmark
    public void multiplyInPlaceScalar(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.multiplyInPlace(new Complex(1.5, 0.5));
        bh.consume(result);
    }

    // Broadcast addition benchmark - uses SIMD when available
    @Benchmark
    public void broadcastAdd(Blackhole bh) {
        ComplexArray result = array1.copy();
        result.broadcastAdd(new Complex(0.1, 0.2));
        bh.consume(result);
    }
}
