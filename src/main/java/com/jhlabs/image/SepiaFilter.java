/*
 * MIT License
 *
 * Copyright (c) 2025 Gunnar Hillert
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package com.jhlabs.image;

/**
 * A filter which applies a sepia tone to an image that can be applied to an RGB image
 * by applying a 3×3 transformation directly to RGB without having to explicitly convert
 * to grayscale first.
 *
 * @author Gunnar Hillert
 */
public class SepiaFilter extends PointFilter {

	public SepiaFilter() {
		this.canFilterIndexColorModel = true;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		int sepiaR = Math.min(255, (int) Math.round(0.393 * r + 0.769 * g + 0.189 * b));
		int sepiaG = Math.min(255, (int) Math.round(0.349 * r + 0.686 * g + 0.168 * b));
		int sepiaB = Math.min(255, (int) Math.round(0.272 * r + 0.534 * g + 0.131 * b));

		return a | (sepiaR << 16) | (sepiaG << 8) | sepiaB;
	}

	@Override
	public String toString() {
		return "Colors/Sepia Out";
	}

}


