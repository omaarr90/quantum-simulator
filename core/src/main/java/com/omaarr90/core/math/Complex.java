package com.omaarr90.core.math;

public record Complex(double real, double imaginary) {

    public static final Complex ZERO = new Complex(0.0, 0.0);
    public static final Complex ONE = new Complex(1.0, 0.0);
    public static final Complex I = new Complex(0.0, 1.0);

    public Complex add(Complex other) {
        return new Complex(this.real + other.real, this.imaginary + other.imaginary);
    }

    public Complex multiply(Complex other) {
        double real = this.real * other.real - this.imaginary * other.imaginary;
        double imag = this.real * other.imaginary + this.imaginary * other.real;
        return new Complex(real, imag);
    }

    public Complex conjugate() {
        return new Complex(this.real, -this.imaginary);
    }

    public double norm() {
        return this.real * this.real + this.imaginary * this.imaginary;
    }

    public double abs() {
        return Math.sqrt(norm());
    }

    public Polar toPolar() {
        return new Polar(abs(), Math.atan2(imaginary, real));
    }

    public record Polar(double r, double theta) {}

    @Override
    public String toString() {
        return String.format("%.5f %c %.5fi", real, imaginary < 0 ? '-' : '+', Math.abs(imaginary));
    }
}
