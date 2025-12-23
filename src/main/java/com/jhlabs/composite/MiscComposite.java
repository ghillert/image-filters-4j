/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.composite;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public final class MiscComposite implements Composite {

	public final static int BLEND = 0;
	public final static int ADD = 1;
	public final static int SUBTRACT = 2;
	public final static int DIFFERENCE = 3;

	public final static int MULTIPLY = 4;
	public final static int DARKEN = 5;
	public final static int BURN = 6;
	public final static int COLOR_BURN = 7;

	public final static int SCREEN = 8;
	public final static int LIGHTEN = 9;
	public final static int DODGE = 10;
	public final static int COLOR_DODGE = 11;

	public final static int HUE = 12;
	public final static int SATURATION = 13;
	public final static int VALUE = 14;
	public final static int COLOR = 15;

	public final static int OVERLAY = 16;
	public final static int SOFT_LIGHT = 17;
	public final static int HARD_LIGHT = 18;
	public final static int PIN_LIGHT = 19;

	public final static int EXCLUSION = 20;
	public final static int NEGATION = 21;
	public final static int AVERAGE = 22;

	public final static int STENCIL = 23;
	public final static int SILHOUETTE = 24;

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

	protected float extraAlpha;
	protected int rule;

	private MiscComposite(int rule) {
		this(rule, 1.0f);
	}

	private MiscComposite(int rule, float alpha) {
		if (alpha < 0.0f || alpha > 1.0f)
			throw new IllegalArgumentException("alpha value out of range");
		if (rule < MIN_RULE || rule > MAX_RULE)
			throw new IllegalArgumentException("unknown composite rule");
		this.rule = rule;
		this.extraAlpha = alpha;
	}

	public static Composite getInstance(int rule, float alpha) {
		switch ( rule ) {
		case MiscComposite.BLEND:
			return AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha );
		case MiscComposite.STENCIL:
			return AlphaComposite.getInstance( AlphaComposite.DST_IN, alpha );
		case MiscComposite.SILHOUETTE:
			return AlphaComposite.getInstance( AlphaComposite.DST_OUT, alpha );
		}
		return new MiscComposite(rule, alpha);
	}

	public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
		return new MiscCompositeContext( rule, extraAlpha, srcColorModel, dstColorModel );
	}

	public float getAlpha() {
		return extraAlpha;
	}

	public int getRule() {
		return rule;
	}

	public int hashCode() {
		return (Float.floatToIntBits(extraAlpha) * 31 + rule);
	}

	public boolean equals(Object o) {
		if (!(o instanceof MiscComposite))
			return false;
		MiscComposite c = (MiscComposite)o;

		if (rule != c.rule)
			return false;
		if (extraAlpha != c.extraAlpha)
			return false;
		return true;
	}
			
}
