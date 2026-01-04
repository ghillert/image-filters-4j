/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

/**
 * A Filter which distorts an image by twisting it from the centre out.
 * The twisting is centred at the centre of the image and extends out to the smallest of
 * the width and height. Pixels outside this radius are unaffected.
 */
public class TwirlFilter extends TransformFilter {

	static final long serialVersionUID = 1550445062822803342L;

	private float angle = 0;
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	private float radius = 0;

	private float radius2 = 0;
	private float icentreX;
	private float icentreY;

	/**
	 * Construct a TwirlFilter with no distortion.
	 */
	public TwirlFilter() {
		setEdgeAction(CLAMP);
	}

	/**
	 * Set the angle of twirl in radians. 0 means no distortion.
	 *
	 * @param angle the angle of twirl. This is the angle by which pixels at the nearest edge of the image will move.
	 */
	public void setAngle(float angle) {
		this.angle = angle;
	}

	/**
	 * Get the angle of twist.
	 *
	 * @return the angle in radians.
	 */
	public float getAngle() {
		return this.angle;
	}

	public void setCentreX(float centreX) {
		this.centreX = centreX;
	}

	public float getCentreX() {
		return this.centreX;
	}

	public void setCentreY(float centreY) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return this.centreY;
	}

	public void setCentre(Point2D centre) {
		this.centreX = (float) centre.getX();
		this.centreY = (float) centre.getY();
	}

	public Point2D getCentre() {
		return new Point2D.Float(this.centreX, this.centreY);
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return this.radius;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		this.icentreX = width * this.centreX;
		this.icentreY = height * this.centreY;
		if (this.radius == 0) {
			this.radius = Math.min(this.icentreX, this.icentreY);
		}
		this.radius2 = this.radius * this.radius;
		return super.filterPixels(width, height, inPixels, transformedSpace);
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		float dx = x - this.icentreX;
		float dy = y - this.icentreY;
		float distance = dx * dx + dy * dy;
		if (distance > this.radius2) {
			out[0] = x;
			out[1] = y;
		}
		else {
			distance = (float) Math.sqrt(distance);
			float a = (float) Math.atan2(dy, dx) + this.angle * (this.radius - distance) / this.radius;
			out[0] = this.icentreX + distance * (float) Math.cos(a);
			out[1] = this.icentreY + distance * (float) Math.sin(a);
		}
	}

	@Override
	public String toString() {
		return "Distort/Twirl...";
	}

}
