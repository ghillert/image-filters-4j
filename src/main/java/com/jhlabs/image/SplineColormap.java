/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.io.Serializable;

/**
 * A Colormap implemented using Catmull-Rom colour splines. The map has a variable number
 * of knots with a minimum of four. The first and last knots give the tangent at the end
 * of the spline, and colours are interpolated from the second to the second-last knots.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class SplineColormap extends ArrayColormap implements Serializable {

	private int numKnots = 4;
	private int[] xKnots = {
			0, 0, 255, 255
	};
	private int[] yKnots = {
			0xff000000, 0xff000000, 0xffffffff, 0xffffffff,
	};

	public SplineColormap() {
		rebuildGradient();
	}

	public SplineColormap(int[] xKnots, int[] yKnots) {
		this.xKnots = xKnots;
		this.yKnots = yKnots;
		this.numKnots = xKnots.length;
		rebuildGradient();
	}

	public int getKnot(int n) {
		return this.yKnots[n];
	}

	public void setKnot(int n, int color) {
		this.yKnots[n] = color;
		rebuildGradient();
	}

	public void addKnot(int x, int color) {
		int[] nx = new int[this.numKnots + 1];
		int[] ny = new int[this.numKnots + 1];
		System.arraycopy(this.xKnots, 0, nx, 0, this.numKnots);
		System.arraycopy(this.yKnots, 0, ny, 0, this.numKnots);
		this.xKnots = nx;
		this.yKnots = ny;
		this.xKnots[this.numKnots] = x;
		this.yKnots[this.numKnots] = color;
		this.numKnots++;
		sortKnots();
		rebuildGradient();
	}

	public void removeKnot(int n) {
		if (this.numKnots <= 4) {
			return;
		}
		if (n < this.numKnots - 1) {
			System.arraycopy(this.xKnots, n + 1, this.xKnots, n, this.numKnots - n - 1);
			System.arraycopy(this.yKnots, n + 1, this.yKnots, n, this.numKnots - n - 1);
		}
		this.numKnots--;
		rebuildGradient();
	}

	public void setKnotPosition(int n, int x) {
		this.xKnots[n] = PixelUtils.clamp(x);
		sortKnots();
		rebuildGradient();
	}

	private void rebuildGradient() {
		this.xKnots[0] = -1;
		this.xKnots[this.numKnots - 1] = 256;
		this.yKnots[0] = this.yKnots[1];
		this.yKnots[this.numKnots - 1] = this.yKnots[this.numKnots - 2];
		for (int i = 0; i < 256; i++) {
			this.map[i] = ImageMath.colorSpline(i, this.numKnots, this.xKnots, this.yKnots);
		}
	}

	private void sortKnots() {
		for (int i = 1; i < this.numKnots; i++) {
			for (int j = 1; j < i; j++) {
				if (this.xKnots[i] < this.xKnots[j]) {
					int t = this.xKnots[i];
					this.xKnots[i] = this.xKnots[j];
					this.xKnots[j] = t;
					t = this.yKnots[i];
					this.yKnots[i] = this.yKnots[j];
					this.yKnots[j] = t;
				}
			}
		}
	}

}
