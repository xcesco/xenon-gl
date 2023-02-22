package com.abubusoft.xenon.camera

import android.opengl.Matrix
import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.math.XenonMath

/**
 * Rappresenta la camera da cui si vede il mondo.
 *
 * @author Francesco Benincasa
 */
class Camera internal constructor(cameraManager: CameraManager) {
    /**
     * informazioni sulla camere
     */
    val info: CameraInfo
    val lookAt: Point3
    val normal: Point3
    val position: Point3
    val rotation: Point3

    /**
     *
     * La possiamo creare solo da CameraManager.
     */
    init {
        position = Point3.set(0f, 0f, 0f)
        lookAt = Point3.set(0f, 0f, -1f)
        normal = Point3.set(0f, 1f, 0f)
        rotation = Point3.set(0f, 0f, 0f)
        info = CameraInfo()
    }

    /**
     * Muove la telecamera sul piano xy, senza modificare la normale e la
     * direzione della camera.
     *
     * Di default la posizione è:
     *
     * (0,0,0) position (0,0,-1) lookAt (0,1,0) normal
     *
     * @param offsetX
     * spostamento sull'asse startX
     * @param offsetY
     * spostamento sull'asse startY
     */
    fun positionOnXYPlaneTo(offsetX: Float, offsetY: Float) {
        lookAt.x = offsetX
        lookAt.y = offsetY
        lookAt.z = -1f
        position.x = offsetX
        position.y = offsetY
        position.z = 0f
        normal.x = offsetY
        normal.y = 1 + XenonMath.abs(offsetX)
        normal.z = 0f

        // ArgonMode4Wallpaper.getInstance().cameraInfo.
        Matrix.setLookAtM(info.cameraMatrix.get(), 0, position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, normal.x, normal.y, normal.z)
        info.projection4CameraMatrix.multiply(info.projectionMatrix, info.cameraMatrix)
    }
}