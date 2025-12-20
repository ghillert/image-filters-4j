/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.image.*;
import com.jhlabs.math.*;

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
	private Colormap colormap = new LinearColormap( 0xffefa900, 0xff6d4e00 );
	private Function2D function = new Noise();

	public WoodFilter() {
	}

	public void setRings(float rings) {
		this.rings = rings;
	}

	public float getRings() {
		return rings;
	}

	public void setFunction(Function2D function) {
		this.function = function;
	}

	public Function2D getFunction() {
		return function;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScale() {
		return scale;
	}

	public void setStretch(float stretch) {
		this.stretch = stretch;
	}

	public float getStretch() {
		return stretch;
	}

	public void setAngle(float angle) {
		this.angle = angle;
		float cos = (float)Math.cos(angle);
		float sin = (float)Math.sin(angle);
		m00 = cos;
		m01 = sin;
		m10 = -sin;
		m11 = cos;
	}

	public float getAngle() {
		return angle;
	}

	public void setTurbulence(float turbulence) {
		this.turbulence = turbulence;
	}

	public float getTurbulence() {
		return turbulence;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}
	
	public Colormap getColormap() {
		return colormap;
	}
	
	public int filterRGB(int x, int y, int rgb) {
		float nx = m00*x + m01*y;
		float ny = m10*x + m11*y;
		nx /= scale;
		ny /= scale * stretch;
		float f = turbulence == 1.0 ? Noise.noise2(nx, ny) : Noise.turbulence2(nx, ny, turbulence);
		f = (f * 0.5f) + 0.5f;

f *= rings*50;
f = f-(int)f;
f *= 1-ImageMath.smoothStep(gain, 1.0f, f);

float h = Noise.noise2(nx*scale, ny*50);
f += bias*h;

		int a = rgb & 0xff000000;
		int v;
		if (colormap != null)
			v = colormap.getColor(f);
		else {
			v = PixelUtils.clamp((int)(f*255));
			int r = v << 16;
			int g = v << 8;
			int b = v;
			v = a|r|g|b;
		}

		return v;
	}

	public String toString() {
		return "Texture/Wood...";
	}
	
}
