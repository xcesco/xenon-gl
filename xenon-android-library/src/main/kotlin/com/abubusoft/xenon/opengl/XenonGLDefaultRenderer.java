package com.abubusoft.xenon.opengl;

import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.android.XenonService4OpenGL;
import com.abubusoft.xenon.engine.Phase;
import com.abubusoft.xenon.engine.TouchManager;
import com.abubusoft.xenon.misc.FPSCounter;
import com.abubusoft.kripton.android.Logger;

import android.opengl.GLES20;

public class XenonGLDefaultRenderer implements XenonGLRenderer {
	
	private Xenon4OpenGL argon;
	private boolean safeMode;
	private float speedAdapter;

	public XenonGLDefaultRenderer()
	{
		argon = XenonBeanContext.getBean(XenonBeanType.XENON);
	}

	@Override
	public void onSurfaceCreated() {
		Logger.info("> onSurfaceCreated");
		argon.onSurfaceCreated();		
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		Logger.info("> onSurfaceChanged");
		argon.onSurfaceChanged(width, height);
		
		argon.onSceneCreation();
	}

	@Override
	public void onDrawFrame() {
		// iniziamo il disegno della scena
		argon.onDrawFrameBegin();
		speedAdapter = 1.0f;
		// in caso di blocco, facciamo finta che sia passato massimo 500 ms
		if (FPSCounter.enlapsedTime > 1000) {
			FPSCounter.enlapsedTime = 500;
		}

		// per essere sicuri che non siamo mai negativi
		if (FPSCounter.enlapsedTime <= 0) {
			FPSCounter.enlapsedTime = 100;
		}

		speedAdapter = FPSCounter.enlapsedTime * 0.001f;

		try {
			// process input
			TouchManager.instance().processMessages();

			//if (viewStatus.isSurfaceReady()) {
				/*
				if (viewStatus.isSurfaceFirstDraw()) {
					xenon.onSceneCreation();
					GLES20.glFlush();
					viewStatus.onSurfaceFirstDrawDone();
				}*/				
			
				// process logic
				argon.onFramePrepare(Phase.LOGIC, FPSCounter.enlapsedTime, speedAdapter);

				// disegnamola
				argon.onFrameDraw(Phase.RENDER, FPSCounter.enlapsedTime, speedAdapter);
				GLES20.glFlush();
			/*} else {
				Logger.info(" **** onFrameDraw - SURFACE NOT READY ");
			}*/

		} catch (Exception e) {
			Logger.error(e.getMessage());
			e.printStackTrace();

			// se siamo in safeMode trappiamo le eccezioni
			if (!safeMode) {
				throw (new RuntimeException(e));
			}
		}

		// termine del disegno della scena
		argon.onDrawFrameEnd();
		
	}

	@Override
	public void onPause() {
		argon.onPause(null);		
	}

	@Override
	public void onResume() {
		argon.onResume(null);
	}

	@Override
	public double getFrameRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFrameRate(double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSafeMode(boolean value) {
		safeMode=value;		
	}

	@Override
	public void setService(XenonService4OpenGL service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preserveEGLContextOnPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
	}


}