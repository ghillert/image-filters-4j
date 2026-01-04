/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * FadeFilter is a subclass of PointFilter that applies a fading effect to an image based on specified parameters.
 * The filter adjusts the alpha channel of each pixel to create a fade effect, which can be customized with various properties.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class FadeFilter extends PointFilter {

	private int width;
	private int height;
	private float angle = 0.0f;
	private float fadeStart = 1.0f;
	private float fadeWidth = 10.0f;
	private int sides;
	private boolean invert;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;

	public void setAngle(float angle) {
		this.angle = angle;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setSides(int sides) {
		this.sides = sides;
	}

	public int getSides() {
		return this.sides;
	}

	public void setFadeStart(float fadeStart) {
		this.fadeStart = fadeStart;
	}

	public float getFadeStart() {
		return this.fadeStart;
	}

	public void setFadeWidth(float fadeWidth) {
		this.fadeWidth = fadeWidth;
	}

	public float getFadeWidth() {
		return this.fadeWidth;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	public boolean getInvert() {
		return this.invert;
	}

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		super.setDimensions(width, height);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float nx = this.m00 * x + this.m01 * y;
		float ny = this.m10 * x + this.m11 * y;
		if (this.sides == 2) {
			nx = (float) Math.sqrt(nx * nx + ny * ny);
		}
		else if (this.sides == 3) {
			nx = ImageMath.mod(nx, 16);
		}
		else if (this.sides == 4) {
			nx = symmetry(nx, 16);
		}
		int alpha = (int) (ImageMath.smoothStep(this.fadeStart, this.fadeStart + this.fadeWidth, nx) * 255);
		if (this.invert) {
			alpha = 255 - alpha;
		}
		return (alpha << 24) | (rgb & 0x00ffffff);
	}

	public float symmetry(float x, float b) {
/*
		int d = (int)(x / b);
		x = ImageMath.mod(x, b);
		if ((d & 1) == 1)
			return b-x;
		return x;
*/
		x = ImageMath.mod(x, 2 * b);
		if (x > b) {
			return 2 * b - x;
		}
		return x;
	}
	
/*
	public float star(float x, float y, int sides, float rMin, float rMax) {
		float sideAngle = 2*Math.PI / sides;
		float angle = Math.atan2(y, x);
		float r = Math.sqrt(x*x + y*y);
		float t = ImageMath.mod(angle, sideAngle) / sideAngle;
		if (t > 0.5)
			t = 1.0-t;
	}
*/

	@Override
	public String toString() {
		return "Fade...";
	}

}

