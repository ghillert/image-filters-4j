/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class ContrastFilter extends TransferFilter {

	private float brightness = 1.0f;
	private float contrast = 0.5f;
	
	protected float transferFunction( float f ) {
		f = f*brightness;
		f = (f-0.5f)*contrast+0.5f;
		return f;
	}

	public void setBrightness(float brightness) {
		this.brightness = brightness;
		initialized = false;
	}
	
	public float getBrightness() {
		return brightness;
	}

	public void setContrast(float contrast) {
		this.contrast = contrast;
		initialized = false;
	}
	
	public float getContrast() {
		return contrast;
	}

	public String toString() {
		return "Colors/Contrast...";
	}

}

