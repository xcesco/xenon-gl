package com.abubusoft.xenon.opengl

import com.abubusoft.xenon.android.surfaceview.ConfigOptions
import com.abubusoft.xenon.android.surfaceview16.ArgonConfigChooser16
import com.abubusoft.xenon.android.surfaceview16.SmartConfigChooser

object XenonGLConfigChooser {
    lateinit var options: ConfigOptions

    var configChooser: ArgonConfigChooser16?=null

    fun build(): ArgonConfigChooser16 {
        if (configChooser == null) {
            configChooser = SmartConfigChooser(options)
        }
        return configChooser!!
    }
}