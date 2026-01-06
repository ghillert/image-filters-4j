/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.util.Random;

/**
 * A filter which adds random noise into an image.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class NoiseFilter extends PointFilter {

	/**
	 * Denotes the Gaussian distribution type for generating random noise.
	 * This constant is used to specify that the noise should follow a Gaussian
	 * (normal) distribution when being applied to an image.
	 */
	public static final int GAUSSIAN = 0;

	/**
	 * Denotes the uniform distribution type for generating random noise.
	 * This constant is used to specify that the noise should follow a uniform
	 * distribution when being applied to an image.
	 */
	public static final int UNIFORM = 1;

	private int amount = 25;
	private int distribution = UNIFORM;
	private boolean monochrome = false;
	private final Random randomNumbers = new Random();

	/**
	 * Constructs a new NoiseFilter instance with default settings.
	 * <p>
	 * The default configuration includes:
	 * - A noise amount of 25.
	 * - A uniform distribution for noise generation.
	 * - Color noise (monochrome disabled).
	 * <p>
	 * This filter can be applied to images to add random noise, either using
	 * a Gaussian or uniform distribution, and optionally in monochrome or full color.
	 */
	public NoiseFilter() {
		super();
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setDistribution(int distribution) {
		this.distribution = distribution;
	}

	public int getDistribution() {
		return this.distribution;
	}

	public void setMonochrome(boolean monochrome) {
		this.monochrome = monochrome;
	}

	public boolean getMonochrome() {
		return this.monochrome;
	}

	private int random(int x) {
		x += (int) ((((this.distribution == GAUSSIAN) ? this.randomNumbers.nextGaussian() : ((2 * this.randomNumbers.nextFloat()) - 1))) * this.amount);
		if (x < 0) {
			x = 0;
		}
		else if (x > 0xff) {
			x = 0xff;
		}
		return x;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		if (this.monochrome) {
			int n = (int) ((((this.distribution == GAUSSIAN) ? this.randomNumbers.nextGaussian() : ((2 * this.randomNumbers.nextFloat()) - 1))) * this.amount);
			r = PixelUtils.clamp(r + n);
			g = PixelUtils.clamp(g + n);
			b = PixelUtils.clamp(b + n);
		}
		else {
			r = random(r);
			g = random(g);
			b = random(b);
		}
		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Stylize/Add Noise...";
	}
}
