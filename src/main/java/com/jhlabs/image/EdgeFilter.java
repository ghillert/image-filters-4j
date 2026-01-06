/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;


import java.awt.Rectangle;
import java.io.Serial;

/**
 * An edge-detection filter.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class EdgeFilter extends WholeImageFilter {

	@Serial
	static final long serialVersionUID = -1084121755410916989L;

	/**
	 * A constant representing the square root of 2 as a single-precision float.
	 */
	public static final float R2 = (float) Math.sqrt(2);

	/**
	 * A kernel matrix representing the vertical component of the Roberts cross-edge detection filter.
	 * This filter highlights edges in an image by detecting changes in intensity in the vertical direction.
	 * The matrix is a 3x3 convolution kernel.
	 */
	public static final float[] ROBERTS_V = {
			0, 0, -1,
			0, 1, 0,
			0, 0, 0,
	};

	/**
	 * A 3x3 convolution kernel representing the horizontal component of the Roberts cross-operator.
	 * The Roberts cross operator is used for edge detection in image processing. This matrix
	 * specifically highlights horizontal edges by applying a convolution operation with the image.
	 */
	public static final float[] ROBERTS_H = {
			-1, 0, 0,
			0, 1, 0,
			0, 0, 0,
	};

	/**
	 * A vertical Prewitt edge detection matrix. The kernel is used for detecting
	 * vertical edges in an image by performing a convolution operation. It emphasizes
	 * changes in pixel intensity in the vertical direction.
	 * <p>
	 * The matrix is represented as a 3x3 kernel with the following values:
	 * <pre>
	 * -1  0  1
	 * -1  0  1
	 * -1  0  1
	 * </pre>
	 * This matrix can be used in conjunction with horizontal edge detection
	 * matrices to detect edges in both directions.
	 */
	public static final float[] PREWITT_V = {
			-1, 0, 1,
			-1, 0, 1,
			-1, 0, 1,
	};

	/**
	 * A 3x3 kernel representing the horizontal component of the Prewitt operator.
	 * The Prewitt operator is a gradient-based edge detection method used in image
	 * processing. It emphasizes horizontal edges by approximating the gradient
	 * along the vertical direction of an image. This kernel is convolved with the
	 * image to detect horizontal features.
	 */
	public static final float[] PREWITT_H = {
			-1, -1, -1,
			0, 0, 0,
			1, 1, 1,
	};

	/**
	 * A predefined vertical Sobel filter kernel used for edge detection in image processing.
	 * The kernel emphasizes vertical edges by calculating the gradient in the vertical direction.
	 * It is a 3x3 matrix often used in convolution operations on image data.
	 */
	public static final float[] SOBEL_V = {
			-1, 0, 1,
			-2, 0, 2,
			-1, 0, 1,
	};

	/**
	 * A 3x3 kernel matrix representing the horizontal Sobel operator used for edge detection
	 * in image processing. The Sobel operator emphasizes horizontal edges by calculating
	 * the gradient of pixel intensity in the horizontal direction. Positive values correspond
	 * to brighter regions transitioning to darker, while negative values indicate the reverse.
	 */
	public static float[] SOBEL_H = {
			-1, -2, -1,
			0, 0, 0,
			1, 2, 1,
	};

	/**
	 * The FREI_CHEN_V variable represents a constant 3x3 matrix used in image processing
	 * for detecting vertical edges. It is a component of the Frei-Chen operator, which is
	 * an edge detection technique that improves results over simpler gradient-based methods
	 * by balancing sensitivity to both noise and edge orientation.
	 * <p>
	 * The matrix values are based on a specific pattern designed to emphasize vertical
	 * edge features in an image, with weights at each position contributing to the edge
	 * detection computation.
	 */
	public static final float[] FREI_CHEN_V = {
			-1, 0, 1,
			-R2, 0, R2,
			-1, 0, 1,
	};

	/**
	 * A 3x3 filter kernel representing the horizontal component of the Frei-Chen edge detection operator.
	 * This operator emphasizes horizontal edges in an image by applying a weighted combination of pixel intensities
	 * to calculate the horizontal gradient. The kernel values are based on the Frei-Chen model, which uses square root
	 * coefficients for improved precision in edge detection compared to other simpler methods.
	 */
	public static float[] FREI_CHEN_H = {
			-1, -R2, -1,
			0, 0, 0,
			1, R2, 1,
	};

	protected float[] vEdgeMatrix = SOBEL_V;
	protected float[] hEdgeMatrix = SOBEL_H;

	/**
	 * Constructs a EdgeFilter.
	 */
	public EdgeFilter() {
		super();
	}

	/**
	 * Sets the vertical edge detection matrix used by the filter.
	 * If not set, a default Sobel vertical edge detection matrix is used (SOBEL_V).
	 * @param vEdgeMatrix an array of floats representing the vertical edge detection matrix
	 */
	public void setVEdgeMatrix(float[] vEdgeMatrix) {
		this.vEdgeMatrix = vEdgeMatrix;
	}

	/**
	 * Retrieves the vertical edge detection matrix used by the filter.
	 * @return an array of floats representing the vertical edge detection matrix.
	 */
	public float[] getVEdgeMatrix() {
		return this.vEdgeMatrix;
	}

	/**
	 * Sets the horizontal edge detection matrix used by the filter.
	 * If not set, a default Sobel horizontal edge detection matrix is used (SOBEL_H).
	 * @param hEdgeMatrix an array of floats representing the horizontal edge detection matrix
	 */
	public void setHEdgeMatrix(float[] hEdgeMatrix) {
		this.hEdgeMatrix = hEdgeMatrix;
	}

	/**
	 * Retrieves the horizontal edge detection matrix used by the filter.
	 * If not explicitly set, a default Sobel horizontal edge detection matrix is used.
	 * @return an array of floats representing the horizontal edge detection matrix.
	 */
	public float[] getHEdgeMatrix() {
		return this.hEdgeMatrix;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = 0;
				int g = 0;
				int b = 0;
				int rh = 0;
				int gh = 0;
				int bh = 0;
				int rv = 0;
				int gv = 0;
				int bv = 0;
				int a = inPixels[y * width + x] & 0xff000000;

				for (int row = -1; row <= 1; row++) {
					int iy = y + row;
					int ioffset;
					if (0 <= iy && iy < height) {
						ioffset = iy * width;
					}
					else {
						ioffset = y * width;
					}
					int moffset = 3 * (row + 1) + 1;
					for (int col = -1; col <= 1; col++) {
						int ix = x + col;
						if (!(0 <= ix && ix < width)) {
							ix = x;
						}
						int rgb = inPixels[ioffset + ix];
						float h = this.hEdgeMatrix[moffset + col];
						float v = this.vEdgeMatrix[moffset + col];

						r = (rgb & 0xff0000) >> 16;
						g = (rgb & 0x00ff00) >> 8;
						b = rgb & 0x0000ff;
						rh += (int) (h * r);
						gh += (int) (h * g);
						bh += (int) (h * b);
						rv += (int) (v * r);
						gv += (int) (v * g);
						bv += (int) (v * b);
					}
				}
				r = (int) (Math.sqrt(rh * rh + rv * rv) / 1.8);
				g = (int) (Math.sqrt(gh * gh + gv * gv) / 1.8);
				b = (int) (Math.sqrt(bh * bh + bv * bv) / 1.8);
				r = PixelUtils.clamp(r);
				g = PixelUtils.clamp(g);
				b = PixelUtils.clamp(b);
				outPixels[index++] = a | (r << 16) | (g << 8) | b;
			}

		}
		return outPixels;
	}

	@Override
	public String toString() {
		return "Blur/Detect Edges";
	}
}
