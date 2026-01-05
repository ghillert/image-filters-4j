/*
 ** Copyright 2005 Huxtable.com. All rights reserved.
 */

package com.jhlabs.image;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.Serializable;
import java.util.Vector;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.ImageFunction2D;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Vector3f;

/**
 * The LightFilter class provides a filter for simulating advanced lighting effects such as bump mapping,
 * shading, and reflection using various lighting models and sources. This class is a part of the image
 * processing library and extends the {@link WholeImageFilter} base class, enabling image-wide filter operations.
 * <p>
 * The filter allows configuration of bump mapping, material properties, lighting sources, and environment
 * maps. It supports a range of lighting calculations, including diffuse and specular shading, with the
 * ability to handle multiple light sources.
 * <p>
 * Features include:
 * - Support for different bump mapping methods, including using images or predefined functions.
 * - Control over lighting parameters such as view distance and material properties.
 * - Integration of an environment map for reflection and advanced shading.
 *
 * @author Jerry Huxtable
 * @author Gunnar Hillert
 */
public class LightFilter extends WholeImageFilter implements Serializable {

	public static final int COLORS_FROM_IMAGE = 0;
	public static final int COLORS_CONSTANT = 1;

	public static final int BUMPS_FROM_IMAGE = 0;
	public static final int BUMPS_FROM_IMAGE_ALPHA = 1;
	public static final int BUMPS_FROM_MAP = 2;
	public static final int BUMPS_FROM_BEVEL = 3;

	public static final int AMBIENT = 0;
	public static final int DISTANT = 1;
	public static final int POINT = 2;
	public static final int SPOT = 3;

	private float bumpHeight;
	private float bumpSoftness;
	private float viewDistance = 10000.0f;
	Material material;
	private final Vector lights;
	private int colorSource = COLORS_FROM_IMAGE;
	private int bumpSource = BUMPS_FROM_IMAGE;
	private Function2D bumpFunction;
	private Image environmentMap;
	private int[] envPixels;
	private int envWidth = 1;
	private int envHeight = 1;
	private final Vector3f l;
	private final Vector3f v;
	private final Vector3f n;
	private final Color4f shadedColor;
	private final Color4f diffuse_color;
	private final Color4f specular_color;
	private final Vector3f tmpv;
	private final Vector3f tmpv2;
	public NormalEvaluator normalEvaluator = new NormalEvaluator();

	public LightFilter() {
		this.lights = new Vector();
		addLight(new DistantLight());
		this.bumpHeight = 1.0f;
		this.bumpSoftness = 5.0f;
		this.material = new Material();
		this.l = new Vector3f();
		this.v = new Vector3f();
		this.n = new Vector3f();
		this.shadedColor = new Color4f();
		this.diffuse_color = new Color4f();
		this.specular_color = new Color4f();
		this.tmpv = new Vector3f();
		this.tmpv2 = new Vector3f();
	}

	public void setBumpFunction(Function2D bumpFunction) {
		this.bumpFunction = bumpFunction;
	}

	public Function2D getBumpFunction() {
		return this.bumpFunction;
	}

	public void setBumpHeight(float bumpHeight) {
		this.bumpHeight = bumpHeight;
	}

	public float getBumpHeight() {
		return this.bumpHeight;
	}

	public void setBumpSoftness(float bumpSoftness) {
		this.bumpSoftness = bumpSoftness;
	}

	public float getBumpSoftness() {
		return this.bumpSoftness;
	}

	public void setViewDistance(float viewDistance) {
		this.viewDistance = viewDistance;
	}

	public float getViewDistance() {
		return this.viewDistance;
	}

	public void setEnvironmentMap(BufferedImage environmentMap) {
		this.environmentMap = environmentMap;
		if (environmentMap != null) {
			this.envWidth = environmentMap.getWidth();
			this.envHeight = environmentMap.getHeight();
			this.envPixels = getRGB(environmentMap, 0, 0, this.envWidth, this.envHeight, null);
		}
		else {
			this.envWidth = 1;
			this.envHeight = 1;
			this.envPixels = null;
		}
	}

	public Image getEnvironmentMap() {
		return this.environmentMap;
	}

	public void setColorSource(int colorSource) {
		this.colorSource = colorSource;
	}

