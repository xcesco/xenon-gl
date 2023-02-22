package com.abubusoft.xenon.android

import android.content.Context
import com.abubusoft.xenon.Xenon4OpenGL
import com.abubusoft.xenon.android.listener.XenonGestureDetector
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer
import com.abubusoft.xenon.opengl.XenonGLRenderer

/**
 * View opengl
 *
 * @author Francesco Benincasa
 */
@Uncryptable
class XenonView4OpenGL(context: Context) : ArgonGLSurfaceView16(context) {
    protected var argon: Xenon4OpenGL? = null

    /**
     * Avvia il contesto xenon
     */
    fun startArgonContext() {
        try {
            // crea l'applicazione
            argon = XenonBeanContext.getBean<Any>(XenonBeanType.XENON) as Xenon4OpenGL
            //ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.MODE);
            argon!!.onViewCreated(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var gestureDetector: XenonGestureDetector? = null

    /**
     * Consente di effettuare il build di un renderer
     *
     * @return istanza di render da usare
     */
    fun createRenderer(): XenonGLRenderer {
        return XenonGLDefaultRenderer()
    }
}