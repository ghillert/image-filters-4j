/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

public class PinchFilter extends TransformFilter {

	static final long serialVersionUID = -3768964940276766810L;

	public PinchFilter() {
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		int m = this.transformedSpace.width / 2;
		out[0] = x + (int) ((x - m) * Math.sin(Math.PI * (float) y / this.transformedSpace.height));
		out[1] = y;
	}

	@Override
	public String toString() {
		return "Distort/Pinch";
	}

}
