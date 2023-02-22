package com.abubusoft.xenon.android.views

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.abubusoft.kripton.android.Logger


/**
 * Credit to @iutinvg for the simple Android compass implementation
 * https://github.com/iutinvg/compass
 */
class XenonGLView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    init {
        Logger.debug("Kotlin init block called.")
        Logger.debug( "inflation started.")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Logger.debug( "onFinishInflate() called.")
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        TODO("Not yet implemented")
    }
}