/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

/**
 * A filter which 'grays out' an image by averaging each pixel with white.
 */
public class GrayFilter extends PointFilter {

	public GrayFilter() {
		canFilterIndexColorModel = true;
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = (r+255)/2;
		g = (g+255)/2;
		b = (b+255)/2;
		return a | (r << 16) | (g << 8) | b;
	}

	public String toString() {
		return "Colors/Gray Out";
	}

}


