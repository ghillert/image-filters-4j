/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * Sets the opacity (alpha) of every pixel in an image to a constant value.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class OpacityFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 5644263685527598345L;

	private int opacity;
	private int opacity24;

	/**
	 * Construct an OpacityFilter with 50% opacity.
	 */
	public OpacityFilter() {
		this(0x88);
	}

	/**
	 * Construct an OpacityFilter with the given opacity (alpha).
	 * @param opacity the opacity (alpha) in the range 0..255
	 */
	public OpacityFilter(int opacity) {
		setOpacity(opacity);
	}

	/**
	 * Set the opacity.
	 * @param opacity the opacity (alpha) in the range 0..255
	 */
	public void setOpacity(int opacity) {
		this.opacity = opacity;
		this.opacity24 = opacity << 24;
	}

	/**
	 * Get the opacity setting.
	 * @return the opacity
	 */
	public int getOpacity() {
		return this.opacity;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		if ((rgb & 0xff000000) != 0) {
			return (rgb & 0xffffff) | this.opacity24;
		}
		return rgb;
	}

	@Override
	public String toString() {
		return "Colors/Transparency...";
	}

}

