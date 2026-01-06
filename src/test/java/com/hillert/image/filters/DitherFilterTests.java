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

import com.hillert.image.filters.support.ImageTestUtils;
import com.jhlabs.image.DitherFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-style tests that verify the {@link DitherFilter}.
 *
 * @author Gunnar Hillert
 */
public class DitherFilterTests {

	private final DitherFilter filter = new DitherFilter();

	/**
	 * Applies the dither filter to the sample image and ensures dimensions and alpha
	 * are preserved while at least one pixel is changed.
	 */
	@Test
	void appliesDitherFilterToSampleImage() {
		final BufferedImage original = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		final BufferedImage result = this.filter.filter(original, null);

		assertThat(result.getWidth()).isEqualTo(original.getWidth());
		assertThat(result.getHeight()).isEqualTo(original.getHeight());

		boolean pixelChanged = false;

		for (int y = 0; y < original.getHeight(); y++) {
			for (int x = 0; x < original.getWidth(); x++) {
				final int originalRgb = original.getRGB(x, y);
				final int ditheredRgb = result.getRGB(x, y);

				assertThat(alpha(ditheredRgb)).isEqualTo(alpha(originalRgb));

				if (originalRgb != ditheredRgb) {
					pixelChanged = true;
				}
			}
		}

		assertThat(pixelChanged).isTrue();
	}

	private static int alpha(final int argb) {
		return (argb >>> 24) & 0xff;
	}

	/**
	 * Entry point for the program, which demonstrates the application of a Dither
	 * filter to an image and displays the original and processed images side by side.
	 * @param args command-line arguments (not used in this program)
	 */
	public static void main(final String[] args) {
		final BufferedImage originalImage = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		final DitherFilter ditherFilter = new DitherFilter();
		ditherFilter.setColorDither(false);
		ditherFilter.setLevels(2);
		ditherFilter.setMatrix(DitherFilter.ditherMagic2x2Matrix);
		final BufferedImage filteredImage = ditherFilter.filter(originalImage, null);
		ImageTestUtils.showSwingUI(originalImage, filteredImage, "Dither Filter Preview");
	}

}
