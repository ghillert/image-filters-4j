/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

/**
 * A simple embossing filter. 
 */
public class BumpFilter extends ConvolveFilter {

	static final long serialVersionUID = 2528502820741699111L;
	
	protected static float[] embossMatrix = {
		-1.0f, -1.0f,  0.0f,
		-1.0f,  1.0f,  1.0f,
		 0.0f,  1.0f,  1.0f
	};

	public BumpFilter() {
		super(embossMatrix);
	}

	public String toString() {
		return "Blur/Emboss Edges";
	}
}
