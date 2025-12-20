/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * A filter to add a border around an image using the supplied Paint, which may be null for no painting.
 */
public class BorderFilter extends AbstractBufferedImageOp {

	private int leftBorder, rightBorder;
	private int topBorder, bottomBorder;
	private Paint borderPaint;

	public BorderFilter() {
	}

	public BorderFilter( int leftBorder, int topBorder, int rightBorder, int bottomBorder, Paint borderPaint ) {
		this.leftBorder = leftBorder;
		this.topBorder = topBorder;
		this.rightBorder = rightBorder;
		this.bottomBorder = bottomBorder;
		this.borderPaint = borderPaint;
	}

	public void setLeftBorder(int leftBorder) {
		this.leftBorder = leftBorder;
	}
	
	public int getLeftBorder() {
		return leftBorder;
	}
	
	public void setRightBorder(int rightBorder) {
		this.rightBorder = rightBorder;
	}
	
	public int getRightBorder() {
		return rightBorder;
	}
	
	public void setTopBorder(int topBorder) {
		this.topBorder = topBorder;
	}

	public int getTopBorder() {
		return topBorder;
	}

	public void setBottomBorder(int bottomBorder) {
		this.bottomBorder = bottomBorder;
	}

	public int getBottomBorder() {
		return bottomBorder;
	}

	public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
		int width = src.getWidth();
		int height = src.getHeight();

		if ( dst == null )
			dst = new BufferedImage( width+leftBorder+rightBorder, height+topBorder+bottomBorder, src.getType() );
		Graphics2D g = dst.createGraphics();
		if ( borderPaint != null ) {
			g.setPaint( borderPaint );
			if ( leftBorder > 0 )
				g.fillRect( 0, 0, leftBorder, height );
			if ( rightBorder > 0 )
				g.fillRect( width-rightBorder, 0, rightBorder, height );
			if ( topBorder > 0 )
				g.fillRect( leftBorder, 0, width-leftBorder-rightBorder, topBorder );
			if ( bottomBorder > 0 )
				g.fillRect( leftBorder, height-bottomBorder, width-leftBorder-rightBorder, bottomBorder );
		}
		g.drawRenderedImage( src, AffineTransform.getTranslateInstance( leftBorder, rightBorder ) );
		g.dispose();
		return dst;
	}

	public String toString() {
		return "Distort/Border...";
	}
}
