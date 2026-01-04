/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class SphereFilter extends TransformFilter {

	static final long serialVersionUID = -8148404526162968279L;

	private float a = 0;
	private float b = 0;
	private float a2 = 0;
	private float b2 = 0;
	private float centreX = 0.5f;
	private float centreY = 0.5f;
	private float refractionIndex = 1.5f;

	private float icentreX;
	private float icentreY;

	public SphereFilter() {
		setEdgeAction(CLAMP);
	}

	public void setRefractionIndex(float refractionIndex) {
		this.refractionIndex = refractionIndex;
	}

	public float getRefractionIndex() {
		return this.refractionIndex;
	}

	public void setRadius(float r) {
		this.a = r;
		this.b = r;
	}

	public float getRadius() {
		return this.a;
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

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		this.icentreX = width * this.centreX;
		this.icentreY = height * this.centreY;
		if (this.a == 0) {
			this.a = width / 2;
		}
		if (this.b == 0) {
			this.b = height / 2;
		}
		this.a2 = this.a * this.a;
		this.b2 = this.b * this.b;
		return super.filterPixels(width, height, inPixels, transformedSpace);
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		float dx = x - this.icentreX;
		float dy = y - this.icentreY;
		float x2 = dx * dx;
		float y2 = dy * dy;
		if (y2 >= (this.b2 - (this.b2 * x2) / this.a2)) {
			out[0] = x;
			out[1] = y;
		}
		else {
			float rRefraction = 1.0f / this.refractionIndex;

			float z = (float) Math.sqrt((1.0f - x2 / this.a2 - y2 / this.b2) * (this.a * this.b));
			float z2 = z * z;

			float xAngle = (float) Math.acos(dx / Math.sqrt(x2 + z2));
			float angle1 = ImageMath.HALF_PI - xAngle;
			float angle2 = (float) Math.asin(Math.sin(angle1) * rRefraction);
			angle2 = ImageMath.HALF_PI - xAngle - angle2;
			out[0] = x - (float) Math.tan(angle2) * z;

			float yAngle = (float) Math.acos(dy / Math.sqrt(y2 + z2));
			angle1 = ImageMath.HALF_PI - yAngle;
			angle2 = (float) Math.asin(Math.sin(angle1) * rRefraction);
			angle2 = ImageMath.HALF_PI - yAngle - angle2;
			out[1] = y - (float) Math.tan(angle2) * z;
		}
	}

	@Override
	public String toString() {
		return "Distort/Sphere...";
	}

}
