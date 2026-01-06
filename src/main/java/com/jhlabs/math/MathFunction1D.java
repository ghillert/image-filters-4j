/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.math;

public class MathFunction1D implements Function1D {

	/**
	 * Represents the integer constant used to denote the sine mathematical operation.
	 * This constant can be used to perform the sine function, which calculates the
	 * sine of a given value, typically in radians.
	 */
	public static final int SIN = 1;

	/**
	 * Represents the integer constant used to denote the cosine mathematical operation.
	 * This constant can be used to perform the cosine function, which calculates the
	 * cosine of a given angle, typically in radians.
	 */
	public static final int COS = 2;

	/**
	 * Represents the integer constant used to denote the tangent mathematical operation.
	 * This constant can be used to perform the tangent function, which calculates the
	 * tangent of a given value, typically in radians.
	 */
	public static final int TAN = 3;

	/**
	 * Represents the integer constant used to denote the square root mathematical operation.
	 * This constant can be used to perform the square root function, which calculates
	 * the non-negative square root of a given value.
	 */
	public static final int SQRT = 4;

	/**
	 * Represents the integer constant used to denote the arcsine (inverse sine) mathematical operation.
	 * This constant can be used to perform the arcsine function, which calculates the angle (in radians)
	 * whose sine is a given value. The result is in the range of -π/2 to π/2.
	 */
	public static final int ASIN = -1;

	/**
	 * Represents the integer constant used to denote the arccosine (inverse cosine) mathematical operation.
	 * This constant can be used to perform the arccosine function, which calculates the angle (in radians)
	 * whose cosine is a given value. The result is in the range of 0 to π.
	 */
	public static final int ACOS = -2;

	/**
	 * Represents the integer constant used to denote the arctangent (inverse tangent) mathematical operation.
	 * This constant can be used to perform the arctangent function, which calculates the angle (in radians)
	 * whose tangent is a given value. The result is in the range of -π/2 to π/2.
	 */
	public static final int ATAN = -3;

	/**
	 * Represents the integer constant used to denote the square mathematical operation.
	 * This constant can be used to perform the square function, which calculates
	 * the square of a given value by multiplying the value with itself.
	 */
	public static final int SQR = -4;

	private final int operation;

	public MathFunction1D(int operation) {
		this.operation = operation;
	}

	@Override
	public float evaluate(float v) {
		switch (this.operation) {
			case SIN:
				return (float) Math.sin(v);
			case COS:
				return (float) Math.cos(v);
			case TAN:
				return (float) Math.tan(v);
			case SQRT:
				return (float) Math.sqrt(v);
			case ASIN:
				return (float) Math.asin(v);
			case ACOS:
				return (float) Math.acos(v);
			case ATAN:
				return (float) Math.atan(v);
			case SQR:
				return v * v;
		}
		return v;
	}
}

