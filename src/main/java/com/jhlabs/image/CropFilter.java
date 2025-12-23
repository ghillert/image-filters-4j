/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

/**
 * A filter which crops an image to a given rectangle.
 */
public class CropFilter extends AbstractBufferedImageOp {

	private int x;
	private int y;
	private int width;
	private int height;

	public CropFilter() {
		this(0, 0, 32, 32);
	}

	public CropFilter(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int w = src.getWidth();
        int h = src.getHeight();

        if ( dst == null ) {
            ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
		}

		Graphics2D g = dst.createGraphics();
		g.drawRenderedImage( src, AffineTransform.getTranslateInstance(-x, -y) );
		g.dispose();

        return dst;
    }

	public String toString() {
		return "Distort/Crop";
	}
}
