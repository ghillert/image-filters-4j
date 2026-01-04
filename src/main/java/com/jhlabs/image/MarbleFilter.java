/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;

import com.jhlabs.math.Noise;

/**
 * This filter applies a marbling effect to an image, displacing pixels by random amounts.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class MarbleFilter extends TransformFilter {

	public float[] sinTable;
	public float[] cosTable;
	public float xScale = 4;
	public float yScale = 4;
	public float amount = 1;
	public float turbulence = 1;

	public MarbleFilter() {
		setEdgeAction(CLAMP);
	}

	public void setXScale(float xScale) {
		this.xScale = xScale;
	}

	public float getXScale() {
		return this.xScale;
	}

	public void setYScale(float yScale) {
		this.yScale = yScale;
	}

	public float getYScale() {
		return this.yScale;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return this.turbulence;
	}

	private void initialize() {
		this.sinTable = new float[256];
		this.cosTable = new float[256];
		for (int i = 0; i < 256; i++) {
			float angle = ImageMath.TWO_PI * i / 256f * this.turbulence;
			this.sinTable[i] = (float) (-this.yScale * Math.sin(angle));
			this.cosTable[i] = (float) (this.yScale * Math.cos(angle));
		}
	}

	private int displacementMap(int x, int y) {
		return PixelUtils.clamp((int) (127 * (1 + Noise.noise2(x / this.xScale, y / this.xScale))));
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		int displacement = displacementMap(x, y);
		out[0] = x + this.sinTable[displacement];
		out[1] = y + this.cosTable[displacement];
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		initialize();
		return super.filterPixels(width, height, inPixels, transformedSpace);
	}

	@Override
	public String toString() {
		return "Distort/Marble...";
	}
}
