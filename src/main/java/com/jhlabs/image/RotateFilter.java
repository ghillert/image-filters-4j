/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Point;
import java.awt.Rectangle;

public class RotateFilter extends TransformFilter {

	static final long serialVersionUID = 1166374736665848180L;

	private float angle;
	private float cos;
	private float sin;
	private boolean resize = true;

	public RotateFilter() {
		this(ImageMath.PI);
	}

	public RotateFilter(float angle) {
		this(angle, true);
	}

	public RotateFilter(float angle, boolean resize) {
		setAngle(angle);
		this.resize = resize;
	}

	public void setAngle(float angle) {
		this.angle = angle;
		this.cos = (float) Math.cos(this.angle);
		this.sin = (float) Math.sin(this.angle);
	}

	public float getAngle() {
		return this.angle;
	}

	@Override
	protected void transformSpace(Rectangle rect) {
		if (this.resize) {
			Point out = new Point(0, 0);
			int minx = Integer.MAX_VALUE;
			int miny = Integer.MAX_VALUE;
			int maxx = Integer.MIN_VALUE;
			int maxy = Integer.MIN_VALUE;
			int w = rect.width;
			int h = rect.height;
			int x = rect.x;
			int y = rect.y;

			for (int i = 0; i < 4; i++) {
				switch (i) {
					case 0:
						transform(x, y, out);
						break;
					case 1:
						transform(x + w, y, out);
						break;
					case 2:
						transform(x, y + h, out);
						break;
					case 3:
						transform(x + w, y + h, out);
						break;
				}
				minx = Math.min(minx, out.x);
				miny = Math.min(miny, out.y);
				maxx = Math.max(maxx, out.x);
				maxy = Math.max(maxy, out.y);
			}

			rect.x = minx;
			rect.y = miny;
			rect.width = maxx - rect.x;
			rect.height = maxy - rect.y;
		}
	}

	protected void transform(int x, int y, Point out) {
		out.x = (int) ((x * this.cos) + (y * this.sin));
		out.y = (int) ((y * this.cos) - (x * this.sin));
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x * this.cos) - (y * this.sin);
		out[1] = (y * this.cos) + (x * this.sin);
	}

	@Override
	public String toString() {
		return "Rotate " + (int) (this.angle * 180 / Math.PI);
	}

}
