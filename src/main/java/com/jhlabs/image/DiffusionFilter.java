/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;


import java.awt.Rectangle;

/**
 * A filter which uses Floyd-Steinberg error diffusion dithering to halftone an image.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class DiffusionFilter extends WholeImageFilter {

	protected static final int[] diffusionMatrix = {
			0, 0, 0,
			0, 0, 7,
			3, 5, 1,
	};

	private int[] matrix;
	private int sum = 3 + 5 + 7 + 1;
	private boolean serpentine = true;
	private boolean colorDither = true;
	private int levels = 6;

	/**
	 * Construct a DiffusionFilter.
	 */
	public DiffusionFilter() {
		setMatrix(diffusionMatrix);
	}

	/**
	 * Set whether to use a serpentine pattern for return or not. This can reduce 'avalanche' artifacts in the output.
	 * @param serpentine true to use serpentine pattern
	 */
	public void setSerpentine(boolean serpentine) {
		this.serpentine = serpentine;
	}

	/**
	 * Return the serpentine setting.
	 * @return the current setting
	 */
	public boolean getSerpentine() {
		return this.serpentine;
	}

	public void setColorDither(boolean colorDither) {
		this.colorDither = colorDither;
	}

	public boolean getColorDither() {
		return this.colorDither;
	}

	public void setMatrix(int[] matrix) {
		this.matrix = matrix;
		this.sum = 0;
		for (int i = 0; i < matrix.length; i++) {
			this.sum += matrix[i];
		}
	}

	/**
	 * Retrieves the matrix used for diffusion filtering.
	 * @return an array of integers representing the diffusion matrix
	 */
	public int[] getMatrix() {
		return this.matrix;
	}

	/**
	 * Sets the number of levels to be used in the diffusion filter.
	 * @param levels the number of levels for the diffusion process. Default is 6.
	 */
	public void setLevels(int levels) {
		this.levels = levels;
	}

	/**
	 * Retrieves the number of levels used in the diffusion filter.
	 * @return the number of levels applied during the diffusion process.
	 */
	public int getLevels() {
		return this.levels;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int[] outPixels = new int[width * height];

		int index = 0;
		int[] map = new int[this.levels];
		for (int i = 0; i < this.levels; i++) {
			int v = 255 * i / (this.levels - 1);
			map[i] = v;
		}
		int[] div = new int[256];
		for (int i = 0; i < 256; i++) {
			div[i] = this.levels * i / 256;
		}

		for (int y = 0; y < height; y++) {
			boolean reverse = this.serpentine && (y & 1) == 1;
			int direction;
			if (reverse) {
				index = y * width + width - 1;
				direction = -1;
			}
			else {
				index = y * width;
				direction = 1;
			}
			for (int x = 0; x < width; x++) {
				int rgb1 = inPixels[index];

				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = rgb1 & 0xff;

				if (!this.colorDither) {
					int gray = (r1 + g1 + b1) / 3;
					r1 = gray;
					g1 = gray;
					b1 = gray;
				}

				int r2 = map[div[r1]];
				int g2 = map[div[g1]];
				int b2 = map[div[b1]];

				outPixels[index] = 0xff000000 | (r2 << 16) | (g2 << 8) | b2;

				int er = r1 - r2;
				int eg = g1 - g2;
				int eb = b1 - b2;

				for (int i = -1; i <= 1; i++) {
					int iy = i + y;
					if (0 <= iy && iy < height) {
						for (int j = -1; j <= 1; j++) {
							int jx = j + x;
							if (0 <= jx && jx < width) {
								int w;
								if (reverse) {
									w = this.matrix[(i + 1) * 3 - j + 1];
								}
								else {
									w = this.matrix[(i + 1) * 3 + j + 1];
								}
								if (w != 0) {
									int k = reverse ? index - j : index + j;
									rgb1 = inPixels[k];
									r1 = (rgb1 >> 16) & 0xff;
									g1 = (rgb1 >> 8) & 0xff;
									b1 = rgb1 & 0xff;
									r1 += er * w / this.sum;
									g1 += eg * w / this.sum;
									b1 += eb * w / this.sum;
									inPixels[k] = (PixelUtils.clamp(r1) << 16) | (PixelUtils.clamp(g1) << 8) | PixelUtils.clamp(b1);
								}
							}
						}
					}
				}
				index += direction;
			}
		}

		return outPixels;
	}

	@Override
	public String toString() {
		return "Colors/Diffusion Dither...";
	}

}

