/**
 *
 */
package com.abubusoft.xenon.camera

import android.opengl.GLES20
import android.opengl.Matrix
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.settings.ProjectionType
import com.abubusoft.xenon.settings.ViewFrustumSettings

/**
 *
 *
 * Gestore delle camere. Gestisce sia la camera principale che tutte le altre. L'unica obbligatoria è quella principale.
 *
 *
 * @author Francesco Benincasa
 */
object CameraManager {
    private lateinit var settings: ViewFrustumSettings

    /**
     *
     *
     * E' la camera di default. Ci deve essere sempre.
     *
     */
    lateinit var camera: Camera

    /**
     *
     *
     * Registra le impostazioni iniziali, quelle recuperate dal file di configurazione xml.
     *
     *
     * @param viewFrustum
     */
    @JvmStatic
    fun init(viewFrustum: ViewFrustumSettings) {
        // copiamo i valori di default.
        settings = viewFrustum
        camera = createCamera()
    }

    /**
     * imposta la perspective
     *
     * @param fovy
     * @param aspect
     * @param zNear
     * @param zFar
     */
    @JvmStatic
    fun perspective(newCamera: Camera?, landscapeMode: Boolean, fovy: Float, aspect: Float) {
        val xmin: Float
        val xmax: Float
        val ymin: Float
        val ymax: Float
        ymax = (newCamera!!.info.zNear * Math.tan(fovy * XenonMath.DEGREES_TO_RADIANS_FACTOR * 0.5)).toFloat()
        ymin = -ymax
        xmin = ymin
        xmax = ymax

        // impostiamo qui cameraInfo.projectionMatrix

        // Matrix.perspectiveM(cameraInfo.projectionMatrix, 0, fovy, aspect,
        // zNear, zFar);
        if (!landscapeMode) {
            Matrix.frustumM(newCamera.info.projectionMatrix.get(), 0, xmin * aspect, xmax * aspect, ymin, ymax, newCamera.info.zNear, newCamera.info.zFar)
        } else {
            Matrix.frustumM(newCamera.info.projectionMatrix.get(), 0, xmin, xmax, ymin / aspect, ymax / aspect, newCamera.info.zNear, newCamera.info.zFar)
        }
    }

    /**
     *
     *
     * Crea una telecamera copiando i valori di default.
     *
     *
     * @return
     */
    @JvmStatic
    fun createCamera(): Camera {
        val newCamera = Camera(this)
        // frustum
        newCamera.info.fieldOfView = settings.fieldOfView
        newCamera.info.zNear = settings.zNear
        newCamera.info.zFar = settings.zFar
        newCamera.info.zInverseFrustmDepthFactor = 1f / (settings.zFar - settings.zNear)
        newCamera.info.frustumSize = settings.size
        newCamera.info.projection = settings.projection
        return newCamera
    }

    @JvmStatic
    fun createCamera(viewportWidth: Int, viewportHeight: Int): Camera {
        val newCamera = createCamera()
        setupCamera(newCamera, viewportWidth, viewportHeight)
        return newCamera
    }

    /**
     *
     *
     * Cambia screenInfo e le impostazioni sulla camera di default.
     *
     *
     * @param screenWidth
     * @param screenHeight
     */
    @JvmStatic
    fun onSurfaceChanged(screenWidth: Int, screenHeight: Int): Camera {

        // imposta il viewport
        GLES20.glViewport(0, 0, screenWidth, screenHeight)
        setupCamera(camera, screenWidth, screenHeight)

        // restituisce camera di default
        return camera
    }

    /**
     *
     *
     *
     *
     * @param newCamera
     * @param screenWidth
     * @param screenHeight
     */
    @JvmStatic
    fun setupCamera(newCamera: Camera?, screenWidth: Int, screenHeight: Int) {
        val aspectRatio = screenWidth.toFloat() / screenHeight
        val landscapeMode = screenWidth > screenHeight
        val correctionX: Float
        val correctionY: Float
        // recuperiamo il tipo di proiezione
        val type = newCamera!!.info.projection
        if (landscapeMode) {
            // width > height
            correctionX = aspectRatio
            correctionY = 1.0f
        } else {
            // width < height
            correctionX = 1.0f
            correctionY = 1.0f / aspectRatio
        }
        if (type == ProjectionType.ORTHOGONAL) {
            val size = newCamera.info.frustumSize / 2.0f
            if (landscapeMode) {
                Matrix.orthoM(newCamera.info.projectionMatrix.get(), 0, -size * correctionX, size * correctionX, -size, size, newCamera.info.zNear, newCamera.info.zFar)
            } else {
                Matrix.orthoM(newCamera.info.projectionMatrix.get(), 0, -size, size, -size * correctionY, size * correctionY, newCamera.info.zNear, newCamera.info.zFar)
            }
        } else {
            val fieldOfView = newCamera.info.fieldOfView
            perspective(newCamera, landscapeMode, fieldOfView, aspectRatio)
        }
        newCamera.info.setViewport(screenWidth, screenHeight)
        newCamera.info.cameraMatrix.buildIdentityMatrix()
        // precalcoliamo moltiplicazione projection startX camera
        newCamera.info.projection4CameraMatrix.multiply(newCamera.info.projectionMatrix, newCamera.info.cameraMatrix)
    }
}