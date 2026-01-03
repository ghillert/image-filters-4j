/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;

/**
 * A filter to perform auto-equalization on an image.
 */
public class EqualizeFilter extends WholeImageFilter implements java.io.Serializable {

	protected int[][] lut;

	public EqualizeFilter() {
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		Histogram histogram = new Histogram(inPixels, width, height, 0, width);

		int i, j;

		if (histogram.getNumSamples() > 0) {
			float scale = 255.0f / histogram.getNumSamples();
			this.lut = new int[3][256];
			for (i = 0; i < 3; i++) {
				this.lut[i][0] = histogram.getFrequency(i, 0);
				for (j = 1; j < 256; j++) {
					this.lut[i][j] = this.lut[i][j - 1] + histogram.getFrequency(i, j);
				}
				for (j = 0; j < 256; j++) {
					this.lut[i][j] = Math.round(this.lut[i][j] * scale);
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
		return "Colors/Equalize";
	}
}
