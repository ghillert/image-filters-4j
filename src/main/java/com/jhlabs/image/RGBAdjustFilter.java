/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class RGBAdjustFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 3509907597266563800L;
	
	public float rFactor, gFactor, bFactor;

	public RGBAdjustFilter() {
		this(0, 0, 0);
	}

	public RGBAdjustFilter(float r, float g, float b) {
		rFactor = 1+r;
		gFactor = 1+g;
		bFactor = 1+b;
		canFilterIndexColorModel = true;
	}

	public void setRFactor( float rFactor ) {
		this.rFactor = 1+rFactor;
	}
	
	public float getRFactor() {
		return rFactor-1;
	}
	
	public void setGFactor( float gFactor ) {
		this.gFactor = 1+gFactor;
	}
	
	public float getGFactor() {
		return gFactor-1;
	}
	
	public void setBFactor( float bFactor ) {
		this.bFactor = 1+bFactor;
	}
	
	public float getBFactor() {
		return bFactor-1;
	}

	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = PixelUtils.clamp((int)(r * rFactor));
		g = PixelUtils.clamp((int)(g * gFactor));
		b = PixelUtils.clamp((int)(b * bFactor));
		return a | (r << 16) | (g << 8) | b;
	}

	public String toString() {
		return "Colors/Adjust RGB...";
	}
}

