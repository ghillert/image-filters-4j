/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.composite;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

/**
 * A set of composite rules for blending images.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public final class MiscComposite implements Composite {

	/**
	 * A constant representing the "blend" compositing rule.
	 * This rule is used to determine how two images are combined
	 * in compositing operations, based on the blend mode.
	 * It defines a specific behavior when merging source and
	 * destination color values.
	 */
	public static final int BLEND = 0;

	/**
	 * Represents the "ADD" compositing rule in image processing.
	 * When applied, this rule combines the source and destination pixel values by summing them.
	 * Typically used to achieve a brightness increase in composited images.
	 */
	public static final int ADD = 1;

	/**
	 * Represents the rule for the "SUBTRACT" compositing operation.
	 * This rule specifies that the output color is determined by subtracting
	 * the source color from the destination color, clamping the result
	 * to ensure it stays within the valid color range.
	 */
	public static final int SUBTRACT = 2;

	/**
	 * Represents the compositing rule for the "difference" operation.
	 * The difference operation calculates the absolute difference
	 * between the source and destination colors. It is often used
	 * in image processing for highlighting differences between
	 * two images or layers.
	 */
	public static final int DIFFERENCE = 3;

	/**
	 * A constant representing the MULTIPLY compositing rule.
	 * This rule multiplies the source and destination colors, resulting in a darker image.
	 * Commonly used to blend images where the darker colors in the source and destination
	 * amplify the effect, while lighter colors create more subdued results.
	 * Defined as part of the blending modes in the {@code MiscComposite} class.
	 */
	public static final int MULTIPLY = 4;

	/**
	 * Represents the "darken" compositing rule in image blending operations.
	 * This rule selects the darker color value on a per-pixel basis
	 * when blending source and destination images.
	 * Used within the {@link MiscComposite} class to apply darken blending.
	 */
	public static final int DARKEN = 5;

	/**
	 * Represents the BURN compositing rule used in image processing.
	 * This rule typically darkens the source and destination colors
	 * by decreasing brightness in overlapping areas, creating a
	 * "burned" effect.
	 */
	public static final int BURN = 6;

	/**
	 * A compositing rule that applies the "Color Burn" blending mode.
	 * The "Color Burn" blending mode darkens the destination color to reflect
	 * the source color by increasing the contrast between the two.
	 * It is commonly used in image compositing to achieve a more dramatic
	 * blending effect where the result retains the hues of both the source
	 * and destination colors while darkening the overall image.
	 * <p>
	 * This constant can be used to specify the compositing behavior when
	 * using the MiscComposite class with the specified rule.
	 */
	public static final int COLOR_BURN = 7;

	/**
	 * Represents a compositing rule used in image processing to blend source and
	 * destination pixels. The SCREEN rule lightens the destination by increasing
	 * brightness, resulting in an effect similar to projecting multiple light sources
	 * onto the same surface. It produces a lighter image while retaining details from
	 * both source and destination.
	 * <p>
	 * This constant is part of a collection of blending rules used to define
	 * how image compositing operations should be performed.
	 */
	public static final int SCREEN = 8;

	/**
	 * A constant representing the "LIGHTEN" compositing rule.
	 * This rule determines how two images are blended by lightening the colors
	 * of overlapping pixels based on their respective color values.
	 * Part of a set of predefined compositing modes used in image processing.
	 */
	public static final int LIGHTEN = 9;

	/**
	 * Represents the Dodge blending mode used in image compositing operations.
	 * <p>
	 * In the Dodge mode, the top layer brightens the colors of the bottom
	 * layer based on its brightness. This blending mode is typically used
	 * to create a lighter effect in compositing by amplifying the brightness.
	 */
	public static final int DODGE = 10;

	/**
	 * Represents the "Color Dodge" blending mode rule for image compositing operations.
	 * This mode brightens the destination color to reflect the source color by decreasing
	 * the contrast between the two. It is used to achieve lighter or glowing effects
	 * in compositing.
	 */
	public static final int COLOR_DODGE = 11;

	/**
	 * A constant representing the "HUE" compositing rule.
	 * This rule is used in image compositing to combine source
	 * and destination images by preserving the hue of the source
	 * image while adopting the saturation and brightness of the
	 * destination image.
	 */
	public static final int HUE = 12;

	/**
	 * Represents the compositing rule for saturation adjustment in image compositing operations.
	 * This rule is used to blend two images while preserving the saturation of the source image.
	 * Part of the MiscComposite set of compositing rules.
	 */
	public static final int SATURATION = 13;

	/**
	 * Represents the rule identifier for the VALUE compositing mode in image processing.
	 * This rule defines a compositing operation where the value component
	 * (brightness) of source and destination colors is manipulated according
	 * to the composite implementation.
	 * <p>
	 * Used in conjunction with {@code MiscComposite} to specify compositing behavior.
	 */
	public static final int VALUE = 14;

	/**
	 * A constant representing the color compositing operation.
	 * This variable is part of the {@code MiscComposite} class, which defines
	 * various blending and compositing modes for image manipulation.
	 * <p>
	 * The {@code COLOR} operation applies a blend mode that colors the target
	 * while retaining essential luminance and saturation levels of the underlying image.
	 */
	public static final int COLOR = 15;

	/**
	 * Represents the overlay blending rule used in image compositing operations.
	 * The overlay rule combines the source and destination colors in such a way
	 * that highlights the mid-tones while preserving shadows and highlights.
	 * This operation is commonly used for creating textures, enhancing contrasts,
	 * and achieving a vivid blending effect.
	 * <p>
	 * In the context of the MiscComposite class, OVERLAY is one of the predefined
	 * compositing rules that can be utilized to blend images with specific visual effects.
	 */
	public static final int OVERLAY = 16;

	/**
	 * A blending mode constant representing the "Soft Light" compositing operation.
	 * <p>
	 * This mode is typically used in image processing to create a subtle, diffused
	 * lighting effect by combining source and destination pixels. The behavior of
	 * this mode depends on the luminance of the source and destination pixels,
	 * producing a softer contrast effect than other blending modes.
	 * <p>
	 * This constant is part of the MiscComposite class and can be used to create
	 * a MiscComposite instance for applying the Soft Light blending mode in a
	 * compositing operation.
	 */
	public static final int SOFT_LIGHT = 17;

	/**
	 * The HARD_LIGHT rule represents a compositing operation where the source
	 * pixels are combined with the destination pixels using a hard light blending mode.
	 * This mode is a combination of the multiply and screen blending techniques,
	 * determined by the brightness of the source pixel. If the source pixel's brightness
	 * is lower than 50%, the multiply blending mode is used; otherwise, the screen
	 * blending mode is applied.
	 * <p>
	 * This constant is used to specify the HARD_LIGHT blending mode in the
	 * {@link MiscComposite} class.
	 */
	public static final int HARD_LIGHT = 18;

	/**
	 * Represents the "Pin Light" blend mode constant used in image compositing operations.
	 * This blend mode combines the effects of both the "Lighten" and "Darken" modes,
	 * depending on the brightness of the source color relative to the destination color.
	 * The constant value can be used to specify this blend mode when creating a composite
	 * or performing rendering operations.
	 */
	public static final int PIN_LIGHT = 19;

	/**
	 * Represents the "EXCLUSION" compositing rule used in image processing.
	 * The EXCLUSION rule blends two colors in such a way that it produces an effect
	 * similar to a softer difference mode, creating a distinct but softer contrast
	 * between the source and destination colors.
	 * <p>
	 * This mode is defined with the integer value of 20.
	 * It is primarily used in the context of advanced compositing operations
	 * for graphics and image manipulation.
	 * <p>
	 * Part of the {@code MiscComposite} class and operates as one of the predefined
	 * blending rules.
	 */
	public static final int EXCLUSION = 20;

	/**
	 * Represents the NEGATION compositing rule used in image processing.
	 * This rule inverts the colors by subtracting the absolute values of
	 * source and destination colors from the maximum color value.
	 * It is often used to achieve high contrast effects in image compositing.
	 */
	public static final int NEGATION = 21;

	/**
	 * Represents the blending rule for the "Average" compositing operation in image processing.
	 * This rule computes the pixel values of the resulting image by averaging the corresponding
	 * pixel values from the source and destination images.
	 * <p>
	 * Used in image compositing to achieve a blend effect where equal contributions
	 * from both images are combined.
	 */
	public static final int AVERAGE = 22;

	/**
	 * Represents a compositing rule that defines the stencil blend mode.
	 * The stencil mode typically allows for masking certain parts of an image,
	 * enabling selective compositing based on a defined shape or pattern.
	 * It is used for advanced blending operations in conjunction with
	 * compositing contexts.
	 */
	public static final int STENCIL = 23;

	/**
	 * Represents the compositing rule that creates a silhouette effect. In this mode,
	 * the source image will be masked out by the destination image, leaving only the
	 * non-overlapping regions of the destination visible.
	 * <p>
	 * Defined as a static constant used in conjunction with compositing operations
	 * to specify the rule to be applied.
	 * <p>
	 * Typically used in image processing when a silhouette effect is desired, such
	 * as isolating shapes or creating artistic visual effects.
	 */
	public static final int SILHOUETTE = 24;

	private static final int MIN_RULE = BLEND;
	private static final int MAX_RULE = SILHOUETTE;

	private static String[] RULE_NAMES = {
		"Normal",
		"Add",
		"Subtract",
		"Difference",

		"Multiply",
		"Darken",
		"Burn",
		"Color Burn",

		"Screen",
		"Lighten",
		"Dodge",
		"Color Dodge",

		"Hue",
		"Saturation",
		"Brightness",
		"Color",

		"Overlay",
		"Soft Light",
		"Hard Light",
		"Pin Light",

		"Exclusion",
		"Negation",
		"Average",

		"Stencil",
		"Silhouette",
	};

	private final float extraAlpha;
	private final int rule;

	private MiscComposite(int rule) {
		this(rule, 1.0f);
	}

	private MiscComposite(int rule, float alpha) {
		if (alpha < 0.0f || alpha > 1.0f) {
			throw new IllegalArgumentException("alpha value out of range");
		}
		if (rule < MIN_RULE || rule > MAX_RULE) {
			throw new IllegalArgumentException("unknown composite rule");
		}
		this.rule = rule;
		this.extraAlpha = alpha;
	}

	public static Composite getInstance(int rule, float alpha) {
		switch (rule) {
		case MiscComposite.BLEND:
			return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		case MiscComposite.STENCIL:
			return AlphaComposite.getInstance(AlphaComposite.DST_IN, alpha);
		case MiscComposite.SILHOUETTE:
			return AlphaComposite.getInstance(AlphaComposite.DST_OUT, alpha);
		}
		return new MiscComposite(rule, alpha);
	}

	@Override
	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new MiscCompositeContext(this.rule, this.extraAlpha, srcColorModel, dstColorModel);
	}

	public float getAlpha() {
		return this.extraAlpha;
	}

	public int getRule() {
		return this.rule;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MiscComposite c)) {
			return false;
		}

		if (this.rule != c.rule) {
			return false;
		}
		if (this.extraAlpha != c.extraAlpha) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return (Float.floatToIntBits(this.extraAlpha) * 31 + this.rule);
	}

}
