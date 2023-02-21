package com.abubusoft.xenon.camera;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Point3;

import android.opengl.Matrix;

/**
 * Rappresenta la camera da cui si vede il mondo.
 * 
 * @author Francesco Benincasa
 * 
 */
public class Camera {

	/**
	 * informazioni sulla camere
	 */
	public final CameraInfo info;

	public final Point3 lookAt;

	public final Point3 normal;

	public final Point3 position;
	public final Point3 rotation;
	
	/**
	 * <p>La possiamo creare solo da CameraManager.</p> 
	 */
	Camera(CameraManager cameraManager) {
		position = Point3.set(0f, 0f, 0f);
		lookAt = Point3.set(0, 0, -1);
		normal = Point3.set(0, 1, 0);

		rotation = Point3.set(0, 0, 0);

		info = new CameraInfo();
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
	 *            spostamento sull'asse startX
	 * @param offsetY
	 *            spostamento sull'asse startY
	 */
	public void positionOnXYPlaneTo(float offsetX, float offsetY) {
		lookAt.x = offsetX;
		lookAt.y = offsetY;
		lookAt.z = -1f;

		position.x = offsetX;
		position.y = offsetY;
		position.z = 0;

		normal.x = offsetY;
		normal.y = 1 + XenonMath.abs(offsetX);
		normal.z = 0f;

		// ArgonMode4Wallpaper.getInstance().cameraInfo.
		Matrix.setLookAtM(info.cameraMatrix.get(), 0, position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z, normal.x, normal.y, normal.z);
		info.projection4CameraMatrix.multiply(info.projectionMatrix, info.cameraMatrix);
	}

}
