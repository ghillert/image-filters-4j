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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.hillert.image.filters.support.ImageTestUtils;
import com.jhlabs.image.CausticsFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-style tests that verify the {@link CausticsFilter}.
 */
public class CausticsFilterTests {

	private static final int FRAME_COUNT = 60;
	private static final int FRAME_DELAY_MILLIS = 60;

	/**
	 * Ensures the filter produces non-uniform highlights while preserving the source dimensions.
	 */
	@Test
	void generatesCausticsPatternForSampleImage() {
		BufferedImage source = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		CausticsFilter filter = buildFilter(24f, 12, 1.15f, 1.5f, 3, 0xff153050);

		BufferedImage result = filter.filter(source, null);

		assertThat(result.getWidth()).isEqualTo(source.getWidth());
		assertThat(result.getHeight()).isEqualTo(source.getHeight());
		assertThat(hasHighlights(result, filter.getBgColor())).isTrue();
	}

	/**
	 * Confirms that a higher brightness setting increases the overall luminance.
	 */
	@Test
	void higherBrightnessProducesBrighterResult() {
		BufferedImage source = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);

		CausticsFilter dimFilter = buildFilter(24f, 6, 1.15f, 1.5f, 2, 0xff102030);
		CausticsFilter brightFilter = buildFilter(24f, 20, 1.15f, 1.5f, 2, 0xff102030);

		BufferedImage dimResult = dimFilter.filter(source, null);
		BufferedImage brightResult = brightFilter.filter(source, null);

		assertThat(totalLuminance(brightResult)).isGreaterThan(totalLuminance(dimResult));
	}

	/**
	 * Builds a configured {@link CausticsFilter}.
	 *
	 * @param scale texture scale
	 * @param brightness brightness per sample
	 * @param amount caustics strength
	 * @param turbulence turbulence/octaves value
	 * @param samples number of samples per pixel
	 * @param backgroundColor base background color
	 * @return configured filter instance
	 */
	private static CausticsFilter buildFilter(float scale, int brightness, float amount, float turbulence,
											int samples, int backgroundColor) {
		CausticsFilter filter = new CausticsFilter();
		filter.setScale(scale);
		filter.setBrightness(brightness);
		filter.setAmount(amount);
		filter.setTurbulence(turbulence);
		filter.setSamples(samples);
		filter.setBgColor(backgroundColor);
		return filter;
	}

	/**
	 * Determines whether the rendered image contains highlights beyond the background color.
	 *
	 * @param image rendered caustics image
	 * @param backgroundColor expected base color
	 * @return {@code true} if any pixel differs from the background color
	 */
	private static boolean hasHighlights(BufferedImage image, int backgroundColor) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				if (image.getRGB(x, y) != backgroundColor) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Computes the aggregate RGB luminance for comparison purposes.
	 *
	 * @param image rendered caustics image
	 * @return sum of all RGB components
	 */
	private static long totalLuminance(BufferedImage image) {
		long sum = 0L;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int rgb = image.getRGB(x, y);
				sum += ((rgb >> 16) & 0xff);
				sum += ((rgb >> 8) & 0xff);
				sum += (rgb & 0xff);
			}
		}
		return sum;
	}

	/**
	 * Demonstrates the caustics filter by rendering a preview next to the original image.
	 *
	 * @param args command-line arguments (unused)
	 */
	public static void main(String[] args) {
		final BufferedImage originalImage = ImageTestUtils.loadImage(ImageTestUtils.TEST_IMAGE_RESOURCE);
		final CausticsFilter demoFilter = buildFilter(28f, 14, 1.2f, 2.0f, 3, 0xff0f1f2f);
		demoFilter.setTime(0.15f);

		// Create a static image
		// BufferedImage causticsImage = demoFilter.filter(originalImage, null);
		// ImageTestUtils.showSwingUI(originalImage, causticsImage, "Caustics Filter Preview");

		// Display an animation
		final BufferedImage[] frames = buildAnimationFrames(originalImage, demoFilter, FRAME_COUNT);
		showAnimation(frames, "Caustics Filter Animation");
	}

	/**
	 * Displays the generated frames as a looping Swing animation.
	 *
	 * @param frames frames to display
	 * @param title window title
	 */
	private static void showAnimation(BufferedImage[] frames, String title) {
		ImageIcon[] icons = new ImageIcon[frames.length];
		for (int i = 0; i < frames.length; i++) {
			icons[i] = new ImageIcon(frames[i]);
		}
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame(title);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JLabel label = new JLabel(icons[0]);
			frame.add(label);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			int[] indexHolder = {0};
			Timer timer = new Timer(FRAME_DELAY_MILLIS, (event) -> {
				indexHolder[0] = (indexHolder[0] + 1) % icons.length;
				label.setIcon(icons[indexHolder[0]]);
			});
			timer.start();
		});
	}

	/**
	 * Renders multiple frames while varying time, amount, and turbulence to create motion.
	 *
	 * @param source base image
	 * @param filter filter to reuse across frames
	 * @param frameCount number of frames to render
	 * @return array of rendered frames
	 */
	private static BufferedImage[] buildAnimationFrames(BufferedImage source, CausticsFilter filter, int frameCount) {
		BufferedImage[] frames = new BufferedImage[frameCount];
		for (int i = 0; i < frameCount; i++) {
			float progress = (float) i / frameCount;
			float wave = (float) Math.sin(progress * 2.0f * Math.PI);
			filter.setTime(progress * 4.0f * (float) Math.PI);
			filter.setAmount(1.1f + 0.4f * wave);
			filter.setTurbulence(1.4f + 0.4f * (wave + 1.0f));
			frames[i] = filter.filter(source, null);
		}
		return frames;
	}
}
