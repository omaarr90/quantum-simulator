package com.omaarr90.core.math;

/**
 * Immutable complex number implementation.
 * <p>
 *     Designed for intrinsic friendliness: only primitive {@code double} fields,
 *     no boxing, and a tiny record that the JIT can scalar‑replace. All
 *     operations return new {@link Complex} instances so callers can fluently
 *     chain arithmetic without hidden allocations.
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

    /** Factory from polar coordinates. */
    public static Complex fromPolar(double r, double theta) {
        return new Complex(r * Math.cos(theta), r * Math.sin(theta));
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
     * Squared magnitude (|z|<sup>2</sup>) – avoids the cost of a square root.
     * Ideal for tight loops and SIMD reductions.
     */
    public double norm() {
        return real * real + imaginary * imaginary;
    }

    /**
     * Euclidean magnitude |z| (also called modulus).
     * Uses {@link Math#hypot(double, double)} for numerical stability.
     */
    public double modulus() {
        return Math.hypot(real, imaginary);
    }

    /**
     * Converts to polar representation.
     *
     * @return polar record (r, theta) where {@code r >= 0} and {@code theta} is in radians
     */
    public Polar toPolar() {
        return new Polar(modulus(), Math.atan2(imaginary, real));
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
