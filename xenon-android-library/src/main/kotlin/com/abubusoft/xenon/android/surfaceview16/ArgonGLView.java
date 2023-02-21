package com.abubusoft.xenon.android.surfaceview16;

import com.abubusoft.xenon.opengl.XenonGLRenderer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

public abstract class ArgonGLView extends SurfaceView {

	public ArgonGLView(Context context) {
		super(context);
	}
	
	public ArgonGLView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public abstract void onPause();
	
	public abstract void onResume();

	public abstract void setRenderer(XenonGLRenderer renderer);

	public abstract void setDebugFlags(int flags);

	public abstract void setEGLContextClientVersion(int version);

	public abstract void setPreserveEGLContextOnPause(boolean value);

	protected XenonGLRenderer mRenderer;
		
	public XenonGLRenderer getRenderer() {
		return mRenderer;
	}
		
}

