/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import com.jhlabs.math.Function2D;
import com.jhlabs.math.ImageFunction2D;
import com.jhlabs.vecmath.Color4f;
import com.jhlabs.vecmath.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;
import java.io.Serializable;
import java.util.Vector;

interface ElevationMap {
	int getHeightAt(int x, int y);
}

public class LightFilter extends WholeImageFilter implements Serializable {
	
	public final static int COLORS_FROM_IMAGE = 0;
	public final static int COLORS_CONSTANT = 1;

	public final static int BUMPS_FROM_IMAGE = 0;
	public final static int BUMPS_FROM_IMAGE_ALPHA = 1;
	public final static int BUMPS_FROM_MAP = 2;
	public final static int BUMPS_FROM_BEVEL = 3;

	private float bumpHeight;
	private float bumpSoftness;
	private float viewDistance = 10000.0f;
	Material material;
	private Vector lights;
	private int colorSource = COLORS_FROM_IMAGE;
	private int bumpSource = BUMPS_FROM_IMAGE;
	private Function2D bumpFunction;
	private Image environmentMap;
	private int[] envPixels;
	private int envWidth = 1, envHeight = 1;
	private Vector3f l;
	private Vector3f v;
	private Vector3f n;
	private Color4f shadedColor;
	private Color4f diffuse_color;
	private Color4f specular_color;
	private Vector3f tmpv, tmpv2;
	public NormalEvaluator normalEvaluator = new NormalEvaluator();

	public LightFilter() {
		lights = new Vector();
		addLight(new DistantLight());
		bumpHeight = 1.0f;
		bumpSoftness = 5.0f;
		material = new Material();
		l = new Vector3f();
		v = new Vector3f();
		n = new Vector3f();
		shadedColor = new Color4f();
		diffuse_color = new Color4f();
		specular_color = new Color4f();
		tmpv = new Vector3f();
		tmpv2 = new Vector3f();
	}

	public void setBumpFunction(Function2D bumpFunction) {
		this.bumpFunction = bumpFunction;
	}

	public Function2D getBumpFunction() {
		return bumpFunction;
	}

	public void setBumpHeight(float bumpHeight) {
		this.bumpHeight = bumpHeight;
	}

	public float getBumpHeight() {
		return bumpHeight;
	}

	public void setBumpSoftness(float bumpSoftness) {
		this.bumpSoftness = bumpSoftness;
	}

	public float getBumpSoftness() {
		return bumpSoftness;
	}

	public void setViewDistance(float viewDistance) {
		this.viewDistance = viewDistance;
	}

	public float getViewDistance() {
		return viewDistance;
	}

	public void setEnvironmentMap(BufferedImage environmentMap) {
		this.environmentMap = environmentMap;
		if (environmentMap != null) {
			envWidth = environmentMap.getWidth();
			envHeight = environmentMap.getHeight();
			envPixels = getRGB( environmentMap, 0, 0, envWidth, envHeight, null );
		} else {
			envWidth = envHeight = 1;
			envPixels = null;
		}
	}

	public Image getEnvironmentMap() {
		return environmentMap;
	}

	public void setColorSource(int colorSource) {
		this.colorSource = colorSource;
	}

	public int getColorSource() {
		return colorSource;
	}

	public void setBumpSource(int bumpSource) {
		this.bumpSource = bumpSource;
	}

	public int getBumpSource() {
		return bumpSource;
	}

	public void setDiffuseColor(int diffuseColor) {
		material.diffuseColor = diffuseColor;
	}

	public int getDiffuseColor() {
		return material.diffuseColor;
	}

	public void addLight(Light light) {
		lights.addElement(light);
	}
	
	public void removeLight(Light light) {
		lights.removeElement(light);
	}
	
	public Vector getLights() {
		return lights;
	}
	
	protected final static float r255 = 1.0f/255.0f;

	protected void setFromRGB( Color4f c, int argb ) {
		c.set( ((argb >> 16) & 0xff) * r255, ((argb >> 8) & 0xff) * r255, (argb & 0xff) * r255, ((argb >> 24) & 0xff) * r255 );
	}
	
