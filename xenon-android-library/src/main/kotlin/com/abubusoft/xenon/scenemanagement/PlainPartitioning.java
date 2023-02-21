package com.abubusoft.xenon.scenemanagement;

import java.util.ArrayList;
import java.util.Collections;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.ScreenInfo;
import com.abubusoft.xenon.entity.Entity;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.scenemanagement.internal.ZComparator;

/**
 * <p>Un semplice gestore di spazio. Semplicemente contiene un elenco di entità.
 * Internamente le entità vengono gestite con una semplice lista.</p>
 * 
 * <p>Il metodo che recupera le entità in base allo z order semplicemente ordina
 * tutte le entità presenti nella scena e le presenta ordinate dal più vicino
 * alla più lontana.</p>
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressWarnings("rawtypes")
public class PlainPartitioning<E extends Entity> implements SpacePartitioning<E> {

	protected BoundingBox2DSelector<E> selector;

	public void clear() {
		list.clear();
		zOrderedList.clear();
	}

	public PlainPartitioning() {
		list = new ArrayList<E>();
		zOrderedList = new ArrayList<E>();
		comparator = new ZComparator();
		temp = new ArrayList<E>();
		selector = new BoundingBox2DSelector<E>();
	}

	/**
	 * lista degli oggetti
	 */
	protected ArrayList<E> list;

	/**
	 * lista degli oggetti ordinati per z
	 */
	protected ArrayList<E> zOrderedList;

	/**
	 * lista temporanea
	 */
	protected ArrayList<E> temp;

	/**
	 * ordinatore
	 */
	protected ZComparator comparator;

	/**
	 * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
	 * varie, invoca il metodo savePosition per memorizzare la posizione inziale
	 * 
	 * @param entity
	 */
	public void add(E entity) {
		orderChanged = true;
		// salva la posizione
		entity.savePosition();

		list.add(entity);
		zOrderedList.add(entity);
	}

	/**
	 * Aggiunge un oggetto mesh in una determinata posizione. Tra le varie,
	 * invoca il metodo savePosition per memorizzare la posizione inziale.
	 * 
	 * @param position
	 * @param mesh
	 * @throws IllegalAccessException
	 * @throws Exception
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public E add(Point3 position, Mesh shape, Class<E> clazz) {
		orderChanged = true;
		E entity = null;
		try {
			entity = clazz.newInstance();
			entity.mesh = shape;
			position.copyInto(entity.position);

			add(entity);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return entity;
	}

	/**
	 * indica se gli elementi hanno cambiato ordine.
	 */
	private boolean orderChanged;

	public boolean isOrderChanged() {
		return orderChanged;
	}

	public void setOrderChanged(boolean orderChanged) {
		this.orderChanged = orderChanged;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.abubusoft.xenon.spacepartitioning.SpacePartitioning#retrieveZOrderedList
	 * ()
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<E> retrieveZOrderedList(Camera camera, ScreenInfo screenInfo) {
		if (orderChanged) {
			Collections.sort(zOrderedList, comparator);
		}
		orderChanged = false;

		return zOrderedList;
	}

	/**
	 * Partiamo dal presupposto che possiamo utilizzare il frame precedentemente
	 * disegnato, quindi vale ancora lo stesso zOrderedList
	 * 
	 * @param touchPoint
	 * @return
	 */
	public E retrieveTouchedEntity(Point3 touchPoint) {
		temp.clear();
		selector.setSelectorPoint(touchPoint);
		E item;

		int n = zOrderedList.size();

		for (int i = 0; i < n; i++) {
			item = zOrderedList.get(i);
			// touchPoint.copyTo(zTransformedTouch);
			if (item.collidable) {

				if (selector.isSelected(item)) {
					return item;
				}
			}
		}

		return null;

	}

}
