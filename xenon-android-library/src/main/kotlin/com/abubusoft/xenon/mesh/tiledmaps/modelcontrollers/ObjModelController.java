package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

import java.util.ArrayList;

import com.abubusoft.xenon.animations.TiledMapAnimation;
import com.abubusoft.xenon.animations.TiledMapTimeline;
import com.abubusoft.xenon.animations.TranslationFrame;
import com.abubusoft.xenon.animations.events.EventFrameListener;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.MeshSprite;
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition;
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer;
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap;
import com.abubusoft.xenon.mesh.tiledmaps.orthogonal.OrthogonalHelper;
import com.abubusoft.xenon.mesh.tiledmaps.path.DijkstraPathFinder;
import com.abubusoft.xenon.mesh.tiledmaps.path.MoveType;
import com.abubusoft.xenon.mesh.tiledmaps.path.MovementMap;
import com.abubusoft.xenon.mesh.tiledmaps.path.MovementMapFactory;
import com.abubusoft.xenon.mesh.tiledmaps.path.Path;
import com.abubusoft.xenon.mesh.tiledmaps.path.PathFinder;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.kripton.android.Logger;

/**
 * @author Francesco Benincasa
 * 
 */
public class ObjModelController {

	/**
	 * 
	 */
	protected PathFinder pathFinder;

	/**
	 * temp x
	 */
	protected float tempX;

	/**
	 * temp y
	 */
	protected float tempY;

	/**
	 * sprite
	 */
	public MeshSprite sprite;

	/**
	 * oggetto da controllare
	 */
	public ObjDefinition obj;

	/**
	 * timeline
	 */
	public TiledMapTimeline timeline;

	/**
	 * Insieme di animazioni. vengono recuperate mediante ordinal del tipo.
	 */
	ArrayList<TiledMapAnimation> animations;

	// ObjActionSequence statusSequence;

	/**
	 * map
	 */
	public TiledMap map;

	/**
	 * fattore di conversione tra schermo e tiled map
	 */
	private float screenToTiledMapFactor;

	private MovementMap movementMap;

	/**
	 * imposta l'azione corrente
	 * 
	 * @param action
	 */
	public void action(ObjActionType action) {
		timeline.removeQueue();
		timeline.add(animations.get(action.ordinal()), true);
	}

	/**
	 * imposta l'azione corrente
	 * 
	 * @param action
	 */
	public void addAction(ObjActionType action) {
		timeline.add(animations.get(action.ordinal()), true);
	}

	/**
	 * pulisce tutte le azioni
	 */
	public void clearActions() {
		timeline.removeQueue();
	}

	/**
	 * @param capacity
	 */
	ObjModelController(ObjDefinition objValue, TiledMap mapValue, int capacity, MeshOptions options) {
		// impostiamo riferimenti vari
		obj = objValue;
		map = mapValue;
		sprite = MeshFactory.createSprite(obj.width, obj.height, options);
		animations = new ArrayList<>(capacity);

		if (false) {
			movementMap = MovementMapFactory.buildMovementMapByTileProperties(map, "path", "path");
			pathFinder = new DijkstraPathFinder(movementMap, false);
		}
		timeline = new TiledMapTimeline();
		timeline.setOnMoveEventListener(new EventFrameListener<TranslationFrame>() {

			@Override
			public void onFrameBegin(TranslationFrame currentFrame) {
				// applichiamo subito le translazioni
				obj.x += currentFrame.translation.x;
				obj.y += currentFrame.translation.y;
				// salviamo questo valore
				tempX = obj.x;
				tempY = obj.y;
			}

			@Override
			public void onFrameEnd(TranslationFrame currentFrame) {
				// per ottenere uno spostamento perfetto senza sbavature,
				// ripristinamo i valori presi all'inizio del frame
				obj.x = tempX;
				obj.y = tempY;

				// al prossimo frame aggiungeremo tutto il delta calcolato pezzo dopo pezzo
			}
		});

		for (int i = 0; i < capacity; i++) {
			animations.add(null);
		}

		float screenSize = XenonMath.max(XenonGL.screenInfo.width, XenonGL.screenInfo.height);
		float windowOfTileSize = map.view().windowDimension;

		screenToTiledMapFactor = windowOfTileSize / screenSize;
	}

	/**
	 * @param enlapsedTime
	 */
	public void update(long enlapsedTime) {
		timeline.update(enlapsedTime);

		// calcoliamo il valore corrente
		obj.x = tempX + timeline.value().translation.x;
		obj.y = tempY + timeline.value().translation.y;
	}
	
	public boolean isNodeAccessible(int keyNode)
	{
		return movementMap.isNodeAccessible(keyNode);
	}

	/**
	 * Input dall'utente
	 * 
	 * @param x
	 * @param y
	 */
	public void positionFromScreen(float screenDistanceX, float screenDistanceY) {
		position(screenDistanceX * screenToTiledMapFactor, screenDistanceY * screenToTiledMapFactor);
	}

	public void position(float x, float y) {
		obj.x = x;
		obj.y = y;

		tempX = 0f;
		tempY = 0f;

		if (map.scrollHorizontalLocked) {
			obj.x = XenonMath.clamp(obj.x, 0f, map.mapWidth);
		}

		if (map.scrollVerticalLocked) {
			obj.y = XenonMath.clamp(obj.y, 0f, map.mapHeight);
		}
	}

