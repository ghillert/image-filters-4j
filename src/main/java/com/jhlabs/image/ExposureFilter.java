/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

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

