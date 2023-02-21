package com.abubusoft.xenon.mesh;

import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.kripton.android.Logger;

/**
 * Classe di utilità.
 * 
 * @author xcesco
 *
 */
public abstract class MeshHelper {

	/**
	 * Dato un vertice, mette nell'array dei mins i valori più bassi per ogni dimensione e nell'array dei max tutti i valori più grandi.
	 * 
	 * @param mins
	 *            array di 3 da usare come container per i valori di dimensioni più bassi
	 * @param maxs
	 *            array di 3 da usare come container per i valori di dimensioni più alti
	 */
	public static void buildMinMaxArray(float x, float y, float z, float[] mins, float[] maxs) {
		if (mins[0] > x)
			mins[0] = x;
		if (mins[1] > y)
			mins[1] = y;
		if (mins[2] > z)
			mins[2] = z;

		if (maxs[0] < x)
			maxs[0] = x;
		if (maxs[1] < y)
			maxs[1] = y;
		if (maxs[2] < z)
			maxs[2] = z;

	}

	/**
	 * Dati gli array di mins e maxs, provvede ad usare i valori in essi contenuti per definire il boundingBox e la boundingSphere.
	 * 
	 * @param shape
	 *            shape in analisi
	 * @param mins
	 *            array di 3 da usare come container per i valori di dimensioni più bassi
	 * @param maxs
	 *            array di 3 da usare come container per i valori di dimensioni più alti
	 */
	public static void defineBoundaries(Mesh shape, float[] mins, float[] maxs) {
		// impostiamo boundingbox
		shape.boundingBox.set(Math.abs(maxs[0] - mins[0]), Math.abs(maxs[1] - mins[1]), Math.abs(maxs[2] - mins[2]));

		// calcoliamo boundingSphere radius, ovvero il raggio della sfera che
		// contiene lo shape
		// Se parti da una sfera avente un raggio di 5 cm, il cubo inscritto è
		// quel cubo che può essere inserito esattamente nella tua sfera, in
		// modo tale che la distanza tra due vertici del cubo che siano opposti
		// tra loro misuri in lunghezza 10 cm (che è il diametro della sfera di
		// partenza). Per calcolare con precisione la lunghezza dello spigolo
		// del cubo inscritto devi dividere il diametro della sfera per la
		// radice quadrata di 3 (1,732 circa).
		// http://vivalascuola.studenti.it/come-determinare-le-misure-di-cubi-legati-a-sfere-140075.html#steps_2
		shape.boundingSphereRadius = (float) (0.8660254037844386 * shape.boundingBox.width);

	}

	/**
	 * <p>
	 * Crea un'istanza di mesh
	 * 
	 * @param options
	 * @return mesh creata
	 */
	public static Mesh create(MeshOptions options) {
		Mesh mesh;
		try {
			mesh = options.meshClazz.newInstance();

		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();

			throw (new XenonRuntimeException(e.getMessage()));
		}

		return mesh;
	}

}
