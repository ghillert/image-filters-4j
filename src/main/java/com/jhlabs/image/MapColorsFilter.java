/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter which replaces one color by another in an image. This is frankly, not often useful, but has its occasional
 * uses when dealing with GIF transparency and the like.
 */
public class MapColorsFilter extends PointFilter {

	private final int oldColor;
	private final int newColor;

	public MapColorsFilter(int oldColor, int newColor) {
		this.canFilterIndexColorModel = true;
		this.oldColor = oldColor;
		this.newColor = newColor;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		if (rgb == this.oldColor) {
			return this.newColor;
		}
		return rgb;
	}
}

