/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Point;

/**
 * A Filter to pixellate images.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class BlockFilter extends TransformFilter {

	static final long serialVersionUID = 8077109551486196569L;

	private int blockSize = 2;

	/**
	 * Set the pixel block size.
	 * @param blockSize the number of pixels along each block edge
	 */
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}

	/**
	 * Get the pixel block size.
	 * @return the number of pixels along each block edge
	 */
	public int getBlockSize() {
		return this.blockSize;
	}


	/**
	 * Construct a BlockFilter.
	 */
	public BlockFilter() {
	}

	protected void transform(int x, int y, Point out) {
		out.x = (x / this.blockSize) * this.blockSize;
		out.y = (y / this.blockSize) * this.blockSize;
	}

	@Override
	protected void transformInverse(int x, int y, float[] out) {
		out[0] = (x / this.blockSize) * this.blockSize;
		out[1] = (y / this.blockSize) * this.blockSize;
	}

	@Override
	public String toString() {
		return "Stylize/Mosaic...";
	}
}

