/**
 * 
 */
package com.abubusoft.xenon.texture;

/**
 * <p>Rappresenta un'immagine caricata in un contesto opengl come texture. Ogni texture viene caricata in un contesto opengl mediante
 * un <b>bindingId</b>, che serve internamente ad opengl. Il <code>TextureManager</code> provvede a caricare una bitmap, assegnarli
 * un bindingId e l'indice posizionale (<b>index</b>) della texture. Questo ultimo indice viene utilizzato spesso e volentieri per referenziare
 * la texture.</p>
 * 
 * <img src="doc-files/img00017.png"/>
 * 
 * <h2>Dimensioni texture</h2>
 * 
 * <p>Le texture devono avere dimensioni potenze di 2, ovvero: 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024.</p>
 * <p>Ci sono sicurametne in giro dei metodi per utilizzare immagini che rispettano tale vincolo, ma preferisco non perdere troppo tempo su questo.</p>
 * <p>Attenzione che su alcuni dispositivi le texture con dimensioni non valide vengono visualizzate in bianco.</p>
 * 
 * <h2>Sistema di riferimento</h2>
 * 
 * <p>Il sistema di coordinate usato è U,V, nell'intervallo (0,1). <b>L'origine del sistema di riferimento nella bitmap
 * usata è il vertice in basso a sinistra</b>.</p>
 * 
 * <img src="doc-files/textureCoords1.png"/>
 * 
 * <p>Vedi <a href="http://stackoverflow.com/questions/5585368/problems-using-wavefront-objs-texture-coordinates-in-android-opengl-es">qui</a>.
 * 
 * <p>
 * your texture coordinate normalisation step is unnecessary — to the extent that I'm not sure why it's in there — and probably broken (what if xcoord is larger than ycoord on the first line, then smaller on the second?)
 * OBJ considers (0, 0) to be the top left of a texture, OpenGL considers it to be the bottom left, so unless you've set the texture matrix stack to invert texture coordinates in code not shown, you need to invert them yourself, e.g. textureCoordinatesMesh.add(1.0 - ycoord);
 * </p>
 * 
 * <img src="doc-files/textureCoords.png"/>
 * 
 * <p></p>
 * 
 * 
 * @author Francesco Benincasa
 * 
 * @see TextureManager
 *
 */
public class Texture {

	/**
	 * <p>Indice della texture non valido</p>
	 */
	public static final int INVALID_INDEX = -1;

	/**
	 * Costruttore della texture. Dovrebbe essere il TextureManager ad utilizzare questo costruttore.
	 * 
	 * @param nameValue
	 * @param bindingId
	 * @param dimension
	 */
	public Texture(String nameValue, int bindingIdValue)
	{
		this.name = nameValue;
		this.bindingId = bindingIdValue;
		updateInfo(null);
	}

	public final String name;

	/**
	 * binding id della texture. Non può essere final in quanto in caso di load può cambiare.
	 */
	public int bindingId;

	/**
	 * indice della texture
	 */
	public int index;

	/**
	 * <p>
	 * Informazioni sulla texture. Nel caso di texture caricate async, può essere nullo.
	 * </p>
	 */
	public TextureInfo info;

	/**
	 * <p>
	 * Indica se la texture è pronta ad essere usata
	 * </p>
	 */
	public boolean ready;

	/**
	 * Crea una nuova referenza
	 * 
	 * @return
	 */
	public TextureReference newReference() {
		return new TextureReference(index);
	}

	/**
	 * <p>
	 * Serve a ricaricare la texture
	 * </p>
	 */
	protected void reload() {

	}
	
	/**
	 * Quando lo schermo si chiude e cerco di scaricare la texture
	 */
	protected void unbind()
	{
		
	}
	
	/**
	 * <p>Aggiorna le info della texture.</p>
	 * @param value
	 */
	public void updateInfo(TextureInfo value)
	{
		this.info=value;
		this.ready=value!=null;
	}
}
