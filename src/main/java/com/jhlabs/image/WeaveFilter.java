/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The {@code WeaveFilter} class is a filter that applies a woven texture effect
 * to an image. It extends the {@code PointFilter} class and provides options
 * to configure the width, gap, colors, and other properties of the woven
 * threads.
 * <p>
 * This filter simulates the effect of horizontal and vertical threads crossing
 * over each other, creating a woven pattern. It supports various configuration
 * options, such as rounding thread edges, shading crossings, and using custom
 * colors or image colors for the threads.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class WeaveFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 4847932412277504482L;

	private float xWidth = 16;
	private float yWidth = 16;
	private float xGap = 6;
	private float yGap = 6;
	private final int rows = 4;
	private final int cols = 4;
	private final int rgbX = 0xffff8080;
	private final int rgbY = 0xff8080ff;
	private boolean useImageColors = true;
	private boolean roundThreads = false;
	private boolean shadeCrossings = true;

	private int[][] matrix = {
			{0, 1, 0, 1},
			{1, 0, 1, 0},
			{0, 1, 0, 1},
			{1, 0, 1, 0},
	};

	/**
	 * Constructs a new instance of the WeaveFilter class.
	 * This filter is designed to simulate a woven texture by manipulating the pixel values
	 * of an image. It allows for customization of thread widths, gaps, and other parameters
	 * to create a weaving effect. The filter also supports the use of image colors, rounded
	 * threads, shading at crossings, and various patterns based on the provided configuration.
	 */
	public WeaveFilter() {
		super();
	}

	/**
	 * Sets the horizontal gap size between the threads in the weaving effect.
	 * @param xGap the horizontal gap size as a float value. A larger value increases the spacing
	 *             between threads in the horizontal direction, while a smaller value reduces it.
	 */
	public void setXGap(float xGap) {
		this.xGap = xGap;
	}

	/**
	 * Sets the horizontal thread width for the weaving effect.
	 * This determines the thickness of the threads in the horizontal direction.
	 * @param xWidth the horizontal thread width as a float value. A larger value increases
	 *               the thickness of the threads in the horizontal direction, while a
	 *               smaller value reduces it.
	 */
	public void setXWidth(float xWidth) {
		this.xWidth = xWidth;
	}

	/**
	 * Retrieves the horizontal thread width for the weaving effect.
	 * This value represents the thickness of the threads in the horizontal direction.
	 * @return the horizontal thread width as a float value.
	 *         A larger value corresponds to thicker threads in the horizontal direction,
	 *         while a smaller value represents thinner threads.
	 */
	public float getXWidth() {
		return this.xWidth;
	}

	/**
	 * Sets the vertical thread width for the weaving effect.
	 * This determines the thickness of the threads in the vertical direction.
	 * @param yWidth the vertical thread width as a float value.
	 *               A larger value increases the thickness of the threads in the vertical direction,
	 *               while a smaller value reduces it.
	 */
	public void setYWidth(float yWidth) {
		this.yWidth = yWidth;
	}

	/**
	 * Retrieves the vertical thread width for the weaving effect.
	 * This value represents the thickness of the threads in the vertical direction.
	 * @return the vertical thread width as a float value.
	 *         A larger value corresponds to thicker threads in the vertical direction,
	 *         while a smaller value represents thinner threads.
	 */
	public float getYWidth() {
		return this.yWidth;
	}

	/**
	 * Retrieves the horizontal gap size between the threads in the weaving effect.
	 * This value determines the space between the threads in the horizontal direction.
	 * @return the horizontal gap size as a float value. A larger value results in wider spacing
	 *         between threads, while a smaller value results in narrower spacing.
	 */
	public float getXGap() {
		return this.xGap;
	}

	/**
	 * Sets the vertical gap size between the threads in the weaving effect.
	 * This determines the space between the threads in the vertical direction.
	 * @param yGap the vertical gap size as a float value. A larger value increases the spacing
	 *             between threads in the vertical direction, while a smaller value reduces it.
	 */
	public void setYGap(float yGap) {
		this.yGap = yGap;
	}

	/**
	 * Retrieves the vertical gap size between the threads in the weaving effect.
	 * This value determines the space between the threads in the vertical direction.
	 * @return the vertical gap size as a float value. A larger value results in wider spacing
	 *         between threads, while a smaller value results in narrower spacing.
	 */
	public float getYGap() {
		return this.yGap;
	}

	/**
	 * Sets the crossing matrix for the weaving effect.
	 * The matrix defines where and how the threads in the pattern overlap or intersect.
	 * Each element in the matrix specifies the properties of the crossing at that position.
	 * @param matrix a 2D integer array representing the crossing pattern.
	 *               Each element corresponds to a specific crossing in the weave structure.
	 */
	public void setCrossings(int[][] matrix) {
		this.matrix = matrix;
	}

	/**
	 * Retrieves the crossing matrix used in the weaving effect.
	 * The crossing matrix defines the pattern of intersections and overlaps
	 * between threads in the woven texture. Each element in the matrix
	 * represents a specific crossing in the weave structure.
	 * @return a 2D integer array representing the crossing matrix.
	 *         Each element in the array specifies the properties or configuration
	 *         of a crossing in the weaving effect.
	 */
	public int[][] getCrossings() {
		return this.matrix;
	}

	/**
	 * Sets whether the weaving effect should use the colors from the input image.
	 * If enabled, the filter will incorporate pixel color information from the image
	 * to create the woven texture. Otherwise, the filter may use default or predefined
	 * colors for the weaving effect.
	 * @param useImageColors a boolean indicating whether to use the colors from the
	 *                       input image. Set to {@code true} to use the image colors,
	 *                       or {@code false} to disable this feature.
	 */
	public void setUseImageColors(boolean useImageColors) {
		this.useImageColors = useImageColors;
	}

	/**
	 * Retrieves the state of the useImageColors property for the weaving effect.
	 * This property determines whether the colors from the input image
	 * are used in the generated woven texture.
	 * @return a boolean indicating whether the colors from the input image are used.
	 *         Returns {@code true} if the weaving effect incorporates the image colors,
	 *         or {@code false} if it does not.
	 */
	public boolean getUseImageColors() {
		return this.useImageColors;
	}

	/**
	 * Sets whether the weaving effect should render threads with a rounded appearance.
	 * When set to {@code true}, this enables rounded edges on the threads,
	 * providing a softer, more realistic woven texture.
	 * When set to {@code false}, the threads will have a flat-edge appearance.
	 * @param roundThreads a boolean indicating whether to render threads with a rounded appearance.
	 *                     Set to {@code true} to enable rounded threads or {@code false} to disable it.
	 */
	public void setRoundThreads(boolean roundThreads) {
		this.roundThreads = roundThreads;
	}

	/**
	 * Retrieves the state of the roundThreads property for the weaving effect.
	 * This property determines whether the threads in the woven texture should
	 * have a rounded appearance or a flat-edge appearance.
	 * @return a boolean indicating the rounding state of the threads.
	 *         Returns {@code true} if the threads are rendered with a rounded
	 *         appearance, or {@code false} if the threads have a flat-edge appearance.
	 */
	public boolean getRoundThreads() {
		return this.roundThreads;
	}

	/**
	 * Sets whether the weaving effect should apply shading at the crossings of threads.
	 * When enabled, the crossings will be rendered with additional shading to create
	 * a more realistic woven texture.
	 * @param shadeCrossings a boolean indicating whether to apply shading at thread crossings.
	 *                       Set to {@code true} to enable shading or {@code false} to disable it.
	 */
	public void setShadeCrossings(boolean shadeCrossings) {
		this.shadeCrossings = shadeCrossings;
	}

	/**
	 * Retrieves the state of the shadeCrossings property for the weaving effect.
	 * This property determines whether shading is applied at the crossings of threads
	 * in the woven texture to create a more realistic appearance.
	 * @return a boolean indicating whether shading is applied at thread crossings.
	 *         Returns {@code true} if shading is enabled, or {@code false} if it is disabled.
	 */
	public boolean getShadeCrossings() {
		return this.shadeCrossings;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		x += this.xWidth + this.xGap / 2;
		y += this.yWidth + this.yGap / 2;
		float nx = ImageMath.mod(x, this.xWidth + this.xGap);
		float ny = ImageMath.mod(y, this.yWidth + this.yGap);
		int ix = (int) (x / (this.xWidth + this.xGap));
		int iy = (int) (y / (this.yWidth + this.yGap));
		boolean inX = nx < this.xWidth;
		boolean inY = ny < this.yWidth;
		float dX;
		float dY;
		float cX;
		float cY;
		int lrgbX;
		int lrgbY;

		if (this.roundThreads) {
			dX = Math.abs(this.xWidth / 2 - nx) / this.xWidth / 2;
			dY = Math.abs(this.yWidth / 2 - ny) / this.yWidth / 2;
		}
		else {
			dX = 0;
			dY = 0;
		}

		if (this.shadeCrossings) {
			cX = ImageMath.smoothStep(this.xWidth / 2, this.xWidth / 2 + this.xGap, Math.abs(this.xWidth / 2 - nx));
			cY = ImageMath.smoothStep(this.yWidth / 2, this.yWidth / 2 + this.yGap, Math.abs(this.yWidth / 2 - ny));
		}
		else {
			cX = 0;
			cY = 0;
		}

		if (this.useImageColors) {
			int localRgb = rgb;
			lrgbX = localRgb;
			lrgbY = localRgb;
		}
		else {
			lrgbX = this.rgbX;
			lrgbY = this.rgbY;
		}
		int v;
		int ixc = ix % this.cols;
		int iyr = iy % this.rows;
		int m = this.matrix[iyr][ixc];
		if (inX) {
			if (inY) {
				v = (m == 1) ? lrgbX : lrgbY;
				v = ImageMath.mixColors(2 * ((m == 1) ? dX : dY), v, 0xff000000);
			}
			else {
				if (this.shadeCrossings) {
					if (m != this.matrix[(iy + 1) % this.rows][ixc]) {
						if (m == 0) {
							cY = 1 - cY;
						}
						cY *= 0.5f;
						lrgbX = ImageMath.mixColors(cY, lrgbX, 0xff000000);
					}
					else if (m == 0) {
						lrgbX = ImageMath.mixColors(0.5f, lrgbX, 0xff000000);
					}
				}
				v = ImageMath.mixColors(2 * dX, lrgbX, 0xff000000);
			}
		}
		else if (inY) {
			if (this.shadeCrossings) {
				if (m != this.matrix[iyr][(ix + 1) % this.cols]) {
					if (m == 1) {
						cX = 1 - cX;
					}
					cX *= 0.5f;
					lrgbY = ImageMath.mixColors(cX, lrgbY, 0xff000000);
				}
				else if (m == 1) {
					lrgbY = ImageMath.mixColors(0.5f, lrgbY, 0xff000000);
				}
			}
			v = ImageMath.mixColors(2 * dY, lrgbY, 0xff000000);
		}
		else {
			v = 0x00000000;
		}
		return v;
	}

	@Override
	public String toString() {
		return "Texture/Weave...";
	}

}


