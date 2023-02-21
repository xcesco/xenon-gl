package com.abubusoft.xenon.math;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.abubusoft.xenon.engine.SharedData;

import android.opengl.Matrix;

/**
 * <p>
 * Matrice per le trasformazioni in opengl. Opengl usa il sistema right handed (vedi <a href="./doc-files/matrix.pdf" >3d_code</a>).
 * </p>
 * 
 * <img src="doc-files/MatrixXVect-300x71.gif"/>
 * 
 * <p>
 * Come si vede dall'immagine di sopra, i vettori vengono accodati alle matrici.
 * </p>
 * 
 * <p>
 * Per essere utilizzabili con le matrici prodotte dalla libreria OpenGL di Android le coordinate devono essere gestite in ordine column-major
 * </p>
 * 
 * <p>
 * Matrix math utilities. These methods operate on OpenGL ES format matrices and vectors stored in float arrays.
 * </p>
 * 
 * <p>
 * Matrices are 4 startX 4 column-vector matrices stored in column-major order:
 * </p>
 * 
 * <pre>
 *  m[offset +  0] m[offset +  4] m[offset +  8] m[offset + 12]
 *  m[offset +  1] m[offset +  5] m[offset +  9] m[offset + 13]
 *  m[offset +  2] m[offset +  6] m[offset + 10] m[offset + 14]
 *  m[offset +  3] m[offset +  7] m[offset + 11] m[offset + 15]
 * </pre>
 * 
 * <p>
 * Vectors are 4 row startX 1 column column-vectors stored in order:
 * </p>
 * 
 * <pre>
 * v[offset + 0]
 * v[offset + 1]
 * v[offset + 2]
 * v[offset + 3]
 * </pre>
 * 
 * @author Francesco Benincasa
 * 
 */
public class Matrix4x4 implements SharedData {

	/**
	 * <p>
	 * Tipo di rotazione. Ne esiste una per ogni asse.
	 * </p>
	 * <p>
	 * <b>Rotazione asse X</b>
	 * </p>
	 * <img src="doc-files/rotazioneX.png"/>
	 * <p>
	 * <b>Rotazione asse Y</b>
	 * </p>
	 * <img src="doc-files/rotazioneY.png"/>
	 * <p>
	 * <b>Rotazione asse Z</b>
	 * </p>
	 * <img src="doc-files/rotazioneZ.png"/>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum RotationType {
			/**
			 * <p>
			 * Rotazione attorno asse X
			 * </p>
			 * <img src="doc-files/rotazioneX.png"/>
			 */
			ROTATION_X,
			/**
			 * <p>
			 * Rotazione attorno asse Y
			 * </p>
			 * <img src="doc-files/rotazioneY.png"/>
			 */
			ROTATION_Y,

