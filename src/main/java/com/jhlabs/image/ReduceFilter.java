/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

public class ReduceFilter extends PointFilter implements java.io.Serializable {

	private int numLevels;
	private int[] levels;
	private boolean initialized = false;

	public ReduceFilter() {
		setNumLevels(6);
	}

	public void setNumLevels(int numLevels) {
		this.numLevels = numLevels;
		this.initialized = false;
	}

	public int getNumLevels() {
		return this.numLevels;
	}

	protected void initialize() {
		this.levels = new int[256];
		if (this.numLevels != 1) {
			for (int i = 0; i < 256; i++) {
				this.levels[i] = 255 * (this.numLevels * i / 256) / (this.numLevels - 1);
			}
		}
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		if (!this.initialized) {
			this.initialized = true;
			initialize();
		}
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = this.levels[r];
		g = this.levels[g];
		b = this.levels[b];
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Colors/Posterize...";
	}

}

