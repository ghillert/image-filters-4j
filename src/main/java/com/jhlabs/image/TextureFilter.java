/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.io.Serial;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;

/**
 * A class that applies a texture filter to an image by manipulating pixel data.
 * This filter uses noise and turbulence functions to create texture effects
 * and supports various transformations such as scaling, stretching, rotation,
 * and color mapping. The filter extends {@code PointFilter} and can be applied
 * to an image on a per-pixel basis.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class TextureFilter extends PointFilter implements java.io.Serializable {

	@Serial
	static final long serialVersionUID = -7538331862272404352L;

	private float scale = 32;
	private float stretch = 1.0f;
	private float angle = 0.0f;
	private float amount = 1.0f;
	private float turbulence = 1.0f;
	private float gain = 0.5f;
	private float bias = 0.5f;
	private int operation;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;
	private Colormap colormap = new Gradient();
	private Function2D function = new Noise();

	/**
	 * Creates a new instance of the TextureFilter class.
	 * This class is designed to provide various texture transformations
	 * and effects such as scaling, stretching, turbulence, and applying
	 * custom color maps. The filter manipulates RGB values based on
	 * configurable parameters.
	 */
	public TextureFilter() {
		super();
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setFunction(Function2D function) {
		this.function = function;
	}

	public Function2D getFunction() {
		return this.function;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	public int getOperation() {
		return this.operation;
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
		nx /= this.scale;
		ny /= this.scale * this.stretch;
		float f = (this.turbulence == 1.0) ? Noise.noise2(nx, ny) : Noise.turbulence2(nx, ny, this.turbulence);
		f = (f * 0.5f) + 0.5f;
		f = ImageMath.gain(f, this.gain);
		f = ImageMath.bias(f, this.bias);
		f *= this.amount;
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
		if (this.operation != PixelUtils.REPLACE) {
			v = PixelUtils.combinePixels(rgb, v, this.operation);
		}
		return v;
	}

	@Override
	public String toString() {
		return "Texture/Noise...";
	}

}
