/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.image.*;

/**
 * A simple blur filter. You should probably use BoxBlurFilter instead.
 */
public class BlurFilter extends ConvolveFilter {

	static final long serialVersionUID = -4753886159026796838L;
 	
 	protected static float[] blurMatrix = {
		1/14f, 2/14f, 1/14f,
		2/14f, 2/14f, 2/14f,
		1/14f, 2/14f, 1/14f
	};

	public BlurFilter() {
		super( blurMatrix );
	}

	public String toString() {
		return "Blur/Simple Blur";
	}
}
