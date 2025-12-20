/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

/**
 * Applies a bit mask to each ARGB pixel of an image. You can use this for, say, masking out the red channel.
 */
public class MaskFilter extends PointFilter implements Serializable {

	private int mask;

	public MaskFilter() {
		this(0xff00ffff);
	}

	public MaskFilter(int mask) {
		canFilterIndexColorModel = true;
		setMask(mask);
	}

	public void setMask(int mask) {
		this.mask = mask;
	}

	public int getMask() {
		return mask;
	}

	public int filterRGB(int x, int y, int rgb) {
		return rgb & mask;
	}

	public String toString() {
		return "Mask";
	}

}
