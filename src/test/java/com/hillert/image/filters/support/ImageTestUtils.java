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
package com.hillert.image.filters.support;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Utility class for image-related test operations such as loading images from resources.
 *
 * @author Gunnar Hillert
 */
public final class ImageTestUtils {

	public static final String TEST_IMAGE_RESOURCE = "images/image-filters-4j.png";

	/**
	 * Loads an image from the specified resource path.
	 * The method locates the image resource using the context class loader and reads it into a {@link BufferedImage}.
	 * If the resource cannot be found or cannot be read, an exception is thrown.
	 * @param resourcePath the path to the image resource within the classpath
	 * @return a {@link BufferedImage} representing the image loaded from the resource
	 * @throws IllegalStateException if the resource cannot be found, or if an I/O error occurs while loading the image
	 */
	public static BufferedImage loadImage(final String resourcePath) {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
			if (inputStream == null) {
				throw new IllegalStateException("Resource not found: " + resourcePath);
			}
			return ImageIO.read(inputStream);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to read image at " + resourcePath, ex);
		}
	}

	/**
	 * Displays two images side by side in a Swing UI with a specified title.
	 * @param originalImage the original image to display
	 * @param filteredImage the filtered image to display
	 * @param title the title for the Swing UI window
	 */
	public static void showSwingUI(
			BufferedImage originalImage,
			BufferedImage filteredImage,
			String title) {

		SwingUtilities.invokeLater(() -> {
			final JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
			panel.add(new JLabel(new ImageIcon(originalImage)));
			panel.add(new JLabel(new ImageIcon(filteredImage)));

			final JFrame frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			frame.add(panel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}
}
