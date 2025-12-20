/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class GainFilter extends TransferFilter {

	private float gain = 0.5f;
	private float bias = 0.5f;
	
	protected float transferFunction( float f ) {
		f = ImageMath.gain(f, gain);
		f = ImageMath.bias(f, bias);
		return f;
	}

	public void setGain(float gain) {
		this.gain = gain;
		initialized = false;
	}
	
	public float getGain() {
		return gain;
	}

	public void setBias(float bias) {
		this.bias = bias;
		initialized = false;
	}
	
	public float getBias() {
		return bias;
	}

	public String toString() {
		return "Colors/Gain...";
	}

}

