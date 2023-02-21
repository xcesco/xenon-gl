package com.abubusoft.xenon.camera;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.settings.ProjectionType;

/**
 * Informazione relativi alla camera e alle informazioni ad essa associate.
 * 
 * @author Francesco Benincasa
 * 
 */
public class CameraInfo {

	/**
	 * angolo di visualizzaizione espresso in gradi.
	 */
	public float fieldOfView;

	/**
	 * distanza del piano più vicino rispetto alla camera del frustum
	 */
	public float zNear;

	/**
	 * distanza del piano più lontano del frustum rispetto alla camera
	 */
	public float zFar;

	/**
	 * tipo di proiezione
	 */
	public ProjectionType projection;

	/**
	 * dimensione della camera (valido solo nella proiezione ORTOGONALE).
	 */
	public float frustumSize;

	/**
	 * <p>
	 * viewport sottoforma di matrice.
	 * </p>
	 * 
	 */
	public int[] viewport = new int[16];

	/**
	 * matrice di trasformazione modelView
	 */
	public Matrix4x4 cameraMatrix = new Matrix4x4();

	/**
	 * matrice di proiezione
	 */
	public Matrix4x4 projectionMatrix = new Matrix4x4();

	/**
	 * <p>
	 * Matrice di moltiplicazione projectionMatrix startX cameraMatrix
	 * </p>
	 * 
	 * <p>
	 * Comoda per evitare ad ogni frame la moltiplicazione delle matrici
	 * </p>
	 */
	public Matrix4x4 projection4CameraMatrix = new Matrix4x4();

	/**
	 * <p>Rappresenta 1f/ (zFar - zNear)</p>
	 */
	public float zInverseFrustmDepthFactor;

	/**
	 * Imposta il viewport in termini di width e height, usato
	 * 
	 * @param width
	 * @param height
	 */
	public void setViewport(int width, int height) {
		viewport[2] = width;
		viewport[3] = height;
	}

}
