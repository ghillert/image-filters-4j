/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

/**
 * A filter which flips images or rotates by multiples of 90 degrees.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class FlipFilter extends AbstractBufferedImageOp {

	/**
	 * Constant representing a horizontal flip operation.
	 * This value is used to specify that an image should be flipped
	 * along its horizontal axis in the {@code FlipFilter} class.
	 */
	public static final int FLIP_H = 1;

	/**
	 * A constant representing the vertical flip operation, where the image is flipped
	 * along its horizontal axis. This value is used to specify the type of operation
	 * applied by the {@code FlipFilter} class during image processing.
	 */
	public static final int FLIP_V = 2;

	/**
	 * A constant used to specify an image flip operation that combines both horizontal and vertical flips.
	 * When applied, this operation reflects the image across both axes, effectively rotating it
	 * 180 degrees while inverting its orientation.
	 */
	public static final int FLIP_HV = 3;

	/**
	 * Constant representing a 90-degree clockwise image flip operation.
	 * This value can be used to specify the desired flip operation
	 * in the {@link FlipFilter} when manipulating images.
	 */
	public static final int FLIP_90CW = 4;

	/**
	 * A constant representing a 90-degree counterclockwise flip operation.
	 * This value is used to specify the type of flip transformation to be
	 * applied to an image using the FlipFilter class.
	 */
	public static final int FLIP_90CCW = 5;

	/**
	 * A constant representing a 180-degree rotation of an image.
	 * Used as an operation type in the {@code FlipFilter} class to rotate images by 180 degrees.
	 */
	public static final int FLIP_180 = 6;

	private int operation;
	private int width;
	private int height;
	private int newWidth;
	private int newHeight;

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
		return this.operation;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int width = src.getWidth();
		int height = src.getHeight();
		int type = src.getType();
		WritableRaster srcRaster = src.getRaster();

		int[] inPixels = getRGB(src, 0, 0, width, height, null);

		int x = 0;
		int y = 0;
		int w = width;
		int h = height;

		int newX = 0;
		int newY = 0;
		int newW = w;
		int newH = h;
		switch (this.operation) {
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
				switch (this.operation) {
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
						newCol = h - row - 1;
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

		if (dst == null) {
			ColorModel dstCM = src.getColorModel();
			dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(newW, newH), dstCM.isAlphaPremultiplied(), null);
		}
		WritableRaster dstRaster = dst.getRaster();
		setRGB(dst, 0, 0, newW, newH, newPixels);

		return dst;
	}

	@Override
	public String toString() {
		switch (this.operation) {
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
