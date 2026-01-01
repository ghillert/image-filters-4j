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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import com.jhlabs.image.SepiaFilter;
import org.junit.jupiter.api.Test;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-style tests that verify the {@link SepiaFilter}.
 *
 * @author Gunnar Hillert
 */
final class SepiaFilterExamplesTests {

	private final SepiaFilter filter = new SepiaFilter();

	/**
	 * Applies the sepia filter to the shared test image and ensures dimensions and
	 * alpha are preserved while at least one pixel is converted to a sepia tone.
	 */
	@Test
	void appliesSepiaToneToSampleImage() {
		final BufferedImage original = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		final BufferedImage result = filter.filter(original, null);

		assertThat(result.getWidth()).isEqualTo(original.getWidth());
		assertThat(result.getHeight()).isEqualTo(original.getHeight());

		boolean pixelChanged = false;

		for (int y = 0; y < original.getHeight(); y++) {
			for (int x = 0; x < original.getWidth(); x++) {
				final int originalRgb = original.getRGB(x, y);
				final int sepiaRgb = result.getRGB(x, y);

				assertThat(alpha(sepiaRgb)).isEqualTo(alpha(originalRgb));

				if (originalRgb != sepiaRgb) {
					assertThat(red(sepiaRgb)).isGreaterThanOrEqualTo(green(sepiaRgb));
					assertThat(green(sepiaRgb)).isGreaterThanOrEqualTo(blue(sepiaRgb));
					pixelChanged = true;
				}
			}
		}

		assertThat(pixelChanged).isTrue();
	}

	private static int alpha(final int argb) {
		return (argb >>> 24) & 0xff;
	}

	private static int red(final int argb) {
		return (argb >> 16) & 0xff;
	}

	private static int green(final int argb) {
		return (argb >> 8) & 0xff;
	}

	private static int blue(final int argb) {
		return argb & 0xff;
	}

	/**
	 * Entry point for the program, which demonstrates the application of a Sepia filter
	 * to an image and displays the original and processed images side by side.
	 * @param args command-line arguments (not used in this program)
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(() -> {
			final BufferedImage original = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
			final BufferedImage grayscaleImage = new SepiaFilter().filter(original, null);

			final JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
			panel.add(new JLabel(new ImageIcon(original)));
			panel.add(new JLabel(new ImageIcon(grayscaleImage)));

			final JFrame frame = new JFrame("Sepia Filter Preview");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(panel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
