/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter that applies an oval shape to an image, setting pixels outside the oval to transparent.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class OvalFilter extends PointFilter {

	private float centreX = 0;
	private float centreY = 0;
	private float a = 0;
	private float b = 0;
	private float a2 = 0;
	private float b2 = 0;

	public OvalFilter() {
	}

	@Override
	public void setDimensions(int width, int height) {
		super.setDimensions(width, height);
		this.centreX = this.a = width / 2;
		this.centreY = this.b = height / 2;
		this.a2 = this.a * this.a;
		this.b2 = this.b * this.b;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float dx = x - this.centreX;
		float dy = y - this.centreY;
		float x2 = dx * dx;
		float y2 = dy * dy;
		if (y2 >= (this.b2 - (this.b2 * x2) / this.a2)) {
			return 0x00000000;
		}
		return rgb;
	}

	@Override
	public String toString() {
		return "Stylize/Oval...";
	}

}
