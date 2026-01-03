/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter which inverts the RGB channels of an image.
 */
public class InvertFilter extends PointFilter {

	public InvertFilter() {
		this.canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		return a | (~rgb & 0x00ffffff);
	}

	@Override
	public String toString() {
		return "Colors/Invert";
	}
}

