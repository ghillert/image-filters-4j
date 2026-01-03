/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

public class SharpenFilter extends ConvolveFilter {

	static final long serialVersionUID = -4883137561307845895L;

	protected static float[] sharpenMatrix = {
			0.0f, -0.2f, 0.0f,
			-0.2f, 1.8f, -0.2f,
			0.0f, -0.2f, 0.0f
	};

	public SharpenFilter() {
		super(sharpenMatrix);
	}

	@Override
	public String toString() {
		return "Blur/Sharpen";
	}
}
