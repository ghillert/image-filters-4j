/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MirrorFilter extends AbstractBufferedImageOp {
	private float opacity = 1.0f;
	private float centreY = 0.5f;
	private float distance;
	private float angle;
	private float rotation;
	private float gap;

	public MirrorFilter() {
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle() {
		return this.angle;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getDistance() {
		return this.distance;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return this.rotation;
	}

	public void setGap(float gap) {
		this.gap = gap;
	}

	public float getGap() {
		return this.gap;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public float getOpacity() {
		return this.opacity;
	}

	public void setCentreY(float centreY) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return this.centreY;
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dst) {
		if (dst == null) {
			dst = createCompatibleDestImage(src, null);
		}
		BufferedImage tsrc = src;
		Shape clip;
		int width = src.getWidth();
		int height = src.getHeight();
		int h = (int) (this.centreY * height);
		int d = (int) (this.gap * height);

		Graphics2D g = dst.createGraphics();
		clip = g.getClip();
		g.clipRect(0, 0, width, h);
		g.drawRenderedImage(src, null);
		g.setClip(clip);
		g.clipRect(0, h + d, width, height - h - d);
		g.translate(0, 2 * h + d);
		g.scale(1, -1);
		g.drawRenderedImage(src, null);
		g.setPaint(new GradientPaint(0, 0, new Color(1.0f, 0.0f, 0.0f, 0.0f), 0, h, new Color(0.0f, 1.0f, 0.0f, this.opacity)));
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_IN));
		g.fillRect(0, 0, width, h);
		g.setClip(clip);
		g.dispose();

		return dst;
	}

	@Override
	public String toString() {
		return "Effects/Mirror...";
	}
}
