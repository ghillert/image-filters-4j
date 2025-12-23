/*
** Copyright 2005 Huxtable.com. All rights reserved.
*/

package com.jhlabs.image;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class FeedbackFilter extends AbstractBufferedImageOp {
    private float centreX = 0.5f, centreY = 0.5f;
    private float distance;
    private float angle;
    private float rotation;
    private float zoom;
    private int iterations;

    public FeedbackFilter() {
	}
	
	public FeedbackFilter( float distance, float angle, float rotation, float zoom ) {
        this.distance = distance;
        this.angle = angle;
        this.rotation = rotation;
        this.zoom = zoom;
    }
    
	public void setAngle( float angle ) {
		this.angle = angle;
	}

	public float getAngle() {
		return angle;
	}
	
	public void setDistance( float distance ) {
		this.distance = distance;
	}

	public float getDistance() {
		return distance;
	}
	
	public void setRotation( float rotation ) {
		this.rotation = rotation;
	}

	public float getRotation() {
		return rotation;
	}
	
	public void setZoom( float zoom ) {
		this.zoom = zoom;
	}

	public float getZoom() {
		return zoom;
	}
	
	public void setCentreX( float centreX ) {
		this.centreX = centreX;
	}

	public float getCentreX() {
		return centreX;
	}
	
	public void setCentreY( float centreY ) {
		this.centreY = centreY;
	}

	public float getCentreY() {
		return centreY;
	}
	
	public void setCentre( Point2D centre ) {
		this.centreX = (float)centre.getX();
		this.centreY = (float)centre.getY();
	}

	public Point2D getCentre() {
		return new Point2D.Float( centreX, centreY );
	}
	
	public void setIterations( int iterations ) {
		this.iterations = iterations;
	}

	public int getIterations() {
		return iterations;
	}
	
    public BufferedImage filter( BufferedImage src, BufferedImage dst ) {
        if ( dst == null )
            dst = createCompatibleDestImage( src, null );
        float cx = (float)src.getWidth() * centreX;
        float cy = (float)src.getHeight() * centreY;
        float imageRadius = (float)Math.sqrt( cx*cx + cy*cy );
        float translateX = (float)(distance * Math.cos( angle ));
        float translateY = (float)(distance * -Math.sin( angle ));
        float scale = (float)Math.exp(zoom);
        float rotate = rotation;

        if ( iterations == 0 ) {
            Graphics2D g = dst.createGraphics();
            g.drawRenderedImage( src, null );
            g.dispose();
            return dst;
        }
        
		Graphics2D g = dst.createGraphics();
		g.drawImage( src, null, null );
//		g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha ) );
        for ( int i = 0; i < iterations; i++ ) {
			g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
			g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

            g.translate( cx+translateX, cy+translateY );
            g.scale( scale, scale );  // The .0001 works round a bug on Windows where drawImage throws an ArrayIndexOutofBoundException
            if ( rotation != 0 )
                g.rotate( rotate );
            g.translate( -cx, -cy );

            g.drawImage( src, null, null );
        }
		g.dispose();
        return dst;
    }
    
	public String toString() {
		return "Effects/Feedback...";
	}
}
