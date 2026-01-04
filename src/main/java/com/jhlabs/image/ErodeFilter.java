/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;

/**
 * Given a binary image, this filter performs binary erosion, setting all removed pixels to the given 'new' color.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class ErodeFilter extends BinaryFilter {

	protected int threshold = 2;

	public ErodeFilter() {
		this.newColor = 0xffffffff;
	}

	/**
	 * Set the threshold - the number of neighbouring pixels for dilation to occur.
	 *
	 * @param threshold the new threshold
	 */
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}

	/**
	 * Return the threshold - the number of neighbouring pixels for dilation to occur.
	 *
	 * @return the current threshold
	 */
	public int getThreshold() {
		return this.threshold;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int[] outPixels = new int[width * height];

		for (int i = 0; i < this.iterations; i++) {
			int index = 0;

			if (i > 0) {
				int[] t = inPixels;
				inPixels = outPixels;
				outPixels = t;
			}
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int pixel = inPixels[y * width + x];
					if (this.blackFunction.isBlack(pixel)) {
						int neighbours = 0;

						for (int dy = -1; dy <= 1; dy++) {
							int iy = y + dy;
							int ioffset;
							if (0 <= iy && iy < height) {
								ioffset = iy * width;
								for (int dx = -1; dx <= 1; dx++) {
									int ix = x + dx;
									if (!(dy == 0 && dx == 0) && 0 <= ix && ix < width) {
										int rgb = inPixels[ioffset + ix];
										if (!this.blackFunction.isBlack(rgb)) {
											neighbours++;
										}
									}
								}
							}
						}

						if (neighbours >= this.threshold) {
							if (this.colormap != null) {
								pixel = this.colormap.getColor((float) i / this.iterations);
							}
							else {
								pixel = this.newColor;
							}
						}
					}
					outPixels[index++] = pixel;
				}
			}
		}

		return outPixels;
	}

	@Override
	public String toString() {
		return "Binary/Erode...";
	}

}

