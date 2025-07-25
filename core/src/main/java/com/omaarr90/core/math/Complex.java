package com.omaarr90.core.math;

public record Complex(double re, double im) {

    public static final Complex ZERO = new Complex(0.0, 0.0);
    public static final Complex ONE = new Complex(1.0, 0.0);
    public static final Complex I = new Complex(0.0, 1.0);

    public Complex add(Complex other) {
        return new Complex(this.re + other.re, this.im + other.im);
    }

    public Complex multiply(Complex other) {
        double real = this.re * other.re - this.im * other.im;
        double imag = this.re * other.im + this.im * other.re;
        return new Complex(real, imag);
    }

    public Complex conjugate() {
        return new Complex(this.re, -this.im);
    }

    public double norm() {
        return this.re * this.re + this.im * this.im;
    }

    public double abs() {
        return Math.sqrt(norm());
    }

    public Polar toPolar() {
        return new Polar(abs(), Math.atan2(im, re));
    }

    public record Polar(double r, double theta) {}

    @Override
    public String toString() {
        return String.format("%.5f %c %.5fi", re, im < 0 ? '-' : '+', Math.abs(im));
    }
}
