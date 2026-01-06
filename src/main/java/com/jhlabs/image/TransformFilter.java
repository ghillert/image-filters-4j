/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;

/**
 * An abstract superclass for filters which distort images in some way. The subclass only needs to override
 * two methods to provide the mapping between source and destination pixels.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public abstract class TransformFilter extends WholeImageFilter {

	/**
	 * Constant representing the "ZERO" edge action.
	 * <p>
	 * This edge action sets pixels outside the image bounds to black (RGB value 0).
	 * It is primarily used in image transformation filters to handle regions
	 * where sampling pixels exceed the image's dimensions.
	 * <p>
	 * Typically used in conjunction with image distortion or transformation
	 * filters to define how the edges of the image are managed.
	 */
	public static final int ZERO = 0;

	/**
	 * Constant representing the "CLAMP" edge action.
	 * <p>
	 * This edge action clamps the coordinates of samples that are outside the image
	 * boundaries to the edges of the image. It ensures that any pixel values beyond
	 * the bounds of the image are set to the value of the nearest pixel within the image.
	 * <p>
	 * Commonly used in image manipulation and transformation filters to handle boundary
	 * conditions in a way that minimizes visual artifacts caused by out-of-bound sampling.
	 */
	public static final int CLAMP = 1;

	/**
	 * A constant representing the "wrap" edge action. When applying a transformation or filter,
	 * this edge action causes the input image to wrap around at the edges, creating a seamless
	 * appearance as pixels are sampled from the opposite edge.
	 * <p>
	 * Typically used in conjunction with image transformations to handle out-of-bounds
	 * pixel sampling behavior.
	 */
	public static final int WRAP = 2;

	protected int edgeAction = ZERO;

	public void setEdgeAction(int edgeAction) {
		this.edgeAction = edgeAction;
	}

	public int getEdgeAction() {
		return this.edgeAction;
	}

	protected abstract void transformInverse(int x, int y, float[] out);

	@Override
	protected void transformSpace(Rectangle rect) {
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int srcWidth = width;
		int srcHeight = height;
		int outWidth = transformedSpace.width;
		int outHeight = transformedSpace.height;
		int outX;
		int outY;
		int srcX;
		int srcY;
		int index = 0;
		int[] outPixels = new int[outWidth * outHeight];

		outX = transformedSpace.x;
		outY = transformedSpace.y;
		int[] rgb = new int[4];
		float[] out = new float[2];

		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				transformInverse(outX + x, outY + y, out);
				srcX = (int) out[0];
				srcY = (int) out[1];
				// int casting rounds towards zero, so we check out[0] < 0, not srcX < 0
				if (out[0] < 0 || srcX >= srcWidth || out[1] < 0 || srcY >= srcHeight) {
					int p;
					switch (this.edgeAction) {
						case ZERO:
						default:
							p = 0;
							break;
						case WRAP:
							p = inPixels[(ImageMath.mod(srcY, srcHeight) * srcWidth) + ImageMath.mod(srcX, srcWidth)];
							break;
						case CLAMP:
							p = inPixels[(ImageMath.clamp(srcY, 0, srcHeight - 1) * srcWidth) + ImageMath.clamp(srcX, 0, srcWidth - 1)];
							break;
					}
					outPixels[index++] = p;
				}
				else {
					float xWeight = out[0] - srcX;
					float yWeight = out[1] - srcY;
					int i = srcWidth * srcY + srcX;
					int dx = (srcX == (srcWidth - 1)) ? 0 : 1;
					int dy = (srcY == (srcHeight - 1)) ? 0 : srcWidth;
					rgb[0] = inPixels[i];
					rgb[1] = inPixels[i + dx];
					rgb[2] = inPixels[i + dy];
					rgb[3] = inPixels[i + dx + dy];
					outPixels[index++] = ImageMath.bilinearInterpolate(xWeight, yWeight, rgb);
				}
			}
		}
		return outPixels;
	}

}

