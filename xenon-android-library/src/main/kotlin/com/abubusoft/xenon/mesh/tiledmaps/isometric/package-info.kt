/**
 *
 * Questo package si occupa di gestire le mappe di tipo isometrico. Tralasciando
 * buona parte della teoria che si trova in rete:
 *
 * <image src="./doc-files/IsoOnScreen.png"></image>
 *
 *
 * La forma a rombo o diamante è tipica di questo tipo di mappe.
 *
 * In memoria la rappresentazione delle celle è la seguente:
 *
 * <image style="width: 100%" src="./doc-files/Map2Window.png"></image>
 *
 *
 * La cella (0,0) è posizionata nell'angolo superiore del diamante. La cella (0,1)
 * viene posizionata in basso a dx e così via.
 *
 *
 * Le dimensioni in memoria di una cella sono uguali, quindi abbiamo in memoria un quadrato. Sulla view,
 * a causa della proiezione isometrica, l'altezza viene dimezzata.
 *
 *
 * L'ordine di draw è basato sempre sulle righe (una riga alla volta).
 *
 * <h2>Adattamento allo schermo e costruzione della view</h2>
 *
 *
 * Per ovviare agli spazi vuoti che la visualizzazione a diamante comporta, si è deciso di
 * utilizzare una mask da riempire da una texture a piacimento.
 *
 *
 * Inoltre, si è deciso di rendere determinare automaticamente il fill screen semplicemente in
 * base all'orientamento dello schermo e non di quanto scritto nelle opzioni.
 *
 * <img style="width: 50%" src="./doc-files/2016-02-13 10.55.45.jpg"></img>
 *
 *
 * L'immagine di sopra sono degli appunti che ho scritto a riguardo.
 *
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.tiledmaps.isometric

import com.abubusoft.xenon.math.Point2.mul
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier.setVertexCoords
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier.setVertexAttributes2
import com.abubusoft.xenon.math.Point2.add
import com.abubusoft.xenon.math.Point2.div
import com.abubusoft.xenon.math.Point2.mod
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler.view
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier.setTextureCoords
import com.abubusoft.xenon.math.Matrix4x4.buildIdentityMatrix
import com.abubusoft.xenon.math.Matrix4x4.multiply
import com.abubusoft.xenon.shader.ShaderTiledMap.setOpacity
import com.abubusoft.xenon.shader.Shader.setVertexCoordinatesArray
import com.abubusoft.xenon.shader.Shader.setTextureCoordinatesArray
import com.abubusoft.xenon.shader.Shader.setModelViewProjectionMatrix
import com.abubusoft.xenon.math.Matrix4x4.asFloatBuffer
import com.abubusoft.xenon.shader.Shader.setIndexBuffer
import com.abubusoft.xenon.shader.Shader.unsetIndexBuffer
import com.abubusoft.xenon.math.Point2.set
import com.abubusoft.xenon.math.Point2.setCoords
import com.abubusoft.xenon.math.XenonMath.clamp
import com.abubusoft.xenon.mesh.MeshFactory.loadFromResources
import com.abubusoft.xenon.context.XenonBeanContext.context
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractMapHandler.draw
import com.abubusoft.xenon.shader.Shader.use
import com.abubusoft.xenon.shader.Shader.setTexture
import com.abubusoft.xenon.shader.drawers.ShaderDrawer.draw
import com.abubusoft.xenon.math.XenonMath.zDistanceForSquare
import com.abubusoft.xenon.vbo.VertexBuffer.update
import com.abubusoft.xenon.math.Matrix4x4.scale
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder.setOffset
import com.abubusoft.xenon.math.Matrix4x4.build
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler.setTextureSelector
import com.abubusoft.xenon.math.Matrix4x4.buildTranslationMatrix
import com.abubusoft.xenon.shader.ShaderTiledMap.setTextureSelectorArray
import com.abubusoft.xenon.vbo.AbstractBuffer.cursorReset
import com.abubusoft.xenon.mesh.modifiers.IndexQuadModifier.setIndexes
import com.abubusoft.xenon.vbo.AbstractBuffer.cursorMove
import com.abubusoft.xenon.vbo.IndexBuffer.update
import com.abubusoft.xenon.vbo.TextureBuffer.update
import com.abubusoft.xenon.vbo.AttributeBuffer.update
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler.onBuildView
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricMapController
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricHelper
import com.abubusoft.xenon.vbo.VertexBuffer
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.mesh.modifiers.VertexQuadModifier
import com.abubusoft.xenon.vbo.AttributeBuffer
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType
import com.abubusoft.xenon.mesh.modifiers.AttributeQuadModifier
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricMapHandler
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer.FillModeType
import com.abubusoft.xenon.mesh.modifiers.TextureQuadModifier
import android.opengl.GLES20
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractMapHandler
import com.abubusoft.xenon.shader.ShaderTexture
import com.abubusoft.xenon.mesh.MeshFactory
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.R
import com.abubusoft.xenon.mesh.MeshFileFormatType
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.texture.TextureManager
import com.abubusoft.xenon.texture.TextureOptions
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.shader.drawers.ShaderDrawer
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapOptions
import com.abubusoft.xenon.ScreenInfo
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapFillScreenType
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricTiledLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricObjectLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.isometric.IsometricImageLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass
import com.abubusoft.xenon.mesh.MeshGrid
import com.abubusoft.xenon.vbo.IndexBuffer
import com.abubusoft.xenon.mesh.modifiers.IndexQuadModifier
import com.abubusoft.xenon.mesh.MeshDrawModeType
