/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.math.Function2D;

/**
 * The MapFilter class is used to distort image coordinates by mapping them through
 * specified two-dimensional functions. It extends the {@link TransformFilter} class and
 * allows custom transformations of the x and y coordinates of an image pixel.
 * <p>
 * The two-dimensional mapping functions for x and y coordinates are provided as
 * instances of the {@code Function2D} interface, which must implement a method
 * to evaluate the mapping based on input coordinates.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class MapFilter extends TransformFilter {

	private Function2D xMapFunction;
	private Function2D yMapFunction;

	public MapFilter() {
	}

	public void setXMapFunction(Function2D xMapFunction) {
		this.xMapFunction = xMapFunction;
	}

	public Function2D getXMapFunction() {
		return this.xMapFunction;
	}

	public void setYMapFunction(Function2D yMapFunction) {
		this.yMapFunction = yMapFunction;
	}

	public Function2D getYMapFunction() {
		return this.yMapFunction;
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		float xMap;
		float yMap;
		xMap = this.xMapFunction.evaluate(x, y);
		yMap = this.yMapFunction.evaluate(x, y);
		out[0] = xMap * this.transformedSpace.width;
		out[1] = yMap * this.transformedSpace.height;
	}

	@Override
	public String toString() {
		return "Distort/Map Coordinates...";
	}
}
