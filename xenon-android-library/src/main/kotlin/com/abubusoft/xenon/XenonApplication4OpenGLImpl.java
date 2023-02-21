package com.abubusoft.xenon;

import com.abubusoft.xenon.android.surfaceview.ConfigOptions;
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.ClientVersionType;
import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.engine.Phase;

import com.abubusoft.kripton.android.Logger;

import android.app.Activity;
import android.content.SharedPreferences;


public abstract class XenonApplication4OpenGLImpl extends XenonApplicationImpl<Xenon4OpenGL> implements XenonApplication4OpenGL {
	
	@Override
	public void onConfigReset() {
		Logger.debug("onConfigReset - default");
	}

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.XenonApplication4OpenGL#chooseArgonGLConfig()
	 */
	@Override
	public ConfigOptions chooseArgonGLConfig() {
		return ConfigOptions.build().clientVersion(ClientVersionType.OPENGL_ES_2);
	}

	@Override
	public void onStartup() {
		Logger.debug("init - default");
	}
	
	@Override
	public void onSceneRestore(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {
		Logger.debug("onSceneRestore - default");
	}

	@Override
	public void onPause(Activity currentActivity) {
		Logger.debug("onPause - default");
		
	}

	@Override
	public void onResume(Activity currentActivity) {
		Logger.debug("onResume - default");
		
	}

	@Override
	public void onFramePrepare(Phase phase, long enlapsedTime, float speedAdapter) {
	}

	/**
	 * <p>Camera di default.</p>
	 */
	public Camera camera;

	/* (non-Javadoc)
	 * @see com.abubusoft.xenon.XenonApplication4OpenGL#setDefaultCamera(com.abubusoft.xenon.camera.Camera)
	 */
	@Override
	public void setDefaultCamera(Camera camera) {
		this.camera=camera;
	}
	
	@Override
	public void onWindowCreate() {

	}

	@Override
	public void onSceneReady(boolean firstSceneCreation, boolean preferencesIsChanged, boolean screenIsChanged) {

	}

}
