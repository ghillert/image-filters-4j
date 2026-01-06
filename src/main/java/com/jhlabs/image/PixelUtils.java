/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Color;
import java.util.Random;

/**
 * Some more useful math functions for image processing.
 * These are becoming obsolete as we move to Java2D. Use {@link com.jhlabs.composite.MiscComposite} instead.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class PixelUtils {

	/**
	 * A constant representing the "replace" operation mode within the PixelUtils class.
	 * This operation mode replaces the existing pixel color value with a new one during pixel manipulation.
	 */
	public static final int REPLACE = 0;

	/**
	 * A constant representing the "normal" operation mode within the PixelUtils class.
	 * This operation mode performs a standard pixel blending operation without any specific modification.
	 */
	public static final int NORMAL = 1;

	/**
	 * A constant representing the minimum value threshold.
	 * Used to define a fixed value of 2 for operations or calculations
	 * where a minimum bound is required.
	 */
	public static final int MIN = 2;

	/**
	 * A constant representing the maximum operational value or limit for certain computations
	 * or processes within the PixelUtils class.
	 */
	public static final int MAX = 3;

	/**
	 * Defines an operation constant representing the "add" blend mode,
	 * where the pixel values of two images are combined by adding
	 * their respective channel values.
	 */
	public static final int ADD = 4;

	/**
	 * Represents an operation mode used for pixel manipulation that performs
	 * subtraction between pixel values. This value is used within methods
	 * related to image processing to define behavior when combining or
	 * modifying pixel data.
	 */
	public static final int SUBTRACT = 5;

	/**
	 * A constant representing the "difference" operation in pixel manipulation.
	 * This is typically used to compute the absolute difference between two pixel values
	 * or colors during image processing operations, emphasizing the variation between them.
	 */
	public static final int DIFFERENCE = 6;

	/**
	 * A constant representing the multiplication blending mode in the {@code PixelUtils} class.
	 * This mode is typically used for combining pixel values by multiplying their color components.
	 */
	public static final int MULTIPLY = 7;

	/**
	 * Represents the operation identifier for the "HUE" blending mode or transformation
	 * in the context of pixel manipulation. This constant is typically used in methods or
	 * processes where pixel values need to be combined, adjusted, or analyzed based on their
	 * hue component within the HSB (Hue, Saturation, Brightness) color model.
	 */
	public static final int HUE = 8;

	/**
	 * Represents the operation mode or constant for adjusting the saturation property
	 * in image processing tasks. Saturation refers to the intensity or vividness of a color.
	 * A higher saturation value corresponds to more vibrant colors, while lower values result
	 * in muted or grayscale tones.
	 */
	public static final int SATURATION = 9;

	/**
	 * Represents the value component of the HSV (Hue, Saturation, Value) color model.
	 * In the HSV model, the value component determines the brightness of a color.
	 * It ranges between 0 (black) and a maximum value, typically used in color manipulation
	 * and operations.
	 */
	public static final int VALUE = 10;

	/**
	 * Represents the "COLOR" operation mode used in pixel manipulation.
	 * This constant is typically used to define a specific blending or
	 * color processing operation in image processing utilities.
	 */
	public static final int COLOR = 11;

	/**
	 * A constant representing the "SCREEN" operation in pixel-based image processing.
	 * This operation is often used in blending modes to achieve a "lighten" effect by
	 * combining two pixel values. The SCREEN operation typically inverts the values
	 * of both pixels, multiplies them, and then inverts the result to create the final value.
	 */
	public static final int SCREEN = 12;

	/**
	 * Represents the "AVERAGE" blending mode used in pixel operations.
	 * This constant indicates a mode where the average of two pixel values
	 * is calculated to blend them.
	 *
	 * Typically used in conjunction with methods that combine or manipulate
	 * pixel values, such as {@code combinePixels}, to produce visual effects
	 * that depend on averaging.
	 */
	public static final int AVERAGE = 13;

	/**
	 * The OVERLAY constant represents a specific blending mode used for combining
	 * pixel values in image processing. This mode overlays one color onto another
	 * to achieve a visually pleasing combination by preserving highlights and shadows
	 * of the base color while using the blend color to saturate or desaturate the result.
	 * <p>
	 * Typically, the overlay mode enhances contrast and provides emphasis to
	 * blended visuals, making it a common choice in graphic editing and compositing
	 * tasks.
	 */
	public static final int OVERLAY = 14;

	/**
	 * Represents the CLEAR blending operation constant used in pixel manipulation.
	 * This operation typically sets the target pixel to a fully transparent state,
	 * effectively clearing its color and alpha information.
	 */
	public static final int CLEAR = 15;

	/**
	 * Represents the EXCHANGE blending operation constant used in pixel manipulation.
	 * This operation swaps the source and destination pixel values, effectively
	 * exchanging their colors.
	 */
	public static final int EXCHANGE = 16;

	/**
	 * Represents the dissolve blend mode operation used in image processing.
	 * This constant is used to define a blending mode where pixels from the source
	 * and destination images are combined in a manner that simulates dissolving or
	 * random transparency effects. Typically applied during the combination of two
	 * images or while rendering transitions.
	 */
	public static final int DISSOLVE = 17;

	/**
	 * Represents the "DST_IN" blending operation constant used in pixel manipulation.
	 * The "DST_IN" operation retains the destination pixel's alpha while blending the
	 * source pixel only in areas where both source and destination pixels overlap.
	 */
	public static final int DST_IN = 18;

	/**
	 * A constant representing the alpha blending operation type, often used in image processing
	 * to handle transparency and blending of colors.
	 */
	public static final int ALPHA = 19;

	/**
	 * A constant representing an operation that converts the alpha channel of a pixel
	 * to grayscale intensity. The conversion applies a formula or transformation where
	 * the transparency (alpha) value of the pixel is interpreted as a grayscale value,
	 * effectively mapping transparency levels to shades of gray.
	 */
	public static final int ALPHA_TO_GRAY = 20;

	private static final Random randomGenerator = new Random();

	/**
	 * Clamp a value to the range 0..255.
	 * @param c the value to clamp
	 * @return the clamped value
	 */
	public static int clamp(int c) {
		if (c < 0) {
			return 0;
		}
		if (c > 255) {
			return 255;
		}
		return c;
	}

	/**
	 * Interpolates between two integer values based on a given fraction
	 * and clamps the result to the range 0 to 255.
	 * @param v1 the starting integer value
	 * @param v2 the ending integer value
	 * @param f a fraction between 0 and 1 representing the interpolation factor,
	 *          where 0 yields v1 and 1 yields v2
	 * @return the interpolated and clamped integer value
	 */
	public static int interpolate(int v1, int v2, float f) {
		return clamp((int) (v1 + f * (v2 - v1)));
	}

	public static int brightness(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		return (r + g + b) / 3;
	}

	public static boolean nearColors(int rgb1, int rgb2, int tolerance) {
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = rgb2 & 0xff;
		return Math.abs(r1 - r2) <= tolerance && Math.abs(g1 - g2) <= tolerance && Math.abs(b1 - b2) <= tolerance;
	}

	private static final float[] hsb1 = new float[3]; //FIXME-not thread safe
	private static final float[] hsb2 = new float[3]; //FIXME-not thread safe

	// Return rgb1 painted onto rgb2
	public static int combinePixels(int rgb1, int rgb2, int op) {
		return combinePixels(rgb1, rgb2, op, 0xff);
	}

	public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha, int channelMask) {
		return (rgb2 & ~channelMask) | combinePixels(rgb1 & channelMask, rgb2, op, extraAlpha);
	}

	public static int combinePixels(int rgb1, int rgb2, int op, int extraAlpha) {
		if (op == REPLACE) {
			return rgb1;
		}
		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = rgb1 & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = rgb2 & 0xff;

		switch (op) {
			case NORMAL:
				break;
			case MIN:
				r1 = Math.min(r1, r2);
				g1 = Math.min(g1, g2);
				b1 = Math.min(b1, b2);
				break;
			case MAX:
				r1 = Math.max(r1, r2);
				g1 = Math.max(g1, g2);
				b1 = Math.max(b1, b2);
				break;
			case ADD:
				r1 = clamp(r1 + r2);
				g1 = clamp(g1 + g2);
				b1 = clamp(b1 + b2);
				break;
			case SUBTRACT:
				r1 = clamp(r2 - r1);
				g1 = clamp(g2 - g1);
				b1 = clamp(b2 - b1);
				break;
			case DIFFERENCE:
				r1 = clamp(Math.abs(r1 - r2));
				g1 = clamp(Math.abs(g1 - g2));
				b1 = clamp(Math.abs(b1 - b2));
				break;
			case MULTIPLY:
				r1 = clamp(r1 * r2 / 255);
				g1 = clamp(g1 * g2 / 255);
				b1 = clamp(b1 * b2 / 255);
				break;
			case DISSOLVE:
				if ((randomGenerator.nextInt() & 0xff) <= a1) {
					r1 = r2;
					g1 = g2;
					b1 = b2;
				}
				break;
			case AVERAGE:
				r1 = (r1 + r2) / 2;
				g1 = (g1 + g2) / 2;
				b1 = (b1 + b2) / 2;
				break;
			case HUE:
			case SATURATION:
			case VALUE:
			case COLOR:
				Color.RGBtoHSB(r1, g1, b1, hsb1);
				Color.RGBtoHSB(r2, g2, b2, hsb2);
				switch (op) {
					case HUE:
						hsb2[0] = hsb1[0];
						break;
					case SATURATION:
						hsb2[1] = hsb1[1];
						break;
					case VALUE:
						hsb2[2] = hsb1[2];
						break;
					case COLOR:
						hsb2[0] = hsb1[0];
						hsb2[1] = hsb1[1];
						break;
				}
				rgb1 = Color.HSBtoRGB(hsb2[0], hsb2[1], hsb2[2]);
				r1 = (rgb1 >> 16) & 0xff;
				g1 = (rgb1 >> 8) & 0xff;
				b1 = rgb1 & 0xff;
				break;
			case SCREEN:
				r1 = 255 - ((255 - r1) * (255 - r2)) / 255;
				g1 = 255 - ((255 - g1) * (255 - g2)) / 255;
				b1 = 255 - ((255 - b1) * (255 - b2)) / 255;
				break;
			case OVERLAY:
				int m;
				int s;
				s = 255 - ((255 - r1) * (255 - r2)) / 255;
				m = r1 * r2 / 255;
				r1 = (s * r1 + m * (255 - r1)) / 255;
				s = 255 - ((255 - g1) * (255 - g2)) / 255;
				m = g1 * g2 / 255;
				g1 = (s * g1 + m * (255 - g1)) / 255;
				s = 255 - ((255 - b1) * (255 - b2)) / 255;
				m = b1 * b2 / 255;
				b1 = (s * b1 + m * (255 - b1)) / 255;
				break;
			case CLEAR:
				r1 = 0xff;
				g1 = 0xff;
				b1 = 0xff;
				break;
			case DST_IN:
				r1 = clamp((r2 * a1) / 255);
				g1 = clamp((g2 * a1) / 255);
				b1 = clamp((b2 * a1) / 255);
				a1 = clamp((a2 * a1) / 255);
				return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
			case ALPHA:
				a1 = a1 * a2 / 255;
				return (a1 << 24) | (r2 << 16) | (g2 << 8) | b2;
			case ALPHA_TO_GRAY:
				int na = 255 - a1;
				return (a1 << 24) | (na << 16) | (na << 8) | na;
		}
		if (extraAlpha != 0xff || a1 != 0xff) {
			a1 = a1 * extraAlpha / 255;
			int a3 = (255 - a1) * a2 / 255;
			r1 = clamp((r1 * a1 + r2 * a3) / 255);
			g1 = clamp((g1 * a1 + g2 * a3) / 255);
			b1 = clamp((b1 * a1 + b2 * a3) / 255);
			a1 = clamp(a1 + a3);
		}
		return (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
	}

}
