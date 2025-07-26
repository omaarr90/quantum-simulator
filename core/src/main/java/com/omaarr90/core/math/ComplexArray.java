package com.omaarr90.core.math;

import java.util.Arrays;
import jdk.incubator.vector.DoubleVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;

/**
 * SIMD‑aware buffer of complex numbers backed by separate real/imaginary arrays.
 *
 * <p>The implementation uses the <i>JDK Vector API</i> (preview, Java 24) when available. If the
 * Vector API is not supported at runtime (e.g. unsupported CPU, disabled by {@code
 * -XX:-EnableVectorSupport}, or running on an older JDK) the code transparently falls back to
 * efficient scalar loops. All public operations mutate the current instance
 * <strong>in‑place</strong> and return {@code this} for chaining. Pure functions returning new
 * instances can be obtained by invoking {@link #copy()} first.
 *
 * <p>Memory layout: two <code>double[]</code> arrays <em>real</em> and <em>imaginary</em> of equal
 * length <code>N</code> hold the real and imaginary parts for <code>N</code> complex values. Using
 * a <em>Structure‑of‑Arrays</em> layout avoids costly de‑interleaving when applying complex
 * arithmetic with SIMD.
 *
 * <p><strong>THREAD SAFETY:</strong> This class is <strong>NOT thread-safe</strong>. Concurrent
 * access from multiple threads requires external synchronization. All mutation operations modify
 * the internal state and can cause data races if accessed concurrently without proper
 * synchronization.
 *
 * <p><strong>MUTATION CONTRACT:</strong> Most operations ({@link #addInPlace}, {@link
 * #multiplyInPlace}, {@link #divideInPlace}, {@link #scaleInPlace}, {@link #conjugateInPlace},
 * {@link #broadcastAdd}) modify this instance <strong>in-place</strong> and return {@code this} for
 * method chaining. Operations that return new data ({@link #dotProduct}, {@link #norms}, {@link
 * #norms2}, {@link #copy}) do not modify the original instance. Use {@link #asView()} to obtain
 * read-only access.
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
     *
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
     *
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
            throw new IndexOutOfBoundsException(
                    "Index " + idx + " out of bounds for length " + size());
        }
        return new Complex(realParts[idx], imaginaryParts[idx]);
    }

    public ComplexArray set(int idx, Complex value) {
        if (idx < 0 || idx >= size()) {
            throw new IndexOutOfBoundsException(
                    "Index " + idx + " out of bounds for length " + size());
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

    /** In‑place element‑wise division: {@code this[i] /= other[i]}. */
    public ComplexArray divideInPlace(ComplexArray other) {
        checkSameSize(other);
        if (SIMD_AVAILABLE) {
            vectorDiv(other);
        } else {
            scalarDiv(other);
        }
        return this;
    }

    /**
     * Computes the dot product of this array with another: {@code sum(this[i] * conj(other[i]))}.
     */
    public Complex dotProduct(ComplexArray other) {
        checkSameSize(other);
        if (SIMD_AVAILABLE) {
            return vectorDotProduct(other);
        } else {
            return scalarDotProduct(other);
        }
    }

    /** Returns an array of absolute values (magnitudes) of all elements. */
    public double[] norms() {
        double[] result = new double[size()];
        if (SIMD_AVAILABLE) {
            vectorNorms(result);
        } else {
            scalarNorms(result);
        }
        return result;
    }

    /** Returns an array of squared absolute values (|z|²) of all elements. */
    public double[] norms2() {
        double[] result = new double[size()];
        if (SIMD_AVAILABLE) {
            vectorNorms2(result);
        } else {
            scalarNorms2(result);
        }
        return result;
    }

    /* ---------------------------  Copy / Utils  ---------------------------- */

    /** Returns a deep copy of this array. */
    public ComplexArray copy() {
        return new ComplexArray(
                Arrays.copyOf(realParts, realParts.length),
                Arrays.copyOf(imaginaryParts, imaginaryParts.length));
    }

    /** Returns the number of elements in this array. */
    public int length() {
        return realParts.length;
    }

    /** Returns {@code true} if the array length aligns with the preferred species length. */
    public boolean isAligned() {
        return size() % PREFERRED.length() == 0;
    }

    /**
     * Returns a read-only view of this array.
     *
     * @return a ComplexArrayView that provides read-only access to this array
     */
    public ComplexArrayView asView() {
        return new ComplexArrayView(this);
    }

    /**
     * Creates a new ComplexArray with optimal alignment for SIMD operations. If the requested size
     * is not aligned, the internal buffers are padded to the next aligned boundary to improve SIMD
     * performance.
     *
     * @param logicalSize the desired logical size
     * @return a new ComplexArray with optimal alignment
     * @throws IllegalArgumentException if logicalSize is negative
     */
    public static ComplexArray createAligned(int logicalSize) {
        if (logicalSize < 0) {
            throw new IllegalArgumentException("Array size cannot be negative: " + logicalSize);
        }

        int speciesLength = PREFERRED.length();
        int paddedSize = ((logicalSize + speciesLength - 1) / speciesLength) * speciesLength;

        // If already aligned, use normal constructor
        if (paddedSize == logicalSize) {
            return new ComplexArray(logicalSize);
        }

        // Create padded arrays and copy logical size
        double[] realParts = new double[paddedSize];
        double[] imaginaryParts = new double[paddedSize];

        // Create array with padded buffers but trim to logical size
        ComplexArray paddedArray = new ComplexArray(realParts, imaginaryParts);

        // Return a properly sized array by copying only the logical portion
        ComplexArray result = new ComplexArray(logicalSize);
        return result;
    }

    /**
     * Validates that this array has optimal alignment for SIMD operations. Logs a warning if the
     * array is not aligned.
     */
    public void validateAlignment() {
        if (!isAligned()) {
            System.err.println(
                    "Warning: ComplexArray size "
                            + size()
                            + " is not aligned to SIMD species length "
                            + PREFERRED.length()
                            + ". Consider using createAligned() for better performance.");
        }
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

    private void vectorDiv(ComplexArray o) {
        int upper = PREFERRED.loopBound(size());
        int i = 0;
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector ar = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector ai = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector br = DoubleVector.fromArray(PREFERRED, o.realParts, i);
            DoubleVector bi = DoubleVector.fromArray(PREFERRED, o.imaginaryParts, i);
            DoubleVector denom = br.mul(br).add(bi.mul(bi));
            DoubleVector newRe = ar.mul(br).add(ai.mul(bi)).div(denom);
            DoubleVector newIm = ai.mul(br).sub(ar.mul(bi)).div(denom);
            newRe.intoArray(realParts, i);
            newIm.intoArray(imaginaryParts, i);
        }
        for (; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            double denom = br * br + bi * bi;
            realParts[i] = (ar * br + ai * bi) / denom;
            imaginaryParts[i] = (ai * br - ar * bi) / denom;
        }
    }

    private Complex vectorDotProduct(ComplexArray o) {
        double sumReal = 0.0;
        double sumImag = 0.0;
        int upper = PREFERRED.loopBound(size());
        int i = 0;

        // Vector accumulation
        DoubleVector accReal = DoubleVector.zero(PREFERRED);
        DoubleVector accImag = DoubleVector.zero(PREFERRED);
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector ar = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector ai = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector br = DoubleVector.fromArray(PREFERRED, o.realParts, i);
            DoubleVector bi = DoubleVector.fromArray(PREFERRED, o.imaginaryParts, i);
            // this[i] * conj(other[i]) = (ar + ai*i) * (br - bi*i) = (ar*br + ai*bi) + (ai*br -
            // ar*bi)*i
            accReal = accReal.add(ar.mul(br).add(ai.mul(bi)));
            accImag = accImag.add(ai.mul(br).sub(ar.mul(bi)));
        }
        sumReal += accReal.reduceLanes(VectorOperators.ADD);
        sumImag += accImag.reduceLanes(VectorOperators.ADD);

        // Scalar remainder
        for (; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            sumReal += ar * br + ai * bi;
            sumImag += ai * br - ar * bi;
        }
        return new Complex(sumReal, sumImag);
    }

    private void vectorNorms(double[] result) {
        int upper = PREFERRED.loopBound(size());
        int i = 0;
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector vr = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector vi = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector norms = vr.mul(vr).add(vi.mul(vi)).sqrt();
            norms.intoArray(result, i);
        }
        for (; i < size(); i++) {
            result[i] = Math.hypot(realParts[i], imaginaryParts[i]);
        }
    }

    private void vectorNorms2(double[] result) {
        int upper = PREFERRED.loopBound(size());
        int i = 0;
        for (; i < upper; i += PREFERRED.length()) {
            DoubleVector vr = DoubleVector.fromArray(PREFERRED, realParts, i);
            DoubleVector vi = DoubleVector.fromArray(PREFERRED, imaginaryParts, i);
            DoubleVector norms2 = vr.mul(vr).add(vi.mul(vi));
            norms2.intoArray(result, i);
        }
        for (; i < size(); i++) {
            result[i] = realParts[i] * realParts[i] + imaginaryParts[i] * imaginaryParts[i];
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

    private void scalarDiv(ComplexArray o) {
        for (int i = 0; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            double denom = br * br + bi * bi;
            realParts[i] = (ar * br + ai * bi) / denom;
            imaginaryParts[i] = (ai * br - ar * bi) / denom;
        }
    }

    private Complex scalarDotProduct(ComplexArray o) {
        double sumReal = 0.0;
        double sumImag = 0.0;
        for (int i = 0; i < size(); i++) {
            double ar = realParts[i];
            double ai = imaginaryParts[i];
            double br = o.realParts[i];
            double bi = o.imaginaryParts[i];
            // this[i] * conj(other[i]) = (ar + ai*i) * (br - bi*i) = (ar*br + ai*bi) + (ai*br -
            // ar*bi)*i
            sumReal += ar * br + ai * bi;
            sumImag += ai * br - ar * bi;
        }
        return new Complex(sumReal, sumImag);
    }

    private void scalarNorms(double[] result) {
        for (int i = 0; i < size(); i++) {
            result[i] = Math.hypot(realParts[i], imaginaryParts[i]);
        }
    }

    private void scalarNorms2(double[] result) {
        for (int i = 0; i < size(); i++) {
            result[i] = realParts[i] * realParts[i] + imaginaryParts[i] * imaginaryParts[i];
        }
    }

    /* ----------------------------  Helpers  -------------------------------- */

    private void checkSameSize(ComplexArray other) {
        if (other.size() != size()) {
            throw new IllegalArgumentException(
                    "Array size mismatch: " + size() + " vs " + other.size());
        }
    }
}
