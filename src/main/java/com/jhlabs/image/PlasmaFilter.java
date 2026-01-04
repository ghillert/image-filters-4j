/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Random;

/**
 * A filter that generates a plasma texture effect on an image.
 * The filter uses a random displacement method to create a colorful, turbulent plasma-like visual effect.
 * Extends the {@link WholeImageFilter} class to operate on the entire image and implements the {@link java.io.Serializable} interface.
 * <p>
 * The following key features are available:
 * <ul>
 * <li>Turbulence: Controls the intensity of randomness in the plasma effect.
 * <li>Scaling: Specifies the scaling factor for the displacement effect.
 * <li>Custom Colormap: Allows specifying a colormap to define the color scheme for the plasma effect.
 * <li>Use of Image Colors: Optionally use the original image colors as a base for the plasma generation.
 * </ul>
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class PlasmaFilter extends WholeImageFilter implements java.io.Serializable {

	static final long serialVersionUID = 6491871753122667752L;

	public float turbulence = 1.0f;
	private float scaling = 0.0f;
	private Colormap colormap = new LinearColormap();
	private final Random randomGenerator;
	private long seed = 567;
	private boolean useImageColors = false;

	public PlasmaFilter() {
		this.randomGenerator = new Random();
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return this.turbulence;
	}

	public void setScaling(float scaling) {
		this.scaling = scaling;
	}

	public float getScaling() {
		return this.scaling;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	public void setUseImageColors(boolean useImageColors) {
		this.useImageColors = useImageColors;
	}

	public boolean getUseImageColors() {
		return this.useImageColors;
	}

	public void randomize() {
		this.seed = new Date().getTime();
	}

	private int randomRGB(int[] inPixels, int x, int y) {
		if (this.useImageColors) {
			return inPixels[y * this.originalSpace.width + x];
		}
		else {
			int r = (int) (255 * this.randomGenerator.nextFloat());
			int g = (int) (255 * this.randomGenerator.nextFloat());
			int b = (int) (255 * this.randomGenerator.nextFloat());
			return 0xff000000 | (r << 16) | (g << 8) | b;
		}
	}

	private int displace(int rgb, float amount) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r = PixelUtils.clamp(r + (int) (amount * (this.randomGenerator.nextFloat() - 0.5)));
		g = PixelUtils.clamp(g + (int) (amount * (this.randomGenerator.nextFloat() - 0.5)));
		b = PixelUtils.clamp(b + (int) (amount * (this.randomGenerator.nextFloat() - 0.5)));
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	private int average(int rgb1, int rgb2) {
		return PixelUtils.combinePixels(rgb1, rgb2, PixelUtils.AVERAGE);
	}

	private int getPixel(int x, int y, int[] pixels, int stride) {
		return pixels[y * stride + x];
	}

	private void putPixel(int x, int y, int rgb, int[] pixels, int stride) {
		pixels[y * stride + x] = rgb;
	}

	private boolean doPixel(int x1, int y1, int x2, int y2, int[] pixels, int stride, int depth, int scale) {
		int mx;
		int my;

		if (depth == 0) {
			int ml;
			int mr;
			int mt;
			int mb;
			int mm;
			int t;

			int tl = getPixel(x1, y1, pixels, stride);
			int bl = getPixel(x1, y2, pixels, stride);
			int tr = getPixel(x2, y1, pixels, stride);
			int br = getPixel(x2, y2, pixels, stride);

			float amount = (256.0f / (2.0f * scale)) * this.turbulence;

			mx = (x1 + x2) / 2;
			my = (y1 + y2) / 2;

			if (mx == x1 && mx == x2 && my == y1 && my == y2) {
				return true;
			}

			if (mx != x1 || mx != x2) {
				ml = average(tl, bl);
				ml = displace(ml, amount);
				putPixel(x1, my, ml, pixels, stride);

				if (x1 != x2) {
					mr = average(tr, br);
					mr = displace(mr, amount);
					putPixel(x2, my, mr, pixels, stride);
				}
			}

			if (my != y1 || my != y2) {
				if (x1 != mx || my != y2) {
					mb = average(bl, br);
					mb = displace(mb, amount);
					putPixel(mx, y2, mb, pixels, stride);
				}

				if (y1 != y2) {
					mt = average(tl, tr);
					mt = displace(mt, amount);
					putPixel(mx, y1, mt, pixels, stride);
				}
			}

			if (y1 != y2 || x1 != x2) {
				mm = average(tl, br);
				t = average(bl, tr);
				mm = average(mm, t);
				mm = displace(mm, amount);
				putPixel(mx, my, mm, pixels, stride);
			}

			return x2 - x1 >= 3 || y2 - y1 >= 3;
		}

		mx = (x1 + x2) / 2;
		my = (y1 + y2) / 2;

		doPixel(x1, y1, mx, my, pixels, stride, depth - 1, scale + 1);
		doPixel(x1, my, mx, y2, pixels, stride, depth - 1, scale + 1);
		doPixel(mx, y1, x2, my, pixels, stride, depth - 1, scale + 1);
		return doPixel(mx, my, x2, y2, pixels, stride, depth - 1, scale + 1);
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int[] outPixels = new int[width * height];

		this.randomGenerator.setSeed(this.seed);

		int w1 = width - 1;
		int h1 = height - 1;
		putPixel(0, 0, randomRGB(inPixels, 0, 0), outPixels, width);
		putPixel(w1, 0, randomRGB(inPixels, w1, 0), outPixels, width);
		putPixel(0, h1, randomRGB(inPixels, 0, h1), outPixels, width);
		putPixel(w1, h1, randomRGB(inPixels, w1, h1), outPixels, width);
		putPixel(w1 / 2, h1 / 2, randomRGB(inPixels, w1 / 2, h1 / 2), outPixels, width);
		putPixel(0, h1 / 2, randomRGB(inPixels, 0, h1 / 2), outPixels, width);
		putPixel(w1, h1 / 2, randomRGB(inPixels, w1, h1 / 2), outPixels, width);
		putPixel(w1 / 2, 0, randomRGB(inPixels, w1 / 2, 0), outPixels, width);
		putPixel(w1 / 2, h1, randomRGB(inPixels, w1 / 2, h1), outPixels, width);

		int depth = 1;
		while (doPixel(0, 0, width - 1, height - 1, outPixels, width, depth, 0)) {
			depth++;
		}

		if (this.colormap != null) {
			int index = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					outPixels[index] = this.colormap.getColor((outPixels[index] & 0xff) / 255.0f);
					index++;
				}
			}
		}
		return outPixels;
	}

	@Override
	public String toString() {
		return "Texture/Plasma...";
	}

}
