package com.abubusoft.xenon.mesh.tiledmaps;

import java.util.ArrayList;
import java.util.HashMap;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.math.Point2;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshGrid;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.MeshTile;
import com.abubusoft.xenon.mesh.tiledmaps.Layer.LayerType;
import com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler;
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView;
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.MapAttributes;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.SAXUtil;
import com.abubusoft.xenon.mesh.tiledmaps.tmx.loader.TMXPredefinedProperties;
import com.abubusoft.xenon.shader.ArgonShaderOptions;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.shader.ShaderTiledMap;
import com.abubusoft.xenon.texture.AtlasTexture;
import com.abubusoft.xenon.vbo.BufferAllocationOptions;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.kripton.android.Logger;

import org.xml.sax.Attributes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseIntArray;

/**
 * <p>
 * Definisce una mappa mediante il formato <a href="http://doc.mapeditor.org/reference/tmx-map-format/">tmx</a>
 * </p>
 * <p>
 * <p>
 * Una tiledmap consiste in una griglia multilayer di tile che vengono visualizzati sullo schermo. L'origine del sistema di riferimento interno alla mappa è posto in alto a sinistra della mappa. La window che viene utilizzata per
 * visualizzare solo parte visibile dello schermo si basa sullo stesso concetto, quindi la sua origine è in alto a sx. Se la window viene posizionata top|left, allora le due origini coincideranno.
 * </p>
 * <p>
 * <img src="doc-files/sistemaRiferimento.jpg"/>
 * <p>
 * <p>
 * Nel momento in cui la windo viene spostata, ad esempio a destra, il sistema di riferimento cambierà.
 * </p>
 * <p>
 * <p>
 * La window rappresenta la finestra sulla mappa. Essa non cambia mai di posizione, viene spostata la mappa.
 * </p>
 * <p>
 * <p>
 * windowCenter e mapCenter non cambiano mai, rimangono come punti di riferimento assoluti. Quando la mappa viene spostata, vengono semplicemente utilizzate le mattonelle apposite.
 * </p>
 * <p>
 * <p>
 * Quando viene effettuato uno scroll della finestra, viene spostato positionInMap che rappresenta il punto in alto a sx della window. Il centro della finestra essendo fisso sarà sempre determinato in base a quello.
 * </p>
 *
 * @author Francesco Benincasa
 * @see #mapCenter
 */
public class TiledMap extends PropertiesCollector {

    public final int backgroundColorR;

    public final int backgroundColorG;

    public final int backgroundColorB;

    public final int backgroundColorA;
    /**
     * <p>
     * Shader utilizzato per il rendering della tiledmap. Viene creato quando si crea la mappa mediante lettura da file.
     * </p>
     */
    public ShaderTiledMap shader;

    /**
     *
     */
    public final MapOrientationType orientation;

    /**
     * <p>
     * Indica la posizione nella mappa del punto in alto a sinistra della window.
     * </p>
     * <p>
     * <img src="doc-files/position.png"/>
     * <p>
     * <p>
     * Il sistema di riferimento utilizzato è quello che ha come origine il punto in alto a sinistra, rivolto verso il basso.
     * </p>
     * <p>
     * <img src="doc-files/sistemaRiferimento.jpg"/>
     */
    public final Point2 positionInMap;

    /**
     * numero di colonne di tiles
     */
    public final int tileColumns;

    /**
     * numero di righe di tiles
     */
    public final int tileRows;

    /**
     * <p>
     * Width di default di una tile.
     * </p>
     * <p>
     * Rappresenta la larghezza della tile nella rappresentazione a window.
     * </p>
     */
    public final int tileWidth;

    /**
     * <p>
     * Height di default di una tile.
     * </p>
     * <p>
     * Rappresenta l'altezza della tile nella rappresentazione a window.
     * </p>
     */
    public final int tileHeight;

    /**
     * <p>
     * lista delle animazioni.
     * </p>
     */
    public final ArrayList<TileAnimation> animations = new ArrayList<TileAnimation>();

    /**
     * <p>
     * durata di default dei frame delle animazioni.
     * </p>
     */
    public long animationFrameDefaultDuration = 100;

