/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serial;

/**
 * The {@code WaterFilter} class is a filter that simulates a water ripple effect
 * on an image. It extends the {@code TransformFilter} class and provides options
 * to configure the wavelength, amplitude, phase, and center of the water ripples.
 * <p>
 * This filter creates a visual effect of water waves by distorting the image
 * based on the specified parameters.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class WaterFilter extends TransformFilter {

	@Serial
	private static final long serialVersionUID = 8789236343162990941L;

	private float wavelength = 16;
	private float amplitude = 10;
	private float phase = 0;
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	private float radius = 0;

	private float radius2 = 0;
	private float icentreX;
	private float icentreY;

	public WaterFilter() {
		setEdgeAction(CLAMP);
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public float getWavelength() {
		return this.wavelength;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

	public float getAmplitude() {
		return this.amplitude;
	}

	public void setPhase(float phase) {
		this.phase = phase;
	}

	public float getPhase() {
		return this.phase;
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

	private boolean inside(int v, int a, int b) {
		return a <= v && v <= b;
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
		float distance2 = dx * dx + dy * dy;
		if (distance2 > this.radius2) {
			out[0] = x;
			out[1] = y;
		}
		else {
			float distance = (float) Math.sqrt(distance2);
			float amount = this.amplitude * (float) Math.sin(distance / this.wavelength * ImageMath.TWO_PI - this.phase);
			amount *= (this.radius - distance) / this.radius;
			if (distance != 0) {
				amount *= this.wavelength / distance;
			}
			out[0] = x + dx * amount;
			out[1] = y + dy * amount;
		}
	}

	@Override
	public String toString() {
		return "Distort/Water Ripples...";
	}

}
