package com.abubusoft.xenon.android.surfaceview16

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceView
import com.abubusoft.xenon.opengl.XenonGLRenderer

abstract class ArgonGLView : SurfaceView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    abstract fun onPause()
    abstract fun onResume()
    abstract fun setRenderer(renderer: XenonGLRenderer)
    abstract fun setDebugFlags(flags: Int)
    abstract fun setEGLContextClientVersion(version: Int)
    abstract fun setPreserveEGLContextOnPause(value: Boolean)

    var renderer: XenonGLRenderer? = null
        protected set
}