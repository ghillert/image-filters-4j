/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import com.jhlabs.math.Noise;

/**
 * The MarbleTexFilter class is a texture generation filter that produces a marble-like effect
 * on an image using techniques such as noise-based turbulence and sinusoidal perturbations.
 * It extends the PointFilter class, making it capable of processing individual pixel values.
 * The filter supports configurable properties such as scale, stretch, angle, turbulence,
 * and color mapping, allowing for significant customization of the resulting texture.
 * <p>
 * The filter can operate in two primary modes:
 * 1. When a Colormap is set, the filter generates colors based on the color mapping provided.
 * 2. When no Colormap is specified, it uses a default set of layered effects to determine
 *    the texture's appearance.
 * <p>
 * Key adjustable parameters:
 * - Scale: Adjusts the scale of the texture.
 * - Stretch: Configures the horizontal stretching of the texture.
 * - Angle: Sets the rotation angle of the texture grid.
 * - Turbulence: Modifies the intensity of noise-driven turbulence.
 * - Turbulence Factor: Scales the amount of turbulence applied to the texture.
 * - Colormap: Defines the mapping of values to colors for the output texture.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class MarbleTexFilter extends PointFilter implements java.io.Serializable {

	private float scale = 32;
	private float stretch = 1.0f;
	private float angle = 0.0f;
	private float turbulence = 1;
	private float turbulenceFactor = 0.5f;
	private Colormap colormap;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;

	public MarbleTexFilter() {
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return this.scale;
	}

	public void setStretch(float stretch) {
		this.stretch = stretch;
	}

	public float getStretch() {
		return this.stretch;
	}

	public void setAngle(float angle) {
		this.angle = angle;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return this.turbulence;
	}

	public void setTurbulenceFactor(float turbulenceFactor) {
		this.turbulenceFactor = turbulenceFactor;
	}

	public float getTurbulenceFactor() {
		return this.turbulenceFactor;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float nx = this.m00 * x + this.m01 * y;
		float ny = this.m10 * x + this.m11 * y;
		nx /= this.scale * this.stretch;
		ny /= this.scale;

		int a = rgb & 0xff000000;
		if (this.colormap != null) {
//			float f = Noise.turbulence2(nx, ny, turbulence);
//			f = 3*turbulenceFactor*f+ny;
//			f = Math.sin(f*Math.PI);
			float chaos = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
//			float f = Math.sin(Math.sin(8.*chaos + 7*nx +3.*ny));
			float f = 3 * this.turbulenceFactor * chaos + ny;
			f = (float) Math.sin(f * Math.PI);
			float perturb = (float) Math.sin(40. * chaos);
			f += .2 * perturb;
			return this.colormap.getColor(f);
		}
		else {
			float red;
			float grn;
			float blu;
			float chaos;
			float brownLayer;
			float greenLayer;
			float perturb;
			float brownPerturb;
			float greenPerturb;
			float grnPerturb;
			float t;

			chaos = this.turbulenceFactor * Noise.turbulence2(nx, ny, this.turbulence);
			t = (float) Math.sin(Math.sin(8. * chaos + 7 * nx + 3. * ny));

			greenLayer = brownLayer = Math.abs(t);

			perturb = (float) Math.sin(40. * chaos);
			perturb = Math.abs(perturb);

			brownPerturb = .6f * perturb + 0.3f;
			greenPerturb = .2f * perturb + 0.8f;
			grnPerturb = .15f * perturb + 0.85f;
			grn = 0.5f * (float) Math.pow(Math.abs(brownLayer), 0.3);
			brownLayer = (float) Math.pow(0.5 * (brownLayer + 1.0), 0.6) * brownPerturb;
			greenLayer = (float) Math.pow(0.5 * (greenLayer + 1.0), 0.6) * greenPerturb;

			red = (0.5f * brownLayer + 0.35f * greenLayer) * 2.0f * grn;
			blu = (0.25f * brownLayer + 0.35f * greenLayer) * 2.0f * grn;
			grn *= Math.max(brownLayer, greenLayer) * grnPerturb;
			int r = (rgb >> 16) & 0xff;
			int g = (rgb >> 8) & 0xff;
			int b = rgb & 0xff;
			r = PixelUtils.clamp((int) (r * red));
			g = PixelUtils.clamp((int) (g * grn));
			b = PixelUtils.clamp((int) (b * blu));
			return (rgb & 0xff000000) | (r << 16) | (g << 8) | b;
		}
	}

	@Override
	public String toString() {
		return "Texture/Marble Texture...";
	}

}
