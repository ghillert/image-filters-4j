/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;

public class OffsetFilter extends TransformFilter implements java.io.Serializable {

	static final long serialVersionUID = 8123120922961090736L;

	private int width, height;
	private int xOffset, yOffset;
	private boolean wrap;

	public OffsetFilter() {
		this(0, 0, true);
	}

	public OffsetFilter(int xOffset, int yOffset, boolean wrap) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.wrap = wrap;
	}

	public void setXOffset(int xOffset) {
		this.xOffset = xOffset;
	}

	public int getXOffset() {
		return this.xOffset;
	}

	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}

	public int getYOffset() {
		return this.yOffset;
	}

	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}

	public boolean getWrap() {
		return this.wrap;
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x + this.width - this.xOffset) % this.width;
		out[1] = (y + this.height - this.yOffset) % this.height;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		this.width = width;
		this.height = height;
		while (this.xOffset < 0) {
			this.xOffset += width;
		}
		while (this.yOffset < 0) {
			this.yOffset += height;
		}
		this.xOffset %= width;
		this.yOffset %= height;
		return super.filterPixels(width, height, inPixels, transformedSpace);
	}

	@Override
	public String toString() {
		return "Distort/Offset...";
	}
}
