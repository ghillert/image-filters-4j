/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

public class RescaleFilter extends TransferFilter {

	static final long serialVersionUID = -2724874183243154495L;

	private float scale = 1.0f;

	@Override
	protected float transferFunction(float v) {
		return PixelUtils.clamp((int) (v * this.scale));
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.initialized = false;
	}

	public float getScale() {
		return this.scale;
	}

	@Override
	public String toString() {
		return "Colors/Rescale...";
	}

}

