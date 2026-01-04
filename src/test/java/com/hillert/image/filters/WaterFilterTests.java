/*
 * MIT License
 *
 * Copyright (c) 2026 Gunnar Hillert
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
package com.hillert.image.filters;

import java.awt.image.BufferedImage;
import java.nio.file.Path;

import com.hillert.image.filters.support.ImageTestUtils;
import com.jhlabs.image.WaterFilter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link com.jhlabs.image.WaterFilter} along with a runnable example.
 *
 * @author Gunnar Hillert
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class WaterFilterTests {

	private static final int IMAGE_WIDTH = 64;
	private static final int IMAGE_HEIGHT = 64;
	private static final Path OUTPUT_DIR = Path.of("target", "water-filter");

	/**
	 * Verifies that pixels within the configured radius are altered by the filter.
	 */
	@Test
	void shouldDistortPixelsWithinRadius() {
		BufferedImage source = createGradientImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WaterFilter filter = buildFilter(20f, 12f, 0.5f, 0.5f, 24f);
		BufferedImage result = filter.filter(source, null);

		assertTrue(hasAnyPixelDifference(source, result),
				"Expected at least one pixel to differ after applying the water filter.");
	}

	/**
	 * Verifies that pixels outside the radius remain unchanged.
	 */
	@Test
	void shouldLeavePixelsOutsideRadiusUntouched() {
		BufferedImage source = createGradientImage(IMAGE_WIDTH, IMAGE_HEIGHT);
		WaterFilter filter = buildFilter(15f, 10f, 0.5f, 0.5f, 16f);
		BufferedImage result = filter.filter(source, null);

		assertEquals(source.getRGB(0, 0), result.getRGB(0, 0),
				"Pixels beyond the radius should remain untouched.");
	}

	/**
	 * Builds a configured {@link WaterFilter}.
	 *
	 * @param amplitude desired amplitude
	 * @param wavelength desired wavelength
	 * @param centreX horizontal center fraction
	 * @param centreY vertical center fraction
	 * @param radius ripple radius
	 * @return configured filter instance
	 */
	private static WaterFilter buildFilter(float amplitude, float wavelength, float centreX, float centreY, float radius) {
		WaterFilter filter = new WaterFilter();
		filter.setAmplitude(amplitude);
		filter.setWavelength(wavelength);
		filter.setCentreX(centreX);
		filter.setCentreY(centreY);
		filter.setRadius(radius);
		return filter;
	}

	/**
	 * Creates a simple ARGB gradient image for deterministic assertions.
	 *
	 * @param width desired width
	 * @param height desired height
	 * @return gradient image
	 */
	private static BufferedImage createGradientImage(int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int alpha = 0xFF;
				int red = (x * 255) / (width - 1);
				int green = (y * 255) / (height - 1);
				int blue = ((x + y) * 255) / (width + height - 2);
				int rgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}

	/**
	 * Checks whether two images differ at any pixel.
	 *
	 * @param original source image
	 * @param processed filtered image
	 * @return {@code true} if at least one pixel differs
	 */
	private static boolean hasAnyPixelDifference(BufferedImage original, BufferedImage processed) {
		for (int y = 0; y < original.getHeight(); y++) {
			for (int x = 0; x < original.getWidth(); x++) {
				if (original.getRGB(x, y) != processed.getRGB(x, y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Entry point for the program, which demonstrates the application of the {@link WaterFilter}
	 * to an image and displays the original and processed images side by side.
	 * @param args command-line arguments (not used in this program)
	 */
	public static void main(final String[] args) {

		final BufferedImage originalImage = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		final BufferedImage waterFilterImage = new WaterFilter().filter(originalImage, null);

		ImageTestUtils.showSwingUI(
				originalImage, waterFilterImage, "Water Filter Preview");
	}
}

