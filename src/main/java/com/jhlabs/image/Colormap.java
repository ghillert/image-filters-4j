/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * An interface for color maps.  These are passed to filters which convert gray values to
 * colors. This is similar to the ColorModel class but works with floating point values.
 */
public interface Colormap {
	/**
	 * Convert a value in the range 0..1 to an RGB color.
	 *
	 * @param v a value in the range 0..1
	 * @return an RGB color
	 */
	int getColor(float v);
}

