/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.*;

public class ExposureFilter extends TransferFilter {

	private float exposure = 1.0f;

	protected float transferFunction( float f ) {
		return 1 - (float)Math.exp(-f * exposure);
	}

	public void setExposure(float exposure) {
		this.exposure = exposure;
		initialized = false;
	}
	
	public float getExposure() {
		return exposure;
	}

	public String toString() {
		return "Colors/Exposure...";
	}

}

