/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.math.Noise;

import java.awt.Rectangle;
import java.util.Random;

/**
 * A filter which simulates underwater caustics. This can be animated to get a bottom-of-the-swimming-pool effect.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class CausticsFilter extends WholeImageFilter {

	private float scale = 32;
	private final float angle = 0.0f;
	public int brightness = 10;
	public float amount = 1.0f;
	public float turbulence = 1.0f;
	public float dispersion = 0.0f;
	public float time = 0.0f;
	private int samples = 2;
	private int bgColor = 0xff799fff;

	private float s, c;

	public CausticsFilter() {
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return this.scale;
	}

	public void setBrightness(int brightness) {
		this.brightness = brightness;
	}

	public int getBrightness() {
		return this.brightness;
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return this.turbulence;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setDispersion(float dispersion) {
		this.dispersion = dispersion;
	}

	public float getDispersion() {
		return this.dispersion;
	}

	public void setTime(float time) {
		this.time = time;
	}

	public float getTime() {
		return this.time;
	}

	public void setSamples(int samples) {
		this.samples = samples;
	}

	public int getSamples() {
		return this.samples;
	}

	public void setBgColor(int c) {
		this.bgColor = c;
	}

	public int getBgColor() {
		return this.bgColor;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		Random random = new Random(0);

		this.s = (float) Math.sin(0.1);
		this.c = (float) Math.cos(0.1);

		int srcWidth = this.originalSpace.width;
		int srcHeight = this.originalSpace.height;
		int outWidth = transformedSpace.width;
		int outHeight = transformedSpace.height;
		int index = 0;
		int[] pixels = new int[outWidth * outHeight];

		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				pixels[index++] = this.bgColor;
			}
		}

		int v = this.brightness / this.samples;
		if (v == 0) {
			v = 1;
		}

		float rs = 1.0f / this.scale;
		float d = 0.95f;
		index = 0;
		for (int y = 0; y < outHeight; y++) {
			for (int x = 0; x < outWidth; x++) {
				for (int s = 0; s < this.samples; s++) {
					float sx = x + random.nextFloat();
					float sy = y + random.nextFloat();
					float nx = sx * rs;
					float ny = sy * rs;
					float xDisplacement, yDisplacement;
					float focus = 0.1f + this.amount;
					xDisplacement = evaluate(nx - d, ny) - evaluate(nx + d, ny);
					yDisplacement = evaluate(nx, ny + d) - evaluate(nx, ny - d);

					if (this.dispersion > 0) {
						for (int c = 0; c < 3; c++) {
							float ca = (1 + c * this.dispersion);
							float srcX = sx + this.scale * focus * xDisplacement * ca;
							float srcY = sy + this.scale * focus * yDisplacement * ca;

							if (srcX < 0 || srcX >= outWidth - 1 || srcY < 0 || srcY >= outHeight - 1) {
							}
							else {
								int i = ((int) srcY) * outWidth + (int) srcX;
								int rgb = pixels[i];
								int r = (rgb >> 16) & 0xff;
								int g = (rgb >> 8) & 0xff;
								int b = rgb & 0xff;
								if (c == 2) {
									r += v;
								}
								else if (c == 1) {
									g += v;
								}
								else {
									b += v;
								}
								if (r > 255) {
									r = 255;
								}
								if (g > 255) {
									g = 255;
								}
								if (b > 255) {
									b = 255;
								}
								pixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
							}
						}
					}
					else {
						float srcX = sx + this.scale * focus * xDisplacement;
						float srcY = sy + this.scale * focus * yDisplacement;

						if (srcX < 0 || srcX >= outWidth - 1 || srcY < 0 || srcY >= outHeight - 1) {
						}
						else {
							int i = ((int) srcY) * outWidth + (int) srcX;
							int rgb = pixels[i];
							int r = (rgb >> 16) & 0xff;
							int g = (rgb >> 8) & 0xff;
							int b = rgb & 0xff;
							r += v;
							g += v;
							b += v;
							if (r > 255) {
								r = 255;
							}
							if (g > 255) {
								g = 255;
							}
							if (b > 255) {
								b = 255;
							}
							pixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
						}
					}
				}
			}
		}
		return pixels;
	}

	private static int add(int rgb, float brightness) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		r += brightness;
		g += brightness;
		b += brightness;
		if (r > 255) {
			r = 255;
		}
		if (g > 255) {
			g = 255;
		}
		if (b > 255) {
			b = 255;
		}
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	private static int add(int rgb, float brightness, int c) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;
		if (c == 2) {
			r += brightness;
		}
		else if (c == 1) {
			g += brightness;
		}
		else {
			b += brightness;
		}
		if (r > 255) {
			r = 255;
		}
		if (g > 255) {
			g = 255;
		}
		if (b > 255) {
			b = 255;
		}
		return 0xff000000 | (r << 16) | (g << 8) | b;
	}

	public static float turbulence2(float x, float y, float time, float octaves) {
		float value = 0.0f;
		float remainder;
		float lacunarity = 2.0f;
		float f = 1.0f;
		int i;

		// to prevent "cascading" effects
		x += 371;
		y += 529;

		for (i = 0; i < (int) octaves; i++) {
			value += Noise.noise3(x, y, time) / f;
			x *= lacunarity;
			y *= lacunarity;
			f *= 2;
		}

		remainder = octaves - (int) octaves;
		if (remainder != 0) {
			value += remainder * Noise.noise3(x, y, time) / f;
		}

		return value;
	}

	protected float evaluate(float x, float y) {
		float xt = this.s * x + this.c * this.time;
		float tt = this.c * x - this.c * this.time;
		float f = this.turbulence == 0.0 ? Noise.noise3(xt, y, tt) : turbulence2(xt, y, tt, this.turbulence);
		return f;
	}

	@Override
	public String toString() {
		return "Texture/Caustics...";
	}

}