    /**
     * <p>
     * layer definiti.
     * </p>
     */
    public final ArrayList<Layer> layers = new ArrayList<Layer>();

    /**
     * <p>
     * insieme di tile Set definiti. Ogni tileset è associato ad una o più texture.
     * </p>
     */
    public ArrayList<TileSet> tileSets = new ArrayList<TileSet>();

    /**
     * <p>
     * Centro della mappa. Può essere usato per inserire degli oggetti in modo assoluto rispetto alla mappa. Esso viene calcolato in base all'origine della tiledMap (top left).
     * </p>
     * <p>
     * <img src="doc-files/calcoloMapCenter1.jpg"/>
     * <p>
     * <p>
     * Ovvero
     * </p>
     * <p>
     * <img src="doc-files/calcoloMapCenter2.jpg"/>
     * <p>
     * <p>
     * Il calcolo algebrico delle coordinate di mapCenter rispetto a windowCenter:
     * </p>
     * <p>
     * <pre>
     * mapCenter.x = tiledMap.mapWidth / 2f;
     * mapCenter.y = tiledMap.mapHeight / 2f;
     * </pre>
     * <p>
     * <p>
     * Non cambia mai, viene definito assieme alla posizione della window
     * </p>
     */
    public final Point2 mapCenter;

    /**
     * <p>
     * Definizione degli oggetti.
     * </p>
     */
    public final HashMap<String, ObjClass> objectClasses = new HashMap<>();

    /**
     * <p>
     * Layer
     */
    public ArrayList<ObjectLayer> objectLayers = new ArrayList<ObjectLayer>();

    private SparseIntArray tileSetPropertyCache = new SparseIntArray();

    private SparseIntArray spriteCache = new SparseIntArray();

    /**
     * <p>
     * Mesh per disegnare parte di layer. Non serve per disegnare i layer, ma quanto per disegnare i vari pezzi, tipicamente per gli oggetti.
     * </p>
     */
    public MeshGrid spriteMesh;

    /**
     * <p>
     * Larghezza della mappa nel sistema di riferimento della mappa. E' data dalla larghezza delle tile per le colonne presenti nella mappa. <b>Attenzione che tra orthogonal project e isometric cambia il suo calcolo</b>.
     * </p>
     * <h3>Calcolo su mappa orthogonal</h3> <code>mapWidth = tileColumns * tileWidth;</code> <h3>Calcolo su mappa isometric</h3> <code>mapWidth = tileColumns * tileHeight;</code>
     * <p>
     * Non tiene conto del bordo del diamante generato per lo scroll.
     * </p>
     */
    public int mapWidth;

    /**
     * <p>
     * Altezza della mappa nel sistema di riferimento della mappa. <b>Attenzione che tra orthogonal project e isometric cambia il suo calcolo</b>.
     * </p>
     * <h3>Calcolo su mappa orthogonal</h3> <code>mapHeight = tileRows * tileHeight;</code> <h3>Calcolo su mappa isometric</h3> <code>mapHeight = tileColumns * tileHeight;</code>
     * <p>
     * Non tiene conto del bordo del diamante generato per lo scroll.
     * </p>
     */
    public int mapHeight;

    /**
     * indica se lo scroll orizzontale è consentito o meno.
     */
    public boolean scrollHorizontalLocked;

    /**
     * indica se lo scroll verticale è consentito o meno.
     */
    public boolean scrollVerticalLocked;

    /**
     * <p>
     * Listener relativo allo scroll.
     * </p>
     */
    public MovementListener movementEventListener;

    /**
     * <p>
     * Numero di area in cui la tiledmap è suddiviso in orizzontale
     * </p>
     */
    public int scrollHorizontalAreaCount;

    /**
     * <p>
     * Numero di area in cui la tiledmap è suddiviso in verticale
     * </p>
     */
    public int scrollVerticalAreaCount;

    public MovementListenerOptions listenerOptions;

    public int scrollHorizontalPreviousArea;

    public int scrollVerticalPreviousArea;

    public int scrollHorizontalCurrentArea;