	protected int[] filterPixels( int width, int height, int[] inPixels, Rectangle transformedSpace ) {
		int index = 0;
		int[] outPixels = new int[width * height];
		float width45 = Math.abs(6.0f * bumpHeight);
		boolean invertBumps = bumpHeight < 0;
		Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
		Vector3f viewpoint = new Vector3f((float)width / 2.0f, (float)height / 2.0f, viewDistance);
		Vector3f normal = new Vector3f();
		Color4f envColor = new Color4f();
		Color4f diffuseColor = new Color4f( new Color(material.diffuseColor) );
		Color4f specularColor = new Color4f( new Color(material.specularColor) );
		Function2D bump = bumpFunction;

		if (bumpSource == BUMPS_FROM_IMAGE || bumpSource == BUMPS_FROM_IMAGE_ALPHA || bumpSource == BUMPS_FROM_MAP || bump == null) {
			if ( bumpSoftness != 0 ) {
				int bumpWidth = width;
				int bumpHeight = height;
				int[] bumpPixels = inPixels;
				if ( bumpSource == BUMPS_FROM_MAP && bumpFunction instanceof ImageFunction2D ) {
					ImageFunction2D if2d = (ImageFunction2D)bumpFunction;
					bumpWidth = if2d.getWidth();
					bumpHeight = if2d.getHeight();
					bumpPixels = if2d.getPixels();
				}
				Kernel kernel = GaussianFilter.makeKernel( bumpSoftness );
				int [] tmpPixels = new int[bumpWidth * bumpHeight];
				int [] softPixels = new int[bumpWidth * bumpHeight];
				GaussianFilter.convolveAndTranspose( kernel, bumpPixels, tmpPixels, bumpWidth, bumpHeight, true, ConvolveFilter.CLAMP_EDGES);
				GaussianFilter.convolveAndTranspose( kernel, tmpPixels, softPixels, bumpHeight, bumpWidth, true, ConvolveFilter.CLAMP_EDGES);
				bump = new ImageFunction2D(softPixels, bumpWidth, bumpHeight, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
			} else
				bump = new ImageFunction2D(inPixels, width, height, ImageFunction2D.CLAMP, bumpSource == BUMPS_FROM_IMAGE_ALPHA);
		}

		float reflectivity = material.reflectivity;
		float areflectivity = (1-reflectivity);
		Vector3f v1 = new Vector3f();
		Vector3f v2 = new Vector3f();
		Vector3f n = new Vector3f();
		Light[] lightsArray = new Light[lights.size()];
		lights.copyInto(lightsArray);
		for (int i = 0; i < lightsArray.length; i++)
			lightsArray[i].prepare(width, height);

		// Loop through each source pixel
		for (int y = 0; y < height; y++) {
			float ny = y;
			position.y = y;
			for (int x = 0; x < width; x++) {
				float nx = x;
				
				// Calculate the normal at this point
				if (bumpSource != BUMPS_FROM_BEVEL) {
					// Complicated and slower method
					// Calculate four normals using the gradients in +/- X/Y directions
					int count = 0;
					normal.x = normal.y = normal.z = 0;
					float m0 = width45*bump.evaluate(nx, ny);
					float m1 = x > 0 ? width45*bump.evaluate(nx - 1.0f, ny)-m0 : -2;
					float m2 = y > 0 ? width45*bump.evaluate(nx, ny - 1.0f)-m0 : -2;
					float m3 = x < width-1 ? width45*bump.evaluate(nx + 1.0f, ny)-m0 : -2;
					float m4 = y < height-1 ? width45*bump.evaluate(nx, ny + 1.0f)-m0 : -2;
					
					if (m1 != -2 && m4 != -2) {
						v1.x = -1.0f; v1.y = 0.0f; v1.z = m1;
						v2.x = 0.0f; v2.y = 1.0f; v2.z = m4;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0)
							n.z = -n.z;
						normal.add(n);
						count++;
					}

					if (m1 != -2 && m2 != -2) {
						v1.x = -1.0f; v1.y = 0.0f; v1.z = m1;
						v2.x = 0.0f; v2.y = -1.0f; v2.z = m2;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0)
							n.z = -n.z;
						normal.add(n);
						count++;
					}

					if (m2 != -2 && m3 != -2) {
						v1.x = 0.0f; v1.y = -1.0f; v1.z = m2;
						v2.x = 1.0f; v2.y = 0.0f; v2.z = m3;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0)
							n.z = -n.z;
						normal.add(n);
						count++;
					}

					if (m3 != -2 && m4 != -2) {
						v1.x = 1.0f; v1.y = 0.0f; v1.z = m3;
						v2.x = 0.0f; v2.y = 1.0f; v2.z = m4;
						n.cross(v1, v2);
						n.normalize();
						if (n.z < 0.0)
							n.z = -n.z;
						normal.add(n);
						count++;
					}

					// Average the four normals
					normal.x /= count;
					normal.y /= count;
					normal.z /= count;
				} else {
					if (normalEvaluator != null)
						normalEvaluator.getNormalAt(x, y, width, height, normal);
				}
				if (invertBumps) {
					normal.x = -normal.x;
					normal.y = -normal.y;
				}
				position.x = x;

				if (normal.z >= 0) {
					// Get the material colour at this point
					if (colorSource == COLORS_FROM_IMAGE)
						setFromRGB(diffuseColor, inPixels[index]);
					else
						setFromRGB(diffuseColor, material.diffuseColor);
					if (reflectivity != 0 && environmentMap != null) {
						//FIXME-too much normalizing going on here
						tmpv2.set(viewpoint);
						tmpv2.z = 100.0f;//FIXME
						tmpv2.sub(position);
						tmpv2.normalize();
						tmpv.set(normal);
						tmpv.normalize();

						// Reflect
						tmpv.scale( 2.0f*tmpv.dot(tmpv2) );
						tmpv.sub(v);
						
						tmpv.normalize();
						setFromRGB(envColor, getEnvironmentMap(tmpv, inPixels, width, height));//FIXME-interpolate()
						diffuseColor.x = reflectivity*envColor.x + areflectivity*diffuseColor.x;
						diffuseColor.y = reflectivity*envColor.y + areflectivity*diffuseColor.y;
						diffuseColor.z = reflectivity*envColor.z + areflectivity*diffuseColor.z;
					}
					// Shade the pixel
					Color4f c = phongShade(position, viewpoint, normal, diffuseColor, specularColor, material, lightsArray);
					int alpha = inPixels[index] & 0xff000000;
					int rgb = ((int)(c.x * 255) << 16) | ((int)(c.y * 255) << 8) | (int)(c.z * 255);
					outPixels[index++] = alpha | rgb;
				} else
					outPixels[index++] = 0;
			}
		}
		return outPixels;
	}

	public Color4f phongShade(Vector3f position, Vector3f viewpoint, Vector3f normal, Color4f diffuseColor, Color4f specularColor, Material material, Light[] lightsArray) {
		shadedColor.set(diffuseColor);
		shadedColor.scale(material.ambientIntensity);

		for (int i = 0; i < lightsArray.length; i++) {
			Light light = lightsArray[i];
			n.set(normal);
			l.set(light.position);
			if (light.type != DISTANT)
				l.sub(position);
			l.normalize();
			float nDotL = n.dot(l);
			if (nDotL >= 0.0) {
				float dDotL = 0;
				
				v.set(viewpoint);
				v.sub(position);
				v.normalize();

				// Spotlight
				if (light.type == SPOT) {
					dDotL = light.direction.dot(l);
					if (dDotL < light.cosConeAngle)
						continue;
				}

				n.scale(2.0f * nDotL);
				n.sub(l);
				float rDotV = n.dot(v);

				float rv;
				if (rDotV < 0.0)
					rv = 0.0f;
				else
					rv = (float)Math.pow(rDotV, material.highlight);

				// Spotlight
				if (light.type == SPOT) {
					dDotL = light.cosConeAngle/dDotL;
					float e = dDotL;
					e *= e;
					e *= e;
					e *= e;
					e = (float)Math.pow(dDotL, light.focus*10)*(1 - e);
					rv *= e;
					nDotL *= e;
				}
				
				diffuse_color.set(diffuseColor);
				diffuse_color.scale(material.diffuseReflectivity);
				diffuse_color.x *= light.realColor.x * nDotL;
				diffuse_color.y *= light.realColor.y * nDotL;
				diffuse_color.z *= light.realColor.z * nDotL;
				specular_color.set(specularColor);
				specular_color.scale(material.specularReflectivity);
				specular_color.x *= light.realColor.x * rv;
				specular_color.y *= light.realColor.y * rv;
				specular_color.z *= light.realColor.z * rv;
				diffuse_color.add(specular_color);
				diffuse_color.clamp( 0, 1 );
				shadedColor.add(diffuse_color);
			}
		}
		shadedColor.clamp( 0, 1 );
		return shadedColor;
	}

	private int[] rgb = new int[4];

	private int getEnvironmentMap(Vector3f normal, int[] inPixels, int width, int height) {
		if (environmentMap != null) {
			float angle = (float)Math.acos(-normal.y);

			float x, y;
			y = angle/ImageMath.PI;

			if (y == 0.0f || y == 1.0f)
				x = 0.0f;
			else {
				float f = normal.x/(float)Math.sin(angle);

				if (f > 1.0f)
					f = 1.0f;
				else if (f < -1.0f) 
					f = -1.0f;

				x = (float)Math.acos(f)/ImageMath.PI;
			}
			// A bit of empirical scaling....
//			x = (x-0.5f)*1.2f+0.5f;
//			y = (y-0.5f)*1.2f+0.5f;
			x = ImageMath.clamp(x * envWidth, 0, envWidth-1);
			y = ImageMath.clamp(y * envHeight, 0, envHeight-1);
			int ix = (int)x;
			int iy = (int)y;

			float xWeight = x-ix;
			float yWeight = y-iy;
			int i = envWidth*iy + ix;
			int dx = ix == envWidth-1 ? 0 : 1;
			int dy = iy == envHeight-1 ? 0 : envWidth;
			rgb[0] = envPixels[i];
			rgb[1] = envPixels[i+dx];
			rgb[2] = envPixels[i+dy];
			rgb[3] = envPixels[i+dx+dy];
			return ImageMath.bilinearInterpolate(xWeight, yWeight, rgb);
		}
		return 0;
	}

	public class NormalEvaluator {
		public final static int RECTANGLE = 0;
		public final static int ROUNDRECT = 1;
		public final static int ELLIPSE = 2;
		
		public final static int LINEAR = 0;
		public final static int SIN = 1;
		public final static int CIRCLE_UP = 2;
		public final static int CIRCLE_DOWN = 3;
		public final static int SMOOTH = 4;
		public final static int PULSE = 5;
		public final static int SMOOTH_PULSE = 6;
		public final static int THING = 7;

		private int margin = 10;
		private int shape = RECTANGLE;
		private int bevel = LINEAR;
		private int cornerRadius = 15;

		public void setMargin(int margin) {
			this.margin = margin;
		}

		public int getMargin() {
			return margin;
		}

		public void setCornerRadius(int cornerRadius) {
			this.cornerRadius = cornerRadius;
		}

		public int getCornerRadius() {
			return cornerRadius;
		}

		public void setShape(int shape) {
			this.shape = shape;
		}

		public int getShape() {
			return shape;
		}

		public void setBevel(int bevel) {
			this.bevel = bevel;
		}

		public int getBevel() {
			return bevel;
		}

		public  void getNormalAt(int x, int y, int width, int height, Vector3f normal) {
			float distance = 0;
			normal.x = normal.y = 0;
			normal.z = 0.707f;
			switch (shape) {
			case RECTANGLE:
				if (x < margin) {
					if (x < y && x < height-y)
						normal.x = -1;
				} else if (width-x <= margin) {
					if (width-x-1 < y && width-x <= height-y)
						normal.x = 1;
				}
				if (normal.x == 0) {
					if (y < margin) {
						normal.y = -1;
					} else if (height-y <= margin)
						normal.y = 1;
				}
				distance = Math.min(Math.min(x, y), Math.min(width-x-1, height-y-1));
				break;
			case ELLIPSE:
				float a = width/2;
				float b = height/2;
				float a2 = a*a;
				float b2 = b*b;
				float dx = x-a;
				float dy = y-b;
				float x2 = dx*dx;
				float y2 = dy*dy;
				distance = (b2 - (b2*x2)/a2) - y2;
				float radius = (float)Math.sqrt(x2+y2);
				distance = 0.5f*distance/((a+b)/2);//FIXME
				if (radius != 0) {
					normal.x = dx/radius;
					normal.y = dy/radius;
				}
				break;
			case ROUNDRECT:
				distance = Math.min(Math.min(x, y), Math.min(width-x-1, height-y-1));
				float c = Math.min(cornerRadius, Math.min(width/2, height/2));
				if ((x < c || width-x <= c) && (y < c || height-y <= c)) {
					if (width-x <= c)
						x -= width-c-c-1;
					if (height-y <= c)
						y -= height-c-c-1;
					dx = x-c;
					dy = y-c;
					x2 = dx*dx;
					y2 = dy*dy;
					radius = (float)Math.sqrt(x2+y2);
					distance = c-radius;
					normal.x = dx/radius;
					normal.y = dy/radius;
				} else if (x < margin) {
					normal.x = -1;
				} else if (width-x <= margin) {
					normal.x = 1;
				} else if (y < margin) {
					normal.y = -1;
				} else if (height-y <= margin)
					normal.y = 1;
				break;
			}
			distance /= margin;
if (distance < 0) {
	normal.z = -1;
	normal.normalize();
	return;
}

			float dx = 1.0f/margin;
			float z1 = bevelFunction(distance);
			float z2 = bevelFunction(distance+dx);
			float dz = z2-z1;
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
			switch (bevel) {
			case LINEAR:
				return ImageMath.clamp(x, 0.0f, 1.0f);
			case SIN:
				return (float)Math.sin(x*Math.PI/2);
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
				return (float)(x < 0.2 ? Math.sin(x/0.2*Math.PI/2) : 0.5+0.5*Math.sin(1+x/0.6*Math.PI/2));
			}
			return x;
		}
	}
	
	public String toString() {
		return "Stylize/Light Effects...";
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
			ambientIntensity = 0.5f;
			diffuseReflectivity = 1.0f;
			specularReflectivity = 1.0f;
			highlight = 3.0f;
			reflectivity = 0.0f;
			diffuseColor = 0xff888888;
			specularColor = 0xffffffff;
		}

		public void setDiffuseColor(int diffuseColor) {
			this.diffuseColor = diffuseColor;
		}

		public int getDiffuseColor() {
			return diffuseColor;
		}

	}

	public final static int AMBIENT = 0;
	public final static int DISTANT = 1;
	public final static int POINT = 2;
	public final static int SPOT = 3;

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
		float centreX = 0.5f, centreY = 0.5f;
		float coneAngle = ImageMath.PI/6;
		float cosConeAngle;
		float distance = 100.0f;

		public Light() {
			this(135*ImageMath.PI/180.0f, 0.5235987755982988f, 1.0f);
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
			return azimuth;
		}

		public void setElevation(float elevation) {
			this.elevation = elevation;
		}

		public float getElevation() {
			return elevation;
		}

		public void setDistance(float distance) {
			this.distance = distance;
		}

		public float getDistance() {
			return distance;
		}

		public void setIntensity(float intensity) {
			this.intensity = intensity;
		}

		public float getIntensity() {
			return intensity;
		}

		public void setConeAngle(float coneAngle) {
			this.coneAngle = coneAngle;
		}

		public float getConeAngle() {
			return coneAngle;
		}

		public void setFocus(float focus) {
			this.focus = focus;
		}

		public float getFocus() {
			return focus;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getColor() {
			return color;
		}

		public void setCentreX(float x) {
			centreX = x;
		}
		
		public float getCentreX() {
			return centreX;
		}

		public void setCentreY(float y) {
			centreY = y;
		}
		
		public float getCentreY() {
			return centreY;
		}

		public void prepare(int width, int height) {
			float lx = (float)(Math.cos(azimuth) * Math.cos(elevation));
			float ly = (float)(Math.sin(azimuth) * Math.cos(elevation));
			float lz = (float)Math.sin(elevation);
			direction = new Vector3f(lx, ly, lz);
			direction.normalize();
			if (type != DISTANT) {
				lx *= distance;
				ly *= distance;
				lz *= distance;
				lx += width * centreX;
				ly += height * (1-centreY);
			}
			position = new Vector3f(lx, ly, lz);
			realColor.set( new Color(color) );
			realColor.scale(intensity);
			cosConeAngle = (float)Math.cos(coneAngle);
		}
		
		public Object clone() {
			try {
				Light copy = (Light)super.clone();
				return copy;
			}
			catch (CloneNotSupportedException e) {
				return null;
			}
		}

		public String toString() {
			return "Light";
		}

	}

	public class AmbientLight extends Light {
		public String toString() {
			return "Ambient Light";
		}
	}

	public class PointLight extends Light {
		public PointLight() {
			type = POINT;
		}

		public String toString() {
			return "Point Light";
		}
	}

	public class DistantLight extends Light {
		public DistantLight() {
			type = DISTANT;
		}

		public String toString() {
			return "Distant Light";
		}
	}

	public class SpotLight extends Light {
		public SpotLight() {
			type = SPOT;
		}

		public String toString() {
			return "Spotlight";
		}
	}
}
