/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.math.BinaryFunction;
import com.jhlabs.math.BlackFunction;

/**
 * A base class for filters which convert an image to black and white.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public abstract class BinaryFilter extends WholeImageFilter {

	protected int newColor = 0xff000000;
	protected BinaryFunction blackFunction = new BlackFunction();
	protected int iterations = 1;
	protected Colormap colormap;

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return this.iterations;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	public void setNewColor(int newColor) {
		this.newColor = newColor;
	}

	public int getNewColor() {
		return this.newColor;
	}

	public void setBlackFunction(BinaryFunction blackFunction) {
		this.blackFunction = blackFunction;
	}

	public BinaryFunction getBlackFunction() {
		return this.blackFunction;
	}

}

