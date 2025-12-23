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
		return xOffset;
	}
	
	public void setYOffset(int yOffset) {
		this.yOffset = yOffset;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	public void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
	public boolean getWrap() {
		return wrap;
	}
	
	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x+width-xOffset) % width;
		out[1] = (y+height-yOffset) % height;
	}

	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		this.width = width;
		this.height = height;
		while (xOffset < 0)
			xOffset += width;
		while (yOffset < 0)
			yOffset += height;
		xOffset %= width;
		yOffset %= height;
		return super.filterPixels( width, height, inPixels, transformedSpace );
	}

	public String toString() {
		return "Distort/Offset...";
	}
}
