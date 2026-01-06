/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The DitherFilter class applies dithering to an image using a specified dithering matrix.
 * It is a subclass of {@link PointFilter} and can process pixel data to achieve color reduction
 * and effects based on dithering techniques.
 * <p>
 * This class supports standard dithering matrices such as ordered dithering, clustered
 * dithering, and halftone dithering, and provides flexibility for custom matrices.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class DitherFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 2408287445119636967L;

	/**
	 * A 2x2 dithering matrix used for ordered dithering in image processing.
	 * The matrix is designed to distribute quantization error systematically
	 * across neighboring pixels, helping to produce smoother gradients.
	 * <p>
	 * The matrix values:
	 * - 0, 2
	 * - 3, 1
	 * <p>
	 * Each value in the matrix represents the threshold level for dithering at the corresponding position.
	 * The thresholds are normalized and applied to determine whether a pixel's value is rounded up or down
	 * during quantization.
	 */
	public static final int[] ditherMagic2x2Matrix = {
			0, 2,
			3, 1
	};

	/**
	 * A 4x4 dithering matrix used for ordered dithering in image processing.
	 * <p>
	 * This matrix is commonly used to create a smoother gradient or simulate
	 * intermediate colors in monochromatic images by distributing quantization errors
	 * spatially. The values in the matrix are used to determine the dithering threshold for
	 * each pixel, which then decides whether a pixel's intensity level is incremented
	 * or left unchanged.
	 * <p>
	 * The matrix contains predefined thresholds arranged in a specific pattern to evenly
	 * distribute quantization artifacts over the image, thereby reducing visual banding.
	 * <p>
	 * The matrix values are as follows:
	 * 0, 14, 3, 13,
	 * 11, 5, 8, 6,
	 * 12, 2, 15, 1,
	 * 7, 9, 4, 10.
	 * <p>
	 * This static field is constant and shared across all instances that require
	 * 4x4 dithering for image manipulation.
	 */
	public static final int[] ditherMagic4x4Matrix = {
			0, 14, 3, 13,
			11, 5, 8, 6,
			12, 2, 15, 1,
			7, 9, 4, 10
	};

	/**
	 * A 4x4 ordered dithering matrix used for image dithering. The matrix defines
	 * a specific pattern for distributing quantization error to produce a visually
	 * appealing result when reducing the color depth of an image.
	 * <p>
	 * The values in the matrix are normalized thresholds used to determine how
	 * to distribute error across adjacent pixels. When applied in a dithering
	 * algorithm, this matrix helps to create smoother transitions and prevent
	 * noticeable banding or abrupt color changes.
	 */
	public static final int[] ditherOrdered4x4Matrix = {
			0, 8, 2, 10,
			12, 4, 14, 6,
			3, 11, 1, 9,
			15, 7, 13, 5
	};

	/**
	 * A 4x4 dithering matrix used in the dithering process to create ordered dithering effects.
	 * Each element in the matrix represents the threshold level for modifying pixel intensities.
	 * This matrix defines a diagonal lines pattern and is used by filters for rendering.
	 * <p>
	 * The values in the matrix are laid out in row-major order, representing:
	 * <p>
	 * Row 1: 0, 1, 2, 3
	 * Row 2: 4, 5, 6, 7
	 * Row 3: 8, 9, 10, 11
	 * Row 4: 12, 13, 14, 15
	 */
	public static final int[] ditherLines4x4Matrix = {
			0, 1, 2, 3,
			4, 5, 6, 7,
			8, 9, 10, 11,
			12, 13, 14, 15
	};

	/**
	 * A 6x6 halftone dithering matrix designed for thresholding pixel values at 90-degree offset angles.
	 * This matrix is typically used in image dithering techniques to simulate shades of gray
	 * in black-and-white or limited color displays by varying the density and arrangement of pixels.
	 * <p>
	 * The matrix contains predefined threshold values ranging from 0 to 36, organized in a 6x6 grid.
	 * These values determine the pixel intensity at each corresponding position in the image.
	 * Lower threshold values correspond to lighter regions, while higher values represent darker areas.
	 * <p>
	 * This matrix, when applied to an image, generates a halftone effect with a dot pattern
	 * rotated or aligned at a 90-degree angle, resulting in a visually structured dithering effect.
	 * It is suitable for various graphic and visual rendering applications where
	 * an analog halftone or artistic style is desired.
	 */
	public static final int[] dither90Halftone6x6Matrix = {
			29, 18, 12, 19, 30, 34,
			17, 7, 4, 8, 20, 28,
			11, 3, 0, 1, 9, 27,
			16, 6, 2, 5, 13, 26,
			25, 15, 10, 14, 21, 31,
			33, 25, 24, 23, 33, 36
	};

	/*
	 * The following dithering matrices are taken from "Digital Halftoning"
	 * by Robert Ulichney, MIT Press, ISBN 0-262-21009-6.
	 */

	/**
	 * Order-6 ordered dither.
	 **/
	public static final int[] ditherOrdered6x6Matrix = {
			1, 59, 15, 55, 2, 56, 12, 52,
			33, 17, 47, 31, 34, 18, 44, 28,
			9, 49, 5, 63, 10, 50, 6, 60,
			41, 25, 37, 21, 42, 26, 38, 22,
			3, 57, 13, 53, 0, 58, 14, 54,
			35, 19, 45, 29, 32, 16, 46, 30,
			11, 51, 7, 61, 8, 48, 4, 62,
			43, 27, 39, 23, 40, 24, 36, 20
	};

	/**
	 * Order-8 ordered dither.
	 */
	public static final int[] ditherOrdered8x8Matrix = {
			1, 235, 59, 219, 15, 231, 55, 215, 2, 232, 56, 216, 12, 228, 52, 212,
			129, 65, 187, 123, 143, 79, 183, 119, 130, 66, 184, 120, 140, 76, 180, 116,
			33, 193, 17, 251, 47, 207, 31, 247, 34, 194, 18, 248, 44, 204, 28, 244,
			161, 97, 145, 81, 175, 111, 159, 95, 162, 98, 146, 82, 172, 108, 156, 92,
			9, 225, 49, 209, 5, 239, 63, 223, 10, 226, 50, 210, 6, 236, 60, 220,
			137, 73, 177, 113, 133, 69, 191, 127, 138, 74, 178, 114, 134, 70, 188, 124,
			41, 201, 25, 241, 37, 197, 21, 255, 42, 202, 26, 242, 38, 198, 22, 252,
			169, 105, 153, 89, 165, 101, 149, 85, 170, 106, 154, 90, 166, 102, 150, 86,
			3, 233, 57, 217, 13, 229, 53, 213, 0, 234, 58, 218, 14, 230, 54, 214,
			131, 67, 185, 121, 141, 77, 181, 117, 128, 64, 186, 122, 142, 78, 182, 118,
			35, 195, 19, 249, 45, 205, 29, 245, 32, 192, 16, 250, 46, 206, 30, 246,
			163, 99, 147, 83, 173, 109, 157, 93, 160, 96, 144, 80, 174, 110, 158, 94,
			11, 227, 51, 211, 7, 237, 61, 221, 8, 224, 48, 208, 4, 238, 62, 222,
			139, 75, 179, 115, 135, 71, 189, 125, 136, 72, 176, 112, 132, 68, 190, 126,
			43, 203, 27, 243, 39, 199, 23, 253, 40, 200, 24, 240, 36, 196, 20, 254,
			171, 107, 155, 91, 167, 103, 151, 87, 168, 104, 152, 88, 164, 100, 148, 84};

	/**
	 * Order-3 clustered dither.
	 */
	public static final int[] ditherCluster3Matrix = {
			9, 11, 10, 8, 6, 7,
			12, 17, 16, 5, 0, 1,
			13, 14, 15, 4, 3, 2,
			8, 6, 7, 9, 11, 10,
			5, 0, 1, 12, 17, 16,
			4, 3, 2, 13, 14, 15};

	/**
	 *  Order-4 clustered dither.
	 */
	public static final int[] ditherCluster4Matrix = {
			18, 20, 19, 16, 13, 11, 12, 15,
			27, 28, 29, 22, 4, 3, 2, 9,
			26, 31, 30, 21, 5, 0, 1, 10,
			23, 25, 24, 17, 8, 6, 7, 14,
			13, 11, 12, 15, 18, 20, 19, 16,
			4, 3, 2, 9, 27, 28, 29, 22,
			5, 0, 1, 10, 26, 31, 30, 21,
			8, 6, 7, 14, 23, 25, 24, 17};

	/**
	 * Order-8 clustered dither.
	 */
	public static final int[] ditherCluster8Matrix = {
			64, 69, 77, 87, 86, 76, 68, 67, 63, 58, 50, 40, 41, 51, 59, 60,
			70, 94, 100, 109, 108, 99, 93, 75, 57, 33, 27, 18, 19, 28, 34, 52,
			78, 101, 114, 116, 115, 112, 98, 83, 49, 26, 13, 11, 12, 15, 29, 44,
			88, 110, 123, 124, 125, 118, 107, 85, 39, 17, 4, 3, 2, 9, 20, 42,
			89, 111, 122, 127, 126, 117, 106, 84, 38, 16, 5, 0, 1, 10, 21, 43,
			79, 102, 119, 121, 120, 113, 97, 82, 48, 25, 8, 6, 7, 14, 30, 45,
			71, 95, 103, 104, 105, 96, 92, 74, 56, 32, 24, 23, 22, 31, 35, 53,
			65, 72, 80, 90, 91, 81, 73, 66, 62, 55, 47, 37, 36, 46, 54, 61,
			63, 58, 50, 40, 41, 51, 59, 60, 64, 69, 77, 87, 86, 76, 68, 67,
			57, 33, 27, 18, 19, 28, 34, 52, 70, 94, 100, 109, 108, 99, 93, 75,
			49, 26, 13, 11, 12, 15, 29, 44, 78, 101, 114, 116, 115, 112, 98, 83,
			39, 17, 4, 3, 2, 9, 20, 42, 88, 110, 123, 124, 125, 118, 107, 85,
			38, 16, 5, 0, 1, 10, 21, 43, 89, 111, 122, 127, 126, 117, 106, 84,
			48, 25, 8, 6, 7, 14, 30, 45, 79, 102, 119, 121, 120, 113, 97, 82,
			56, 32, 24, 23, 22, 31, 35, 53, 71, 95, 103, 104, 105, 96, 92, 74,
			62, 55, 47, 37, 36, 46, 54, 61, 65, 72, 80, 90, 91, 81, 73, 66};

	private int[] matrix;
	private int rows;
	private int cols;
	private int levels;
	protected int[] mod;
	protected int[] div;
	protected int[] map;
	private boolean colorDither;
	private boolean initialized = false;

	/**
	 * Construct a DitherFilter with a 4x4 dithering matrix.
	 */
	public DitherFilter() {
		this.rows = 2;
		this.cols = 2;
		this.matrix = ditherMagic4x4Matrix;
		this.levels = 6;
		this.colorDither = true;
	}

	/**
	 * Set the dithering matrix.
	 * @param matrix the dithering matrix.
	 */
	public void setMatrix(int[] matrix) {
		this.matrix = matrix;
	}

	public int[] getMatrix() {
		return this.matrix;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public int getLevels() {
		return this.levels;
	}

	public boolean isColorDither() {
		return this.colorDither;
	}

	public void setColorDither(boolean colorDither) {
		this.colorDither = colorDither;
	}

	protected void initialize() {
		int size = (int) Math.sqrt(this.matrix.length);
		this.rows = size;
		this.cols = size;
		this.map = new int[this.levels];
		for (int i = 0; i < this.levels; i++) {
			int v = 255 * i / (this.levels - 1);
			this.map[i] = v;
		}
		this.div = new int[256];
		this.mod = new int[256];
		int rc = (this.rows * this.cols + 1);
		for (int i = 0; i < 256; i++) {
			this.div[i] = (this.levels - 1) * i / 256;
			this.mod[i] = i * rc / 256;
		}
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		if (!this.initialized) {
			this.initialized = true;
			initialize();
		}
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		int col = x % this.cols;
		int row = y % this.rows;
		int v = this.matrix[row * this.cols + col];
		if (this.colorDither) {
			r = this.map[(this.mod[r] > v) ? (this.div[r] + 1) : this.div[r]];
			g = this.map[(this.mod[g] > v) ? (this.div[g] + 1) : this.div[g]];
			b = this.map[(this.mod[b] > v) ? (this.div[b] + 1) : this.div[b]];
		}
		else {
			int value = (r + g + b) / 3;
			int dithered = this.map[(this.mod[value] > v) ? (this.div[value] + 1) : this.div[value]];
			r = dithered;
			g = dithered;
			b = dithered;
		}
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Colors/Dither...";
	}

}

