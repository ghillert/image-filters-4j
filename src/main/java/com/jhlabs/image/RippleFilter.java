/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;

import com.jhlabs.math.Noise;

/**
 * A filter which distorts an image by rippling it in the X or Y directions.
 * The amplitude and wavelength of rippling can be specified as well as whether
 * pixels going off the edges are wrapped or not.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class RippleFilter extends TransformFilter {

	static final long serialVersionUID = 5101667633854087384L;

	/**
	 * A constant representing the sine wave type in the ripple filter.
	 * This value is used to configure the filter to apply sine wave-based transformations
	 * to the input image.
	 */
	public static final int SINE = 0;

	/**
	 * A constant representing a sawtooth waveform type that can be used to create ripple effects
	 * in the {@code RippleFilter}. This waveform produces a repeated linear ascending and descending
	 * pattern, commonly used in graphical transformations to achieve jagged ripple effects.
	 */
	public static final int SAWTOOTH = 1;

	/**
	 * This constant represents the triangular waveform type for the ripple effect
	 * in the associated filter. It is used to specify that the ripple should
	 * follow a triangular pattern when applied.
	 */
	public static final int TRIANGLE = 2;

	/**
	 * Constant representing the noise wave type in the ripple filter.
	 * Used to apply a noise-based distortion pattern to an image.
	 */
	public static final int NOISE = 3;

	private float xAmplitude;
	private float yAmplitude;
	private float xWavelength;
	private float yWavelength;
	private int waveType;

	/**
	 * Construct a RIppleFIlter.
	 */
	public RippleFilter() {
		this.xAmplitude = 5.0f;
		this.yAmplitude = 0.0f;
		float wavelength = 16.0f;
		this.xWavelength = wavelength;
		this.yWavelength = wavelength;
	}

	/**
	 * Set the amplitude of ripple in the X direction.
	 * @param xAmplitude the amplitude (in pixels).
	 */
	public void setXAmplitude(float xAmplitude) {
		this.xAmplitude = xAmplitude;
	}

	/**
	 * Get the amplitude of ripple in the X direction.
	 * @return the amplitude (in pixels).
	 */
	public float getXAmplitude() {
		return this.xAmplitude;
	}

	/**
	 * Set the wavelength of ripple in the X direction.
	 * @param xWavelength the wavelength (in pixels).
	 */
	public void setXWavelength(float xWavelength) {
		this.xWavelength = xWavelength;
	}

	/**
	 * Get the wavelength of ripple in the X direction.
	 * @return the wavelength (in pixels).
	 */
	public float getXWavelength() {
		return this.xWavelength;
	}

	/**
	 * Set the amplitude of ripple in the Y direction.
	 * @param yAmplitude the amplitude (in pixels).
	 */
	public void setYAmplitude(float yAmplitude) {
		this.yAmplitude = yAmplitude;
	}

	/**
	 * Get the amplitude of ripple in the Y direction.
	 * @return the amplitude (in pixels).
	 */
	public float getYAmplitude() {
		return this.yAmplitude;
	}

	/**
	 * Set the wavelength of ripple in the Y direction.
	 * @param yWavelength the wavelength (in pixels).
	 */
	public void setYWavelength(float yWavelength) {
		this.yWavelength = yWavelength;
	}

	/**
	 * Get the wavelength of ripple in the Y direction.
	 * @return the wavelength (in pixels).
	 */
	public float getYWavelength() {
		return this.yWavelength;
	}


	public void setWaveType(int waveType) {
		this.waveType = waveType;
	}

	public int getWaveType() {
		return this.waveType;
	}

	@Override
	protected void transformSpace(Rectangle r) {
		if (this.edgeAction == ZERO) {
			r.x -= (int) this.xAmplitude;
			r.width += (int) (2 * this.xAmplitude);
			r.y -= (int) this.yAmplitude;
			r.height += (int) (2 * this.yAmplitude);
		}
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		float nx = (float) y / this.xWavelength;
		float ny = (float) x / this.yWavelength;
		float fx;
		float fy;
		switch (this.waveType) {
			case SINE:
			default:
				fx = (float) Math.sin(nx);
				fy = (float) Math.sin(ny);
				break;
			case SAWTOOTH:
				fx = ImageMath.mod(nx, 1);
				fy = ImageMath.mod(ny, 1);
				break;
			case TRIANGLE:
				fx = ImageMath.triangle(nx);
				fy = ImageMath.triangle(ny);
				break;
			case NOISE:
				fx = Noise.noise1(nx);
				fy = Noise.noise1(ny);
				break;
		}
		out[0] = x + this.xAmplitude * fx;
		out[1] = y + this.yAmplitude * fy;
	}

	@Override
	public String toString() {
		return "Distort/Ripple...";
	}

}
