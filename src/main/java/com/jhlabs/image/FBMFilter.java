/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImage;
import java.util.Random;

import com.jhlabs.math.CellularFunction2D;
import com.jhlabs.math.FBM;
import com.jhlabs.math.Function2D;
import com.jhlabs.math.Noise;
import com.jhlabs.math.RidgedFBM;
import com.jhlabs.math.SCNoise;
import com.jhlabs.math.VLNoise;

/**
 * A filter which produces textures from fractal Brownian motion.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class FBMFilter extends PointFilter implements Cloneable {

	/**
	 * Represents the type of noise filter to be applied in the FBM filtering process.
	 * This constant is used to specify a standard noise function.
	 * It is part of the configuration options for generating textures or patterns
	 * using the {@link FBMFilter} class.
	 */
	public static final int NOISE = 0;

	/**
	 * Constant representing the "RIDGED" noise type used in the {@link FBMFilter}.
	 * This noise type emphasizes ridged patterns, often used in procedural
	 * texture generation for creating terrains, clouds, and other natural phenomena.
	 */
	public static final int RIDGED = 1;

	/**
	 * Constant representing the VL Noise basis type used in the {@link FBMFilter}.
	 * This value is used to configure the noise generation algorithm within
	 * the {@link FBMFilter}, specifically for creating textures and procedural patterns
	 * based on VL Noise.
	 */
	public static final int VLNOISE = 2;

	/**
	 * A constant representing the "SCNOISE" noise type used in the {@link FBMFilter} class.
	 * This value is associated with using a specific type of noise function for generating fractal-based
	 * patterns or textures. It may be used in conjunction with the filter's operations and settings
	 * to define the noise behavior in the resulting processed image.
	 */
	public static final int SCNOISE = 3;

	/**
	 * A constant representing the "Cellular" noise basis type used in procedural texture generation.
	 * This value can be used to configure the noise generation behavior in the associated filter.
	 */
	public static final int CELLULAR = 4;

	private float scale = 32;
	private float stretch = 1.0f;
	private float angle = 0.0f;
	private float amount = 1.0f;
	private float H = 1.0f;
	private float octaves = 4.0f;
	private float lacunarity = 2.0f;
	private float gain = 0.5f;
	private float bias = 0.5f;
	private int operation;
	private float m00 = 1.0f;
	private float m01 = 0.0f;
	private float m10 = 0.0f;
	private float m11 = 1.0f;
	private float min;
	private float max;
	private Colormap colormap = new Gradient();
	private boolean ridged;
	private FBM fBm;
	protected Random random = new Random();
	private int basisType = NOISE;
	private Function2D basis;

	public FBMFilter() {
		setBasisType(NOISE);
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public float getAmount() {
		return this.amount;
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
		float cos = (float) Math.cos(this.angle);
		float sin = (float) Math.sin(this.angle);
		this.m00 = cos;
		this.m01 = sin;
		this.m10 = -sin;
		this.m11 = cos;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setOctaves(float octaves) {
		this.octaves = octaves;
	}

	public float getOctaves() {
		return this.octaves;
	}

	public void setH(float H) {
		this.H = H;
	}

	public float getH() {
		return this.H;
	}

	public void setLacunarity(float lacunarity) {
		this.lacunarity = lacunarity;
	}

	public float getLacunarity() {
		return this.lacunarity;
	}

	public void setGain(float gain) {
		this.gain = gain;
	}

	public float getGain() {
		return this.gain;
	}

	public void setBias(float bias) {
		this.bias = bias;
	}

	public float getBias() {
		return this.bias;
	}

	public void setColormap(Colormap colormap) {
		this.colormap = colormap;
	}

	public Colormap getColormap() {
		return this.colormap;
	}

	public void setBasisType(int basisType) {
		this.basisType = basisType;
		switch (basisType) {
			default:
			case NOISE:
				this.basis = new Noise();
				break;
			case RIDGED:
				this.basis = new RidgedFBM();
				break;
			case VLNOISE:
				this.basis = new VLNoise();
				break;
			case SCNOISE:
				this.basis = new SCNoise();
				break;
			case CELLULAR:
				this.basis = new CellularFunction2D();
				break;
		}
	}

	public int getBasisType() {
		return this.basisType;
	}

	public void setBasis(Function2D basis) {
		this.basis = basis;
	}

	public Function2D getBasis() {
		return this.basis;
	}

	protected FBM makeFBM(float H, float lacunarity, float octaves) {
		FBM fbm = new FBM(H, lacunarity, octaves, this.basis);
		float[] minmax = Noise.findRange(fbm, null);
		this.min = minmax[0];
		this.max = minmax[1];
		return fbm;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		this.fBm = makeFBM(this.H, this.lacunarity, this.octaves);
		return super.filter(src, dst);
	}

	@Override
	public int filterRGB(int x, int y, int rgb) {
		float nx = this.m00 * x + this.m01 * y;
		float ny = this.m10 * x + this.m11 * y;
		nx /= this.scale;
		ny /= this.scale * this.stretch;
		float f = this.fBm.evaluate(nx, ny);
		// Normalize to 0..1
		f = (f - this.min) / (this.max - this.min);
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
		return "Texture/Fractal Brownian Motion...";
	}

}
