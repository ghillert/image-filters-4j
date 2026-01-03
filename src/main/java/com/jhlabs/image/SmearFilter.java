/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;
import java.util.Date;
import java.util.Random;

public class SmearFilter extends WholeImageFilter implements java.io.Serializable {

	static final long serialVersionUID = 6491871753122667752L;

	public final static int CROSSES = 0;
	public final static int LINES = 1;
	public final static int CIRCLES = 2;
	public final static int SQUARES = 3;

	private final Colormap colormap = new LinearColormap();
	private float angle = 0;
	private float density = 0.5f;
	private float scatter = 0.0f;
	private int distance = 8;
	private final Random randomGenerator;
	private long seed = 567;
	private int shape = LINES;
	private float mix = 0.5f;
	private int fadeout = 0;
	private boolean background = false;

	public SmearFilter() {
		this.randomGenerator = new Random();
	}

	public void setShape(int shape) {
		this.shape = shape;
	}

	public int getShape() {
		return this.shape;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return this.distance;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public float getDensity() {
		return this.density;
	}

	public void setScatter(float scatter) {
		this.scatter = scatter;
	}

	public float getScatter() {
		return this.scatter;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setMix(float mix) {
		this.mix = mix;
	}

	public float getMix() {
		return this.mix;
	}

	public void setFadeout(int fadeout) {
		this.fadeout = fadeout;
	}

	public int getFadeout() {
		return this.fadeout;
	}

	public void setBackground(boolean background) {
		this.background = background;
	}

	public boolean getBackground() {
		return this.background;
	}

	public void randomize() {
		this.seed = new Date().getTime();
	}

	private float random(float low, float high) {
		return low + (high - low) * this.randomGenerator.nextFloat();
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int[] outPixels = new int[width * height];

		this.randomGenerator.setSeed(this.seed);
		float sinAngle = (float) Math.sin(this.angle);
		float cosAngle = (float) Math.cos(this.angle);

		int i = 0;
		int numShapes;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				outPixels[i] = this.background ? 0xffffffff : inPixels[i];
				i++;
			}
		}

		switch (this.shape) {
			case CROSSES:
				//Crosses
				numShapes = (int) (2 * this.density * width * height / (this.distance + 1));
				for (i = 0; i < numShapes; i++) {
					int x = (this.randomGenerator.nextInt() & 0x7fffffff) % width;
					int y = (this.randomGenerator.nextInt() & 0x7fffffff) % height;
					int length = this.randomGenerator.nextInt() % this.distance + 1;
					int rgb = inPixels[y * width + x];
					for (int x1 = x - length; x1 < x + length + 1; x1++) {
						if (x1 >= 0 && x1 < width) {
							int rgb2 = this.background ? 0xffffffff : outPixels[y * width + x1];
							outPixels[y * width + x1] = ImageMath.mixColors(this.mix, rgb2, rgb);
						}
					}
					for (int y1 = y - length; y1 < y + length + 1; y1++) {
						if (y1 >= 0 && y1 < height) {
							int rgb2 = this.background ? 0xffffffff : outPixels[y1 * width + x];
							outPixels[y1 * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
						}
					}
				}
				break;
			case LINES:
				numShapes = (int) (2 * this.density * width * height / 2);

				for (i = 0; i < numShapes; i++) {
					int sx = (this.randomGenerator.nextInt() & 0x7fffffff) % width;
					int sy = (this.randomGenerator.nextInt() & 0x7fffffff) % height;
					int rgb = inPixels[sy * width + sx];
					int length = (this.randomGenerator.nextInt() & 0x7fffffff) % this.distance;
					int dx = (int) (length * cosAngle);
					int dy = (int) (length * sinAngle);

					int x0 = sx - dx;
					int y0 = sy - dy;
					int x1 = sx + dx;
					int y1 = sy + dy;
					int x, y, d, incrE, incrNE, ddx, ddy;

					if (x1 < x0) {
						ddx = -1;
					}
					else {
						ddx = 1;
					}
					if (y1 < y0) {
						ddy = -1;
					}
					else {
						ddy = 1;
					}
					dx = x1 - x0;
					dy = y1 - y0;
					dx = Math.abs(dx);
					dy = Math.abs(dy);
					x = x0;
					y = y0;

					if (x < width && x >= 0 && y < height && y >= 0) {
						int rgb2 = this.background ? 0xffffffff : outPixels[y * width + x];
						outPixels[y * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
					}
					if (Math.abs(dx) > Math.abs(dy)) {
						d = 2 * dy - dx;
						incrE = 2 * dy;
						incrNE = 2 * (dy - dx);

						while (x != x1) {
							if (d <= 0) {
								d += incrE;
							}
							else {
								d += incrNE;
								y += ddy;
							}
							x += ddx;
							if (x < width && x >= 0 && y < height && y >= 0) {
								int rgb2 = this.background ? 0xffffffff : outPixels[y * width + x];
								outPixels[y * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
							}
						}
					}
					else {
						d = 2 * dx - dy;
						incrE = 2 * dx;
						incrNE = 2 * (dx - dy);

						while (y != y1) {
							if (d <= 0) {
								d += incrE;
							}
							else {
								d += incrNE;
								x += ddx;
							}
							y += ddy;
							if (x < width && x >= 0 && y < height && y >= 0) {
								int rgb2 = this.background ? 0xffffffff : outPixels[y * width + x];
								outPixels[y * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
							}
						}
					}
				}
				break;
			case SQUARES:
			case CIRCLES:
				int radius = this.distance + 1;
				int radius2 = radius * radius;
				numShapes = (int) (2 * this.density * width * height / radius);
				for (i = 0; i < numShapes; i++) {
					int sx = (this.randomGenerator.nextInt() & 0x7fffffff) % width;
					int sy = (this.randomGenerator.nextInt() & 0x7fffffff) % height;
					int rgb = inPixels[sy * width + sx];
					for (int x = sx - radius; x < sx + radius + 1; x++) {
						for (int y = sy - radius; y < sy + radius + 1; y++) {
							int f;
							if (this.shape == CIRCLES) {
								f = (x - sx) * (x - sx) + (y - sy) * (y - sy);
							}
							else {
								f = 0;
							}
							if (x >= 0 && x < width && y >= 0 && y < height && f <= radius2) {
								int rgb2 = this.background ? 0xffffffff : outPixels[y * width + x];
								outPixels[y * width + x] = ImageMath.mixColors(this.mix, rgb2, rgb);
							}
						}
					}
				}
		}
/*
		//Sketch
		int Low = 0, High = 255;
		int i1 = height;
		int j1 = width;
		int aan[][] = new int[i1][j1];
		float aaf[][] = new float[i1][j1];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int rgb = inPixels[y*width+x];
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				aan[y][x] = r + g + b;
			}
		}
		for (int y = 0; y < i1; y++) {
			for (int x = 0; x < j1; x++) {
				int j2 = (aan[(y - 1 + i1) % i1][(x + 1 + j1) % j1] + aan[y][(x + 1 + j1) % j1] * 2 + aan[(y + 1 + i1) % i1][(x + 1 + j1) % j1] - aan[(y - 1 + i1) % i1][(x - 1 + j1) % j1] - aan[y][(x - 1 + j1) % j1] * 2 - aan[(y + 1 + i1) % i1][(x - 1 + j1) % j1]) / 8;
				int k2 = (aan[(y + 1 + i1) % i1][(x - 1 + j1) % j1] + aan[(y + 1 + i1) % i1][x] * 2 + aan[(y + 1 + i1) % i1][(x + 1 + j1) % j1] - aan[(y - 1 + i1) % i1][(x - 1 + j1) % j1] - aan[(y - 1 + i1) % i1][x] * 2 - aan[(y - 1 + i1) % i1][(x + 1 + j1) % j1]) / 8;
				aaf[y][x] = -((float)Math.sqrt(Math.sqrt((float)(j2 * j2 + k2 * k2))));
			}
		}
		float f1 = aaf[0][0];
		float f2 = aaf[0][0];
		for (int y = 0; y < i1; y++) {
			for (int x = 0; x < j1; x++) {
				if (aaf[y][x] < f1)
					f1 = aaf[y][x];
				if (aaf[y][x] > f2)
					f2 = aaf[y][x];
			}
		}
		for (int y = 0; y < i1; y++) {
			for (int x = 0; x < j1; x++) {
				int a = outPixels[y*width+x] & 0xff000000;
				if (x == 0 || x == j1 - 1 || y == 0 || y == i1 - 1)
					outPixels[y*width+x] = a | 0xffffff;
				else {
					int j4 = Low + (int)((aaf[y][x] - f1) * (High - Low) / (f2 - f1));
					outPixels[y*width+x] = a | (j4 << 16) | (j4 << 8) | j4;
				}
			}
		}
*/

		return outPixels;
	}

	@Override
	public String toString() {
		return "Effects/Smear...";
	}

}
