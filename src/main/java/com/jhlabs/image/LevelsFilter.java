/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;

/**
 * A filter which allows levels adjustment on an image.
 */
public class LevelsFilter extends WholeImageFilter {

	protected int[][] lut;
	protected float lowLevel = 0;
	protected float highLevel = 1;
	protected float lowOutputLevel = 0;
	protected float highOutputLevel = 1;

	public LevelsFilter() {
	}

	public void setLowLevel(float lowLevel) {
		this.lowLevel = lowLevel;
	}

	public float getLowLevel() {
		return this.lowLevel;
	}

	public void setHighLevel(float highLevel) {
		this.highLevel = highLevel;
	}

	public float getHighLevel() {
		return this.highLevel;
	}

	public void setLowOutputLevel(float lowOutputLevel) {
		this.lowOutputLevel = lowOutputLevel;
	}

	public float getLowOutputLevel() {
		return this.lowOutputLevel;
	}

	public void setHighOutputLevel(float highOutputLevel) {
		this.highOutputLevel = highOutputLevel;
	}

	public float getHighOutputLevel() {
		return this.highOutputLevel;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		Histogram histogram = new Histogram(inPixels, width, height, 0, width);

		int i, j;

		if (histogram.getNumSamples() > 0) {
			float scale = 255.0f / histogram.getNumSamples();
			this.lut = new int[3][256];

			float low = this.lowLevel * 255;
			float high = this.highLevel * 255;
			if (low == high) {
				high++;
			}
			for (i = 0; i < 3; i++) {
				for (j = 0; j < 256; j++) {
					this.lut[i][j] = PixelUtils.clamp((int) (255 * (this.lowOutputLevel + (this.highOutputLevel - this.lowOutputLevel) * (j - low) / (high - low))));
				}
			}
		}
		else {
			this.lut = null;
		}

		i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				inPixels[i] = filterRGB(x, y, inPixels[i]);
				i++;
			}
		}
		this.lut = null;

		return inPixels;
	}

	public int filterRGB(int x, int y, int rgb) {
		if (this.lut != null) {
			int a = rgb & 0xff000000;
			int r = this.lut[Histogram.RED][(rgb >> 16) & 0xff];
			int g = this.lut[Histogram.GREEN][(rgb >> 8) & 0xff];
			int b = this.lut[Histogram.BLUE][rgb & 0xff];

			return a | (r << 16) | (g << 8) | b;
		}
		return rgb;
	}

	@Override
	public String toString() {
		return "Colors/Levels...";
	}
}
