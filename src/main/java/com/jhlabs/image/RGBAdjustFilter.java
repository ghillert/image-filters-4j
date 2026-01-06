/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter that adjusts the red, green, and blue components of an image.
 * The adjustment is applied by scaling each color channel by a specified factor.
 * <p>
 * This filter extends the {@code PointFilter} class, allowing for point-by-point
 * modification of image pixels.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class RGBAdjustFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 3509907597266563800L;

	private float rFactor;
	private float gFactor;
	private float bFactor;

	public RGBAdjustFilter() {
		this(0, 0, 0);
	}

	public RGBAdjustFilter(float r, float g, float b) {
		this.rFactor = 1 + r;
		this.gFactor = 1 + g;
		this.bFactor = 1 + b;
		this.canFilterIndexColorModel = true;
	}

	public void setRFactor(float rFactor) {
		this.rFactor = 1 + rFactor;
	}

	public float getRFactor() {
		return this.rFactor - 1;
	}

	public void setGFactor(float gFactor) {
		this.gFactor = 1 + gFactor;
	}

	public float getGFactor() {
		return this.gFactor - 1;
	}

	public void setBFactor(float bFactor) {
		this.bFactor = 1 + bFactor;
	}

	public float getBFactor() {
		return this.bFactor - 1;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = PixelUtils.clamp((int) (r * this.rFactor));
		g = PixelUtils.clamp((int) (g * this.gFactor));
		b = PixelUtils.clamp((int) (b * this.bFactor));
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Colors/Adjust RGB...";
	}
}

