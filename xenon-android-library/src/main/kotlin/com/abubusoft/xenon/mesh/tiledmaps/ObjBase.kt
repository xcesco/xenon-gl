package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * <p>
 * Rappresenta un oggetto base, entità che viene definita tipicamente negli object layer ma che può rappresentare anche una classe (template) di object o uno sprite object.
 * </p>
 * 
 * <p>
 * Molto importante è il concetto di <b>marker</b>. Ad ogni ciclo di disegno, per come è composto il mondo box2d e per come può essere interrogato mediante una query AABB, un
 * oggetto può essere rilevato più volte, nel caso in cui vi siano associate più fixture. Per evitare disegni duplicati, si è pensato di introdurre una sorta di marcatura temporale
 * che indica l'ultima volta che l'oggetto è stato disegnato. Con questo marker è possibile determinare se l'oggetto è stato già disegnato per questo frame o meno.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class ObjBase extends PropertiesCollector {
	
	/**
	 * <p>Indica se l'oggetto è visibile o meno.</p>
	 */
	public boolean visible;
	

	/**
	 * Categorie di oggetti
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public enum CategoryType {
		/**
		 * oggetto di tipo classe
		 */
		CLASS,
		/**
		 * istanza di classe
		 */
		INSTANCE,
		/**
		 * sprite
		 */
		SPRITE,
		/**
		 * definizione
		 */
		DEFINITION;
	};

	/**
	 * <p>
	 * categoria di instanza. Consente di specificare il tipo di oggetto mediante {@link CategoryType}.
	 * </p>
	 */
	public CategoryType category;

	/**
	 * nome della classe
	 */
	public String name;

	/**
	 * coordinate left dell'oggetto. Le coordinate sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
	 */
	public float x;

	/**
	 * coordinate top dell'oggetto. Le coordinate sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
	 */
	public float y;

	/**
	 * tipo di body
	 */
	public String type;

	/**
	 * larghezza. Le dimensioni sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
	 */
	public float width;

	/**
	 * altezza. Le dimensioni sono espresse in pixel, con sistema di riferimento in alto a sx della mappa.
	 */
	public float height;

	/**
	 * <p>
	 * contatore generale dei frame. Va da 1 a 1000.
	 * </p>
	 */
	protected static int globalFrameMarker = 1;

	/**
	 * <p>
	 * Reinizializza il frame marker.
	 * </p>
	 */
	public static void resetFrameMarker() {
		globalFrameMarker = 1;
	}

	/**
	 * <p>
	 * aggiorna ad inizio frame il global frame marker.
	 * </p>
	 */
	public static void updateGlobalFrameMarker() {
		globalFrameMarker = ((globalFrameMarker + 1) % 1000);

		// se è 0, valore usato per i marker non ancora usati
		if (globalFrameMarker == 0)
			globalFrameMarker = 1;
	}

	/**
	 * <p>
	 * Viene utilizzato per indicare qual'è stato l'ultimo frame per il quale questo oggetto è stato disegnato.
	 * </p>
	 */
	protected int marker = 0;

	/**
	 * <p>
	 * Aggiorna il marker di questa istanza portandolo ad assumere lo stesso valore del globalFrameMarker.
	 * </p>
	 */
	public void updateFrameMarker() {
		marker = globalFrameMarker;
	}

	/**
	 * <p>
	 * L'oggetto è stato già marcato per questo frame.
	 * </p>
	 * 
	 * @return
	 * 		true se per questo frame l'oggetto è stato già marcato
	 */
	public boolean isFrameMarkerUpdated() {
		return marker == globalFrameMarker;
	}
}