			/**
			 * <p>
			 * Rotazione attorno asse Z
			 * </p>
			 * <img src="doc-files/rotazioneZ.png"/>
			 */
			ROTATION_Z
	};

	/**
	 * Imposta una matrice come matrice identità.
	 * 
	 * @param m
	 *            matrice
	 */
	protected static void buildIdentityMatrix(float[] m) {
		// Matrix.setIdentityM(m, 0);
		for (int i = 0; i < 16; i++) {
			m[i] = 0;
		}
		for (int i = 0; i < 16; i += 5) {
			m[i] = 1.0f;
		}
	}

	/**
	 * Imposta la matrice <code>m</code> come matrice di rotazione. L'angolo è espresso in gradi.
	 * 
	 * @param m
	 *            matrice su cui operare
	 * @param angle
	 *            angolo di rotazione
	 * @param rotation
	 *            tipo di rotazione
	 * 
	 */
	protected static void buildRotationMatrix(float[] matrix, RotationType rotation, float angle) {
		buildIdentityMatrix(matrix);
		rotate(matrix, rotation, angle);
	}

	/**
	 * Computes the length of a vector
	 * 
	 * @param x
	 *            startX coordinate of a vector
	 * @param y
	 *            startY coordinate of a vector
	 * @param z
	 *            z coordinate of a vector
	 * @return the length of a vector
	 */
	public static float length(float x, float y, float z) {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * <p>
	 * Moltiplica la matrice sourceA per sourceB e mette il contenuto in desMatrix.
	 * </p>
	 * 
	 * <code>destMatrix = sourceA * sourceB</code>
	 * 
	 * @param destMatrix
	 * @param sourceA
	 * @param sourceB
	 */
	public static void multiply(Matrix4x4 destMatrix, Matrix4x4 sourceA, Matrix4x4 sourceB) {
		multiply(destMatrix.tempMultiplyMatrix, sourceA.matrix, sourceB.matrix);

		System.arraycopy(destMatrix.tempMultiplyMatrix, 0, destMatrix.matrix, 0, 16);
	}

	/**
	 * <p>
	 * Moltiplica la matrice attualmente in corso (M) per la matrice di rotazione (R) i cui parametri sono stati passati a questo metodo. Gli angoli sono espressi in gradi.
	 * </p>
	 * 
	 * <code>M'= M startX R</code>
	 * 
	 * <p>
	 * Un esempio:
	 * </p>
	 * <img src="doc-files/rotations_example.jpg"/> <br/>
	 * <p>
	 * Le rotazioni ammesse avvengono sui tre assi startX, startY, z:
	 * </p>
	 * <p>
	 * <b>Rotazione asse X</b>:
	 * </p>
	 * 
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneX.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneX.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Y</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneY.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneY.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Z</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneZ.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneZ.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * @see RotationType
	 * 
	 * @param rotation
	 *            tipo di rotazione
	 * 
	 * @param angle
	 *            angolo di rotazione in gradi.
	 */
	public static void rotate(float[] matrix, RotationType rotation, float angle) {
		// gli angoli rimangono in gradi.

		switch (rotation) {
		case ROTATION_X:
			Matrix.rotateM(matrix, 0, angle, 1f, 0, 0);
			break;
		case ROTATION_Y:
			// rotazione startY
			Matrix.rotateM(matrix, 0, angle, 0, 1f, 0);
			break;
		case ROTATION_Z:
			// rotazione z
			Matrix.rotateM(matrix, 0, angle, 0, 0, 1f);
			break;
		}
	}

	/**
	 * matrice
	 */
	protected float[] matrix = new float[16];

	/**
	 * buffer nativo per importare le matrici negli shader
	 */
	private FloatBuffer matrixFloatBuffer;

	/**
	 * matrice di appoggio per le moltiplicazioni
	 */
	private float[] tempMultiplyMatrix = new float[16];

	/**
	 * Aggiorna il float buffer e lo restituisce come valore di ritorno del
	 * 
	 * @return floatbuffer
	 */
	public FloatBuffer asFloatBuffer() {
		if (matrixFloatBuffer == null) {
			matrixFloatBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		}

		if (!floatBufferlock) {
			// nel caso in cui non sia posizionato all'inizio lo posizioniamo manualmente
			// if (matrixFloatBuffer.position()!=0) matrixFloatBuffer.position(0);
			matrixFloatBuffer.put(matrix).position(0);
		}
		return matrixFloatBuffer;
	}

	/**
	 * <p>
	 * Se true il floatBuffer non viene aggiornato quando si usa il metodo asFloatBuffer.
	 * </p>
	 */
	protected boolean floatBufferlock = false;

	/**
	 * Blocca il contenuto del float buffer
	 */
	public void lock() {
		// effetuaimo l'unlock, copiamo i dati nel float buffer e rimettiamo il lock
		floatBufferlock = false;
		asFloatBuffer();
		floatBufferlock = true;
	}

	/**
	 * sblocca tutto
	 */
	public void unlock() {
		// effetuaimo l'unlock, copiamo i dati nel float buffer e rimettiamo il lock
		floatBufferlock = false;
	}

	/**
	 * Verifica stato di lock
	 */
	public boolean isLocked() {
		return floatBufferlock;
	}

	/**
	 * <p>
	 * Crea una matrice per il frustum associato.
	 * </p>
	 * 
	 * <img src="doc-files/projection_example.gif"/>
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param nearZ
	 * @param farZ
	 */
	private void buildFrustumMatrix(float left, float right, float bottom, float top, float nearZ, float farZ) {
		/*
		 * float deltaX = right - left; float deltaY = top - bottom; float deltaZ = farZ - nearZ; float[] frust = matrix;
		 * 
		 * if ((nearZ <= 0.0f) || (farZ <= 0.0f) || (deltaX <= 0.0f) || (deltaY <= 0.0f) || (deltaZ <= 0.0f)) return;
		 * 
		 * frust[0 * 4 + 0] = 2.0f * nearZ / deltaX; frust[0 * 4 + 1] = frust[0 * 4 + 2] = frust[0 * 4 + 3] = 0.0f;
		 * 
		 * frust[1 * 4 + 1] = 2.0f * nearZ / deltaY; frust[1 * 4 + 0] = frust[1 * 4 + 2] = frust[1 * 4 + 3] = 0.0f;
		 * 
		 * frust[2 * 4 + 0] = (right + left) / deltaX; frust[2 * 4 + 1] = (top + bottom) / deltaY; frust[2 * 4 + 2] = -(nearZ + farZ) / deltaZ; frust[2 * 4 + 3] = -1.0f;
		 * 
		 * frust[3 * 4 + 2] = -2.0f * nearZ * farZ / deltaZ; frust[3 * 4 + 0] = frust[3 * 4 + 1] = frust[3 * 4 + 3] = 0.0f;
		 */
		if (left == right) {
			throw new IllegalArgumentException("left == right");
		}
		if (top == bottom) {
			throw new IllegalArgumentException("top == bottom");
		}
		if (nearZ == farZ) {
			throw new IllegalArgumentException("near == far");
		}
		if (nearZ <= 0.0f) {
			throw new IllegalArgumentException("near <= 0.0f");
		}
		if (farZ <= 0.0f) {
			throw new IllegalArgumentException("far <= 0.0f");
		}
		final float r_width = 1.0f / (right - left);
		final float r_height = 1.0f / (top - bottom);
		final float r_depth = 1.0f / (nearZ - farZ);
		final float x = 2.0f * (nearZ * r_width);
		final float y = 2.0f * (nearZ * r_height);
		final float A = (right + left) * r_width;
		final float B = (top + bottom) * r_height;
		final float C = (farZ + nearZ) * r_depth;
		final float D = 2.0f * (farZ * nearZ * r_depth);
		matrix[0] = x;
		matrix[5] = y;
		matrix[8] = A;
		matrix[9] = B;
		matrix[10] = C;
		matrix[14] = D;
		matrix[11] = -1.0f;
		matrix[1] = 0.0f;
		matrix[2] = 0.0f;
		matrix[3] = 0.0f;
		matrix[4] = 0.0f;
		matrix[6] = 0.0f;
		matrix[7] = 0.0f;
		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[15] = 0.0f;

		// multiply(frust, matrix);
	}

	/**
	 * Crea una matrice d'identità.
	 */
	public void buildIdentityMatrix() {
		buildIdentityMatrix(matrix);
		// buildIdentityMatrix(tempMatrix);
		// buildIdentityMatrix(tempMultiplyMatrix);
	}

	/**
	 * Crea una copia della matrice data come parametro.
	 */
	public void build(Matrix4x4 origin) {
		for (int i = 0; i < matrix.length; i++) {
			matrix[i] = origin.matrix[i];
		}
	}

	/**
	 * Define a viewing transformation in terms of an eye point, a center of view, and an up vector.
	 * 
	 * @param eyeX
	 *            eye point X
	 * @param eyeY
	 *            eye point Y
	 * @param eyeZ
	 *            eye point Z
	 * @param centerX
	 *            center of view X
	 * @param centerY
	 *            center of view Y
	 * @param centerZ
	 *            center of view Z
	 * @param upX
	 *            up vector X
	 * @param upY
	 *            up vector Y
	 * @param upZ
	 *            up vector Z
	 */
	public void buildLookAtMatrix(float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {

		// See the OpenGL GLUT documentation for gluLookAt for a description
		// of the algorithm. We implement it in a straightforward way:

		float fx = centerX - eyeX;
		float fy = centerY - eyeY;
		float fz = centerZ - eyeZ;

		// Normalize f
		float rlf = 1.0f / Matrix.length(fx, fy, fz);
		fx *= rlf;
		fy *= rlf;
		fz *= rlf;

		// compute s = f startX up (startX means "cross product")
		float sx = fy * upZ - fz * upY;
		float sy = fz * upX - fx * upZ;
		float sz = fx * upY - fy * upX;

		// and normalize s
		float rls = 1.0f / Matrix.length(sx, sy, sz);
		sx *= rls;
		sy *= rls;
		sz *= rls;

		// compute u = s startX f
		float ux = sy * fz - sz * fy;
		float uy = sz * fx - sx * fz;
		float uz = sx * fy - sy * fx;

		matrix[0] = sx;
		matrix[1] = ux;
		matrix[2] = -fx;
		matrix[3] = 0.0f;

		matrix[4] = sy;
		matrix[5] = uy;
		matrix[6] = -fy;
		matrix[7] = 0.0f;

		matrix[8] = sz;
		matrix[9] = uz;
		matrix[10] = -fz;
		matrix[11] = 0.0f;

		matrix[12] = 0.0f;
		matrix[13] = 0.0f;
		matrix[14] = 0.0f;
		matrix[15] = 1.0f;

		translate(1, -eyeX, -eyeY, -eyeZ);
	}

	/**
	 * <p>
	 * Costruisce una matrice di proiezione ortogonale
	 * </p>
	 * 
	 * <img src="doc-files/projection_example.gif"/>
	 * 
	 * @param left
	 * @param right
	 * @param bottom
	 * @param top
	 * @param nearZ
	 * @param farZ
	 */
	public void buildOrthoProjectionMatrix(float left, float right, float bottom, float top, float nearZ, float farZ) {
		float deltaX = right - left;
		float deltaY = top - bottom;
		float deltaZ = farZ - nearZ;
		buildIdentityMatrix();

		if ((deltaX == 0.0f) || (deltaY == 0.0f) || (deltaZ == 0.0f))
			return;

		matrix[0 * 4 + 0] = 2.0f / deltaX;

		matrix[1 * 4 + 1] = 2.0f / deltaY;

		matrix[2 * 4 + 2] = -2.0f / deltaZ;

		matrix[3 * 4 + 0] = -(right + left) / deltaX;
		matrix[3 * 4 + 1] = -(top + bottom) / deltaY;
		matrix[3 * 4 + 2] = -(nearZ + farZ) / deltaZ;

		// multiply(tempMatrix, matrix);
	}

	/**
	 * <p>
	 * Costruire una matrice di proiezione.
	 * </p>
	 * <img src="doc-files/camera_elements_example.jpg"/>
	 * 
	 * @param fieldOfView
	 * @param aspect
	 * @param nearZ
	 * @param farZ
	 */
	public void buildPerspectiveProjectionMatrix(float fieldOfView, float aspect, float nearZ, float farZ) {
		float frustumW, frustumH;

		frustumH = (float) Math.tan(fieldOfView * XenonMath.DEGREES_TO_RADIANS_FACTOR) * nearZ;

		if (aspect <= 1.0f) {
			frustumW = frustumH * aspect;
		} else {
			frustumW = frustumH;
			frustumH = frustumH / aspect;
		}

		// Matrix.frustumM(m, offset, left, right, bottom, top, near, far)
		buildIdentityMatrix();
		buildFrustumMatrix(-frustumW, frustumW, -frustumH, frustumH, nearZ, farZ);
	}

	/**
	 * <p>
	 * Imposta la matrice come matrice di rotazione. L'angolo è espresso in gradi.
	 * </p>
	 * 
	 * <p>
	 * Un esempio:
	 * </p>
	 * <img src="doc-files/rotations_example.jpg"/> <br/>
	 * <p>
	 * Le rotazioni ammesse avvengono sui tre assi startX, startY, z:
	 * </p>
	 * <p>
	 * <b>Rotazione asse X</b>:
	 * </p>
	 * 
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneX.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneX.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Y</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneY.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneY.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Z</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneZ.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneZ.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * @see RotationType
	 * 
	 * @param rotation
	 *            tipo di rotazione
	 * @param angle
	 *            angolo di rotazione in gradi.
	 * 
	 */
	public void buildRotationMatrix(RotationType rotation, float angle) {
		buildRotationMatrix(matrix, rotation, angle);
	}

	/**
	 * <p>
	 * Definisce una matrice che effettua lo scaling.
	 * </p>
	 * 
	 * <img src="doc-files/scalingMatrix.png"/>
	 * 
	 * <p>
	 * Per ogni dimensione definiamo il fattore di scala
	 * </p>
	 * 
	 * @param sx
	 * @param sy
	 * @param sz
	 */
	public void buildScaleMatrix(float sx, float sy, float sz) {
		buildIdentityMatrix();
		matrix[0 * 4 + 0] *= sx;
		matrix[1 * 4 + 1] *= sy;
		matrix[2 * 4 + 2] *= sz;
	}

	/**
	 * Moltiplica la matrice attuale per una matrice di scala
	 * 
	 * @param sx
	 * @param sy
	 * @param sz
	 */
	public void scale(float sx, float sy, float sz) {
		Matrix.scaleM(matrix, 0, sx, sy, sz);
	}

	/**
	 * Moltiplica la matrice attuale per una matrice di scala. Se 1 lascia tutto inalterato
	 * 
	 * @param scale
	 */
	public void scale(float scale) {
		Matrix.scaleM(matrix, 0, scale, scale, scale);
	}

	/**
	 * <p>
	 * Imposta la matrice come matrice di traslazione. Viene prima creata una matrice identità e poi vengono impostati i parametri di traslazione.
	 * </p>
	 * 
	 * @param tx
	 *            fattore di traslazione startX
	 * @param ty
	 *            fattore di traslazione startY
	 * @param tz
	 *            fattore di traslazione z
	 */
	public void buildTranslationMatrix(float tx, float ty, float tz) {
		buildTranslationMatrix(1, tx, ty, tz);
	}

	/**
	 * <p>
	 * Imposta la matrice come matrice di traslazione. Viene prima creata una matrice identità e poi vengono impostati i parametri di traslazione.
	 * </p>
	 * 
	 * <pre>
	 * * * * tx
	 * * * * ty
	 * * * * tz
	 * * * * 1
	 * </pre>
	 * 
	 * @param module
	 *            traslazione
	 * @param tx
	 *            fattore di traslazione startX
	 * @param ty
	 *            fattore di traslazione startY
	 * @param tz
	 *            fattore di traslazione z
	 */
	public void buildTranslationMatrix(float module, float tx, float ty, float tz) {
		buildIdentityMatrix();
		Matrix.translateM(matrix, 0, module * tx, module * ty, module * tz);
	}

	public float[] get() {
		return matrix;
	}

	/**
	 * <p>
	 * Moltiplica due matrici. Usa una matrice temporanea per evitare che un uso della matrice interna di questa classe come parametro della funzioni generi problemi.
	 * </p>
	 * 
	 * <code>result = m1 * m2</code>
	 * 
	 * @param m1
	 * @param m2
	 * @param result
	 */
	private static void multiply(float[] result, float[] m1, float[] m2) {
		// Matrix.multiplyMM(result, 0, m1, 0, m2, 0);
		result[0 + 0] = m1[0 + 0] * m2[0] + m1[0 + 4] * m2[1] + m1[0 + 8] * m2[2] + m1[0 + 12] * m2[3];
		result[0 + 4] = m1[0 + 0] * m2[4 + 0] + m1[0 + 4] * m2[4 + 1] + m1[0 + 8] * m2[4 + 2] + m1[0 + 12] * m2[4 + 3];
		result[0 + 8] = m1[0 + 0] * m2[8 + 0] + m1[0 + 4] * m2[8 + 1] + m1[0 + 8] * m2[8 + 2] + m1[0 + 12] * m2[8 + 3];
		result[0 + 12] = m1[0 + 0] * m2[12 + 0] + m1[0 + 4] * m2[12 + 1] + m1[0 + 8] * m2[12 + 2] + m1[0 + 12] * m2[12 + 3];

		result[1 + 0] = m1[1 + 0] * m2[0] + m1[1 + 4] * m2[1] + m1[1 + 8] * m2[2] + m1[1 + 12] * m2[3];
		result[1 + 4] = m1[1 + 0] * m2[4 + 0] + m1[1 + 4] * m2[4 + 1] + m1[1 + 8] * m2[4 + 2] + m1[1 + 12] * m2[4 + 3];
		result[1 + 8] = m1[1 + 0] * m2[8 + 0] + m1[1 + 4] * m2[8 + 1] + m1[1 + 8] * m2[8 + 2] + m1[1 + 12] * m2[8 + 3];
		result[1 + 12] = m1[1 + 0] * m2[12 + 0] + m1[1 + 4] * m2[12 + 1] + m1[1 + 8] * m2[12 + 2] + m1[1 + 12] * m2[12 + 3];

		result[2 + 0] = m1[2 + 0] * m2[0] + m1[2 + 4] * m2[1] + m1[2 + 8] * m2[2] + m1[2 + 12] * m2[3];
		result[2 + 4] = m1[2 + 0] * m2[4 + 0] + m1[2 + 4] * m2[4 + 1] + m1[2 + 8] * m2[4 + 2] + m1[2 + 12] * m2[4 + 3];
		result[2 + 8] = m1[2 + 0] * m2[8 + 0] + m1[2 + 4] * m2[8 + 1] + m1[2 + 8] * m2[8 + 2] + m1[2 + 12] * m2[8 + 3];
		result[2 + 12] = m1[2 + 0] * m2[12 + 0] + m1[2 + 4] * m2[12 + 1] + m1[2 + 8] * m2[12 + 2] + m1[2 + 12] * m2[12 + 3];

		result[3 + 0] = m1[3 + 0] * m2[0] + m1[3 + 4] * m2[1] + m1[3 + 8] * m2[2] + m1[3 + 12] * m2[3];
		result[3 + 4] = m1[3 + 0] * m2[4 + 0] + m1[3 + 4] * m2[4 + 1] + m1[3 + 8] * m2[4 + 2] + m1[3 + 12] * m2[4 + 3];
		result[3 + 8] = m1[3 + 0] * m2[8 + 0] + m1[3 + 4] * m2[8 + 1] + m1[3 + 8] * m2[8 + 2] + m1[3 + 12] * m2[8 + 3];
		result[3 + 12] = m1[3 + 0] * m2[12 + 0] + m1[3 + 4] * m2[12 + 1] + m1[3 + 8] * m2[12 + 2] + m1[3 + 12] * m2[12 + 3];
	}

	/**
	 * <p>
	 * Moltiplica questa matrice con quella passata come argomento.
	 * </p>
	 * 
	 * <code>this = this * matrixB</code>
	 * 
	 * <p>
	 * Viene usata la matrice temporanea tempMultiplyMatrix
	 * </p>
	 * 
	 * @param matrixB
	 */
	public void multiply(Matrix4x4 matrixB) {
		multiply(this, this, matrixB);
	}

	/**
	 * <p>
	 * Effettua la moltiplicazione della matrice per un vettore
	 * </p>
	 * 
	 * <img src="doc-files/MatrixXVect-300x71.gif"/>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param vectorInput
	 * @param vectorOutput
	 */
	public void multiply(Vector3 vectorInput, Vector3 vectorOutput) {
		vectorOutput.x = vectorInput.x * matrix[0] + vectorInput.y * matrix[4] + vectorInput.z * matrix[8] + matrix[12];
		vectorOutput.y = vectorInput.x * matrix[1] + vectorInput.y * matrix[5] + vectorInput.z * matrix[9] + matrix[13];
		vectorOutput.z = vectorInput.x * matrix[2] + vectorInput.y * matrix[6] + vectorInput.z * matrix[10] + matrix[14];
	}

	/**
	 * <p>
	 * Effettua la moltiplicazione della matrice per un vettore. Il vettore di float deve essere di dimensioni 3.
	 * </p>
	 * 
	 * <img src="doc-files/MatrixXVect-300x71.gif"/>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param vectorInput
	 * @param vectorOutput
	 */
	public void multiply(float[] vectorInput, float[] vectorOutput) {
		vectorOutput[0] = vectorInput[0] * matrix[0] + vectorInput[1] * matrix[4] + vectorInput[2] * matrix[8] + matrix[12];
		vectorOutput[1] = vectorInput[1] * matrix[1] + vectorInput[1] * matrix[5] + vectorInput[2] * matrix[9] + matrix[13];
		vectorOutput[2] = vectorInput[2] * matrix[2] + vectorInput[1] * matrix[6] + vectorInput[2] * matrix[10] + matrix[14];
	}

	/**
	 * <p>
	 * Effettua la moltiplicazione della matrice per un vettore
	 * </p>
	 * 
	 * <img src="doc-files/MatrixXVect-300x71.gif"/>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @param vectorInput
	 */
	public Vector3 multiply(Vector3 vectorInput) {
		Vector3 vectorOutput = new Vector3();

		multiply(vectorInput, vectorOutput);

		return vectorOutput;
	}

	/**
	 * <p>
	 * Moltiplica A * B mettendo il risultato nella matrice corrente.
	 * </p>
	 * 
	 * <code>this = A * B</code>
	 * 
	 * <p>
	 * Viene utilizzata una tempMultiplyMatrix dato che le due matrici potrebbero essere questa matrice.
	 * </p>
	 * 
	 * @param m1
	 * @param m2
	 */
	public void multiply(Matrix4x4 m1, Matrix4x4 m2) {
		multiply(tempMultiplyMatrix, m1.matrix, m2.matrix);

		System.arraycopy(tempMultiplyMatrix, 0, matrix, 0, 16);
	}

	/**
	 * <p>
	 * Moltiplica la matrice attualmente in corso (M) per la matrice di rotazione (R) i cui parametri sono stati passati a questo metodo.
	 * </p>
	 * 
	 * <code>M'= M startX R</code>
	 * 
	 * <p>
	 * Un esempio:
	 * </p>
	 * <img src="doc-files/rotations_example.jpg"/> <br/>
	 * <p>
	 * Le rotazioni ammesse avvengono sui tre assi startX, startY, z:
	 * </p>
	 * <p>
	 * <b>Rotazione asse X</b>:
	 * </p>
	 * 
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneX.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneX.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Y</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneY.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneY.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * <p>
	 * <b>Rotazione asse Z</b>:
	 * </p>
	 * <table>
	 * <tr>
	 * <td><img src="doc-files/rotazioneZ.png"/></td>
	 * <td><img src="doc-files/matriceRotazioneZ.png"/></td>
	 * </tr>
	 * </table>
	 * 
	 * @see RotationType
	 * 
	 * @param rotation
	 *            tipo di rotazione
	 * 
	 * @param angle
	 *            angolo di rotazione in gradi
	 */
	public void rotate(RotationType rotation, float angle) {
		rotate(matrix, rotation, angle);
	}

	public void set(float[] value) {
		matrix = value;
	}

	/**
	 * <p>
	 * Trasla la matrice attuale.
	 * </p>
	 * 
	 * <img src="doc-files/translationMatrix.png"/>
	 * 
	 * @param tx
	 *            fattore di traslazione sull'asse startX
	 * @param ty
	 *            fattore di traslazione sull'asse startY
	 * @param tz
	 *            fattore di traslazione sull'asse z
	 * 
	 */
	public void translate(float tx, float ty, float tz) {
		translate(1, tx, ty, tz);
	}

	/**
	 * <p>
	 * Trasla la matrice attuale.
	 * </p>
	 * 
	 * <img src="doc-files/translationMatrix.png"/>
	 * 
	 * <p>
	 * Per traslare solo lungo un asse, basta impostare il modulo ad 1 e mettere il rispettivo fattore alla traslazione desiderata. Si è scelta questa soluzione in quanto è la più flessibile.
	 * </p>
	 * 
	 * @param module
	 *            traslazione
	 * @param tx
	 *            fattore di traslazione sull'asse startX
	 * @param ty
	 *            fattore di traslazione sull'asse startY
	 * @param tz
	 *            fattore di traslazione sull'asse z
	 */
	public void translate(float module, float tx, float ty, float tz) {
		Matrix.translateM(matrix, 0, module * tx, module * ty, module * tz);
	}

	/**
	 * <p>
	 * Passa dal buffer usato nella fase LOGIC al floatbuffer usato nella fase RENDER.
	 * </p>
	 * 
	 * @see com.abubusoft.xenon.engine.SharedData#update()
	 */
	@Override
	public void update() {
		boolean temp = floatBufferlock;

		floatBufferlock = false;
		asFloatBuffer();
		floatBufferlock = temp;
	}

}