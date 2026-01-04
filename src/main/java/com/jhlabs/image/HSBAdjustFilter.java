/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Color;

/**
 * A filter which adjusts the hue, saturation and brightness of an image.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class HSBAdjustFilter extends PointFilter {

	public float hFactor;
	public float sFactor;
	public float bFactor;
	private final float[] hsb = new float[3];

	public HSBAdjustFilter() {
		this(0, 0, 0);
	}

	public HSBAdjustFilter(float r, float g, float b) {
		this.hFactor = r;
		this.sFactor = g;
		this.bFactor = b;
		this.canFilterIndexColorModel = true;
	}

	public void setHFactor(float hFactor) {
		this.hFactor = hFactor;
	}

	public float getHFactor() {
		return this.hFactor;
	}

	public void setSFactor(float sFactor) {
		this.sFactor = sFactor;
	}

	public float getSFactor() {
		return this.sFactor;
	}

	public void setBFactor(float bFactor) {
		this.bFactor = bFactor;
	}

	public float getBFactor() {
		return this.bFactor;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		Color.RGBtoHSB(r, g, b, this.hsb);
		this.hsb[0] += this.hFactor;
		while (this.hsb[0] < 0) {
			this.hsb[0] += Math.PI * 2;
		}
		this.hsb[1] += this.sFactor;
		if (this.hsb[1] < 0) {
			this.hsb[1] = 0;
		}
		else if (this.hsb[1] > 1.0) {
			this.hsb[1] = 1.0f;
		}
		this.hsb[2] += this.bFactor;
		if (this.hsb[2] < 0) {
			this.hsb[2] = 0;
		}
		else if (this.hsb[2] > 1.0) {
			this.hsb[2] = 1.0f;
		}
		rgb = Color.HSBtoRGB(this.hsb[0], this.hsb[1], this.hsb[2]);
		return a | (rgb & 0xffffff);
	}

	@Override
	public String toString() {
		return "Colors/Adjust HSB...";
	}
}