	public int getColorSource() {
		return this.colorSource;
	}

	public void setBumpSource(int bumpSource) {
		this.bumpSource = bumpSource;
	}

	public int getBumpSource() {
		return this.bumpSource;
	}

	public void setDiffuseColor(int diffuseColor) {
		this.material.diffuseColor = diffuseColor;
	}

	public int getDiffuseColor() {
		return this.material.diffuseColor;
	}

	public void addLight(Light light) {
		this.lights.addElement(light);
	}

	public void removeLight(Light light) {
		this.lights.removeElement(light);
	}

	public Vector getLights() {
		return this.lights;
	}

	protected static final float r255 = 1.0f / 255.0f;

	protected void setFromRGB(Color4f c, int argb) {
		c.set(((argb >> 16) & 0xff) * r255, ((argb >> 8) & 0xff) * r255, (argb & 0xff) * r255, ((argb >> 24) & 0xff) * r255);
	}

	@Override
	protected int[] filterPixels(int width, int height, int[] inPixels, Rectangle transformedSpace) {
		int index = 0;
		int[] outPixels = new int[width * height];
		float width45 = Math.abs(6.0f * this.bumpHeight);
		boolean invertBumps = this.bumpHeight < 0;
		Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f viewpoint = new Vector3f((float) width / 2.0f, (float) height / 2.0f, this.viewDistance);
		Vector3f normal = new Vector3f();
		Color4f envColor = new Color4f();
		Color4f diffuseColor = new Color4f(new Color(this.material.diffuseColor));
		Color4f specularColor = new Color4f(new Color(this.material.specularColor));
		Function2D bump = this.bumpFunction;

		if (this.bumpSource == BUMPS_FROM_IMAGE || this.bumpSource == BUMPS_FROM_IMAGE_ALPHA || this.bumpSource == BUMPS_FROM_MAP || bump == null) {
			if (this.bumpSoftness != 0) {
				int bumpWidth = width;
				int bumpHeight = height;
				int[] bumpPixels = inPixels;
				if (this.bumpSource == BUMPS_FROM_MAP && this.bumpFunction instanceof ImageFunction2D if2d) {
					bumpWidth = if2d.getWidth();
					bumpHeight = if2d.getHeight();
					bumpPixels = if2d.getPixels();
				}
				Kernel kernel = GaussianFilter.makeKernel(this.bumpSoftness);
				int[] tmpPixels = new int[bumpWidth * bumpHeight];
				int[] softPixels = new int[bumpWidth * bumpHeight];
				GaussianFilter.convolveAndTranspose(kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, ConvolveFilter.CLAMP_EDGES);
				GaussianFilter.convolveAndTranspose(kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, ConvolveFilter.CLAMP_EDGES);
				bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, ImageFunction2D.CLAMP, this.bumpSource == BUMPS_FROM_IMAGE_ALPHA);
			}
			else {
				bump = new ImageFunction2D(inPixels, width, height, ImageFunction2D.CLAMP, this.bumpSource == BUMPS_FROM_IMAGE_ALPHA);
			}
		}

		float reflectivity = this.material.reflectivity;
		float areflectivity = (1 - reflectivity);
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f n = new Vector3f();
		Light[] lightsArray = new Light[this.lights.size()];
		this.lights.copyInto(lightsArray);
		for (int i = 0; i < lightsArray.length; i++) {
			lightsArray[i].prepare(width, height);
		}

