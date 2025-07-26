package com.omaarr90.core.math;

/**
 * Immutable complex number implementation for quantum computing applications.
 * <p>
 * Represents a complex number z = a + bi where a and b are real numbers,
 * and i is the imaginary unit (i² = -1).
 * </p>
 * <p>
 * <strong>IMMUTABILITY GUARANTEE:</strong> This class is completely immutable.
 * Every arithmetic operation ({@link #add}, {@link #sub}, {@link #mul}, {@link #div}, 
 * {@link #scale}, {@link #conjugate}) returns a <strong>new</strong> {@link Complex} 
 * instance, leaving the original unchanged. No method modifies the state of existing instances.
 * </p>
 * <p>
 * Designed for intrinsic friendliness: only primitive {@code double} fields,
 * no boxing, and a tiny record that the JIT can scalar‑replace. All
 * operations return new {@link Complex} instances so callers can fluently
 * chain arithmetic without hidden allocations.
 * </p>
 */
public record Complex(double real, double imaginary) {

    /*---------------------------------------------------------------------
     *  Construction helpers & constants
     *--------------------------------------------------------------------*/

    /** Zero (0 + 0i). */
    public static final Complex ZERO = new Complex(0.0, 0.0);
    /** One  (1 + 0i). */
    public static final Complex ONE  = new Complex(1.0, 0.0);
    /** Imaginary unit (0 + 1i). */
    public static final Complex I    = new Complex(0.0, 1.0);
    /** Negative imaginary unit (0 - 1i). */
    public static final Complex NEG_I = new Complex(0.0, -1.0);

    /** Factory from polar coordinates. */
    public static Complex fromPolar(double r, double theta) {
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
    }

    /** 
     * Creates a complex number from Euler's formula: e^(iθ) = cos(θ) + i*sin(θ).
     * 
     * @param theta the angle in radians
     * @return e^(iθ) = cos(θ) + i*sin(θ)
     */
    public static Complex exp(double theta) {
        return new Complex(Math.cos(theta), Math.sin(theta));
    }

    /*---------------------------------------------------------------------
     *  Basic arithmetic – intrinsically friendly (no boxing)
     *--------------------------------------------------------------------*/

    /**
     * Adds {@code other} to {@code this}.
     *
     * @return {@code this + other}
     */
    public Complex add(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    /**
     * Subtracts {@code other} from {@code this}.
     *
     * @param other the complex number to subtract
     * @return {@code this - other}
     */
    public Complex sub(Complex other) {
        return new Complex(real - other.real, imaginary - other.imaginary);
    }

    /**
     * Multiplies {@code this} by {@code other}.
     *
     * @return {@code this * other}
     */
    public Complex mul(Complex other) {
        return new Complex(
                real * other.real - imaginary * other.imaginary,
                real * other.imaginary + imaginary * other.real
        );
    }

    /**
     * Divides {@code this} by {@code other}.
     * <p>
     * Complex division: (a+bi)/(c+di) = ((ac+bd) + (bc-ad)i)/(c²+d²)
     * </p>
     *
     * @param other the complex number to divide by
     * @return {@code this / other}
     * @throws ArithmeticException if {@code other} is zero (within machine precision)
     */
    public Complex div(Complex other) {
        double denominator = other.real * other.real + other.imaginary * other.imaginary;
        if (denominator == 0.0) {
            throw new ArithmeticException("Division by zero complex number");
        }
        return new Complex(
                (real * other.real + imaginary * other.imaginary) / denominator,
                (imaginary * other.real - real * other.imaginary) / denominator
        );
    }

    /**
     * Multiplies by a real scalar.
     *
     * @return {@code this * scalar}
     */
    public Complex scale(double scalar) {
        return new Complex(real * scalar, imaginary * scalar);
    }

    /**
     * Complex conjugate.
     *
     * @return {@code conj(this)}
     */
    public Complex conjugate() {
        return new Complex(real, -imaginary);
    }

    /*---------------------------------------------------------------------
     *  Magnitudes & polar representation
     *--------------------------------------------------------------------*/

    /**
     * Squared magnitude (|z|²) – avoids the cost of a square root.
     * Ideal for tight loops and SIMD reductions.
     *
     * @return |z|² = real² + imaginary²
     */
    public double abs2() {
        return real * real + imaginary * imaginary;
    }

    /**
     * Euclidean magnitude |z| (also called modulus or absolute value).
     * Uses {@link Math#hypot(double, double)} for numerical stability.
     *
     * @return |z| = √(real² + imaginary²)
     */
    public double abs() {
        return Math.hypot(real, imaginary);
    }

    /**
     * Argument (phase angle) of the complex number.
     * Returns the angle θ in polar representation z = r*e^(iθ).
     *
     * @return arg(z) = atan2(imaginary, real) in radians, range [-π, π]
     */
    public double arg() {
        return Math.atan2(imaginary, real);
    }

    /**
     * Converts to polar representation.
     *
     * @return polar record (r, θ) where {@code r ≥ 0} and {@code θ} is in radians
     */
    public Polar toPolar() {
        return new Polar(abs(), Math.atan2(imaginary, real));
    }

    /** Immutable polar coordinate representation. */
    public record Polar(double r, double theta) {
        public Complex toComplex() {
            return fromPolar(r, theta);
        }
    }

    /*---------------------------------------------------------------------
     *  Debug helpers
     *--------------------------------------------------------------------*/

    @Override
    public String toString() {
        return "(" + real + (imaginary < 0 ? " - " : " + ") + Math.abs(imaginary) + "i)";
    }
}
