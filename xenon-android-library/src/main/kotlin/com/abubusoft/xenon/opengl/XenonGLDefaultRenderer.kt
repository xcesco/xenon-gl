package com.abubusoft.xenon.opengl

import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.Xenon4OpenGL
import com.abubusoft.xenon.android.XenonService4OpenGL
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.engine.Phase
import com.abubusoft.xenon.engine.TouchManager
import com.abubusoft.xenon.misc.FPSCounter

class XenonGLDefaultRenderer : XenonGLRenderer {
    private val argon: Xenon4OpenGL?
    private var safeMode = false
    private var speedAdapter = 0f

    init {
        argon = getBean<Xenon4OpenGL>(XenonBeanType.XENON)
    }

    override fun onSurfaceCreated() {
        Logger.info("> onSurfaceCreated")
        argon!!.onSurfaceCreated()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        Logger.info("> onSurfaceChanged")
        argon!!.onSurfaceChanged(width, height)
        argon.onSceneCreation()
    }

    override fun onDrawFrame() {
        // iniziamo il disegno della scena
        argon!!.onDrawFrameBegin()
        speedAdapter = 1.0f
        // in caso di blocco, facciamo finta che sia passato massimo 500 ms
        if (FPSCounter.enlapsedTime > 1000) {
            FPSCounter.enlapsedTime = 500
        }

        // per essere sicuri che non siamo mai negativi
        if (FPSCounter.enlapsedTime <= 0) {
            FPSCounter.enlapsedTime = 100
        }
        speedAdapter = FPSCounter.enlapsedTime * 0.001f
        try {
            // process input
            TouchManager.instance().processMessages()

            //if (viewStatus.isSurfaceReady()) {
            /*
				if (viewStatus.isSurfaceFirstDraw()) {
					xenon.onSceneCreation();
					GLES20.glFlush();
					viewStatus.onSurfaceFirstDrawDone();
				}*/

            // process logic
            argon.onFramePrepare(Phase.LOGIC, FPSCounter.enlapsedTime, speedAdapter)

            // disegnamola
            argon.onFrameDraw(Phase.RENDER, FPSCounter.enlapsedTime, speedAdapter)
            GLES20.glFlush()
            /*} else {
				Logger.info(" **** onFrameDraw - SURFACE NOT READY ");
			}*/
        } catch (e: Exception) {
            Logger.error(e.message)
            e.printStackTrace()

            // se siamo in safeMode trappiamo le eccezioni
            if (!safeMode) {
                throw RuntimeException(e)
            }
        }

        // termine del disegno della scena
        argon.onDrawFrameEnd()
    }

    override fun onPause() {
        argon!!.onPause(null)
    }

    override fun onResume() {
        argon!!.onResume(null)
    }

    // TODO Auto-generated method stub
    // TODO Auto-generated method stub
    override var frameRate: Double
        get() =// TODO Auto-generated method stub
            0
        set(value) {
            // TODO Auto-generated method stub
        }

    override fun setSafeMode(value: Boolean) {
        safeMode = value
    }

    override fun setService(service: XenonService4OpenGL?) {
        // TODO Auto-generated method stub
    }

    override fun preserveEGLContextOnPause() {
        // TODO Auto-generated method stub
    }

    override fun onDestroy() {
        // TODO Auto-generated method stub
    }
}