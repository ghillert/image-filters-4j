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
package com.hillert.imageFilters.examples;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.jhlabs.image.GaussianFilter;
import org.junit.jupiter.api.Test;

import com.jhlabs.image.GrayscaleFilter;

import javax.swing.*;

/**
 * Integration-style tests that verify the {@link GrayscaleFilter} accurately
 * converts colors to grayscale while preserving alpha and leaving grayscale
 * pixels unchanged.
 *
 * @author Gunnar Hillert
 */
final class GrayscaleFilterExamplesTests {

	private final GrayscaleFilter filter = new GrayscaleFilter();

	/**
	 * Verifies that a colorful pixel is converted to the expected NTSC luma
	 * intensity on every RGB channel.
	 */
	@Test
	void convertsColorPixelToExpectedLuma() {
		final BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final int original = new Color(40, 100, 200, 255).getRGB();
		source.setRGB(0, 0, original);

		final int converted = filter.filter(source, null).getRGB(0, 0);
		final int expectedLuma = computeExpectedLuma(original);

		assertThat(extractRed(converted)).isEqualTo(expectedLuma);
		assertThat(extractGreen(converted)).isEqualTo(expectedLuma);
		assertThat(extractBlue(converted)).isEqualTo(expectedLuma);
	}

	/**
	 * Ensures that the alpha channel of each pixel remains unchanged after
	 * applying the grayscale filter.
	 */
	@Test
	void preservesAlphaChannel() {
		final BufferedImage source = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		final int original = new Color(60, 80, 90, 0x55).getRGB();
		source.setRGB(0, 0, original);

		final int converted = filter.filter(source, null).getRGB(0, 0);

		assertThat(extractAlpha(converted)).isEqualTo(0x55);
	}

	/**
	 * Confirms that pixels which are already grayscale are left untouched by
	 * the filter, guaranteeing idempotency.
	 */
	@Test
	void leavesAlreadyGrayscalePixelsUnchanged() {
		final BufferedImage source = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
		final int gray = new Color(120, 120, 120, 255).getRGB();
		fillImage(source, gray);

		final BufferedImage result = filter.filter(source, null);

		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				assertThat(result.getRGB(x, y)).isEqualTo(gray);
			}
		}
	}

	private static void fillImage(final BufferedImage image, final int color) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				image.setRGB(x, y, color);
			}
		}
	}

	private static int computeExpectedLuma(final int argb) {
		final int r = (argb >> 16) & 0xff;
		final int g = (argb >> 8) & 0xff;
		final int b = argb & 0xff;
		return (r * 77 + g * 151 + b * 28) >> 8;
	}

	private static int extractAlpha(final int argb) {
		return (argb >>> 24) & 0xff;
	}

	private static int extractRed(final int argb) {
		return (argb >> 16) & 0xff;
	}

	private static int extractGreen(final int argb) {
		return (argb >> 8) & 0xff;
	}

	private static int extractBlue(final int argb) {
		return argb & 0xff;
	}

	/**
	 * Entry point for the program, which demonstrates the application of a Gaussian blur
	 * filter on an image and displays the original and processed images side by side.
	 *
	 * @param args command-line arguments (not used in this program)
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(() -> {
			final BufferedImage original = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
			final BufferedImage grayscaleImage = new GrayscaleFilter().filter(original, null);

			final JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
			panel.add(new JLabel(new ImageIcon(original)));
			panel.add(new JLabel(new ImageIcon(grayscaleImage)));

			final JFrame frame = new JFrame("Grayscale Filter Preview");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(panel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
