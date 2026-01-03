/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter which averages the 3x3 neighbourhood of each pixel, providing a simple blur.
 *
 * @author Jerry Huxtable
 */
public class AverageFilter extends ConvolveFilter {

	protected static float[] theMatrix = {0.1f, 0.1f, 0.1f, 0.1f, 0.2f, 0.1f, 0.1f, 0.1f, 0.1f};

	public AverageFilter() {
		super(theMatrix);
	}

	@Override
	public String toString() {
		return "Blur/Average Blur";
	}
}
