/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

/**
 * A Filter to invert the alpha channel of an image. This is really only useful for inverting selections, where we only use the alpha channel.
 */
public class InvertAlphaFilter extends PointFilter {

	public InvertAlphaFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		return rgb ^ 0xff000000;
	}

	public String toString() {
		return "Alpha/Invert";
	}
}
