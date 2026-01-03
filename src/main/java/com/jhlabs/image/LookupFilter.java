/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * A filter which uses the brightness of each pixel to lookup a color from a colormap.
 */
public class LookupFilter extends PointFilter {

	private Colormap colormap = new Gradient();

	public LookupFilter() {
		this.canFilterIndexColorModel = true;
	}

	public LookupFilter(Colormap colormap) {
		this.canFilterIndexColorModel = true;
		this.colormap = colormap;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
//		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		rgb = (r + g + b) / 3;
		return this.colormap.getColor(rgb / 255.0f);
	}

	@Override
	public String toString() {
		return "Colors/Lookup...";
	}

}


