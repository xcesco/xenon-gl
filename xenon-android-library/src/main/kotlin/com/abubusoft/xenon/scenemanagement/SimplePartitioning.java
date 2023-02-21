package com.abubusoft.xenon.scenemanagement;

import java.util.ArrayList;
import java.util.List;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.ScreenInfo;
import com.abubusoft.xenon.android.wallpaper.WallpaperManager;
import com.abubusoft.xenon.entity.Entity;
import com.abubusoft.xenon.math.AlignedRectangle2D;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Circle;
import com.abubusoft.xenon.math.Point3;
import com.abubusoft.xenon.math.Sphere;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.scenemanagement.internal.ZComparator;
import com.abubusoft.xenon.core.util.ElioCollection;

/**
 * Un semplice gestore di spazio. Come per il {@link PlainPartitioning}, le
 * entità vengono gestite mediante una semplice lista.
 * 
 * Il metodo {@#retrieveZOrderedList()} ordina gli
 * elementi per z e poi analizza il rettangolo che rappresenta la parte di
 * schermo con la bounding sphere dell'entity.
 * 
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressWarnings("rawtypes")
public class SimplePartitioning<E extends Entity> implements SpacePartitioning<E> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.abubusoft.xenon.spacepartitioning.SpacePartitioning#clear()
	 */
	public void clear() {
		list.clear();
		zOrderedList.clear();

		temp.clear();
		tempOrderedList.clear();
	}

	/**
	 * costruttore
	 */
	public SimplePartitioning() {
		list = new ArrayList<E>();
		zOrderedList = new ArrayList<E>();
		comparator = new ZComparator<E>();
		temp = new ArrayList<E>();
		tempOrderedList = new ArrayList<E>();
	}

	/**
	 * lista degli oggetti
	 */
	protected List<E> list;

	protected AlignedRectangle2D cameraRect;

	protected Point3 worldPoint1 = new Point3();
	protected Point3 worldPoint2 = new Point3();
	protected Circle circle = new Circle();

	/**
	 * lista degli oggetti ordinati per z
	 */
	protected ArrayList<E> zOrderedList;

	/**
	 * lista degli oggetti ordinati per z
	 */
	protected ArrayList<E> tempOrderedList;

	/**
	 * lista temporanea
	 */
	protected ArrayList<E> temp;

	/**
	 * ordinatore
	 */
	protected ZComparator<E> comparator;

	private float oldWallpaperOffsetX = -1f;

	private float[] pointInPlane = new float[16];

	/**
	 * Aggiunge un elemento alla lista di elementi presenti nel mondo. Tra le
	 * varie, invoca il metodo savePosition per memorizzare la posizione inziale
	 * 
	 * @param entity
	 */
	public void add(E entity) {
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
	 * @throws Exception
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public E add(Point3 position, Mesh shape, Class<E> clazz) {
		E entity = null;
		try {
			entity = clazz.newInstance();
			entity.mesh = shape;
			position.copyInto(entity.position);

			add(entity);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return entity;
	}

	/**
	 * Ordina gli elementi per z e poi analizza il rettangolo che rappresenta la
	 * parte di schermo con la bounding sphere dell'entity.
	 * 
	 * @see com.abubusoft.xenon.scenemanagement.SpacePartitioning#retrieveZOrderedList
	 */
	public ArrayList<E> retrieveZOrderedList(Camera camera, ScreenInfo screenInfo) {

		// definiamo il rect a livello di camera
		if (cameraRect == null) {
			cameraRect = new AlignedRectangle2D(camera.position.x, camera.position.y, screenInfo.width, screenInfo.height);
		} else {
			cameraRect.center.setCoords(camera.position.x, camera.position.y);
			cameraRect.width = screenInfo.width;
			cameraRect.height = screenInfo.height;
		}

		// è stato cambiato lo screen?
		if (WallpaperManager.instance().getScreenOffset() != oldWallpaperOffsetX) {

			// se non cambia nulla, al prossimo ciclo non passiamo per di qua.
			oldWallpaperOffsetX = WallpaperManager.instance().getScreenOffset();

			// Collections.sort(zOrderedList, comparator);
			ElioCollection.sort(zOrderedList, comparator);
			tempOrderedList.clear();
			float lastZ = 10;

			E item;
			int n = zOrderedList.size();
			for (int i = 0; i < n; i++) {
				item = zOrderedList.get(i);

				// calcoliamo dimensione camera in base alla distanza z
				if (lastZ < item.position.z) {
					lastZ = item.position.z;

					XenonMath.convertViewToWorldST(camera, worldPoint1, pointInPlane, 0, 0, lastZ);
					XenonMath.convertViewToWorldST(camera, worldPoint2, pointInPlane, screenInfo.width, screenInfo.height, lastZ);

					cameraRect.width = XenonMath.abs(worldPoint2.x - worldPoint1.x);
					cameraRect.height = XenonMath.abs(worldPoint2.y - worldPoint1.y);

					cameraRect.center.x = (worldPoint1.x + worldPoint2.x) / 2;
					cameraRect.center.y = (worldPoint1.y + worldPoint2.y) / 2;

				}
				circle.center.x = item.position.x;
				circle.center.y = item.position.y;
				circle.radius = item.getBoundingRadius();

				// vediamo se inserire l'oggetto nella lista degli elementi
				// visibili a schermo
				if (cameraRect.intersect(circle)) {
					tempOrderedList.add(item);
				}

			}
		} else {
			// Se siamo qua, lo screen non è cambiato, dobbiamo solo ordinare
			// gli elementi in base allo z-order
			ElioCollection.sort(tempOrderedList, comparator);
		}

		// TODO da ottimizzare
		// time = Timer.now() - time;
		// Logger.debug("CALCOLO "+tempOrderedList.size()+" su "+zOrderedList.size()+" in "+time+" ms.");

		return tempOrderedList;
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
		Sphere bounding = new Sphere();

		E item;
		int n = zOrderedList.size();

		// for (E item : tempOrderedList) {
		for (int i = 0; i < n; i++) {
			item = zOrderedList.get(i);
			if (item.collidable) {
				bounding.set(item.position, item.getBoundingRadius());

				if (bounding.intersect(touchPoint)) {
					return item;
				}
			}
		}

		return null;

	}

}
