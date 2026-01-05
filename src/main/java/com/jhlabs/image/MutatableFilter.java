/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.image.BufferedImageOp;

/**
 * The MutatableFilter interface provides a structure for filters that can mutate their parameters
 * dynamically. It is primarily used to alter image filters' behavior or visuals with a provided mutation
 * level, destination filter, and options for preserving graphic properties.
 *
 * @author Jerry Huxtable
 */
public interface MutatableFilter {
	void mutate(float mutationLevel, BufferedImageOp dst, boolean keepShape, boolean keepColors);
}
