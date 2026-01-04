/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * The {@code PinchFilter} class is a specific implementation of the {@code TransformFilter}
 * that applies a pinch distortion effect to an image. This effect creates a stretching or
 * compressing of the image pixels based on their position relative to the center of the image.
 * <p>
 * The filter modifies the pixel coordinates by applying a sinusoidal transformation,
 * which distorts the image in a unique "pinch" style. The distortion effect is most
 * noticeable near the center of the image and diminishes outward.
 * <p>
 * This class overrides the {@code transformInverse} method to define the mapping of the
 * source image pixels to the resultant output image after distortion. It also provides
 * a custom string representation of the effect.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class PinchFilter extends TransformFilter {

	static final long serialVersionUID = -3768964940276766810L;

	public PinchFilter() {
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		int m = this.transformedSpace.width / 2;
		out[0] = x + (int) ((x - m) * Math.sin(Math.PI * (float) y / this.transformedSpace.height));
		out[1] = y;
	}

	@Override
	public String toString() {
		return "Distort/Pinch";
	}

}
