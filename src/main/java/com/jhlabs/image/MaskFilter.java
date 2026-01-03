/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.io.Serializable;

/**
 * Applies a bit mask to each ARGB pixel of an image. You can use this for, say, masking out the red channel.
 */
public class MaskFilter extends PointFilter implements Serializable {

	private int mask;

	public MaskFilter() {
		this(0xff00ffff);
	}

	public MaskFilter(int mask) {
		this.canFilterIndexColorModel = true;
		setMask(mask);
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return this.mask;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		return rgb & this.mask;
	}

	@Override
	public String toString() {
		return "Mask";
	}

}
