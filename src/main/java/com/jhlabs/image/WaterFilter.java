/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.Point2D;

public class WaterFilter extends TransformFilter {

	static final long serialVersionUID = 8789236343162990941L;
	
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
		setEdgeAction( CLAMP );
	}

	public void setWavelength(float wavelength) {
		this.wavelength = wavelength;
	}

	public float getWavelength() {
		return wavelength;
	}

	public void setAmplitude(float amplitude) {
		this.amplitude = amplitude;
	}

	public float getAmplitude() {
		return amplitude;
	}

	public void setPhase(float phase) {
		this.phase = phase;
	}

	public float getPhase() {
		return phase;
	}

	public void setCentreX( float centreX ) {
		this.centreX = centreX;
	}

	public float getCentreX() {
		return centreX;
	}
	
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return centreY;
	}
	
	public void setCentre( Point2D centre ) {
		this.centreX = (float)centre.getX();
		this.centreY = (float)centre.getY();
	}

	public Point2D getCentre() {
		return new Point2D.Float( centreX, centreY );
	}
	
	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}

	private boolean inside(int v, int a, int b) {
		return a <= v && v <= b;
	}
	
	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		icentreX = width * centreX;
		icentreY = height * centreY;
		if ( radius == 0 )
			radius = Math.min(icentreX, icentreY);
		radius2 = radius*radius;
		return super.filterPixels( width, height, inPixels, transformedSpace );
	}
	
	protected void transformInverse(int x, int y, float[] out) {
		float dx = x-icentreX;
		float dy = y-icentreY;
		float distance2 = dx*dx + dy*dy;
		if (distance2 > radius2) {
			out[0] = x;
			out[1] = y;
		} else {
			float distance = (float)Math.sqrt(distance2);
			float amount = amplitude * (float)Math.sin(distance / wavelength * ImageMath.TWO_PI - phase);
			amount *= (radius-distance)/radius;
			if ( distance != 0 )
				amount *= wavelength/distance;
			out[0] = x + dx*amount;
			out[1] = y + dy*amount;
		}
	}

	public String toString() {
		return "Distort/Water Ripples...";
	}
	
}