		// Loop through each source pixel
		for (int y = 0; y < height; y++) {
			float ny = y;
			position.y = y;
			for (int x = 0; x < width; x++) {
				float nx = x;

				// Calculate the normal at this point
				if (this.bumpSource != BUMPS_FROM_BEVEL) {
					// Complicated and slower method
					// Calculate four normals using the gradients in +/- X/Y directions
					int count = 0;
					normal.x = 0;
					normal.y = 0;
					normal.z = 0;
					float m0 = width45 * bump.evaluate(nx, ny);
					float m1 = (x > 0) ? ((width45 * bump.evaluate(nx - 1.0f, ny)) - m0) : -2;
					float m2 = (y > 0) ? ((width45 * bump.evaluate(nx, ny - 1.0f)) - m0) : -2;
					float m3 = (x < (width - 1)) ? ((width45 * bump.evaluate(nx + 1.0f, ny)) - m0) : -2;
					float m4 = (y < (height - 1)) ? ((width45 * bump.evaluate(nx, ny + 1.0f)) - m0) : -2;

					if (m1 != -2 && m4 != -2) {
						v1.x = -1.0f;
						v1.y = 0.0f;
						v1.z = m1;
						v2.x = 0.0f;
						v2.y = 1.0f;
						v2.z = m4;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0) {
							n.z = -n.z;
						}
						normal.add(n);
						count++;
					}

					if (m1 != -2 && m2 != -2) {
						v1.x = -1.0f;
						v1.y = 0.0f;
						v1.z = m1;
						v2.x = 0.0f;
						v2.y = -1.0f;
						v2.z = m2;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0) {
							n.z = -n.z;
						}
						normal.add(n);
						count++;
					}

					if (m2 != -2 && m3 != -2) {
						v1.x = 0.0f;
						v1.y = -1.0f;
						v1.z = m2;
						v2.x = 1.0f;
						v2.y = 0.0f;
						v2.z = m3;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0) {
							n.z = -n.z;
						}
						normal.add(n);
						count++;
					}

