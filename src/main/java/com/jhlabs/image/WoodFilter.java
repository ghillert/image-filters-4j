/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.RGBImageFilter;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;

/**
 * The WoodFilter class is used to create a procedural wood texture effect on an image.
 * It extends the {@link RGBImageFilter} class and implements the {@link java.io.Serializable} interface.
 * The filter modifies the color values of an image's pixels to generate a wood-like
 * appearance with rings, turbulence, and other configurable properties.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class WoodFilter extends RGBImageFilter implements java.io.Serializable {

	private float scale = 100;
	private float stretch = 1.0f;
	private float angle = 0.0f;
	public float rings = 0.5f;
	public float turbulence = 1.0f;
	public float gain = 0.8f;
	public float bias = 0.1f;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;
	private Colormap colormap = new LinearColormap(0xffefa900, 0xff6d4e00);
	private Function2D function = new Noise();

	/**
	 * Construct a WoodFilter.
	 */
	public WoodFilter() {
	}

	/**
	 * Set the number of rings in the wood texture.
	 * @param rings the number of rings
	 */
	public void setRings(float rings) {
		this.rings = rings;
	}

	/**
	 * Get the number of rings in the wood texture.
	 * @return the number of rings
	 */
	public float getRings() {
		return this.rings;
	}

	/**
	 * Set the function used to generate the wood texture.
	 * @param function the function to use
	 */
	public void setFunction(Function2D function) {
		this.function = function;
	}

	/**
	 * Get the function used to generate the wood texture.
	 * @return the function used
	 */
	public Function2D getFunction() {
		return this.function;
	}

	/**
	 * Set the scale of the wood texture.
	 * @param scale the scale factor
	 */
	public void setScale(float scale) {
		this.scale = scale;
	}

	/**
	 * Get the scale of the wood texture.
	 * @return the scale factor
	 */
	public float getScale() {
		return this.scale;
	}

	/**
	 * Set the stretch of the wood texture.
	 * @param stretch the stretch factor
	 */
	public void setStretch(float stretch) {
		this.stretch = stretch;
	}

	/**
	 * Get the stretch of the wood texture.
	 * @return the stretch factor
	 */
	public float getStretch() {
		return this.stretch;
	}

	/**
	 * Set the angle of the wood texture.
	 * @param angle the angle in radians
	 */
	public void setAngle(float angle) {
		this.angle = angle;
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
	}

	/**
	 * Get the angle of the wood texture.
	 * @return the angle in radians
	 */
	public float getAngle() {
		return this.angle;
	}

	/**
	 * Set the turbulence of the wood texture.
	 * @param turbulence the turbulence factor
	 */
	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	/**
	 * Get the turbulence of the wood texture.
	 * @return the turbulence factor
	 */
	public float getTurbulence() {
		return this.turbulence;
	}

	/**
	 * Set the colormap used to generate the wood texture.
	 * @param colormap the colormap to use
	 */
	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	/**
	 * Get the colormap used to generate the wood texture.
	 * @return the colormap used
	 */
	public Colormap getColormap() {
		return this.colormap;
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float nx = this.m00 * x + this.m01 * y;
		float ny = this.m10 * x + this.m11 * y;
		nx /= this.scale;
		ny /= this.scale * this.stretch;
		float f = (this.turbulence == 1.0) ? Noise.noise2(nx, ny) : Noise.turbulence2(nx, ny, this.turbulence);
		f = (f * 0.5f) + 0.5f;

		f *= this.rings * 50;
		f = f - (int) f;
		f *= 1 - ImageMath.smoothStep(this.gain, 1.0f, f);

		float h = Noise.noise2(nx * this.scale, ny * 50);
		f += this.bias * h;

		int a = rgb & 0xff000000;
		int v;
		if (this.colormap != null) {
			v = this.colormap.getColor(f);
		}
		else {
			v = PixelUtils.clamp((int) (f * 255));
			int r = v << 16;
			int g = v << 8;
			int b = v;
			v = a | r | g | b;
		}

		return v;
	}

	@Override
	public String toString() {
		return "Texture/Wood...";
	}

}
