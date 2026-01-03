/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.composite;

import java.awt.*;
import java.awt.image.ColorModel;

/**
 * A set of composite rules for blending images.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public final class MiscComposite implements Composite {

	public static final int BLEND = 0;
	public static final int ADD = 1;
	public static final int SUBTRACT = 2;
	public static final int DIFFERENCE = 3;

	public static final int MULTIPLY = 4;
	public static final int DARKEN = 5;
	public static final int BURN = 6;
	public static final int COLOR_BURN = 7;

	public static final int SCREEN = 8;
	public static final int LIGHTEN = 9;
	public static final int DODGE = 10;
	public static final int COLOR_DODGE = 11;

	public static final int HUE = 12;
	public static final int SATURATION = 13;
	public static final int VALUE = 14;
	public static final int COLOR = 15;

	public static final int OVERLAY = 16;
	public static final int SOFT_LIGHT = 17;
	public static final int HARD_LIGHT = 18;
	public static final int PIN_LIGHT = 19;

	public static final int EXCLUSION = 20;
	public static final int NEGATION = 21;
	public static final int AVERAGE = 22;

	public static final int STENCIL = 23;
	public static final int SILHOUETTE = 24;

	private static final int MIN_RULE = BLEND;
	private static final int MAX_RULE = SILHOUETTE;

	public static String[] RULE_NAMES = {
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
