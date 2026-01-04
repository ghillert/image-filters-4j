/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A Filter to invert the alpha channel of an image. This is really only useful for inverting selections, where we only use the alpha channel.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class InvertAlphaFilter extends PointFilter {

	public InvertAlphaFilter() {
		this.canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		return rgb ^ 0xff000000;
	}

	@Override
	public String toString() {
		return "Alpha/Invert";
	}
}
