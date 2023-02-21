/**
 * 
 */
package com.abubusoft.xenon.scenemanagement;

import java.util.ArrayList;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.ScreenInfo;
import com.abubusoft.xenon.entity.Entity;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.core.Uncryptable;

/**
 * Rappresenta un sistema di partizionamento dello spazio 2D/3D.
 * 
 * @author Francesco Benincasa
 *
 * @param <E>
 */
@SuppressWarnings("rawtypes")
@Uncryptable
public interface SpacePartitioning<E extends Entity> {
	
	
	/**
	 * azzera lo stato dello space partioning
	 */
	public void clear();
	
	/**
	 * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
	 * varie, invoca il metodo savePosition per memorizzare la posizione inziale
	 * 
	 * @param entity
	 */
	public void add(E entity);
	/**
	 * Aggiunge un oggetto mesh in una determinata posizione. Tra le varie,
	 * invoca il metodo savePosition per memorizzare la posizione inziale.
	 * 
	 * @param position
	 * @throws Exception
	 * @throws InstantiationException
	 */
	public E add(Point3 position, Mesh shape, Class<E> clazz);
	
	/**
	 * recupera l'elenco delle entità da visualizzare in ordine di z crescente, ovvero
	 * da quello più vicino a quello più distante.
	 * 
	 * @return
	 */
	public ArrayList<E> retrieveZOrderedList(Camera camera, ScreenInfo screenInfo);

	/**
	 * Partiamo dal presupposto che possiamo utilizzare il frame precedentemente
	 * disegnato, quindi vale ancora lo stesso zOrderedList
	 * 
	 * @param touchPoint
	 * @return
	 */
	public E retrieveTouchedEntity(Point3 touchPoint);
}
