/*******************************************************************************
 * Copyright 2012 bmanuel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.bitfire.postprocessing.effects;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.bitfire.postprocessing.PostProcessorEffect;
import com.bitfire.postprocessing.filters.RadialBlur;
import com.bitfire.postprocessing.filters.RadialBlur.Quality;
import com.bitfire.postprocessing.filters.Zoom;

/** Implements a zooming effect: either a radial blur filter or a zoom filter is used. */
public final class Zoomer extends PostProcessorEffect<Zoomer.Settings> {
	public static class Settings implements EffectSettings {
		public float originX, originY;
		public float blurStrength = 3.4f;
		public float zoom;
		
		public int initViewportWidth;
		public int initViewportHeight;
		public RadialBlur.Quality initQuality;
		
		public Settings( int initViewportWidth, int initViewportHeight, Quality initQuality ) {
			this.initViewportWidth = initViewportWidth;
			this.initViewportHeight = initViewportHeight;
			this.initQuality = initQuality;
		}
		
		public Settings() {}
	}
	
	private boolean doRadial = false;
	private RadialBlur radialBlur = null;
	private Zoom zoom = null;
	private float oneOnW, oneOnH;
	private float userOriginX, userOriginY;

	/** Creating a Zoomer specifying the radial blur quality will enable radial blur */
	public Zoomer( int viewportWidth, int viewportHeight, RadialBlur.Quality quality ) {
		this( new Settings( viewportWidth, viewportHeight, quality ) );
	}

	/** Creating a Zoomer without any parameter will use plain simple zooming */
	public Zoomer( int viewportWidth, int viewportHeight ) {
		this( new Settings( viewportWidth, viewportHeight, null ));
	}
	
	public Zoomer( Settings settings ) {
		super( settings );
		setup( settings.initViewportWidth, settings.initViewportWidth, settings.initQuality );
	}

	private void setup( int viewportWidth, int viewportHeight, Quality radialQuality ) {
		if (radialQuality != null) {
			radialBlur = new RadialBlur( radialQuality );
		}
		
		if( radialBlur != null ) {
			doRadial = true;
			zoom = null;
		} else {
			doRadial = false;
			zoom = new Zoom();
		}

		oneOnW = 1f / (float)viewportWidth;
		oneOnH = 1f / (float)viewportHeight;
	}

	/** Specify the zoom origin, in screen coordinates. */
	public void setOrigin( Vector2 o ) {
		setOrigin( o.x, o.y );
	}

	/** Specify the zoom origin, in screen coordinates. */
	public void setOrigin( float x, float y ) {
		userOriginX = x;
		userOriginY = y;

		if( doRadial ) {
			radialBlur.setOrigin( x * oneOnW, 1f - y * oneOnH );
		} else {
			zoom.setOrigin( x * oneOnW, 1f - y * oneOnH );
		}
	}

	public void setBlurStrength( float strength ) {
		if( doRadial ) {
			radialBlur.setStrength( strength );
		}
	}

	public void setZoom( float zoom ) {
		if( doRadial ) {
			radialBlur.setZoom( 1f / zoom );
		} else {
			this.zoom.setZoom( 1f / zoom );
		}
	}

	public float getZoom() {
		if( doRadial ) {
			return 1f / radialBlur.getZoom();
		} else {
			return 1f / zoom.getZoom();
		}
	}

	public float getBlurStrength() {
		if( doRadial ) {
			return radialBlur.getStrength();
		}

		return -1;
	}

	public float getOriginX() {
		return userOriginX;
	}

	public float getOriginY() {
		return userOriginY;
	}

	@Override
	public void dispose() {
		if( radialBlur != null ) {
			radialBlur.dispose();
			radialBlur = null;
		}

		if( zoom != null ) {
			zoom.dispose();
			zoom = null;
		}
	}

	@Override
	public void rebind() {
		radialBlur.rebind();
	}

	@Override
	public void render( FrameBuffer src, FrameBuffer dest ) {
		restoreViewport( dest );
		if( doRadial ) {
			radialBlur.setInput( src ).setOutput( dest ).render();
		} else {
			zoom.setInput( src ).setOutput( dest ).render();
		}
	}

	@Override
	public void refreshSettings() {
		setOrigin(settings.originX, settings.originY);
		setZoom(settings.zoom);
	}
}
