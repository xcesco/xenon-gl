package com.abubusoft.xenon

import android.app.Activity
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.android.surfaceview.ConfigOptions.ClientVersionType
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.engine.Phase

abstract class XenonApplication4OpenGLImpl : XenonApplicationImpl(), XenonApplication4OpenGL {
    override fun onConfigReset() {
        Logger.debug("onConfigReset - default")
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.XenonApplication4OpenGL#chooseArgonGLConfig()
	 */
    override fun chooseArgonGLConfig(): ConfigOptions {
        return ConfigOptions.build().clientVersion(ClientVersionType.OPENGL_ES_2)
    }

    override fun onStartup() {
        Logger.debug("init - default")
    }

    override fun onSceneRestore(firstSceneCreation: Boolean, preferencesIsChanged: Boolean, screenIsChanged: Boolean) {
        Logger.debug("onSceneRestore - default")
    }

    override fun onPause(currentActivity: Activity?) {
        Logger.debug("onPause - default")
    }

    override fun onResume(currentActivity: Activity?) {
        Logger.debug("onResume - default")
    }

    override fun onFramePrepare(phase: Phase?, enlapsedTime: Long, speedAdapter: Float) {}

    /**
     *
     * Camera di default.
     */
    var camera: Camera? = null

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.XenonApplication4OpenGL#setDefaultCamera(com.abubusoft.xenon.camera.Camera)
	 */
    override fun setDefaultCamera(camera: Camera) {
        this.camera = camera
    }

    override fun onWindowCreate() {}
    override fun onSceneReady(firstSceneCreation: Boolean, preferencesIsChanged: Boolean, screenIsChanged: Boolean) {}
}