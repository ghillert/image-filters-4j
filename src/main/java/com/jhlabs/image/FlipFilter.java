/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.image.*;

/**
 * A filter which flips images or rotates by multiples of 90 degrees.
 */
public class FlipFilter extends AbstractBufferedImageOp {

	public static final int FLIP_H = 1;
	public static final int FLIP_V = 2;
	public static final int FLIP_HV = 3;
	public static final int FLIP_90CW = 4;
	public static final int FLIP_90CCW = 5;
	public static final int FLIP_180 = 6;

	private int operation;
	private int width, height;
	private int newWidth, newHeight;

	public FlipFilter() {
		this(FLIP_HV);
	}

	public FlipFilter(int operation) {
		this.operation = operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public int getOperation() {
		return operation;
	}

    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        int width = src.getWidth();
        int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

		int[] inPixels = getRGB( src, 0, 0, width, height, null );

		int x = 0, y = 0;
		int w = width;
		int h = height;

		int newX = 0;
		int newY = 0;
		int newW = w;
		int newH = h;
		switch (operation) {
		case FLIP_H:
			newX = width - (x + w);
			break;
		case FLIP_V:
			newY = height - (y + h);
			break;
		case FLIP_HV:
			newW = h;
			newH = w;
			newX = y;
			newY = x;
			break;
		case FLIP_90CW:
			newW = h;
			newH = w;
			newX = height - (y + h);
			newY = x;
			break;
		case FLIP_90CCW:
			newW = h;
			newH = w;
			newX = y;
			newY = width - (x + w);
			break;
		case FLIP_180:
			newX = width - (x + w);
			newY = height - (y + h);
			break;
		}

		int[] newPixels = new int[newW * newH];

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				int index = row * width + col;
				int newRow = row;
				int newCol = col;
				switch (operation) {
				case FLIP_H:
					newCol = w - col - 1;
					break;
				case FLIP_V:
					newRow = h - row - 1;
					break;
				case FLIP_HV:
					newRow = col;
					newCol = row;
					break;
				case FLIP_90CW:
					newRow = col;
					newCol = h - row - 1;;
					break;
				case FLIP_90CCW:
					newRow = w - col - 1;
					newCol = row;
					break;
				case FLIP_180:
					newRow = h - row - 1;
					newCol = w - col - 1;
					break;
				}
				int newIndex = newRow * newW + newCol;
				newPixels[newIndex] = inPixels[index];
			}
		}

        if ( dst == null ) {
            ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(newW, newH), dstCM.isAlphaPremultiplied(), null);
		}
		WritableRaster dstRaster = dst.getRaster();
		setRGB( dst, 0, 0, newW, newH, newPixels );

        return dst;
    }

	public String toString() {
		switch (operation) {
		case FLIP_H:
			return "Flip Horizontal";
		case FLIP_V:
			return "Flip Vertical";
		case FLIP_HV:
			return "Flip Diagonal";
		case FLIP_90CW:
			return "Rotate 90";
		case FLIP_90CCW:
			return "Rotate -90";
		case FLIP_180:
			return "Rotate 180";
		}
		return "Flip";
	}
}