    public int scrollVerticalCurrentArea;

    /**
     * rappresenta il controller con il quale pilotare la visualizzazione sulla tiled map.
     */
    public MapController controller;

    private String renderOrder;

    /**
     * view sulla mappa
     */
    final TiledMapView view;

    public TiledMapView view() {
        return view;
    }

    /**
     *
     */
    public void setMovementListener(MovementListener scrollEventListenerValue, MovementListenerOptions optionsValue) {
        listenerOptions = optionsValue;
        listenerOptions.horizontalAreaInvSize = 1f * listenerOptions.horizontalAreaCount / mapWidth;
        listenerOptions.verticalAreaInvSize = 1f * listenerOptions.verticalAreaCount / mapHeight;
        movementEventListener = scrollEventListenerValue;

        resetScrollAreas();
    }

    /**
     * effettua il reset delle aree in cui è suddivisa la mappa
     */
    public void resetScrollAreas() {
        scrollHorizontalCurrentArea = -1;
        scrollVerticalCurrentArea = -1;

        scrollHorizontalPreviousArea = -1;
        scrollVerticalPreviousArea = -1;
    }

    /**
     * Costruttore, definiamo orientation, columns, rows, tileWidth, tileHeight.
     *
     * @param atts attributi
     */
    @SuppressLint("DefaultLocale")
    public TiledMap(Attributes atts) {
        // orientation
        String orientation = SAXUtil.getString(atts, MapAttributes.ORIENTATION);
        if (orientation == null)
            orientation = MapOrientationType.ORTHOGONAL.toString();
        this.orientation = MapOrientationType.valueOf(orientation.toUpperCase());

        this.renderOrder = SAXUtil.getString(atts, MapAttributes.RENDER_ORDER);
        this.tileColumns = SAXUtil.getInt(atts, MapAttributes.WIDTH);
        this.tileRows = SAXUtil.getInt(atts, MapAttributes.HEIGHT);
        this.tileWidth = SAXUtil.getInt(atts, MapAttributes.TILE_WIDTH);
        this.tileHeight = SAXUtil.getInt(atts, MapAttributes.TILE_HEIGHT);

        int bgColor=Color.parseColor(SAXUtil.getString(atts, MapAttributes.BACKGROUND_COLOR, "#000000FF"));

        this.backgroundColorR=Color.red(bgColor);
        this.backgroundColorG=Color.green(bgColor);
        this.backgroundColorB=Color.blue(bgColor);
        this.backgroundColorA=Color.alpha(bgColor);

        // creiamo l'handler
        this.handler = this.orientation.createMapHandler(this);

        view = new TiledMapView();
        positionInMap = new Point2();
        mapCenter = new Point2();

        resetScrollAreas();
    }

    /**
     * <p>
     * Aggiunge il layer group al set di group object e alla lista di layers.
     *
     * @param group
     */
    public void addObjectGroup(ObjectLayer group) {
        objectLayers.add(group);

        addLayer(group);
    }

    /**
     * <p>
     * Normalmente i layer object non vengono disegnati. Per farlo, bisogna definire loro un drawer.
     * </p>
     *
     * @param objectLayerName
     * @param drawer
     * @return true se è stato trovato l'object layer con il nome specificato
     */
    public boolean attachObjectLayerDrawer(String objectLayerName, ObjectLayerDrawer drawer) {
        for (int i = 0; i < objectLayers.size(); i++) {
            if (objectLayerName.equalsIgnoreCase(this.objectLayers.get(i).name)) {
                this.objectLayers.get(i).setObjectDrawer(drawer);
                return true;
            }
        }
        return false;
    }

    public void addTileSet(TileSet tileSet) {
        tileSets.add(tileSet);
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
    }

    public ArrayList<ObjectLayer> getObjectGroups() {
        return objectLayers;
    }

