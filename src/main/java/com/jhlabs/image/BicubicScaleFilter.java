/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * Scales an image using bi-cubic interpolation, which can't be done with AffineTransformOp.
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class BicubicScaleFilter extends AbstractBufferedImageOp {

	private final int width;
	private final int height;

	public BicubicScaleFilter() {
		this(32, 32);
	}

	/**
	 * Constructor for a filter which scales the input image to the given width and height using bicubic interpolation.
	 * Unfortunately, it appears that bicubic actually looks worse than bilinear interpolation on most Java implementations,
	 * but you can be the judge.
	 * @param width the width of the output image
	 * @param height the height of the output image
	 */
	public BicubicScaleFilter(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int w = src.getWidth();
		int h = src.getHeight();

		if (dst == null) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
		}

		Graphics2D g = dst.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.drawImage(src, 0, 0, this.width, this.height, null);
		g.dispose();

		return dst;
	}

	@Override
	public String toString() {
		return "Distort/Bicubic Scale";
	}

}
