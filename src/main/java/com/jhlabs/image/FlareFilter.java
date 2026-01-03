/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.math.Noise;

/**
 * An experimental filter for rendering lens flares.
 */
public class FlareFilter extends PointFilter {

	private final int rays = 50;
	private int radius;
	private float baseAmount = 1.0f;
	private float ringAmount = 0.2f;
	private float rayAmount = 0.1f;
	private int color = 0xffffffff;
	private int width, height;
	private int centreX, centreY;
	private float ringWidth = 1.6f;

	private final float linear = 0.03f;
	private final float gauss = 0.006f;
	private final float mix = 0.50f;
	private final float falloff = 6.0f;
	private float sigma;

	public FlareFilter() {
		setRadius(25);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	public void setRingWidth(float ringWidth) {
		this.ringWidth = ringWidth;
	}

	public float getRingWidth() {
		return this.ringWidth;
	}

	public void setBaseAmount(float baseAmount) {
		this.baseAmount = baseAmount;
	}

	public float getBaseAmount() {
		return this.baseAmount;
	}

	public void setRingAmount(float ringAmount) {
		this.ringAmount = ringAmount;
	}

	public float getRingAmount() {
		return this.ringAmount;
	}

	public void setRayAmount(float rayAmount) {
		this.rayAmount = rayAmount;
	}

	public float getRayAmount() {
		return this.rayAmount;
	}

	public void setRadius(int radius) {
		this.radius = radius;
		this.sigma = (float) radius / 3;
	}

	public int getRadius() {
		return this.radius;
	}

	@Override
	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
//		radius = (int)(Math.min(width/2, height/2) - ringWidth - falloff);
		this.centreX = width / 2;
		this.centreY = height / 2;
		super.setDimensions(width, height);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float dx = x - this.centreX;
		float dy = y - this.centreY;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);
		float a = (float) Math.exp(-distance * distance * this.gauss) * this.mix + (float) Math.exp(-distance * this.linear) * (1 - this.mix);
		float ring;

		a *= this.baseAmount;

		if (distance > this.radius + this.ringWidth) {
			a = ImageMath.lerp((distance - (this.radius + this.ringWidth)) / this.falloff, a, 0);
		}

		if (distance < this.radius - this.ringWidth || distance > this.radius + this.ringWidth) {
			ring = 0;
		}
		else {
			ring = Math.abs(distance - this.radius) / this.ringWidth;
			ring = 1 - ring * ring * (3 - 2 * ring);
			ring *= this.ringAmount;
		}

		a += ring;

		float angle = (float) Math.atan2(dx, dy) + ImageMath.PI;
		angle = (ImageMath.mod(angle / ImageMath.PI * 17 + 1.0f + Noise.noise1(angle * 10), 1.0f) - 0.5f) * 2;
		angle = Math.abs(angle);
		angle = (float) Math.pow(angle, 5.0);

		float b = this.rayAmount * angle / (1 + distance * 0.1f);
		a += b;
//		b = ImageMath.clamp(b, 0, 1);
//		rgb = PixelUtils.combinePixels(0xff802010, rgb, PixelUtils.NORMAL, (int)(b*255));

		a = ImageMath.clamp(a, 0, 1);
		return ImageMath.mixColors(a, rgb, this.color);
	}

	@Override
	public String toString() {
		return "Stylize/Flare...";
	}
}
