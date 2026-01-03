/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.util.Random;

/**
 * A filter which adds random noise into an image.
 */
public class NoiseFilter extends PointFilter {

	public final static int GAUSSIAN = 0;
	public final static int UNIFORM = 1;

	private int amount = 25;
	private int distribution = UNIFORM;
	private boolean monochrome = false;
	private final Random randomNumbers = new Random();

	public NoiseFilter() {
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
		x += (int) (((this.distribution == GAUSSIAN ? this.randomNumbers.nextGaussian() : 2 * this.randomNumbers.nextFloat() - 1)) * this.amount);
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
			int n = (int) (((this.distribution == GAUSSIAN ? this.randomNumbers.nextGaussian() : 2 * this.randomNumbers.nextFloat() - 1)) * this.amount);
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
