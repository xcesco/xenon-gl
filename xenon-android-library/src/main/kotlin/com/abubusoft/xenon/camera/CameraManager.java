/**
 * 
 */
package com.abubusoft.xenon.camera;

import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.settings.ProjectionType;
import com.abubusoft.xenon.settings.ViewFrustumSettings;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 * <p>
 * Gestore delle camere. Gestisce sia la camera principale che tutte le altre. L'unica obbligatoria è quella principale.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class CameraManager {

	private static final CameraManager instance = new CameraManager();

	private ViewFrustumSettings settings;

	private CameraManager() {
	}

	public static CameraManager instance() {
		return instance;
	}

	/**
	 * <p>
	 * E' la camera di default. Ci deve essere sempre.
	 * </p>
	 */
	public Camera camera;

	/**
	 * <p>
	 * Registra le impostazioni iniziali, quelle recuperate dal file di configurazione xml.
	 * </p>
	 * 
	 * @param viewFrustum
	 */
	public void init(ViewFrustumSettings viewFrustum) {
		camera = new Camera(this);

		// copiamo i valori di default.
		settings = viewFrustum;

		camera = createCamera();
	}

	/**
	 * imposta la perspective
	 * 
	 * @param fovy
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	void perspective(Camera newCamera, boolean landscapeMode, float fovy, float aspect) {

		float xmin, xmax, ymin, ymax;

		ymax = (float) (newCamera.info.zNear * Math.tan(fovy * XenonMath.DEGREES_TO_RADIANS_FACTOR * 0.5));
		ymin = -ymax;
		xmin = ymin;
		xmax = ymax;

		// impostiamo qui cameraInfo.projectionMatrix

		// Matrix.perspectiveM(cameraInfo.projectionMatrix, 0, fovy, aspect,
		// zNear, zFar);

		if (!landscapeMode) {
			Matrix.frustumM(newCamera.info.projectionMatrix.get(), 0, xmin * aspect, xmax * aspect, ymin, ymax, newCamera.info.zNear, newCamera.info.zFar);
		} else {
			Matrix.frustumM(newCamera.info.projectionMatrix.get(), 0, xmin, xmax, ymin / aspect, ymax / aspect, newCamera.info.zNear, newCamera.info.zFar);
		}
	}

	/**
	 * <p>
	 * Crea una telecamera copiando i valori di default.
	 * </p>
	 * 
	 * @return
	 */
	Camera createCamera() {
		Camera newCamera = new Camera(this);
		// frustum
		newCamera.info.fieldOfView = settings.fieldOfView;
		newCamera.info.zNear = settings.zNear;
		newCamera.info.zFar = settings.zFar;
		newCamera.info.zInverseFrustmDepthFactor = 1f / (settings.zFar - settings.zNear);
		newCamera.info.frustumSize = settings.size;
		newCamera.info.projection = settings.projection;

		return newCamera;
	}

	public Camera createCamera(int viewportWidth, int viewportHeight) {
		Camera newCamera = createCamera();
		setupCamera(newCamera, viewportWidth, viewportHeight);

		return newCamera;
	}

	/**
	 * <p>
	 * Cambia screenInfo e le impostazioni sulla camera di default.
	 * </p>
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public Camera onSurfaceChanged(int screenWidth, int screenHeight) {
		
		// imposta il viewport
		GLES20.glViewport(0, 0, screenWidth, screenHeight);
		setupCamera(camera, screenWidth, screenHeight);

		// restituisce camera di default
		return camera;
	}


	/**
	 * <p>
	 * </p>
	 * 
	 * @param newCamera
	 * @param screenWidth
	 * @param screenHeight
	 */
	void setupCamera(Camera newCamera, int screenWidth, int screenHeight) {
		float aspectRatio = (float) screenWidth / screenHeight;
		boolean landscapeMode = screenWidth > screenHeight;
		float correctionX;
		float correctionY;
		// recuperiamo il tipo di proiezione
		ProjectionType type = newCamera.info.projection;

		if (landscapeMode) {
			// width > height
			correctionX = aspectRatio;
			correctionY = 1.0f;
		} else {
			// width < height
			correctionX = 1.0f;
			correctionY = 1.0f / aspectRatio;
		}

		if (type == ProjectionType.ORTHOGONAL) {
			float size = newCamera.info.frustumSize / 2.0f;

			if (landscapeMode) {
				Matrix.orthoM(newCamera.info.projectionMatrix.get(), 0, -size * correctionX, size * correctionX, -size, size, newCamera.info.zNear, newCamera.info.zFar);
			} else {
				Matrix.orthoM(newCamera.info.projectionMatrix.get(), 0, -size, size, -size * correctionY, size * correctionY, newCamera.info.zNear, newCamera.info.zFar);
			}

		} else {
			float fieldOfView = newCamera.info.fieldOfView;
			perspective(newCamera, landscapeMode, fieldOfView, aspectRatio);
		}

		newCamera.info.setViewport(screenWidth, screenHeight);
		newCamera.info.cameraMatrix.buildIdentityMatrix();
		// precalcoliamo moltiplicazione projection startX camera
		newCamera.info.projection4CameraMatrix.multiply(newCamera.info.projectionMatrix, newCamera.info.cameraMatrix);
	}

}
