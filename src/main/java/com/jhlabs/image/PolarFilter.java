/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;

public class PolarFilter extends TransformFilter {

	public final static int RECT_TO_POLAR = 0;
	public final static int POLAR_TO_RECT = 1;
	public final static int INVERT_IN_CIRCLE = 2;

	private int type;
	private float width, height;
	private float centreX, centreY;
	private float radius;

	public PolarFilter() {
		this(RECT_TO_POLAR);
	}

	public PolarFilter(int type) {
		this.type = type;
		setEdgeAction(CLAMP);
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		this.width = width;
		this.height = height;
		this.centreX = width / 2;
		this.centreY = height / 2;
		this.radius = Math.max(this.centreY, this.centreX);
		return super.filterPixels(width, height, inPixels, transformedSpace);
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getType() {
		return this.type;
	}

	private float sqr(float x) {
		return x * x;
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		float theta, t;
		float m, xmax, ymax;
		float r = 0;

		switch (this.type) {
			case RECT_TO_POLAR:
				theta = 0;
				if (x >= this.centreX) {
					if (y > this.centreY) {
						theta = ImageMath.PI - (float) Math.atan((x - this.centreX) / (y - this.centreY));
						r = (float) Math.sqrt(sqr(x - this.centreX) + sqr(y - this.centreY));
					}
					else if (y < this.centreY) {
						theta = (float) Math.atan((x - this.centreX) / (this.centreY - y));
						r = (float) Math.sqrt(sqr(x - this.centreX) + sqr(this.centreY - y));
					}
					else {
						theta = ImageMath.HALF_PI;
						r = x - this.centreX;
					}
				}
				else if (x < this.centreX) {
					if (y < this.centreY) {
						theta = ImageMath.TWO_PI - (float) Math.atan((this.centreX - x) / (this.centreY - y));
						r = (float) Math.sqrt(sqr(this.centreX - x) + sqr(this.centreY - y));
					}
					else if (y > this.centreY) {
						theta = ImageMath.PI + (float) Math.atan((this.centreX - x) / (y - this.centreY));
						r = (float) Math.sqrt(sqr(this.centreX - x) + sqr(y - this.centreY));
					}
					else {
						theta = 1.5f * ImageMath.PI;
						r = this.centreX - x;
					}
				}
				if (x != this.centreX) {
					m = Math.abs((y - this.centreY) / (x - this.centreX));
				}
				else {
					m = 0;
				}

				if (m <= (this.height / this.width)) {
					if (x == this.centreX) {
						xmax = 0;
						ymax = this.centreY;
					}
					else {
						xmax = this.centreX;
						ymax = m * xmax;
					}
				}
				else {
					ymax = this.centreY;
					xmax = ymax / m;
				}

				out[0] = (this.width - 1) - (this.width - 1) / ImageMath.TWO_PI * theta;
				out[1] = this.height * r / this.radius;
				break;
			case POLAR_TO_RECT:
				theta = x / this.width * ImageMath.TWO_PI;
				float theta2;

				if (theta >= 1.5f * ImageMath.PI) {
					theta2 = ImageMath.TWO_PI - theta;
				}
				else if (theta >= ImageMath.PI) {
					theta2 = theta - ImageMath.PI;
				}
				else if (theta >= 0.5f * ImageMath.PI) {
					theta2 = ImageMath.PI - theta;
				}
				else {
					theta2 = theta;
				}

				t = (float) Math.tan(theta2);
				if (t != 0) {
					m = 1.0f / t;
				}
				else {
					m = 0;
				}

				if (m <= (this.height / this.width)) {
					if (theta2 == 0) {
						xmax = 0;
						ymax = this.centreY;
					}
					else {
						xmax = this.centreX;
						ymax = m * xmax;
					}
				}
				else {
					ymax = this.centreY;
					xmax = ymax / m;
				}

				r = this.radius * (y / this.height);

				float nx = -r * (float) Math.sin(theta2);
				float ny = r * (float) Math.cos(theta2);

				if (theta >= 1.5f * ImageMath.PI) {
					out[0] = this.centreX - nx;
					out[1] = this.centreY - ny;
				}
				else if (theta >= Math.PI) {
					out[0] = this.centreX - nx;
					out[1] = this.centreY + ny;
				}
				else if (theta >= 0.5 * Math.PI) {
					out[0] = this.centreX + nx;
					out[1] = this.centreY + ny;
				}
				else {
					out[0] = this.centreX + nx;
					out[1] = this.centreY - ny;
				}
				break;
			case INVERT_IN_CIRCLE:
				float dx = x - this.centreX;
				float dy = y - this.centreY;
				float distance2 = dx * dx + dy * dy;
				out[0] = this.centreX + this.centreX * this.centreX * dx / distance2;
				out[1] = this.centreY + this.centreY * this.centreY * dy / distance2;
				break;
		}
	}

	@Override
	public String toString() {
		return "Distort/Polar Coordinates...";
	}

}
