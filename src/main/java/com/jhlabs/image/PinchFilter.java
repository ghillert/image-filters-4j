/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class PinchFilter extends TransformFilter {

	static final long serialVersionUID = -3768964940276766810L;
	
	public PinchFilter() {
	}

	protected void transformInverse(int x, int y, float[] out) {
		int m = transformedSpace.width/2;
		out[0] = x + (int)((x-m)*Math.sin(Math.PI*(float)y/transformedSpace.height));
		out[1] = y;
	}

	public String toString() {
		return "Distort/Pinch";
	}

}
