/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;

/**
 * Given a binary image, this filter converts it to its outline, replacing all interior pixels with the 'new' color.
 */
public class OutlineFilter extends BinaryFilter {

	public OutlineFilter() {
		this.newColor = 0xffffffff;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int index = 0;
		int[] outPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = inPixels[y * width + x];
				if (this.blackFunction.isBlack(pixel)) {
					int neighbours = 0;

					for (int dy = -1; dy <= 1; dy++) {
						int iy = y + dy;
						int ioffset;
						if (0 <= iy && iy < height) {
							ioffset = iy * width;
							for (int dx = -1; dx <= 1; dx++) {
								int ix = x + dx;
								if (!(dy == 0 && dx == 0) && 0 <= ix && ix < width) {
									int rgb = inPixels[ioffset + ix];
									if (this.blackFunction.isBlack(rgb)) {
										neighbours++;
									}
								}
								else {
									neighbours++;
								}
							}
						}
					}

					if (neighbours == 9) {
						pixel = this.newColor;
					}
				}
				outPixels[index++] = pixel;
			}

		}
		return outPixels;
	}

	@Override
	public String toString() {
		return "Binary/Outline...";
	}

}

