package com.omaarr90.core.math;

import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorSpecies;

import java.util.Arrays;

/**
 * SIMD‑aware buffer of complex numbers backed by separate real/imaginary arrays.
 * <p>
 *     The implementation uses the <i>JDK Vector API</i> (preview, Java 24) when available.
 *     If the Vector API is not supported at runtime (e.g. unsupported CPU, disabled by
 *     {@code -XX:-EnableVectorSupport}, or running on an older JDK) the code transparently
 *     falls back to efficient scalar loops.  All public operations mutate the current
 *     instance <strong>in‑place</strong> and return {@code this} for chaining.  Pure functions
 *     returning new instances can be obtained by invoking {@link #copy()} first.
 * </p>
 * <p>
 *     Memory layout: two <code>double[]</code> arrays <em>real</em> and <em>imaginary</em> of equal length
 *     <code>N</code> hold the real and imaginary parts for <code>N</code> complex values.
 *     Using a <em>Structure‑of‑Arrays</em> layout avoids costly de‑interleaving when applying
 *     complex arithmetic with SIMD.
 * </p>
 */
public final class ComplexArray {

    /** Preferred species for the current CPU/VM. */
    private static final VectorSpecies<Double> PREFERRED = DoubleVector.SPECIES_PREFERRED;

    /** {@code true} when the VM can generate vector instructions for the chosen species. */
    private static final boolean SIMD_AVAILABLE = PREFERRED.length() > 1;

    private final double[] realParts;
    private final double[] imaginaryParts;

