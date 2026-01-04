/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The {@code ExposureFilter} class adjusts the exposure levels of an image.
 * It modifies the brightness of the image by applying an exponential
 * transfer function to the pixel values, based on a user-specified exposure value.
 * <p>
 * This filter is a subclass of the {@code TransferFilter} class and specifically
 * overrides the transfer function behavior to achieve its effect.
 * <p>
 * The exposure level can be configured using the {@code setExposure} method,
 * and the current exposure value can be retrieved using the {@code getExposure} method.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class ExposureFilter extends TransferFilter {

	private float exposure = 1.0f;

	@Override
	protected float transferFunction(float f) {
		return 1 - (float) Math.exp(-f * this.exposure);
	}

	public void setExposure(float exposure) {
		this.exposure = exposure;
		this.initialized = false;
	}

	public float getExposure() {
		return this.exposure;
	}

	@Override
	public String toString() {
		return "Colors/Exposure...";
	}

}

