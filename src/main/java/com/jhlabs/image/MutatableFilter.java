/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.image.BufferedImageOp;

public interface MutatableFilter {
	public void mutate(float mutationLevel, BufferedImageOp dst, boolean keepShape, boolean keepColors);
}
