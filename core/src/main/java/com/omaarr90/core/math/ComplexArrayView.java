package com.omaarr90.core.math;

/**
 * Read-only view of a {@link ComplexArray} that exposes only getter operations.
 *
 * <p>This class provides a safe way to share complex array data without allowing modifications. All
 * mutation operations are not available, preventing accidental modifications to the underlying
 * data.
 *
 * <p><strong>THREAD SAFETY:</strong> This class is not thread-safe. External synchronization is
 * required if instances are accessed concurrently from multiple threads.
 */
public final class ComplexArrayView {

    private final ComplexArray array;

    /**
     * Creates a read-only view of the given ComplexArray.
     *
     * @param array the array to create a view of
     * @throws IllegalArgumentException if array is null
     */
    public ComplexArrayView(ComplexArray array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        this.array = array;
    }

    /**
     * Returns the number of elements in this array.
     *
     * @return the size of the array
     */
    public int size() {
        return array.size();
    }

    /**
     * Returns the number of elements in this array.
     *
     * @return the length of the array
     */
    public int length() {
        return array.length();
    }

    /**
     * Gets the complex number at the specified index.
     *
     * @param idx the index
     * @return the complex number at the given index
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public Complex get(int idx) {
        return array.get(idx);
    }

    /**
     * Returns {@code true} if the array length aligns with the preferred species length.
     *
     * @return true if aligned for optimal SIMD performance
     */
    public boolean isAligned() {
        return array.isAligned();
    }

    /**
     * Computes the dot product of this array with another: {@code sum(this[i] * conj(other[i]))}.
     *
     * @param other the other array
     * @return the dot product
     * @throws IllegalArgumentException if arrays have different sizes
     */
    public Complex dotProduct(ComplexArrayView other) {
        return array.dotProduct(other.array);
    }

    /**
     * Computes the dot product of this array with another: {@code sum(this[i] * conj(other[i]))}.
     *
     * @param other the other array
     * @return the dot product
     * @throws IllegalArgumentException if arrays have different sizes
     */
    public Complex dotProduct(ComplexArray other) {
        return array.dotProduct(other);
    }

    /**
     * Returns an array of absolute values (magnitudes) of all elements.
     *
     * @return a new array containing the norms
     */
    public double[] norms() {
        return array.norms();
    }

    /**
     * Returns an array of squared absolute values (|z|²) of all elements.
     *
     * @return a new array containing the squared norms
     */
    public double[] norms2() {
        return array.norms2();
    }

    /**
     * Returns a deep copy of the underlying array.
     *
     * @return a new ComplexArray with the same values
     */
    public ComplexArray copy() {
        return array.copy();
    }

    @Override
    public String toString() {
        return array.toString();
    }
}
