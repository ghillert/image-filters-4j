/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter which adjusts the contrast of an image.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class ContrastFilter extends TransferFilter {

	private float brightness = 1.0f;
	private float contrast = 0.5f;

	@Override
	protected float transferFunction(float f) {
		f = f * this.brightness;
		f = (f - 0.5f) * this.contrast + 0.5f;
		return f;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
		this.initialized = false;
	}

	public float getBrightness() {
		return this.brightness;
	}

	public void setContrast(float contrast) {
		this.contrast = contrast;
		this.initialized = false;
	}

	public float getContrast() {
		return this.contrast;
	}

	@Override
	public String toString() {
		return "Colors/Contrast...";
	}

}

