/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

/**
 * An image histogram.
 */
public class Histogram {

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int GRAY = 3;

	protected int[][] histogram;
	protected int numSamples;
	protected int[] minValue;
	protected int[] maxValue;
	protected int[] minFrequency;
	protected int[] maxFrequency;
	protected float[] mean;
	protected boolean isGray;

	public Histogram() {
		this.histogram = null;
		this.numSamples = 0;
		this.isGray = true;
		this.minValue = null;
		this.maxValue = null;
		this.minFrequency = null;
		this.maxFrequency = null;
		this.mean = null;
	}

	public Histogram(int[] pixels, int w, int h, int offset, int stride) {
		this.histogram = new int[3][256];
		this.minValue = new int[4];
		this.maxValue = new int[4];
		this.minFrequency = new int[3];
		this.maxFrequency = new int[3];
		this.mean = new float[3];

		this.numSamples = w * h;
		this.isGray = true;

		int index = 0;
		for (int y = 0; y < h; y++) {
			index = offset + y * stride;
			for (int x = 0; x < w; x++) {
				int rgb = pixels[index++];
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				this.histogram[RED][r]++;
				this.histogram[GREEN][g]++;
				this.histogram[BLUE][b]++;
			}
		}

		for (int i = 0; i < 256; i++) {
			if (this.histogram[RED][i] != this.histogram[GREEN][i] || this.histogram[GREEN][i] != this.histogram[BLUE][i]) {
				this.isGray = false;
				break;
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 256; j++) {
				if (this.histogram[i][j] > 0) {
					this.minValue[i] = j;
					break;
				}
			}

			for (int j = 255; j >= 0; j--) {
				if (this.histogram[i][j] > 0) {
					this.maxValue[i] = j;
					break;
				}
			}

			this.minFrequency[i] = Integer.MAX_VALUE;
			this.maxFrequency[i] = 0;
			for (int j = 0; j < 256; j++) {
				this.minFrequency[i] = Math.min(this.minFrequency[i], this.histogram[i][j]);
				this.maxFrequency[i] = Math.max(this.maxFrequency[i], this.histogram[i][j]);
				this.mean[i] += (float) (j * this.histogram[i][j]);
			}
			this.mean[i] /= (float) this.numSamples;
		}
		this.minValue[GRAY] = Math.min(Math.min(this.minValue[RED], this.minValue[GREEN]), this.minValue[BLUE]);
		this.maxValue[GRAY] = Math.max(Math.max(this.maxValue[RED], this.maxValue[GREEN]), this.maxValue[BLUE]);
	}

	public boolean isGray() {
		return this.isGray;
	}

	public int getNumSamples() {
		return this.numSamples;
	}

	public int getFrequency(int value) {
		if (this.numSamples > 0 && this.isGray && value >= 0 && value <= 255) {
			return this.histogram[0][value];
		}
		return -1;
	}

	public int getFrequency(int channel, int value) {
		if (this.numSamples < 1 || channel < 0 || channel > 2 ||
				value < 0 || value > 255) {
			return -1;
		}
		return this.histogram[channel][value];
	}

	public int getMinFrequency() {
		if (this.numSamples > 0 && this.isGray) {
			return this.minFrequency[0];
		}
		return -1;
	}

	public int getMinFrequency(int channel) {
		if (this.numSamples < 1 || channel < 0 || channel > 2) {
			return -1;
		}
		return this.minFrequency[channel];
	}


	public int getMaxFrequency() {
		if (this.numSamples > 0 && this.isGray) {
			return this.maxFrequency[0];
		}
		return -1;
	}

	public int getMaxFrequency(int channel) {
		if (this.numSamples < 1 || channel < 0 || channel > 2) {
			return -1;
		}
		return this.maxFrequency[channel];
	}


	public int getMinValue() {
		if (this.numSamples > 0 && this.isGray) {
			return this.minValue[0];
		}
		return -1;
	}

	public int getMinValue(int channel) {
		return this.minValue[channel];
	}

	public int getMaxValue() {
		if (this.numSamples > 0 && this.isGray) {
			return this.maxValue[0];
		}
		return -1;
	}

	public int getMaxValue(int channel) {
		return this.maxValue[channel];
	}

	public float getMeanValue() {
		if (this.numSamples > 0 && this.isGray) {
			return this.mean[0];
		}
		return -1.0F;
	}

	public float getMeanValue(int channel) {
		if (this.numSamples > 0 && RED <= channel && channel <= BLUE) {
			return this.mean[channel];
		}
		return -1.0F;
	}


}
