/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImageOp;

/**
 * @author Jerry Huxtable
 */
public interface MutatableFilter {
	void mutate(float mutationLevel, BufferedImageOp dst, boolean keepShape, boolean keepColors);
}
