/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;

/**
 * The OilFilter class provides an implementation of an oil-painting
 * effect for image processing. It extends the {@code WholeImageFilter}
 * class, applying a stylized and blurred appearance to an image
 * by aggregating pixel intensity levels within a specified range.
 * The filter simulates the effect of traditional oil painting by
 * segmenting the image into regions with averaged color values.
 * <p>
 * This filter offers adjustable parameters such as range and levels.
 * <ul>
 * <li>The range defines the area surrounding each pixel for computing
 *     its new value, controlling the extent of the filter's impact.</li>
 * <li>The levels determine the number of intensity steps in the
 *     final image, affecting the color granularity.</li>
 * </ul>
 *
 * The filter modifies the whole image by iterating over its
 * pixels, calculating local histograms for RGB color channels
 * based on a neighborhood, and replacing each pixel's value
 * with a predominant aggregated intensity.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class OilFilter extends WholeImageFilter {

	static final long serialVersionUID = 1722613531684653826L;

	private int range = 3;
	private int levels = 256;

	public OilFilter() {
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getRange() {
		return this.range;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public int getLevels() {
		return this.levels;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int index = 0;
		int[] rHistogram = new int[this.levels];
		int[] gHistogram = new int[this.levels];
		int[] bHistogram = new int[this.levels];
		int[] rTotal = new int[this.levels];
		int[] gTotal = new int[this.levels];
		int[] bTotal = new int[this.levels];
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int i = 0; i < this.levels; i++) {
					rHistogram[i] = gHistogram[i] = bHistogram[i] = rTotal[i] = gTotal[i] = bTotal[i] = 0;
				}

				for (int row = -this.range; row <= this.range; row++) {
					int iy = y + row;
					int ioffset;
					if (0 <= iy && iy < height) {
						ioffset = iy * width;
						for (int col = -this.range; col <= this.range; col++) {
							int ix = x + col;
							if (0 <= ix && ix < width) {
								int rgb = inPixels[ioffset + ix];
								int r = (rgb >> 16) & 0xff;
								int g = (rgb >> 8) & 0xff;
								int b = rgb & 0xff;
								int ri = r * this.levels / 256;
								int gi = g * this.levels / 256;
								int bi = b * this.levels / 256;
								rTotal[ri] += r;
								gTotal[gi] += g;
								bTotal[bi] += b;
								rHistogram[ri]++;
								gHistogram[gi]++;
								bHistogram[bi]++;
							}
						}
					}
				}

				int r = 0, g = 0, b = 0;
				for (int i = 1; i < this.levels; i++) {
					if (rHistogram[i] > rHistogram[r]) {
						r = i;
					}
					if (gHistogram[i] > gHistogram[g]) {
						g = i;
					}
					if (bHistogram[i] > bHistogram[b]) {
						b = i;
					}
				}
				r = rTotal[r] / rHistogram[r];
				g = gTotal[g] / gHistogram[g];
				b = rTotal[b] / bHistogram[b];
				outPixels[index++] = 0xff000000 | (r << 16) | (g << 8) | b;
			}
		}
		return outPixels;
	}

	@Override
	public String toString() {
		return "Stylize/Oil...";
	}

}

