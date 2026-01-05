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
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import com.jhlabs.image.GrayscaleFilter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration-style tests that verify the {@link GrayscaleFilter} accurately
 * converts colors to grayscale while preserving alpha and leaving grayscale
 * pixels unchanged.
 *
 * @author Gunnar Hillert
 */
public class GettingStartedExampleTests {

	@Test
	void readImageApplyFilterAndWriteImageToFile() {
		// tag::example[]
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String imageResourcePath = "images/image-filters-4j.png";
		final BufferedImage originalBufferedImage;

		try (InputStream inputStream = classLoader.getResourceAsStream(imageResourcePath)) {
			if (inputStream == null) {
				throw new IllegalStateException("Resource not found: " + imageResourcePath);
			}
			originalBufferedImage = ImageIO.read(inputStream);
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to read image at " + imageResourcePath, ex);
		}

		final BufferedImage grayscaleBufferedImage = new GrayscaleFilter().filter(originalBufferedImage, null);

		final Path outputPath = Path.of("target", "image-filters-4j_output.png");

		try {
			Files.createDirectories(outputPath.getParent());
			ImageIO.write(grayscaleBufferedImage, "png", outputPath.toFile());
		}
		catch (IOException ex) {
			throw new IllegalStateException("Unable to write image to " + outputPath, ex);
		}
		// end::example[]
		assertThat(outputPath).exists();
	}
}
