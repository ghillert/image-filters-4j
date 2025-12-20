/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;
import com.jhlabs.math.*;

/**
 * This filter applies a marbling effect to an image, displacing pixels by random amounts.
 */
public class MarbleFilter extends TransformFilter {

	public float[] sinTable, cosTable;
	public float xScale = 4;
	public float yScale = 4;
	public float amount = 1;
	public float turbulence = 1;
	
	public MarbleFilter() {
		setEdgeAction(CLAMP);
	}
	
	public void setXScale(float xScale) {
		this.xScale = xScale;
	}

	public float getXScale() {
		return xScale;
	}

	public void setYScale(float yScale) {
		this.yScale = yScale;
	}

	public float getYScale() {
		return yScale;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getAmount() {
		return amount;
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return turbulence;
	}

	private void initialize() {
		sinTable = new float[256];
		cosTable = new float[256];
		for (int i = 0; i < 256; i++) {
			float angle = ImageMath.TWO_PI*i/256f*turbulence;
			sinTable[i] = (float)(-yScale*Math.sin(angle));
			cosTable[i] = (float)(yScale*Math.cos(angle));
		}
	}

	private int displacementMap(int x, int y) {
		return PixelUtils.clamp((int)(127 * (1+Noise.noise2(x / xScale, y / xScale))));
	}
	
	protected void transformInverse(int x, int y, float[] out) {
		int displacement = displacementMap(x, y);
		out[0] = x + sinTable[displacement];
		out[1] = y + cosTable[displacement];
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		initialize();
		return super.filterPixels( width, height, inPixels, transformedSpace );
	}

	public String toString() {
		return "Distort/Marble...";
	}
}
