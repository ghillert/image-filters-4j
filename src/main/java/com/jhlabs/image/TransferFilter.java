/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImage;

public abstract class TransferFilter extends PointFilter {

	protected int[] rTable;
	protected int[] gTable;
	protected int[] bTable;
	protected boolean initialized = false;

	public TransferFilter() {
		this.canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = this.rTable[r];
		g = this.gTable[g];
		b = this.bTable[b];
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		if (!this.initialized) {
			initialize();
			this.initialized = true;
		}
		return super.filter(src, dst);
	}

	protected void initialize() {
		this.rTable = this.gTable = this.bTable = makeTable();
	}

	protected int[] makeTable() {
		int[] table = new int[256];
		for (int i = 0; i < 256; i++) {
			table[i] = PixelUtils.clamp((int) (255 * transferFunction(i / 255.0f)));
		}
		return table;
	}

	protected float transferFunction(float v) {
		return 0;
	}

}