    /**
     * Creates an uninitialised ComplexArray of {@code size} elements.
     * @throws IllegalArgumentException if size is negative
     */
    public ComplexArray(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Array size cannot be negative: " + size);
        }
        this.realParts = new double[size];
        this.imaginaryParts = new double[size];
    }

    /**
     * Creates a ComplexArray from existing <em>Structure‑of‑Arrays</em> buffers.
     * @throws IllegalArgumentException when the arrays differ in length
     */
    public ComplexArray(double[] realParts, double[] imaginaryParts) {
        if (realParts.length != imaginaryParts.length) {
            throw new IllegalArgumentException("Real and imaginary arrays must be the same length");
        }
        this.realParts = realParts;
        this.imaginaryParts = imaginaryParts;
    }

    public int size() {
        return realParts.length;
    }

    public Complex get(int idx) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException("Index " + idx + " out of bounds for length " + size());
        }
        return new Complex(realParts[idx], imaginaryParts[idx]);
    }

    public ComplexArray set(int idx, Complex value) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException("Index " + idx + " out of bounds for length " + size());
        }
        realParts[idx] = value.real();
        imaginaryParts[idx] = value.imaginary();
        return this;
    }

    /* --------------------------  Core arithmetic  -------------------------- */

    /** In‑place element‑wise addition: {@code this[i] += other[i]}. */
    public ComplexArray addInPlace(ComplexArray other) {
        checkSameSize(other);
        if (SIMD_AVAILABLE) {
            vectorAdd(other);
        } else {
            scalarAdd(other);
        }
        return this;
    }

    /** In‑place element‑wise multiplication: {@code this[i] *= other[i]}. */
    public ComplexArray multiplyInPlace(ComplexArray other) {
        checkSameSize(other);
        if (SIMD_AVAILABLE) {
            vectorMul(other);
        } else {
            scalarMul(other);
        }
        return this;
    }

    /** Scales all elements by a real scalar {@code k} in‑place. */
    public ComplexArray scaleInPlace(double k) {
        if (SIMD_AVAILABLE) {
            DoubleVector kVec = DoubleVector.broadcast(PREFERRED, k);
            int upper = PREFERRED.loopBound(size());
            int i = 0;
            for (; i < upper; i += PREFERRED.length()) {
                DoubleVector vr = DoubleVector.fromArray(PREFERRED, realParts, i);
                DoubleVector vi = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
                vr.mul(kVec).intoArray(realParts, i);
                vi.mul(kVec).intoArray(imaginaryParts, i);
            }
            for (; i < size(); i++) {
                realParts[i] *= k;
                imaginaryParts[i] *= k;
            }
        } else {
            for (int i = 0; i < size(); i++) {
                realParts[i] *= k;
                imaginaryParts[i] *= k;
            }
        }
        return this;
    }

    /** Broadcast multiplication by a complex scalar {@code s} in‑place. */
    public ComplexArray multiplyInPlace(Complex s) {
        double sr = s.real();
        double si = s.imaginary();
        if (SIMD_AVAILABLE) {
            DoubleVector vrScale = DoubleVector.broadcast(PREFERRED, sr);
            DoubleVector viScale = DoubleVector.broadcast(PREFERRED, si);
            int upper = PREFERRED.loopBound(size());
            int i = 0;
            for (; i < upper; i += PREFERRED.length()) {
                DoubleVector vr = DoubleVector.fromArray(PREFERRED, realParts, i);
                DoubleVector vi = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
                DoubleVector newRe = vr.mul(vrScale).sub(vi.mul(viScale));
                DoubleVector newIm = vr.mul(viScale).add(vi.mul(vrScale));
                newRe.intoArray(realParts, i);
                newIm.intoArray(imaginaryParts, i);
            }
            for (; i < size(); i++) {
                double r = realParts[i];
                double v = imaginaryParts[i];
                realParts[i] = r * sr - v * si;
                imaginaryParts[i] = r * si + v * sr;
            }
        } else {
            for (int i = 0; i < size(); i++) {
                double r = realParts[i];
                double v = imaginaryParts[i];
                realParts[i] = r * sr - v * si;
                imaginaryParts[i] = r * si + v * sr;
            }
        }
        return this;
    }

    /* ---------------------------  Copy / Utils  ---------------------------- */

    /** Returns a deep copy of this array. */
    public ComplexArray copy() {
        return new ComplexArray(Arrays.copyOf(realParts, realParts.length), Arrays.copyOf(imaginaryParts, imaginaryParts.length));
    }

    /** Returns the number of elements in this array. */
    public int length() {
        return realParts.length;
    }

    /** Returns {@code true} if the array length aligns with the preferred species length. */
    public boolean isAligned() {
        return size() % PREFERRED.length() == 0;
    }

    /** Broadcast addition of a complex scalar {@code value} to all elements in‑place. */
    public ComplexArray broadcastAdd(Complex value) {
        double vr = value.real();
        double vi = value.imaginary();
        if (SIMD_AVAILABLE) {
            DoubleVector vrVec = DoubleVector.broadcast(PREFERRED, vr);
            DoubleVector viVec = DoubleVector.broadcast(PREFERRED, vi);
            int upper = PREFERRED.loopBound(size());
            int i = 0;
            for (; i < upper; i += PREFERRED.length()) {
                DoubleVector realVec = DoubleVector.fromArray(PREFERRED, realParts, i);
                DoubleVector imagVec = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
                realVec.add(vrVec).intoArray(realParts, i);
                imagVec.add(viVec).intoArray(imaginaryParts, i);
            }
            for (; i < size(); i++) {
                realParts[i] += vr;
                imaginaryParts[i] += vi;
            }
        } else {
            for (int i = 0; i < size(); i++) {
                realParts[i] += vr;
                imaginaryParts[i] += vi;
            }
        }
        return this;
    }

    /** Element‑wise conjugation in‑place: {@code this[i] = conj(this[i])}. */
    public ComplexArray conjugateInPlace() {
        if (SIMD_AVAILABLE) {
            DoubleVector negOne = DoubleVector.broadcast(PREFERRED, -1.0);
            int upper = PREFERRED.loopBound(size());
            int i = 0;
            for (; i < upper; i += PREFERRED.length()) {
                DoubleVector imagVec = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
                imagVec.mul(negOne).intoArray(imaginaryParts, i);
            }
            for (; i < size(); i++) {
                imaginaryParts[i] = -imaginaryParts[i];
            }
        } else {
            for (int i = 0; i < size(); i++) {
                imaginaryParts[i] = -imaginaryParts[i];
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size(); i++) {
            sb.append(realParts[i]).append("+").append(imaginaryParts[i]).append("i");
            if (i < size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /* -----------------------  Vectorised kernels  -------------------------- */

    private void vectorAdd(ComplexArray o) {
        int upper = PREFERRED.loopBound(size());
        int i = 0;
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector vr = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector vi = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector or = DoubleVector.fromArray(PREFERRED, o.realParts, i);
            DoubleVector oi = DoubleVector.fromArray(PREFERRED, o.imaginaryParts, i);
            vr.add(or).intoArray(realParts, i);
            vi.add(oi).intoArray(imaginaryParts, i);
        }
        for (; i < size(); i++) {
            realParts[i] += o.realParts[i];
            imaginaryParts[i] += o.imaginaryParts[i];
        }
    }

    private void vectorMul(ComplexArray o) {
        int upper = PREFERRED.loopBound(size());
        int i = 0;
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector ar = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector ai = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector br = DoubleVector.fromArray(PREFERRED, o.realParts, i);
            DoubleVector bi = DoubleVector.fromArray(PREFERRED, o.imaginaryParts, i);
            DoubleVector newRe = ar.mul(br).sub(ai.mul(bi));
            DoubleVector newIm = ar.mul(bi).add(ai.mul(br));
            newRe.intoArray(realParts, i);
            newIm.intoArray(imaginaryParts, i);
        }
        for (; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            realParts[i] = ar * br - ai * bi;
            imaginaryParts[i] = ar * bi + ai * br;
        }
    }

    /* -----------------------  Scalar fallbacks  ---------------------------- */

    private void scalarAdd(ComplexArray o) {
        for (int i = 0; i < size(); i++) {
            realParts[i] += o.realParts[i];
            imaginaryParts[i] += o.imaginaryParts[i];
        }
    }

    private void scalarMul(ComplexArray o) {
        for (int i = 0; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            realParts[i] = ar * br - ai * bi;
            imaginaryParts[i] = ar * bi + ai * br;
        }
    }

    /* ----------------------------  Helpers  -------------------------------- */

    private void checkSameSize(ComplexArray other) {
        if (other.size() != size()) {
            throw new IllegalArgumentException("Array size mismatch: " + size() + " vs " + other.size());
        }
    }
}
