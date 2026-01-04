/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The GainFilter class is a specialized implementation of the {@link TransferFilter} that
 * modifies the intensity distribution of an image through a gain and bias adjustment.
 * The gain factor is used to control the contrast of midtones in the image, while the
 * bias factor shifts the intensity values to control brightness.
 * <p>
 * The filter works by applying a transfer function to each pixel in the image, where
 * the gain and bias are applied sequentially to adjust the intensity levels. This
 * allows for fine control over the image's tone curve.
 * <p>
 * Methods are provided to set and get the gain and bias values, and the filter regenerates
 * its internal lookup tables whenever either value is modified.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class GainFilter extends TransferFilter {

	private float gain = 0.5f;
	private float bias = 0.5f;

	@Override
	protected float transferFunction(float f) {
		f = ImageMath.gain(f, this.gain);
		f = ImageMath.bias(f, this.bias);
		return f;
	}

	public void setGain(float gain) {
		this.gain = gain;
		this.initialized = false;
	}

	public float getGain() {
		return this.gain;
	}

	public void setBias(float bias) {
		this.bias = bias;
		this.initialized = false;
	}

	public float getBias() {
		return this.bias;
	}

	@Override
	public String toString() {
		return "Colors/Gain...";
	}

}

