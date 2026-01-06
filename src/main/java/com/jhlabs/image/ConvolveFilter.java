/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Kernel;

/**
 * A filter which applies a convolution kernel to an image.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class ConvolveFilter extends AbstractBufferedImageOp {

	/**
	 * A constant indicating an edge action where no special treatment is applied
	 * to the edges of an image during convolution. Pixels near the edge of the
	 * image are implicitly treated as having a value of zero outside the bounds
	 * of the image.
	 * <p>
	 * This value is typically used as a parameter to specify the behavior of
	 * edge handling in convolution operations, such as when applying a kernel to
	 * an image.
	 */
	public static final int ZERO_EDGES = 0;

	/**
	 * Indicates that the edge pixels of an image should be clamped to the nearest valid pixels
	 * during convolution operations. This ensures that any references to pixels outside the
	 * image boundaries are replaced with the closest edge pixel.
	 */
	public static final int CLAMP_EDGES = 1;

	/**
	 * A constant used to specify the edge action for convolution operations in the {@code ConvolveFilter} class.
	 * When this value is set, the filter wraps the pixel values around the edges of the image during the convolution process.
	 * This can be useful for achieving seamless effects or handling boundary conditions in certain image processing scenarios.
	 */
	public static final int WRAP_EDGES = 2;

	protected Kernel kernel = null;
	private boolean alpha = true;
	private int edgeAction = CLAMP_EDGES;

	/**
	 * Construct a filter with a null kernel. This is only useful if you're going to change the kernel later on.
	 */
	public ConvolveFilter() {
		this(new float[9]);
	}

	/**
	 * Construct a filter with the given 3x3 kernel.
	 *
	 * @param matrix an array of 9 floats containing the kernel
	 */
	public ConvolveFilter(float[] matrix) {
		this(new Kernel(3, 3, matrix));
	}

	/**
	 * Construct a filter with the given kernel.
	 *
	 * @param rows   the number of rows in the kernel
	 * @param cols   the number of columns in the kernel
	 * @param matrix an array of rows*cols floats containing the kernel
	 */
	public ConvolveFilter(int rows, int cols, float[] matrix) {
		this(new Kernel(cols, rows, matrix));
	}

	/**
	 * Construct a filter with the given 3x3 kernel.
	 * @param kernel the convolution kernel
	 */
	public ConvolveFilter(Kernel kernel) {
		this.kernel = kernel;
	}

	public void setKernel(Kernel kernel) {
		this.kernel = kernel;
	}

	public Kernel getKernel() {
		return this.kernel;
	}

	public void setEdgeAction(int edgeAction) {
		this.edgeAction = edgeAction;
	}

	public int getEdgeAction() {
		return this.edgeAction;
	}

	public boolean isAlpha() {
		return this.alpha;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int width = src.getWidth();
		int height = src.getHeight();

		if (dst == null) {
			dst = createCompatibleDestImage(src, null);
		}

		int[] inPixels = new int[width * height];
		int[] outPixels = new int[width * height];
		getRGB(src, 0, 0, width, height, inPixels);

		convolve(this.kernel, inPixels, outPixels, width, height, this.alpha, this.edgeAction);

		setRGB(dst, 0, 0, width, height, outPixels);
		return dst;
	}

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
		if (dstCM == null) {
			dstCM = src.getColorModel();
		}
		return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()), dstCM.isAlphaPremultiplied(), null);
	}

	@Override
	public Rectangle2D getBounds2D(BufferedImage src) {
		return new Rectangle(0, 0, src.getWidth(), src.getHeight());
	}

	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		if (dstPt == null) {
			dstPt = new Point2D.Double();
		}
		dstPt.setLocation(srcPt.getX(), srcPt.getY());
		return dstPt;
	}

	@Override
	public RenderingHints getRenderingHints() {
		return null;
	}

	public static void convolve(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, int edgeAction) {
		convolve(kernel, inPixels, outPixels, width, height, true, edgeAction);
	}

	public static void convolve(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
		if (kernel.getHeight() == 1) {
			convolveH(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
		}
		else if (kernel.getWidth() == 1) {
			convolveV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
		}
		else {
			convolveHV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
		}
	}

	/**
	 * Convolve with a 2D kernel.
	 * @param kernel the convolution kernel
	 * @param inPixels the input pixel array
	 * @param outPixels the output pixel array
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param alpha whether to process the alpha channel
	 * @param edgeAction the edge action to use
	 */
	public static void convolveHV(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
		int index = 0;
		float[] matrix = kernel.getKernelData(null);
		int rows = kernel.getHeight();
		int cols = kernel.getWidth();
		int rows2 = rows / 2;
		int cols2 = cols / 2;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float r = 0;
				float g = 0;
				float b = 0;
				float a = 0;

				for (int row = -rows2; row <= rows2; row++) {
					int iy = y + row;
					int ioffset;
					if (0 <= iy && iy < height) {
						ioffset = iy * width;
					}
					else if (edgeAction == CLAMP_EDGES) {
						ioffset = y * width;
					}
					else if (edgeAction == WRAP_EDGES) {
						ioffset = ((iy + height) % height) * width;
					}
					else {
						continue;
					}
					int moffset = cols * (row + rows2) + cols2;
					for (int col = -cols2; col <= cols2; col++) {
						float f = matrix[moffset + col];

						if (f != 0) {
							int ix = x + col;
							if (!(0 <= ix && ix < width)) {
								if (edgeAction == CLAMP_EDGES) {
									ix = x;
								}
								else if (edgeAction == WRAP_EDGES) {
									ix = (x + width) % width;
								}
								else {
									continue;
								}
							}
							int rgb = inPixels[ioffset + ix];
							a += f * ((rgb >> 24) & 0xff);
							r += f * ((rgb >> 16) & 0xff);
							g += f * ((rgb >> 8) & 0xff);
							b += f * (rgb & 0xff);
						}
					}
				}
				int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 0xff;
				int ir = PixelUtils.clamp((int) (r + 0.5));
				int ig = PixelUtils.clamp((int) (g + 0.5));
				int ib = PixelUtils.clamp((int) (b + 0.5));
				outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
			}
		}
	}

	/**
	 * Convolve with a kernel consisting of one row.
	 * @param kernel the convolution kernel
	 * @param inPixels the input pixel array
	 * @param outPixels the output pixel array
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param alpha whether to process the alpha channel
	 * @param edgeAction the edge action to use
	 */
	public static void convolveH(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
		int index = 0;
		float[] matrix = kernel.getKernelData(null);
		int cols = kernel.getWidth();
		int cols2 = cols / 2;

		for (int y = 0; y < height; y++) {
			int ioffset = y * width;
			for (int x = 0; x < width; x++) {
				float r = 0;
				float g = 0;
				float b = 0;
				float a = 0;
				int moffset = cols2;
				for (int col = -cols2; col <= cols2; col++) {
					float f = matrix[moffset + col];

					if (f != 0) {
						int ix = x + col;
						if (ix < 0) {
							if (edgeAction == CLAMP_EDGES) {
								ix = 0;
							}
							else if (edgeAction == WRAP_EDGES) {
								ix = (x + width) % width;
							}
						}
						else if (ix >= width) {
							if (edgeAction == CLAMP_EDGES) {
								ix = width - 1;
							}
							else if (edgeAction == WRAP_EDGES) {
								ix = (x + width) % width;
							}
						}
						int rgb = inPixels[ioffset + ix];
						a += f * ((rgb >> 24) & 0xff);
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 0xff;
				int ir = PixelUtils.clamp((int) (r + 0.5));
				int ig = PixelUtils.clamp((int) (g + 0.5));
				int ib = PixelUtils.clamp((int) (b + 0.5));
				outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
			}
		}
	}

	/**
	 * Convolve with a kernel consisting of one column.
	 * @param kernel the convolution kernel
	 * @param inPixels the input pixel array
	 * @param outPixels the output pixel array
	 * @param width the width of the image
	 * @param height the height of the image
	 * @param alpha whether to process the alpha channel
	 * @param edgeAction the edge action to use
	 */
	public static void convolveV(Kernel kernel, int[] inPixels, int[] outPixels, int width, int height, boolean alpha, int edgeAction) {
		int index = 0;
		float[] matrix = kernel.getKernelData(null);
		int rows = kernel.getHeight();
		int rows2 = rows / 2;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float r = 0;
				float g = 0;
				float b = 0;
				float a = 0;

				for (int row = -rows2; row <= rows2; row++) {
					int iy = y + row;
					int ioffset;
					if (iy < 0) {
						if (edgeAction == CLAMP_EDGES) {
							ioffset = 0;
						}
						else if (edgeAction == WRAP_EDGES) {
							ioffset = ((y + height) % height) * width;
						}
						else {
							ioffset = iy * width;
						}
					}
					else if (iy >= height) {
						if (edgeAction == CLAMP_EDGES) {
							ioffset = (height - 1) * width;
						}
						else if (edgeAction == WRAP_EDGES) {
							ioffset = ((y + height) % height) * width;
						}
						else {
							ioffset = iy * width;
						}
					}
					else {
						ioffset = iy * width;
					}

					float f = matrix[row + rows2];

					if (f != 0) {
						int rgb = inPixels[ioffset + x];
						a += f * ((rgb >> 24) & 0xff);
						r += f * ((rgb >> 16) & 0xff);
						g += f * ((rgb >> 8) & 0xff);
						b += f * (rgb & 0xff);
					}
				}
				int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 0xff;
				int ir = PixelUtils.clamp((int) (r + 0.5));
				int ig = PixelUtils.clamp((int) (g + 0.5));
				int ib = PixelUtils.clamp((int) (b + 0.5));
				outPixels[index++] = (ia << 24) | (ir << 16) | (ig << 8) | ib;
			}
		}
	}

	@Override
	public String toString() {
		return "Blur/Convolve...";
	}
}
