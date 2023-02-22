package com.abubusoft.xenon.camera

import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.settings.ProjectionType

/**
 * Informazione relativi alla camera e alle informazioni ad essa associate.
 *
 * @author Francesco Benincasa
 */
class CameraInfo {
    /**
     * angolo di visualizzaizione espresso in gradi.
     */
    var fieldOfView = 0f

    /**
     * distanza del piano più vicino rispetto alla camera del frustum
     */
    var zNear = 0f

    /**
     * distanza del piano più lontano del frustum rispetto alla camera
     */
    var zFar = 0f

    /**
     * tipo di proiezione
     */
    var projection: ProjectionType? = null

    /**
     * dimensione della camera (valido solo nella proiezione ORTOGONALE).
     */
    var frustumSize = 0f

    /**
     *
     *
     * viewport sottoforma di matrice.
     *
     *
     */
    var viewport = IntArray(16)

    /**
     * matrice di trasformazione modelView
     */
    var cameraMatrix = Matrix4x4()

    /**
     * matrice di proiezione
     */
    var projectionMatrix = Matrix4x4()

    /**
     *
     *
     * Matrice di moltiplicazione projectionMatrix startX cameraMatrix
     *
     *
     *
     *
     * Comoda per evitare ad ogni frame la moltiplicazione delle matrici
     *
     */
    var projection4CameraMatrix = Matrix4x4()

    /**
     *
     * Rappresenta 1f/ (zFar - zNear)
     */
    var zInverseFrustmDepthFactor = 0f

    /**
     * Imposta il viewport in termini di width e height, usato
     *
     * @param width
     * @param height
     */
    fun setViewport(width: Int, height: Int) {
        viewport[2] = width
        viewport[3] = height
    }
}