    /**
     * <p>
     * Per ogni layer, in base alle tile o alla texture associata (per le imageLayer) assegna le varie texture.
     * </p>
     */
    public void assignTextureToLayers(Context context) {
        Layer currentLayer;
        Tile currentTile;

        int currentGid;
        AtlasTexture currentTexture;
        byte textureIndex;

        int atlasRow;
        int atlasCol;

        float temp;

        // impostiamo texture
        texturesCount = tileSets.size();

        textureCounterMaxPerLayer = 0;

        // per ogni layer
        for (int l = 0; l < layers.size(); l++) {
            currentLayer = layers.get(l);

            // imposta la velocità
            currentLayer.speedPercentageX = currentLayer.getPropertyAsFloat(TMXPredefinedProperties.SPEED_PERCENTAGE_X, 1.0f);
            currentLayer.speedPercentageY = currentLayer.getPropertyAsFloat(TMXPredefinedProperties.SPEED_PERCENTAGE_Y, 1.0f);

            switch (currentLayer.type) {
                case TILED: {
                    TiledLayer currentTiledLayer = (TiledLayer) currentLayer;

                    // per ogni tile presente nel layer
                    int numTiles = currentTiledLayer.tileRows * currentTiledLayer.tileColumns;

                    for (int i = 0; i < numTiles; i++) {
                        currentTile = currentTiledLayer.tiles[i];
                        currentGid = currentTile.gid;

                        currentTexture = getTextureByGID(currentGid);

                        if (currentTexture != null) {
                            textureIndex = (byte) currentTiledLayer.textureList.indexOf(currentTexture);

                            // aggiungiamo la texture se finora non era stata usata
                            // in nessuna tile
                            if (textureIndex == -1) {
                                currentTiledLayer.textureList.add(currentTexture);
                                textureIndex = (byte) (currentTiledLayer.textureList.size() - 1);
                            }

                            currentTile.textureSelector = textureIndex;

                            // calcoliamo dimensioni del tile nella texture
                            atlasRow = currentTile.atlasRow;
                            atlasCol = currentTile.atlasColumn;
                        /*
                         * // come da http://stackoverflow.com/questions/3962385/ android-opengl-es-texture-bleeding
						 */

                            // fix su texel adjustment
                            float halfPixelAdjust = (float) (0.5 * ((1.0f / currentTexture.columnsCount) / currentTile.width));
                            // float halfPixelAdjust = 0f;

                            currentTexture.selectedCurrentFrame(atlasRow, atlasCol);
                            currentTile.lowY = currentTexture.getCoordStartY() + halfPixelAdjust;
                            currentTile.highY = currentTexture.getCoordEndY() - halfPixelAdjust;
                            currentTile.lowX = currentTexture.getCoordStartX() + halfPixelAdjust;
                            currentTile.highX = currentTexture.getCoordEndX() - halfPixelAdjust;

                            if (!currentTile.diagonalFlip) {
                                if (currentTile.horizontalFlip) {
                                    temp = currentTile.lowX;
                                    currentTile.lowX = currentTile.highX;
                                    currentTile.highX = temp;
                                }

                                if (currentTile.verticalFlip) {
                                    temp = currentTile.lowY;
                                    currentTile.lowY = currentTile.highY;
                                    currentTile.highY = temp;
                                }
                            } else {

                                if (!currentTile.verticalFlip) {
                                    temp = currentTile.lowX;
                                    currentTile.lowX = currentTile.highX;
                                    currentTile.highX = temp;
                                }

                                if (!currentTile.horizontalFlip) {
                                    temp = currentTile.lowY;
                                    currentTile.lowY = currentTile.highY;
                                    currentTile.highY = temp;
                                }
                            }
                        } else {
                            // impostiamola come empty tile
                            currentTile.gid = 0;
                        }
                    }

                    // usiamo il counter delle texture
                    textureCounterMaxPerLayer = Math.max(textureCounterMaxPerLayer, currentTiledLayer.textureList.size());
                }
                break;
                case IMAGE: {
                    // carica la texture per l'imageLayer
                    ImageLayer currentImageLayer = (ImageLayer) currentLayer;
                    TileSet currentTileSet;
                    boolean trovato = false;

                    String textureSource = currentImageLayer.imageSource;
                    // se troviamo un tileSet con questo elemento, lo riusiamo, dato
                    // che la texture è stata
                    // già caricata.
                    int numTileSet = this.tileSets.size();
                    for (int i = 0; i < numTileSet; i++) {
                        currentTileSet = tileSets.get(i);

                        if (currentTileSet.imageSource.equals(textureSource)) {
                            // abbiamo trovato un tileset con la stessa texture,
                            // quindi la riusiamo
                            currentImageLayer.textureList.add(currentTileSet.texture);
                            trovato = true;
                            break;
                        }
                    }

                    if (!trovato) {
                        currentImageLayer.loadTexture(context);
                        // il layer usa una texture che non è contemplata nei
                        // tileSet, quindi dobbiamo incrementare il
                        // numero di texture usate
                        texturesCount++;
                    }

                }
                break;
                case OBJECTS:
                    // non faccio niente
                    break;

            }
        }
    }

