/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

/**
 * Utility class for performing common image operations.
 * This class provides methods for tasks such as image creation,
 * image cloning, subimage extraction, and drawing utility methods.
 * All methods in this class are static.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public abstract class ImageUtils {

	public static final int SELECTED = 0xff000000;
	public static final int UNSELECTED = 0x00000000;

	public static BufferedImage backgroundImage = null;

	public static BufferedImage createImage(ImageProducer producer) {
		PixelGrabber pg = new PixelGrabber(producer, 0, 0, -1, -1, null, 0, 0);
		try {
			pg.grabPixels();
		}
		catch (InterruptedException ex) {
			throw new RuntimeException("Image fetch interrupted", ex);
		}
		if ((pg.status() & ImageObserver.ABORT) != 0) {
			throw new RuntimeException("Image fetch aborted");
		}
		if ((pg.status() & ImageObserver.ERROR) != 0) {
			throw new RuntimeException("Image fetch error");
		}
		BufferedImage p = new BufferedImage(pg.getWidth(), pg.getHeight(), BufferedImage.TYPE_INT_ARGB);
		p.setRGB(0, 0, pg.getWidth(), pg.getHeight(), (int[]) pg.getPixels(), 0, pg.getWidth());
		return p;
	}

	// Returns a *copy* of a subimage of image. This avoids the performance problems associated with BufferedImage.getSubimage.
	public static BufferedImage getSubimage(BufferedImage image, int x, int y, int w, int h) {
		BufferedImage newImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawRenderedImage(image, AffineTransform.getTranslateInstance(-x, -y));
		g.dispose();
		return newImage;
	}

	public static BufferedImage cloneImage(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImage.createGraphics();
		g.drawRenderedImage(image, null);
		g.dispose();
		return newImage;
	}

	public static void paintCheckedBackground(Component c, Graphics g, int x, int y, int width, int height) {
		if (backgroundImage == null) {
			backgroundImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
			Graphics bg = backgroundImage.createGraphics();
			for (int by = 0; by < 64; by += 8) {
				for (int bx = 0; bx < 64; bx += 8) {
					bg.setColor(((bx ^ by) & 8) != 0 ? Color.lightGray : Color.white);
					bg.fillRect(bx, by, 8, 8);
				}
			}
			bg.dispose();
		}

		if (backgroundImage != null) {
			Shape saveClip = g.getClip();
			Rectangle r = g.getClipBounds();
			if (r == null) {
				r = new Rectangle(c.getSize());
			}
			r = r.intersection(new Rectangle(x, y, width, height));
			g.setClip(r);
			int w = backgroundImage.getWidth();
			int h = backgroundImage.getHeight();
			if (w != -1 && h != -1) {
				int x1 = (r.x / w) * w;
				int y1 = (r.y / h) * h;
				int x2 = ((r.x + r.width + w - 1) / w) * w;
				int y2 = ((r.y + r.height + h - 1) / h) * h;
				for (y = y1; y < y2; y += h) {
					for (x = x1; x < x2; x += w) {
						g.drawImage(backgroundImage, x, y, c);
					}
				}
			}
			g.setClip(saveClip);
		}
	}

	public static Rectangle getSelectedBounds(BufferedImage p) {
		int width = p.getWidth();
		int height = p.getHeight();
		int maxX = 0;
		int maxY = 0;
		int minX = width;
		int minY = height;
		boolean anySelected = false;
		int y1;

		for (y1 = height - 1; y1 >= 0; y1--) {
			for (int x = 0; x < minX; x++) {
				if ((p.getRGB(x, y1) & 0xff000000) != 0) {
					minX = x;
					maxY = y1;
					anySelected = true;
					break;
				}
			}
			for (int x = width - 1; x >= maxX; x--) {
				if ((p.getRGB(x, y1) & 0xff000000) != 0) {
					maxX = x;
					maxY = y1;
					anySelected = true;
					break;
				}
			}
			if (anySelected) {
				break;
			}
		}
		for (int y = 0; y < y1; y++) {
			for (int x = 0; x < minX; x++) {
				if ((p.getRGB(x, y) & 0xff000000) != 0) {
					minX = x;
					if (y < minY) {
						minY = y;
					}
					anySelected = true;
					break;
				}
			}
			for (int x = width - 1; x >= maxX; x--) {
				if ((p.getRGB(x, y) & 0xff000000) != 0) {
					maxX = x;
					if (y < minY) {
						minY = y;
					}
					anySelected = true;
					break;
				}
			}
		}
		if (anySelected) {
			return new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
		}
		return null;
	}

	/**
	 * Compose src onto dst using the alpha of sel to interpolate between the two.
	 * I can't think of a way to do this using AlphaComposite.
	 */
	public static void composeThroughMask(Raster src, WritableRaster dst, Raster sel) {
		int x = src.getMinX();
		int y = src.getMinY();
		int w = src.getWidth();
		int h = src.getHeight();

		int[] srcRGB = null;
		int[] selRGB = null;
		int[] dstRGB = null;

		for (int i = 0; i < h; i++) {
			srcRGB = src.getPixels(x, y, w, 1, srcRGB);
			selRGB = sel.getPixels(x, y, w, 1, selRGB);
			dstRGB = dst.getPixels(x, y, w, 1, dstRGB);

			int k = x;
			for (int j = 0; j < w; j++) {
				int sr = srcRGB[k];
				int dir = dstRGB[k];
				int sg = srcRGB[k + 1];
				int dig = dstRGB[k + 1];
				int sb = srcRGB[k + 2];
				int dib = dstRGB[k + 2];
				int sa = srcRGB[k + 3];
				int dia = dstRGB[k + 3];

				float a = selRGB[k + 3] / 255f;
				float ac = 1 - a;

				dstRGB[k] = (int) (a * sr + ac * dir);
				dstRGB[k + 1] = (int) (a * sg + ac * dig);
				dstRGB[k + 2] = (int) (a * sb + ac * dib);
				dstRGB[k + 3] = (int) (a * sa + ac * dia);
				k += 4;
			}

			dst.setPixels(x, y, w, 1, dstRGB);
			y++;
		}
	}
}