					if (m3 != -2 && m4 != -2) {
						v1.x = 1.0f;
						v1.y = 0.0f;
						v1.z = m3;
						v2.x = 0.0f;
						v2.y = 1.0f;
						v2.z = m4;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0) {
							n.z = -n.z;
						}
						normal.add(n);
						count++;
					}

					// Average the four normals
					normal.x /= count;
					normal.y /= count;
					normal.z /= count;
				}
				else {
					if (this.normalEvaluator != null) {
						this.normalEvaluator.getNormalAt(x, y, width, height, normal);
					}
				}
				if (invertBumps) {
					normal.x = -normal.x;
					normal.y = -normal.y;
				}
				position.x = x;

				if (normal.z >= 0) {
					// Get the material colour at this point
					if (this.colorSource == COLORS_FROM_IMAGE) {
						setFromRGB(diffuseColor, inPixels[index]);
					}
					else {
						setFromRGB(diffuseColor, this.material.diffuseColor);
					}
					if (reflectivity != 0 && this.environmentMap != null) {
						// FIXME-too much normalizing going on here
						this.tmpv2.set(viewpoint);
						this.tmpv2.z = 100.0f; //FIXME
						this.tmpv2.sub(position);
						this.tmpv2.normalize();
						this.tmpv.set(normal);
						this.tmpv.normalize();

						// Reflect
						this.tmpv.scale(2.0f * this.tmpv.dot(this.tmpv2));
						this.tmpv.sub(this.v);

						this.tmpv.normalize();
						setFromRGB(envColor, getEnvironmentMap(this.tmpv, inPixels, width, height)); //FIXME-interpolate()
						diffuseColor.x = reflectivity * envColor.x + areflectivity * diffuseColor.x;
						diffuseColor.y = reflectivity * envColor.y + areflectivity * diffuseColor.y;
						diffuseColor.z = reflectivity * envColor.z + areflectivity * diffuseColor.z;
					}
					// Shade the pixel
					Color4f c = phongShade(position, viewpoint, normal, diffuseColor, specularColor, this.material, lightsArray);
					int alpha = inPixels[index] & 0xff000000;
					int rgb = ((int) (c.x * 255) << 16) | ((int) (c.y * 255) << 8) | (int) (c.z * 255);
					outPixels[index++] = alpha | rgb;
				}
				else {
					outPixels[index++] = 0;
				}
			}
		}
		return outPixels;
	}

	public Color4f phongShade(Vector3f position, Vector3f viewpoint, Vector3f normal, Color4f diffuseColor, Color4f specularColor, Material material, Light[] lightsArray) {
		this.shadedColor.set(diffuseColor);
		this.shadedColor.scale(material.ambientIntensity);

		for (int i = 0; i < lightsArray.length; i++) {
			Light light = lightsArray[i];
			this.n.set(normal);
			this.l.set(light.position);
			if (light.type != DISTANT) {
				this.l.sub(position);
			}
			this.l.normalize();
			float nDotL = this.n.dot(this.l);
			if (nDotL >= 0.0) {
				float dDotL = 0;

				this.v.set(viewpoint);
				this.v.sub(position);
				this.v.normalize();

				// Spotlight
				if (light.type == SPOT) {
					dDotL = light.direction.dot(this.l);
					if (dDotL < light.cosConeAngle) {
						continue;
					}
				}

				this.n.scale(2.0f * nDotL);
				this.n.sub(this.l);
				float rDotV = this.n.dot(this.v);

				float rv;
				if (rDotV < 0.0) {
					rv = 0.0f;
				}
				else {
					rv = (float) Math.pow(rDotV, material.highlight);
				}

				// Spotlight
				if (light.type == SPOT) {
					dDotL = light.cosConeAngle / dDotL;
					float e = dDotL;
					e *= e;
					e *= e;
					e *= e;
					e = (float) Math.pow(dDotL, light.focus * 10) * (1 - e);
					rv *= e;
					nDotL *= e;
				}

				this.diffuse_color.set(diffuseColor);
				this.diffuse_color.scale(material.diffuseReflectivity);
				this.diffuse_color.x *= light.realColor.x * nDotL;
				this.diffuse_color.y *= light.realColor.y * nDotL;
				this.diffuse_color.z *= light.realColor.z * nDotL;
				this.specular_color.set(specularColor);
				this.specular_color.scale(material.specularReflectivity);
				this.specular_color.x *= light.realColor.x * rv;
				this.specular_color.y *= light.realColor.y * rv;
				this.specular_color.z *= light.realColor.z * rv;
				this.diffuse_color.add(this.specular_color);
				this.diffuse_color.clamp(0, 1);
				this.shadedColor.add(this.diffuse_color);
			}
		}
		this.shadedColor.clamp(0, 1);
		return this.shadedColor;
	}

	private final int[] rgb = new int[4];

	private int getEnvironmentMap(Vector3f normal, int[] inPixels, int width, int height) {
		if (this.environmentMap != null) {
			float angle = (float) Math.acos(-normal.y);

			float x;
			float y;
			y = angle / ImageMath.PI;

			if (y == 0.0f || y == 1.0f) {
				x = 0.0f;
			}
			else {
				float f = normal.x / (float) Math.sin(angle);

				if (f > 1.0f) {
					f = 1.0f;
				}
				else if (f < -1.0f) {
					f = -1.0f;
				}

				x = (float) Math.acos(f) / ImageMath.PI;
			}
			// A bit of empirical scaling....
//			x = (x-0.5f)*1.2f+0.5f;
//			y = (y-0.5f)*1.2f+0.5f;
			x = ImageMath.clamp(x * this.envWidth, 0, this.envWidth - 1);
			y = ImageMath.clamp(y * this.envHeight, 0, this.envHeight - 1);
			int ix = (int) x;
			int iy = (int) y;

			float xWeight = x - ix;
			float yWeight = y - iy;
			int i = this.envWidth * iy + ix;
			int dx = (ix == (this.envWidth - 1)) ? 0 : 1;
			int dy = (iy == (this.envHeight - 1)) ? 0 : this.envWidth;
			this.rgb[0] = this.envPixels[i];
			this.rgb[1] = this.envPixels[i + dx];
			this.rgb[2] = this.envPixels[i + dy];
			this.rgb[3] = this.envPixels[i + dx + dy];
			return ImageMath.bilinearInterpolate(xWeight, yWeight, this.rgb);
		}
		return 0;
	}

	@Override
	public String toString() {
		return "Stylize/Light Effects...";
	}

	public class NormalEvaluator {
		public static final int RECTANGLE = 0;
		public static final int ROUNDRECT = 1;
		public static final int ELLIPSE = 2;

		public static final int LINEAR = 0;
		public static final int SIN = 1;
		public static final int CIRCLE_UP = 2;
		public static final int CIRCLE_DOWN = 3;
		public static final int SMOOTH = 4;
		public static final int PULSE = 5;
		public static final int SMOOTH_PULSE = 6;
		public static final int THING = 7;

		private int margin = 10;
		private int shape = RECTANGLE;
		private int bevel = LINEAR;
		private int cornerRadius = 15;

		public void setMargin(int margin) {
			this.margin = margin;
		}

		public int getMargin() {
			return this.margin;
		}

		public void setCornerRadius(int cornerRadius) {
			this.cornerRadius = cornerRadius;
		}

		public int getCornerRadius() {
			return this.cornerRadius;
		}

		public void setShape(int shape) {
			this.shape = shape;
		}

		public int getShape() {
			return this.shape;
		}

		public void setBevel(int bevel) {
			this.bevel = bevel;
		}

		public int getBevel() {
			return this.bevel;
		}

		public void getNormalAt(int x, int y, int width, int height, Vector3f normal) {
			float distance = 0;
			normal.x = 0;
			normal.y = 0;
			normal.z = 0.707f;
			switch (this.shape) {
				case RECTANGLE:
					if (x < this.margin) {
						if (x < y && x < height - y) {
							normal.x = -1;
						}
					}
					else if (width - x <= this.margin) {
						if (width - x - 1 < y && width - x <= height - y) {
							normal.x = 1;
						}
					}
					if (normal.x == 0) {
						if (y < this.margin) {
							normal.y = -1;
						}
						else if (height - y <= this.margin) {
							normal.y = 1;
						}
					}
					distance = Math.min(Math.min(x, y), Math.min(width - x - 1, height - y - 1));
					break;
				case ELLIPSE:
					float a = width / 2;
					float b = height / 2;
					float a2 = a * a;
					float b2 = b * b;
					float dx = x - a;
					float dy = y - b;
					float x2 = dx * dx;
					float y2 = dy * dy;
					distance = (b2 - (b2 * x2) / a2) - y2;
					float radius = (float) Math.sqrt(x2 + y2);
					distance = 0.5f * distance / ((a + b) / 2); // FIXME
					if (radius != 0) {
						normal.x = dx / radius;
						normal.y = dy / radius;
					}
					break;
				case ROUNDRECT:
					distance = Math.min(Math.min(x, y), Math.min(width - x - 1, height - y - 1));
					float c = Math.min(this.cornerRadius, Math.min(width / 2, height / 2));
					if ((x < c || width - x <= c) && (y < c || height - y <= c)) {
						if (width - x <= c) {
							x -= width - c - c - 1;
						}
						if (height - y <= c) {
							y -= height - c - c - 1;
						}
						dx = x - c;
						dy = y - c;
						x2 = dx * dx;
						y2 = dy * dy;
						radius = (float) Math.sqrt(x2 + y2);
						distance = c - radius;
						normal.x = dx / radius;
						normal.y = dy / radius;
					}
					else if (x < this.margin) {
						normal.x = -1;
					}
					else if (width - x <= this.margin) {
						normal.x = 1;
					}
					else if (y < this.margin) {
						normal.y = -1;
					}
					else if (height - y <= this.margin) {
						normal.y = 1;
					}
					break;
			}
			distance /= this.margin;
			if (distance < 0) {
				normal.z = -1;
				normal.normalize();
				return;
			}

			float dx = 1.0f / this.margin;
			float z1 = bevelFunction(distance);
			float z2 = bevelFunction(distance + dx);
			float dz = z2 - z1;
			normal.z = dx;
			normal.x *= dz;
			normal.y *= dz;
			/*
						if (dz == 0)
							normal.z = 1e10;
						else {
							float f = dz/(1.0/margin);
							normal.x /= f;
							normal.y /= f;
							normal.z *= f;
						}
			*/

			normal.normalize();
		}

		private float bevelFunction(float x) {
			x = ImageMath.clamp(x, 0.0f, 1.0f);
			switch (this.bevel) {
				case LINEAR:
					return ImageMath.clamp(x, 0.0f, 1.0f);
				case SIN:
					return (float) Math.sin(x * Math.PI / 2);
				case CIRCLE_UP:
					return ImageMath.circleUp(x);
				case CIRCLE_DOWN:
					return ImageMath.circleDown(x);
				case SMOOTH:
					return ImageMath.smoothStep(0.1f, 0.9f, x);
				case PULSE:
					return ImageMath.pulse(0.0f, 1.0f, x);
				case SMOOTH_PULSE:
					return ImageMath.smoothPulse(0.0f, 0.1f, 0.5f, 1.0f, x);
				case THING:
					return (float) ((x < 0.2) ? Math.sin(x / 0.2 * Math.PI / 2) : (0.5 + (0.5 * Math.sin(1 + x / 0.6 * Math.PI / 2))));
			}
			return x;
		}
	}

	public static class Material {
		int diffuseColor;
		int specularColor;
		float ambientIntensity;
		float diffuseReflectivity;
		float specularReflectivity;
		float highlight;
		float reflectivity;

		public Material() {
			this.ambientIntensity = 0.5f;
			this.diffuseReflectivity = 1.0f;
			this.specularReflectivity = 1.0f;
			this.highlight = 3.0f;
			this.reflectivity = 0.0f;
			this.diffuseColor = 0xff888888;
			this.specularColor = 0xffffffff;
		}

		public void setDiffuseColor(int diffuseColor) {
			this.diffuseColor = diffuseColor;
		}

		public int getDiffuseColor() {
			return this.diffuseColor;
		}

	}

	public static class Light implements Cloneable {

		int type = AMBIENT;
		Vector3f position;
		Vector3f direction;
		Color4f realColor = new Color4f();
		int color = 0xffffffff;
		float intensity;
		float azimuth;
		float elevation;
		float focus = 0.5f;
		float centreX = 0.5f;
		float centreY = 0.5f;
		float coneAngle = ImageMath.PI / 6;
		float cosConeAngle;
		float distance = 100.0f;

		public Light() {
			this(135 * ImageMath.PI / 180.0f, 0.5235987755982988f, 1.0f);
		}

		public Light(float azimuth, float elevation, float intensity) {
			this.azimuth = azimuth;
			this.elevation = elevation;
			this.intensity = intensity;
		}

		public void setAzimuth(float azimuth) {
			this.azimuth = azimuth;
		}

		public float getAzimuth() {
			return this.azimuth;
		}

		public void setElevation(float elevation) {
			this.elevation = elevation;
		}

		public float getElevation() {
			return this.elevation;
		}

		public void setDistance(float distance) {
			this.distance = distance;
		}

		public float getDistance() {
			return this.distance;
		}

		public void setIntensity(float intensity) {
			this.intensity = intensity;
		}

		public float getIntensity() {
			return this.intensity;
		}

		public void setConeAngle(float coneAngle) {
			this.coneAngle = coneAngle;
		}

		public float getConeAngle() {
			return this.coneAngle;
		}

		public void setFocus(float focus) {
			this.focus = focus;
		}

		public float getFocus() {
			return this.focus;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getColor() {
			return this.color;
		}

		public void setCentreX(float x) {
			this.centreX = x;
		}

		public float getCentreX() {
			return this.centreX;
		}

		public void setCentreY(float y) {
			this.centreY = y;
		}

		public float getCentreY() {
			return this.centreY;
		}

		public void prepare(int width, int height) {
			float lx = (float) (Math.cos(this.azimuth) * Math.cos(this.elevation));
			float ly = (float) (Math.sin(this.azimuth) * Math.cos(this.elevation));
			float lz = (float) Math.sin(this.elevation);
			this.direction = new Vector3f(lx, ly, lz);
			this.direction.normalize();
			if (this.type != DISTANT) {
				lx *= this.distance;
				ly *= this.distance;
				lz *= this.distance;
				lx += width * this.centreX;
				ly += height * (1 - this.centreY);
			}
			this.position = new Vector3f(lx, ly, lz);
			this.realColor.set(new Color(this.color));
			this.realColor.scale(this.intensity);
			this.cosConeAngle = (float) Math.cos(this.coneAngle);
		}

		@Override
		public Object clone() {
			try {
				Light copy = (Light) super.clone();
				return copy;
			}
			catch (CloneNotSupportedException ex) {
				return null;
			}
		}

		@Override
		public String toString() {
			return "Light";
		}

	}

	public class AmbientLight extends Light {
		@Override
		public String toString() {
			return "Ambient Light";
		}
	}

	public class PointLight extends Light {
		public PointLight() {
			this.type = POINT;
		}

		@Override
		public String toString() {
			return "Point Light";
		}
	}

	public class DistantLight extends Light {
		public DistantLight() {
			this.type = DISTANT;
		}

		@Override
		public String toString() {
			return "Distant Light";
		}
	}

	public class SpotLight extends Light {
		public SpotLight() {
			this.type = SPOT;
		}

		@Override
		public String toString() {
			return "Spotlight";
		}
	}
}
