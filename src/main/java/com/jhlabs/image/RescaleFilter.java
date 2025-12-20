/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class RescaleFilter extends TransferFilter {

	static final long serialVersionUID = -2724874183243154495L;
	
	private float scale = 1.0f;
	
	protected float transferFunction( float v ) {
		return PixelUtils.clamp((int)(v * scale));
	}

	public void setScale(float scale) {
		this.scale = scale;
		initialized = false;
	}
	
	public float getScale() {
		return scale;
	}

	public String toString() {
		return "Colors/Rescale...";
	}

}

