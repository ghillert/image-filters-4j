/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.composite.MiscComposite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RaysFilter extends MotionBlurOp {

	private float opacity = 1.0f;
	private float threshold = 0.0f;
	private float strength = 0.5f;
	private boolean raysOnly = false;
	private Colormap colormap;

	public RaysFilter() {
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public float getOpacity() {
		return this.opacity;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public float getThreshold() {
		return this.threshold;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public float getStrength() {
		return this.strength;
	}

	public void setRaysOnly(boolean raysOnly) {
		this.raysOnly = raysOnly;
	}

	public boolean getRaysOnly() {
		return this.raysOnly;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		int width = src.getWidth();
		int height = src.getHeight();
		int[] pixels = new int[width];
		int[] srcPixels = new int[width];

		BufferedImage rays = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		int threshold3 = (int) (this.threshold * 3 * 255);
		for (int y = 0; y < height; y++) {
			getRGB(src, 0, y, width, 1, pixels);
			for (int x = 0; x < width; x++) {
				int rgb = pixels[x];
				int a = rgb & 0xff000000;
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;
				int l = r + g + b;
				if (l < threshold3) {
					pixels[x] = 0xff000000;
				}
				else {
					l /= 3;
					pixels[x] = a | (l << 16) | (l << 8) | l;
				}
			}
			setRGB(rays, 0, y, width, 1, pixels);
		}

		rays = super.filter(rays, null);

		for (int y = 0; y < height; y++) {
			getRGB(rays, 0, y, width, 1, pixels);
			getRGB(src, 0, y, width, 1, srcPixels);
			for (int x = 0; x < width; x++) {
				int rgb = pixels[x];
				int a = rgb & 0xff000000;
				int r = (rgb >> 16) & 0xff;
				int g = (rgb >> 8) & 0xff;
				int b = rgb & 0xff;

				if (this.colormap != null) {
					int l = r + g + b;
					rgb = this.colormap.getColor(l * this.strength * (1 / 3f));
				}
				else {
					r = PixelUtils.clamp((int) (r * this.strength));
					g = PixelUtils.clamp((int) (g * this.strength));
					b = PixelUtils.clamp((int) (b * this.strength));
					rgb = a | (r << 16) | (g << 8) | b;
				}

				pixels[x] = rgb;
			}
			setRGB(rays, 0, y, width, 1, pixels);
		}

		if (dst == null) {
			dst = createCompatibleDestImage(src, null);
		}

		Graphics2D g = dst.createGraphics();
		if (!this.raysOnly) {
			g.setComposite(AlphaComposite.SrcOver);
			g.drawRenderedImage(src, null);
		}
		g.setComposite(MiscComposite.getInstance(MiscComposite.ADD, this.opacity));
		g.drawRenderedImage(rays, null);
		g.dispose();

		return dst;
	}

	@Override
	public String toString() {
		return "Stylize/Rays...";
	}
}
