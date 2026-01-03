/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

public class ThresholdFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = -1899610620205446828L;

	private int lowerThreshold;
	private int lowerThreshold3;
	private int upperThreshold;
	private int upperThreshold3;
	private int white = 0xffffff;
	private int black = 0x000000;

	public ThresholdFilter() {
		this(127);
	}

	public ThresholdFilter(int t) {
		setLowerThreshold(t);
		setUpperThreshold(t);
	}

	public void setLowerThreshold(int lowerThreshold) {
		this.lowerThreshold = lowerThreshold;
		this.lowerThreshold3 = lowerThreshold * 3;
	}

	public int getLowerThreshold() {
		return this.lowerThreshold;
	}

	public void setUpperThreshold(int upperThreshold) {
		this.upperThreshold = upperThreshold;
		this.upperThreshold3 = upperThreshold * 3;
	}

	public int getUpperThreshold() {
		return this.upperThreshold;
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
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int l = r + g + b;
		if (l < this.lowerThreshold3) {
			return a | this.black;
		}
		else if (l > this.upperThreshold3) {
			return a | this.white;
		}
		return rgb;
	}

	@Override
	public String toString() {
		return "Stylize/Threshold...";
	}
}
