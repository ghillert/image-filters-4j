/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImage;

public class TemperatureFilter extends PointFilter {

	private float temperature = 6650f;

	private float rFactor, gFactor, bFactor;

	public TemperatureFilter() {
		this.canFilterIndexColorModel = true;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public float getTemperature() {
		return this.temperature;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		this.temperature = Math.max(1000, Math.min(10000, this.temperature));

		int t = 3 * (int) ((this.temperature - 1000) / 100.0f);
		this.rFactor = 1.0f / blackBodyRGB[t];
		this.gFactor = 1.0f / blackBodyRGB[t + 1];
		this.bFactor = 1.0f / blackBodyRGB[t + 2];

		// Normalize
		float m = Math.max(Math.max(this.rFactor, this.gFactor), this.bFactor);
		this.rFactor /= m;
		this.gFactor /= m;
		this.bFactor /= m;

		return super.filter(src, dst);
	}

	// Black body table from http://www.vendian.org/mncharity/dir3/blackbody/UnstableURLs/bbr_color.html
	static float[] blackBodyRGB = {
			/* 1000 K */ 1.0000f, 0.0337f, 0.0000f,
			/* 1100 K */ 1.0000f, 0.0592f, 0.0000f,
			/* 1200 K */ 1.0000f, 0.0846f, 0.0000f,
			/* 1300 K */ 1.0000f, 0.1096f, 0.0000f,
			/* 1400 K */ 1.0000f, 0.1341f, 0.0000f,
			/* 1500 K */ 1.0000f, 0.1578f, 0.0000f,
			/* 1600 K */ 1.0000f, 0.1806f, 0.0000f,
			/* 1700 K */ 1.0000f, 0.2025f, 0.0000f,
			/* 1800 K */ 1.0000f, 0.2235f, 0.0000f,
			/* 1900 K */ 1.0000f, 0.2434f, 0.0000f,
			/* 2000 K */ 1.0000f, 0.2647f, 0.0033f,
			/* 2100 K */ 1.0000f, 0.2889f, 0.0120f,
			/* 2200 K */ 1.0000f, 0.3126f, 0.0219f,
			/* 2300 K */ 1.0000f, 0.3360f, 0.0331f,
			/* 2400 K */ 1.0000f, 0.3589f, 0.0454f,
			/* 2500 K */ 1.0000f, 0.3814f, 0.0588f,
			/* 2600 K */ 1.0000f, 0.4034f, 0.0734f,
			/* 2700 K */ 1.0000f, 0.4250f, 0.0889f,
			/* 2800 K */ 1.0000f, 0.4461f, 0.1054f,
			/* 2900 K */ 1.0000f, 0.4668f, 0.1229f,
			/* 3000 K */ 1.0000f, 0.4870f, 0.1411f,
			/* 3100 K */ 1.0000f, 0.5067f, 0.1602f,
			/* 3200 K */ 1.0000f, 0.5259f, 0.1800f,
			/* 3300 K */ 1.0000f, 0.5447f, 0.2005f,
			/* 3400 K */ 1.0000f, 0.5630f, 0.2216f,
			/* 3500 K */ 1.0000f, 0.5809f, 0.2433f,
			/* 3600 K */ 1.0000f, 0.5983f, 0.2655f,
			/* 3700 K */ 1.0000f, 0.6153f, 0.2881f,
			/* 3800 K */ 1.0000f, 0.6318f, 0.3112f,
			/* 3900 K */ 1.0000f, 0.6480f, 0.3346f,
			/* 4000 K */ 1.0000f, 0.6636f, 0.3583f,
			/* 4100 K */ 1.0000f, 0.6789f, 0.3823f,
			/* 4200 K */ 1.0000f, 0.6938f, 0.4066f,
			/* 4300 K */ 1.0000f, 0.7083f, 0.4310f,
			/* 4400 K */ 1.0000f, 0.7223f, 0.4556f,
			/* 4500 K */ 1.0000f, 0.7360f, 0.4803f,
			/* 4600 K */ 1.0000f, 0.7494f, 0.5051f,
			/* 4700 K */ 1.0000f, 0.7623f, 0.5299f,
			/* 4800 K */ 1.0000f, 0.7750f, 0.5548f,
			/* 4900 K */ 1.0000f, 0.7872f, 0.5797f,
			/* 5000 K */ 1.0000f, 0.7992f, 0.6045f,
			/* 5100 K */ 1.0000f, 0.8108f, 0.6293f,
			/* 5200 K */ 1.0000f, 0.8221f, 0.6541f,
			/* 5300 K */ 1.0000f, 0.8330f, 0.6787f,
			/* 5400 K */ 1.0000f, 0.8437f, 0.7032f,
			/* 5500 K */ 1.0000f, 0.8541f, 0.7277f,
			/* 5600 K */ 1.0000f, 0.8642f, 0.7519f,
			/* 5700 K */ 1.0000f, 0.8740f, 0.7760f,
			/* 5800 K */ 1.0000f, 0.8836f, 0.8000f,
			/* 5900 K */ 1.0000f, 0.8929f, 0.8238f,
			/* 6000 K */ 1.0000f, 0.9019f, 0.8473f,
			/* 6100 K */ 1.0000f, 0.9107f, 0.8707f,
			/* 6200 K */ 1.0000f, 0.9193f, 0.8939f,
			/* 6300 K */ 1.0000f, 0.9276f, 0.9168f,
			/* 6400 K */ 1.0000f, 0.9357f, 0.9396f,
			/* 6500 K */ 1.0000f, 0.9436f, 0.9621f,
			/* 6600 K */ 1.0000f, 0.9513f, 0.9844f,
			/* 6700 K */ 0.9937f, 0.9526f, 1.0000f,
			/* 6800 K */ 0.9726f, 0.9395f, 1.0000f,
			/* 6900 K */ 0.9526f, 0.9270f, 1.0000f,
			/* 7000 K */ 0.9337f, 0.9150f, 1.0000f,
			/* 7100 K */ 0.9157f, 0.9035f, 1.0000f,
			/* 7200 K */ 0.8986f, 0.8925f, 1.0000f,
			/* 7300 K */ 0.8823f, 0.8819f, 1.0000f,
			/* 7400 K */ 0.8668f, 0.8718f, 1.0000f,
			/* 7500 K */ 0.8520f, 0.8621f, 1.0000f,
			/* 7600 K */ 0.8379f, 0.8527f, 1.0000f,
			/* 7700 K */ 0.8244f, 0.8437f, 1.0000f,
			/* 7800 K */ 0.8115f, 0.8351f, 1.0000f,
			/* 7900 K */ 0.7992f, 0.8268f, 1.0000f,
			/* 8000 K */ 0.7874f, 0.8187f, 1.0000f,
			/* 8100 K */ 0.7761f, 0.8110f, 1.0000f,
			/* 8200 K */ 0.7652f, 0.8035f, 1.0000f,
			/* 8300 K */ 0.7548f, 0.7963f, 1.0000f,
			/* 8400 K */ 0.7449f, 0.7894f, 1.0000f,
			/* 8500 K */ 0.7353f, 0.7827f, 1.0000f,
			/* 8600 K */ 0.7260f, 0.7762f, 1.0000f,
			/* 8700 K */ 0.7172f, 0.7699f, 1.0000f,
			/* 8800 K */ 0.7086f, 0.7638f, 1.0000f,
			/* 8900 K */ 0.7004f, 0.7579f, 1.0000f,
			/* 9000 K */ 0.6925f, 0.7522f, 1.0000f,
			/* 9100 K */ 0.6848f, 0.7467f, 1.0000f,
			/* 9200 K */ 0.6774f, 0.7414f, 1.0000f,
			/* 9300 K */ 0.6703f, 0.7362f, 1.0000f,
			/* 9400 K */ 0.6635f, 0.7311f, 1.0000f,
			/* 9500 K */ 0.6568f, 0.7263f, 1.0000f,
			/* 9600 K */ 0.6504f, 0.7215f, 1.0000f,
			/* 9700 K */ 0.6442f, 0.7169f, 1.0000f,
			/* 9800 K */ 0.6382f, 0.7124f, 1.0000f,
			/* 9900 K */ 0.6324f, 0.7081f, 1.0000f,
			/* 10000 K */ 0.6268f, 0.7039f, 1.0000f
	};

	public void setTemperatureFromRGB(int rgb) {
		float r = (rgb >> 16) & 0xff;
		float g = (rgb >> 8) & 0xff;
		float b = rgb & 0xff;

		int start, end, m;
		float rb;

		rb = r / b;

		start = 0;
		end = blackBodyRGB.length / 3;
		m = (start + end) / 2;

		for (start = 0, r = blackBodyRGB.length, m = (start + end) / 2; end - start > 1; m = (start + end) / 2) {
			int m3 = m * 3;
			if (blackBodyRGB[m3] / blackBodyRGB[m3 + 2] > rb) {
				start = m;
			}
			else {
				end = m;
			}
		}

		setTemperature(m * 100.0f + 1000.0f);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		int a = rgb & 0xff000000;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = rgb & 0xff;

		r *= this.rFactor;
		g *= this.gFactor;
		b *= this.bFactor;

		return a | (r << 16) | (g << 8) | b;
	}

	@Override
	public String toString() {
		return "Colors/Temperature...";
	}

}


