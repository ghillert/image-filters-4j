/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class TileImageFilter extends AbstractBufferedImageOp implements java.io.Serializable {

	static final long serialVersionUID = 4926390225069192478L;

	public static final int FLIP_NONE = 0;
	public static final int FLIP_H = 1;
	public static final int FLIP_V = 2;
	public static final int FLIP_HV = 3;
	public static final int FLIP_180 = 4;

	private int width;
	private int height;
	private int tileWidth;
	private int tileHeight;

	public TileImageFilter() {
		this(32, 32);
	}

	public TileImageFilter(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return this.width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getHeight() {
		return this.height;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int tileWidth = src.getWidth();
		int tileHeight = src.getHeight();

		if (dst == null) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(this.width, this.height), dstCM.isAlphaPremultiplied(), null);
		}

		Graphics2D g = dst.createGraphics();
		for (int y = 0; y < this.height; y += tileHeight) {
			for (int x = 0; x < this.width; x += tileWidth) {
				g.drawImage(src, null, x, y);
			}
		}
		g.dispose();

		return dst;
	}

	private int[][] symmetryMatrix = null;
	private int symmetryRows = 2, symmetryCols = 2;

	public void setSymmetryMatrix(int[][] symmetryMatrix) {
		this.symmetryMatrix = symmetryMatrix;
		this.symmetryRows = symmetryMatrix.length;
		this.symmetryCols = symmetryMatrix[0].length;
	}

	public int[][] getSymmetryMatrix() {
		return this.symmetryMatrix;
	}

	@Override
	public String toString() {
		return "Tile";
	}
}
