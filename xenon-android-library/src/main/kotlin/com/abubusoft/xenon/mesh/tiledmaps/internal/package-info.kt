/**
 * Gestione interna delle mappe. In questo package sono messe tutte le classi che servono alla gestione delle mappe
 * ma che non sono esposte volutamente verso il client, dato che non vi accede direttamente.
 *
 * @author xcesco
 */
package com.abubusoft.xenon.mesh.tiledmaps.internal

import com.abubusoft.xenon.shader.Shader.use
import com.abubusoft.xenon.shader.Shader.setTexture
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledMapView
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.AbstractMapController
import com.abubusoft.xenon.mesh.tiledmaps.TiledMap
import com.abubusoft.xenon.mesh.tiledmaps.internal.MapHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerOffsetHolder
import android.opengl.GLES20
import com.abubusoft.xenon.mesh.tiledmaps.ObjDefinition
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.mesh.tiledmaps.ImageLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.AbstractLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.internal.LayerDrawer
import com.abubusoft.xenon.math.Point2
import com.abubusoft.xenon.mesh.tiledmaps.TiledMapOptions
import com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers.MapController
import com.abubusoft.xenon.mesh.tiledmaps.internal.TiledLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.TiledLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.ObjectLayerHandler
import com.abubusoft.xenon.mesh.tiledmaps.ObjectLayer
import com.abubusoft.xenon.mesh.tiledmaps.internal.ImageLayerHandler
import com.abubusoft.xenon.vbo.VertexBuffer
import com.abubusoft.xenon.vbo.TextureBuffer
import com.abubusoft.xenon.vbo.IndexBuffer
import com.abubusoft.xenon.vbo.BufferManager
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass
