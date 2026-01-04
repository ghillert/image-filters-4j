/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The {@code WeaveFilter} class is a filter that applies a woven texture effect
 * to an image. It extends the {@code PointFilter} class and provides options
 * to configure the width, gap, colors, and other properties of the woven
 * threads.
 * <p>
 * This filter simulates the effect of horizontal and vertical threads crossing
 * over each other, creating a woven pattern. It supports various configuration
 * options, such as rounding thread edges, shading crossings, and using custom
 * colors or image colors for the threads.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class WeaveFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 4847932412277504482L;

	private float xWidth = 16;
	private float yWidth = 16;
	private float xGap = 6;
	private float yGap = 6;
	private final int rows = 4;
	private final int cols = 4;
	private final int rgbX = 0xffff8080;
	private final int rgbY = 0xff8080ff;
	private boolean useImageColors = true;
	private boolean roundThreads = false;
	private boolean shadeCrossings = true;

	public int[][] matrix = {
			{0, 1, 0, 1},
			{1, 0, 1, 0},
			{0, 1, 0, 1},
			{1, 0, 1, 0},
	};

	public WeaveFilter() {
	}

	public void setXGap(float xGap) {
		this.xGap = xGap;
	}

	public void setXWidth(float xWidth) {
		this.xWidth = xWidth;
	}

	public float getXWidth() {
		return this.xWidth;
	}

	public void setYWidth(float yWidth) {
		this.yWidth = yWidth;
	}

	public float getYWidth() {
		return this.yWidth;
	}

	public float getXGap() {
		return this.xGap;
	}

	public void setYGap(float yGap) {
		this.yGap = yGap;
	}

	public float getYGap() {
		return this.yGap;
	}

	public void setCrossings(int[][] matrix) {
		this.matrix = matrix;
	}

	public int[][] getCrossings() {
		return this.matrix;
	}

	public void setUseImageColors(boolean useImageColors) {
		this.useImageColors = useImageColors;
	}

	public boolean getUseImageColors() {
		return this.useImageColors;
	}

	public void setRoundThreads(boolean roundThreads) {
		this.roundThreads = roundThreads;
	}

	public boolean getRoundThreads() {
		return this.roundThreads;
	}

	public void setShadeCrossings(boolean shadeCrossings) {
		this.shadeCrossings = shadeCrossings;
	}

	public boolean getShadeCrossings() {
		return this.shadeCrossings;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		x += this.xWidth + this.xGap / 2;
		y += this.yWidth + this.yGap / 2;
		float nx = ImageMath.mod(x, this.xWidth + this.xGap);
		float ny = ImageMath.mod(y, this.yWidth + this.yGap);
		int ix = (int) (x / (this.xWidth + this.xGap));
		int iy = (int) (y / (this.yWidth + this.yGap));
		boolean inX = nx < this.xWidth;
		boolean inY = ny < this.yWidth;
		float dX;
		float dY;
		float cX;
		float cY;
		int lrgbX;
		int lrgbY;

		if (this.roundThreads) {
			dX = Math.abs(this.xWidth / 2 - nx) / this.xWidth / 2;
			dY = Math.abs(this.yWidth / 2 - ny) / this.yWidth / 2;
		}
		else {
			dX = dY = 0;
		}

		if (this.shadeCrossings) {
			cX = ImageMath.smoothStep(this.xWidth / 2, this.xWidth / 2 + this.xGap, Math.abs(this.xWidth / 2 - nx));
			cY = ImageMath.smoothStep(this.yWidth / 2, this.yWidth / 2 + this.yGap, Math.abs(this.yWidth / 2 - ny));
		}
		else {
			cX = cY = 0;
		}

		if (this.useImageColors) {
			lrgbX = lrgbY = rgb;
		}
		else {
			lrgbX = this.rgbX;
			lrgbY = this.rgbY;
		}
		int v;
		int ixc = ix % this.cols;
		int iyr = iy % this.rows;
		int m = this.matrix[iyr][ixc];
		if (inX) {
			if (inY) {
				v = m == 1 ? lrgbX : lrgbY;
				v = ImageMath.mixColors(2 * (m == 1 ? dX : dY), v, 0xff000000);
			}
			else {
				if (this.shadeCrossings) {
					if (m != this.matrix[(iy + 1) % this.rows][ixc]) {
						if (m == 0) {
							cY = 1 - cY;
						}
						cY *= 0.5f;
						lrgbX = ImageMath.mixColors(cY, lrgbX, 0xff000000);
					}
					else if (m == 0) {
						lrgbX = ImageMath.mixColors(0.5f, lrgbX, 0xff000000);
					}
				}
				v = ImageMath.mixColors(2 * dX, lrgbX, 0xff000000);
			}
		}
		else if (inY) {
			if (this.shadeCrossings) {
				if (m != this.matrix[iyr][(ix + 1) % this.cols]) {
					if (m == 1) {
						cX = 1 - cX;
					}
					cX *= 0.5f;
					lrgbY = ImageMath.mixColors(cX, lrgbY, 0xff000000);
				}
				else if (m == 1) {
					lrgbY = ImageMath.mixColors(0.5f, lrgbY, 0xff000000);
				}
			}
			v = ImageMath.mixColors(2 * dY, lrgbY, 0xff000000);
		}
		else {
			v = 0x00000000;
		}
		return v;
	}

	@Override
	public String toString() {
		return "Texture/Weave...";
	}

}


