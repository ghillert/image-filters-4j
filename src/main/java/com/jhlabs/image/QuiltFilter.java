/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Rectangle;
import java.util.Date;
import java.util.Random;

/**
 * The QuiltFilter class is a WholeImageFilter implementation used to generate chaotic quilt-like textures.
 * It processes an entire image based on parametric mathematical functions and uses a specified colormap
 * to map the generated values into colors, creating visually intriguing textures.
 * <p>
 * This filter is capable of producing rich patterns through iterative mathematical calculations involving
 * sine, cosine, and random perturbation. The generation of the patterns is highly configurable through
 * adjustable parameters.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class QuiltFilter extends WholeImageFilter implements java.io.Serializable {

	private final Random randomGenerator;
	private long seed = 567;
	private int iterations = 25000;
	private float a = -0.59f;
	private float b = 0.2f;
	private float c = 0.1f;
	private float d = 0;
	private int k = 0;
	private Colormap colormap = new LinearColormap();

	public QuiltFilter() {
		this.randomGenerator = new Random();
	}

	public void randomize() {
		this.seed = new Date().getTime();
		this.randomGenerator.setSeed(this.seed);
		this.a = this.randomGenerator.nextFloat();
		this.b = this.randomGenerator.nextFloat();
		this.c = this.randomGenerator.nextFloat();
		this.d = this.randomGenerator.nextFloat();
		this.k = this.randomGenerator.nextInt() % 20 - 10;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return this.iterations;
	}

	public void setA(float a) {
		this.a = a;
	}

	public float getA() {
		return this.a;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getB() {
		return this.b;
	}

	public void setC(float c) {
		this.c = c;
	}

	public float getC() {
		return this.c;
	}

	public void setD(float d) {
		this.d = d;
	}

	public float getD() {
		return this.d;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getK() {
		return this.k;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int[] outPixels = new int[width * height];

		int i = 0;
		int max = 0;

		float x = 0.1f;
		float y = 0.3f;

		for (int n = 0; n < 20; n++) {
			float mx = ImageMath.PI * x;
			float my = ImageMath.PI * y;
			float smx2 = (float) Math.sin(2 * mx);
			float smy2 = (float) Math.sin(2 * my);
			float x1 = (float) (this.a * smx2 + this.b * smx2 * Math.cos(2 * my) +
					this.c * Math.sin(4 * mx) + this.d * Math.sin(6 * mx) * Math.cos(4 * my) + this.k * x);
			x1 = (x1 >= 0) ? (x1 - (int) x1) : ((x1 - (int) x1) + 1);

			float y1 = (float) (this.a * smy2 + this.b * smy2 * Math.cos(2 * mx) +
					this.c * Math.sin(4 * my) + this.d * Math.sin(6 * my) * Math.cos(4 * mx) + this.k * y);
			y1 = (y1 >= 0) ? (y1 - (int) y1) : ((y1 - (int) y1) + 1);
			x = x1;
			y = y1;
		}

		for (int n = 0; n < this.iterations; n++) {
			float mx = ImageMath.PI * x;
			float my = ImageMath.PI * y;
			float x1 = (float) (this.a * Math.sin(2 * mx) + this.b * Math.sin(2 * mx) * Math.cos(2 * my) +
					this.c * Math.sin(4 * mx) + this.d * Math.sin(6 * mx) * Math.cos(4 * my) + this.k * x);
			x1 = (x1 >= 0) ? (x1 - (int) x1) : ((x1 - (int) x1) + 1);

			float y1 = (float) (this.a * Math.sin(2 * my) + this.b * Math.sin(2 * my) * Math.cos(2 * mx) +
					this.c * Math.sin(4 * my) + this.d * Math.sin(6 * my) * Math.cos(4 * mx) + this.k * y);
			y1 = (y1 >= 0) ? (y1 - (int) y1) : ((y1 - (int) y1) + 1);
			x = x1;
			y = y1;
			int ix = (int) (width * x);
			int iy = (int) (height * y);
			if (ix >= 0 && ix < width && iy >= 0 && iy < height) {
				int t = outPixels[width * iy + ix]++;
				if (t > max) {
					max = t;
				}
			}
		}

		if (this.colormap != null) {
			int index = 0;
			for (y = 0; y < height; y++) {
				for (x = 0; x < width; x++) {
					outPixels[index] = this.colormap.getColor(outPixels[index] / (float) max);
					index++;
				}
			}
		}
		return outPixels;
	}

	@Override
	public String toString() {
		return "Texture/Chaotic Quilt...";
	}

}