	/**
	 * <p>
	 * Cerca in tutti i layer object una definizione con il nome passato come parametro. Se non trova nulla restituisce <code>null</code>.
	 * </p>
	 * 
	 * @param name
	 * @return
	 */
	public static ObjDefinition objectFind(TiledMap map, String name) {
		ObjectLayer layer;
		ArrayList<ObjDefinition> objects;
		ObjDefinition current;
		for (int i = 0; i < map.objectLayers.size(); i++) {
			layer = map.objectLayers.get(i);

			objects = layer.getObjects();

			for (int j = 0; j < objects.size(); j++) {
				current = objects.get(j);

				if (name.equals(current.name)) {
					return current;
				}
			}
		}
		return null;
	}

	/**
	 * <p>
	 * Effettua lo scroll della definizione. Le coordinate sono quelle dello schermo. Questo implica che devono esssere riportate alle dimensioni della tiled map.
	 * </p>
	 * 
	 * @param screenDistanceX
	 * @param screenDistanceY
	 */

	public void moveFromScreen(float screenDistanceX, float screenDistanceY) {
		move(-screenDistanceX * screenToTiledMapFactor, -screenDistanceY * screenToTiledMapFactor);
	}

	/**
	 * <p>
	 * Effettua lo scroll della definizione. Le coordinate sono quelle della mappa. Il movimento è solo in verticale o in orizzontale
	 * </p>
	 * 
	 * @param distanceX
	 * @param distanceY
	 */

	public void move(float distanceX, float distanceY) { // ci muoviamo solo in orizzontale o in verticale if (Math.abs(distanceX) > Math.abs(distanceY)) { distanceY = 0;
		// distanceX=Math.round(distanceX/movement)*movement; } else if (Math.abs(distanceY) > Math.abs(distanceX)) { distanceX = 0;
		// distanceY=Math.round(distanceY/movement)*movement; }

		Logger.info("Scroll %s , %s", distanceX, distanceY);

		// applichiamo le distanze obj.x += distanceX; obj.y += distanceY;

		// rimaniamo nel boundary della tiled map if (map.scrollHorizontalLocked) { obj.x = XenonMath.clamp(obj.x, 0f, map.mapWidth); }

		if (map.scrollVerticalLocked) {
			obj.y = XenonMath.clamp(obj.y, 0f, map.mapHeight);
		}

		moveInternal(distanceX, distanceY);
	}

	/**
	 * Dalla posizione corrente, prova a
	 * 
	 * @param mapX
	 * @param mapY
	 */
	public void navigateTo(float mapX, float mapY) {
		int start = OrthogonalHelper.translateMapCoordsToTileId(map, obj.x, obj.y);
		// vogliamo posizionare il centro dello sprite proprio dove abbiamo selezionato, quindi dobbiamo
		// aggiungere meta size per passare dalle coordinate topleft a quelle centrali.
		int end = OrthogonalHelper.translateMapCoordsToTileId(map, mapX, mapY );

		if (end < 0 || end >= map.tileRows * map.tileColumns) {
			// destinazione non valida
			Logger.warn("Destination not valid %s", end);
		} else {

			Logger.info("NAVIGATE from %s to %s", start, end);
			Path path = pathFinder.findPath(null, start, end);

			MoveType action = null;

			for (int i = 0; i < path.size(); i++) {
				action = path.getMove(i);
				switch (action) {
				case UP:
					addAction(ObjActionType.MOVE_UP);
					break;
				case LEFT:
					addAction(ObjActionType.MOVE_LEFT);
					break;
				case DOWN:
					addAction(ObjActionType.MOVE_DOWN);
					break;
				case RIGHT:
					addAction(ObjActionType.MOVE_RIGHT);
					break;
				}
			}

			if (action != null) {
				switch (action) {
				case UP:
					addAction(ObjActionType.STAY_UP);
					break;
				case LEFT:
					addAction(ObjActionType.STAY_LEFT);
					break;
				case DOWN:
					addAction(ObjActionType.STAY_DOWN);
					break;
				case RIGHT:
					addAction(ObjActionType.STAY_RIGHT);
					break;
				}
			}
		}
	}

	public void moveStop() {
		timeline.forceNext();
		String currentAnimationName = timeline.getAnimationName();

		if (currentAnimationName != null) {
			currentAnimationName = currentAnimationName.replace("move", "stay");
		}
	}

	public boolean isSequenceRunning() {
		return timeline.isAnimationPlaying();
	}

	public boolean isSequenceFinished() {
		return timeline.isAnimationFinished();
	}

	protected void moveInternal(float distanceX, float distanceY) {
		// impostiamo l'animazione
		if (distanceY == 0f) {
			if (distanceX > 0f) {
				timeline.add(animations.get(ObjActionType.MOVE_RIGHT.ordinal()), true);
			} else {
				timeline.add(animations.get(ObjActionType.MOVE_LEFT.ordinal()), true);
			}
		} else {
			if (distanceY > 0f) {
				timeline.add(animations.get(ObjActionType.MOVE_DOWN.ordinal()), true);
			} else {
				timeline.add(animations.get(ObjActionType.MOVE_UP.ordinal()), true);
			}
		}
	}
}