    /**
     * <p>
     * Data una gid, restituisce una texture. Se la gid è 0 allora restituisce una texture null, ad indicare che la tile è vuota.
     * </p>
     *
     * @param gid
     * @return
     */
    private AtlasTexture getTextureByGID(int gid) {
        try {
            // analizza i tileset dal più alto al più basso
            for (int i = tileSets.size() - 1; i >= 0; i--) {
                TileSet tileSet = tileSets.get(i);
                if (gid >= tileSet.firstGID) {

                    return tileSet.texture;
                }
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return null;
    }

    /**
     * <p>
     * </p>
     *
     * @param gid
     * @return sprite associato al gid
     */
    public MeshTile getSpriteByGID(int gid) {
        try {
            if (spriteCache.indexOfKey(gid) >= 0) {
                int index = spriteCache.get(gid);
                return tileSets.get(index).getSprite(gid);
            }
            for (int i = tileSets.size() - 1; i >= 0; i--) {
                TileSet tileSet = tileSets.get(i);
                if (gid >= tileSet.firstGID) {
                    MeshTile sprite = tileSet.getSprite(gid);
                    spriteCache.put(gid, i);

                    return sprite;
                }
            }
        } catch (Exception e) {
            Logger.error(e.toString());
        }
        return null;
    }

    /**
     * Restuisce il valore di una proprietà di una tile, mediante un sistema di cache delle tile
     *
     * @param gid
     * @return
     */
    public ArrayList<TileProperty> getTileProperties(int gid) {
        if (tileSetPropertyCache.indexOfKey(gid) >= 0) {
            int index = tileSetPropertyCache.get(gid);
            return tileSets.get(index).getTileProperty(gid);
        }
        for (int i = 0; i < tileSets.size(); i++) {
            TileSet tileSet = tileSets.get(i);
            ArrayList<TileProperty> props = tileSet.getTileProperty(gid);
            if (props != null) {
                tileSetPropertyCache.put(gid, i);
                return props;
            }
        }
        return null;
    }

    /**
     * Restuisce il valore di una proprietà di una tile, mediante un sistema di cache delle tile
     *
     * @param gid
     * @param key
     * @param defaultValue
     * @return valore della chiave sottforma di stringa
     */
    public String getTileProperty(int gid, String key, String defaultValue) {
        ArrayList<TileProperty> props = getTileProperties(gid);

        // se non abbiamo proprietà restituiamo comunque il defaultValue
        if (props == null)
            return defaultValue;

        int n = props.size();
        for (int i = 0; i < n; i++) {
            if (props.get(i).getName().equals(key)) {
                return props.get(i).getValue();
            }
        }

        return defaultValue;
    }

    /**
     * <p>
     * Data una chiave, prova a rimuovere un object layer.
     * </p>
     *
     * @param key
     */
    public boolean removeObjectgroup(String key) {
        ObjectLayer og = findtObjectGroup(key);

        if (og != null) {
            // rimuoviamo sia dai layer che dagli objectgroup
            layers.remove(og);
            return objectLayers.remove(og);
        }

        return false;
    }

    /**
     * <p>
     * Rimuove un objectlayer, partendo dalla sua chiave.
     * </p>
     *
     * @param key
     * @return
     */
    public boolean removeLayer(String key) {
        Layer layer = findLayer(key);

        if (layer != null) {
            return layers.remove(layer);
        }

        return false;
    }

    /**
     * <p>
     * Imposta la visibilità di un layer o di un object layer.
     * </p>
     *
     * @param name
     * @param visibleValue
     */
    public void setVisible(String name, boolean visibleValue) {
        for (int i = 0; i < objectLayers.size(); i++) {
            if (name.equalsIgnoreCase(this.objectLayers.get(i).name)) {
                this.objectLayers.get(i).visible = visibleValue;
                return;
            }
        }

        for (int i = 0; i < layers.size(); i++) {
            if (name.equalsIgnoreCase(this.layers.get(i).name)) {
                this.layers.get(i).visible = visibleValue;
                return;
            }
        }
    }

    /**
     * <p>
     * Indica se un layer o un object layer è visibile.
     * </p>
     *
     * @param name
     * @param visibleValue
     */
    public boolean isVisible(String name) {
        for (int i = 0; i < objectLayers.size(); i++) {
            if (name.equalsIgnoreCase(this.objectLayers.get(i).name)) {
                return this.objectLayers.get(i).visible;
            }
        }

        for (int i = 0; i < layers.size(); i++) {
            if (name.equalsIgnoreCase(this.layers.get(i).name)) {
                return this.layers.get(i).visible;
            }
        }

        return false;
    }

    /**
     * Attacca la tiledmap alla camera passata come argomento. Questo consente di creare una window sulla tiled map agganciata alla camera. Il numero di tiles per riga e colonna è data dalla divisione dello schermo in base alle dimensioni
     * dei tile, più due tile per dimensione, per gestire eventuali spostamenti
     *
     * @param cameraValue
     * @param options
     * @return controller
     */
    public MapController buildView(Camera cameraValue, TiledMapOptions options) {
        // mapCenter viene calcolata relativa alla tileMap Origin
        mapCenter.x = mapWidth / 2f;
        mapCenter.y = mapHeight / 2f;

        // TODO quello che segue relativo alla view è da mettere nel map handler (cambia in base al tipo di mappa)
        handler.onBuildView(view, cameraValue, options);

        Logger.info("XENON - -----------------------");
        Logger.info("XENON - distanceFromViewer %s ", view.distanceFromViewer);
        Logger.info("XENON - windowWidth %s windowHeight %s", view.windowWidth, view.windowHeight);
        Logger.info("XENON - mapWidth %s mapHeight %s", mapWidth, mapHeight);
        Logger.info("XENON - windowCenterX %s windowCenterY %s", view.windowCenter.x, view.windowCenter.y);
        Logger.info("XENON - windowTileRows %s windowTileColumns %s", view.windowTileRows, view.windowTileColumns);
        Logger.info("XENON - tileWidth %s tileHeight %s", tileWidth, tileHeight);
        Logger.info("XENON - -----------------------");


        // impostazioni di scroll
        scrollVerticalLocked = options.scrollVerticalLocked;
        scrollHorizontalLocked = options.scrollHorizontalLocked;

        // invochiamo l'evento onWindowCreate per tutti i layer
        int n = layers.size();
        for (int i = 0; i < n; i++) {
            layers.get(i).onBuildView(view);
        }

        // impostiamo il controller. Lo impostiamo qua perchè deve avere una tiledMap già pronta prima di poter
        // funzionare
        if (options.createController) {
            controller = handler.buildMapController(this, cameraValue);
        }

        position(options.startPosition);

        // creiamo lo shape per disegnare parti di layer.
        BufferAllocationOptions bufferOptions = BufferAllocationOptions.build().indexAllocation(BufferAllocationType.DYNAMIC).textureAllocation(BufferAllocationType.CLIENT).vertexAllocation(BufferAllocationType.CLIENT);
        spriteMesh = MeshFactory.createTiledGrid(tileWidth * view.windowTileColumns, tileHeight * view.windowTileRows, view.windowTileRows, view.windowTileColumns, MeshOptions.build().bufferAllocationOptions(bufferOptions));

        return controller;
    }

    /**
     * numero di texture usate. Il numero è ricavato dai vari tileSet e dagli imageLayer, i quali hanno un'immagine ad uso proprio.
     */
    public int texturesCount;

    /**
     * numero di texture massime usate in contemporanea in un layer
     */
    public int textureCounterMaxPerLayer;

    /**
     * Se true indica che c'è una texture per layer e non più di una.
     */
    public boolean onlyOneTexture4Layer;

    /**
     * handler necessario della mappa. La sua implementazione cambia in base al tipo di mappa
     */
    public final MapHandler handler;

    /**
     * <p>
     * Muove la posizione della mappa, in termini di posizione relativa. Lo spostamento viene
     * effettuato sul sistema della mappa.
     * </p>
     *
     * @param position
     */
    public void position(TiledMapPositionType position) {
        float x = 0;
        float y = 0;

        float msX = view.mapMaxPositionValueX;// (mapWidth - view.windowWidth);
        float msY = view.mapMaxPositionValueY;// (mapHeight - view.windowHeight);

        switch (position) {
            case LEFT_TOP:
                x = 0f;
                y = 0f;
                break;
            case LEFT_CENTER:
                x = 0f;
                y = 0f;
                if (msY > 0f)
                    y = msY / 2f;
                break;
            case LEFT_BOTTOM:
                x = 0f;
                y = 0f;
                if (msY > 0f)
                    y = msY;
                break;
            case MIDDLE_TOP:
                x = 0f;
                if (msX > 0f)
                    x = msX / 2f;
                y = 0;
                break;
            case MIDDLE_CENTER:
                x = 0f;
                if (msX > 0f)
                    x = msX / 2f;
                y = 0;
                if (msY > 0)
                    y = msY / 2f;
                break;
            case MIDDLE_BOTTOM:
                x = 0f;
                if (msX > 0f)
                    x = msX / 2f;
                y = 0;
                if (msY > 0f)
                    y = msY;
                break;
            case RIGHT_TOP:
                x = 0f;
                if (msX > 0f)
                    x = msX;
                y = 0f;
                break;
            case RIGHT_CENTER:
                x = 0f;
                if (msX > 0f)
                    x = msX;
                y = 0f;
                if (msY > 0f)
                    y = msY / 2f;
                break;
            case RIGHT_BOTTOM:
                x = 0f;
                if (msX > 0f)
                    x = msX;
                y = 0f;
                if (msY > 0f)
                    y = msY;
                break;
        }

        positionInMap.setCoords(x, y);
        for (int i = 0; i < layers.size(); i++) {
            layers.get(i).position(x, y);
        }

        // in caso di spostamento e di listener!=null mandiamo evento
        if (movementEventListener != null) {
            movementEventListener.onPosition(x, y);
        }

        resetScrollAreas();
    }

    /**
     * Inizializza la tile, caricando lo shader.
     *
     * @param context
     */
    protected void init(Context context) {
        onlyOneTexture4Layer = textureCounterMaxPerLayer == 1;
        shader = ShaderManager.instance().createShaderTiledMap(textureCounterMaxPerLayer == 1, ArgonShaderOptions.build().numberOfTextures(texturesCount));
        ObjDefinition.resetFrameMarker();
    }

    public void updateTime(long deltaTime) {
        TileAnimation animation;
        for (int i = 0; i < animations.size(); i++) {
            animation = animations.get(i);
            animation.update(deltaTime);
        }

    }

    /**
     * <p>
     * Cerca negli object layer un layer con il nome specificato.
     * </p>
     *
     * @param key
     * @return layer o null
     */
    public TiledLayer findLayer(String key) {
        int n = layers.size();
        for (int i = 0; i < n; i++) {
            Layer current = layers.get(i);

            if (key.equals(current.name) && current.type == LayerType.TILED) {
                return (TiledLayer) current;
            }
        }

        return null;
    }

    public ObjectLayer findtObjectGroup(String key) {
        int n = objectLayers.size();
        ObjectLayer current;
        for (int i = 0; i < n; i++) {
            current = objectLayers.get(i);

            if (key.equals(current.name)) {
                return (ObjectLayer) current;
            }

        }

        return null;
    }
}