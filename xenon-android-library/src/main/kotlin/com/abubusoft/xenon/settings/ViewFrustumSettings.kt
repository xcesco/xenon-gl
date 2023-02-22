package com.abubusoft.xenon.settings

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.core.Uncryptable

/**
 *
 * Impostazioni del frustum.
 *
 *
 * In order to visualize a scene from different angles a virtual camera is often
 * used. The virtual camera setup, commonly done with gluPerspective and
 * gluLookAt functions, determines what is visible on screen.
 *
 *
 * The view frustum is the volume that contains everything that is potentially
 * (there may be occlusions) visible on the screen. This volume is defined
 * according to the camera’s settings, and when using a perspective projection
 * takes the shape of a truncated pyramid.
 *
 * <img src="doc-files/vf.gif"></img>
 *
 *
 *
 *
 * @author Francesco Benincasa
 */
@Uncryptable
@BindType
class ViewFrustumSettings {
    /**
     * Definisce quale lato della view la camera deve ricoprire: altezza o
     * larghezza.
     */
    @Bind("viewFrustumAlign")
    var align = ViewFrustumAlignType.HEIGHT_ALIGN

    /**
     * field of view
     */
    @Bind("viewFrustumFieldOfView")
    var fieldOfView = 30f

    @Bind("viewFrustumProjection")
    var projection = ProjectionType.PERSPECTIVE

    /**
     * distanza del piano più vicino rispetto alla camera del frustum
     */
    @Bind("viewFrustumZNear")
    var zNear = 1.0f

    /**
     * distanza del piano più lontano del frustum rispetto alla camera
     */
    @Bind("viewFrustumZFar")
    var zFar = 1000f

    /**
     * dimensione della camera. Utile solo nel caso di proiezione ortogonale
     */
    @Bind("viewFrustumSize")
    var size = 1000f
}