/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class StampFilter extends PointFilter {

	private float threshold;
	private float softness = 0;
	protected float radius = 5;
	private float lowerThreshold3;
	private float upperThreshold3;
	private int white = 0xffffffff;
	private int black = 0xff000000;

	public StampFilter() {
		this(0.5f);
	}

	public StampFilter(float threshold) {
		setThreshold(threshold);
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return this.radius;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return this.threshold;
	}

	public void setSoftness(float softness) {
		this.softness = softness;
	}

	public float getSoftness() {
		return this.softness;
	}

	public void setWhite(int white) {
		this.white = white;
	}

	public int getWhite() {
		return this.white;
	}

	public void setBlack(int black) {
		this.black = black;
	}

	public int getBlack() {
		return this.black;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		dst = new GaussianFilter((int) this.radius).filter(src, null);
		this.lowerThreshold3 = 255 * 3 * (this.threshold - this.softness * 0.5f);
		this.upperThreshold3 = 255 * 3 * (this.threshold + this.softness * 0.5f);
		return super.filter(dst, dst);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int l = r + g + b;
		float f = ImageMath.smoothStep(this.lowerThreshold3, this.upperThreshold3, l);
		return ImageMath.mixColors(f, this.black, this.white);
	}

	@Override
	public String toString() {
		return "Stylize/Stamp...";
	}
}
