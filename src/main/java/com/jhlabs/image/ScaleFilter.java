/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Scales an image using the area-averaging algorithm, which can't be done with AffineTransformOp.
 */
public class ScaleFilter extends AbstractBufferedImageOp {

	private final int width;
	private final int height;

	public ScaleFilter() {
		this(32, 32);
	}

	public ScaleFilter(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int w = src.getWidth();
		int h = src.getHeight();

		if (dst == null) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(w, h), dstCM.isAlphaPremultiplied(), null);
		}

		Image scaleImage = src.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
		Graphics2D g = dst.createGraphics();
		g.drawImage(src, 0, 0, this.width, this.height, null);
		g.dispose();

		return dst;
	}

	@Override
	public String toString() {
		return "Distort/Scale";
	}

}
