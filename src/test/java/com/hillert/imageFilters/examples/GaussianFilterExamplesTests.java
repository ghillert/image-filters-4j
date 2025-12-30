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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import com.jhlabs.image.GaussianFilter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A set of test cases for validating the functionality of the {@link GaussianFilter} class.
 * These tests ensure that the GaussianFilter correctly applies a Gaussian blur effect
 * on images while maintaining expected stability and quality.
 * <p>
 * The class also includes a main method to visually demonstrate the blurring effect
 * using a sample image. The visual preview displays the original image and the
 * blurred image side by side, enabling quick verification.
 * <p>
 * Features include:
 *
 * <ul>
 *   <li>Verifying the stability of solid-colored images when passed through the filter.
 *   <li>Loading an image resource from a specified path.
 *   <li>Demonstrating the Gaussian blur effect dynamically via a graphical user interface.
 * </ul>
 *
 * @author Gunnar Hillert
 */
final class GaussianFilterExamplesTests {

	/**
	 * Verifies that applying the {@link GaussianFilter} to a BufferedImage with a solid color
	 * does not alter the pixel values, ensuring stability of the filter for uniform input images.
	 * <p>
	 * This test creates a solid-colored 3x3 image, applies a Gaussian filter with a radius of 2.0f,
	 * and asserts that the pixel at the center of the filtered image retains its original color.
	 */
	@Test
	void filterKeepsSolidImageStable() {
		final BufferedImage source = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
		final int color = new Color(80, 120, 200, 255).getRGB();
		for (int y = 0; y < source.getHeight(); y++) {
			for (int x = 0; x < source.getWidth(); x++) {
				source.setRGB(x, y, color);
			}
		}

		final GaussianFilter gaussianFilter = new GaussianFilter(2.0f);
		final BufferedImage result = gaussianFilter.filter(source, null);

		assertThat(result.getRGB(1, 1)).isEqualTo(color);
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
			final BufferedImage blurred = new GaussianFilter(5.0f).filter(original, null);

			final JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
			panel.add(new JLabel(new ImageIcon(original)));
			panel.add(new JLabel(new ImageIcon(blurred)));

			final JFrame frame = new JFrame("Gaussian Filter Preview");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(panel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}

