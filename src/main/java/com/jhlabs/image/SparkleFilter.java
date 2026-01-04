/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.util.Random;

public class SparkleFilter extends PointFilter implements java.io.Serializable {

	static final long serialVersionUID = 1692413049411710802L;

	private int rays = 50;
	private int radius = 25;
	private int amount = 50;
	private int color = 0xffffffff;
	private int randomness = 25;
	private int width;
	private int height;
	private int centreX;
	private int centreY;
	private final long seed = 371;
	private float[] rayLengths;
	private final Random randomNumbers = new Random();

	public SparkleFilter() {
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	public void setRandomness(int randomness) {
		this.randomness = randomness;
	}

	public int getRandomness() {
		return this.randomness;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAmount() {
		return this.amount;
	}

	public void setRays(int rays) {
		this.rays = rays;
	}

	public int getRays() {
		return this.rays;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getRadius() {
		return this.radius;
	}

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
		this.centreX = width / 2;
		this.centreY = height / 2;
		super.setDimensions(width, height);
		this.randomNumbers.setSeed(this.seed);
		this.rayLengths = new float[this.rays];
		for (int i = 0; i < this.rays; i++) {
			this.rayLengths[i] = this.radius + this.randomness / 100.0f * this.radius * (float) this.randomNumbers.nextGaussian();
		}
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float dx = x - this.centreX;
		float dy = y - this.centreY;
		float distance = dx * dx + dy * dy;
		float angle = (float) Math.atan2(dy, dx);
		float d = (angle + ImageMath.PI) / (ImageMath.TWO_PI) * this.rays;
		int i = (int) d;
		float f = d - i;

		if (this.radius != 0) {
			float length = ImageMath.lerp(f, this.rayLengths[i % this.rays], this.rayLengths[(i + 1) % this.rays]);
			float g = length * length / (distance + 0.0001f);
			g = (float) Math.pow(g, (100 - this.amount) / 50.0);
			f -= 0.5f;
//			f *= amount/50.0f;
			f = 1 - f * f;
			f *= g;
		}
		f = ImageMath.clamp(f, 0, 1);
		return ImageMath.mixColors(f, rgb, this.color);
	}

	@Override
	public String toString() {
		return "Stylize/Sparkle...";
	}
}
