package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType

@BindType("settings")
class XenonSettings {
    @Bind
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var version = "1.0.0"

    @Bind
    var application = ApplicationSettings()

    @Bind
    var viewFrustum = ViewFrustumSettings()

    @Bind
    var openGL = OpenGLSettings()

    @Bind
    var logger = LoggerSettings()

    init {
        application = ApplicationSettings()
    }

    companion object {
        /**
         * Impostazioni standard per una grafica did tipo 2d (simulata)
         */
        fun configAsWorld2D(): XenonSettings {
            val instance = XenonSettings()

            // indica se disegnare in safe mode
            instance.openGL.safeMode = true
            instance.viewFrustum.projection = ProjectionType.ORTHOGONAL
            instance.viewFrustum.align = ViewFrustumAlignType.HEIGHT_ALIGN

            // vale solo per il 2D
            instance.viewFrustum.size = 1000.0f

            // non serve
            instance.viewFrustum.fieldOfView = 0f
            instance.viewFrustum.zNear = -10.0f
            instance.viewFrustum.zFar = 10.0f
            instance.openGL.version = 1
            instance.openGL.debug = false
            instance.openGL.maxFPS = 30

            // impostiamo i listener di default
            instance.application.gestureListenerClazz = null
            return instance
        }

        /**
         * @return
         */
        fun configAsWorld3D(): XenonSettings {
            val instance = XenonSettings()

            // indica se disegnare in safe mode
            instance.openGL.safeMode = true

            // parametri non devono essere resettati
            instance.application.resetConfig = false
            instance.viewFrustum.projection = ProjectionType.PERSPECTIVE
            instance.viewFrustum.align = ViewFrustumAlignType.HEIGHT_ALIGN

            // vale solo per il 2D
            instance.viewFrustum.size = 0.0f

            // non serve
            instance.viewFrustum.fieldOfView = 60f
            instance.viewFrustum.zNear = 10f
            instance.viewFrustum.zFar = 3000f
            instance.openGL.version = 1
            instance.openGL.debug = false
            instance.openGL.maxFPS = 30

            // impostiamo i listener di default
            instance.application.gestureListenerClazz = null
            return instance
        }
    }
}