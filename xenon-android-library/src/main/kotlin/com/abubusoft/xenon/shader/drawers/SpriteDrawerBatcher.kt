package com.abubusoft.xenon.shader.drawers

import android.opengl.GLES20
import com.abubusoft.xenon.animations.TextureAnimationHandler
import com.abubusoft.xenon.animations.TextureKeyFrame
import com.abubusoft.xenon.animations.TextureTimeline
import com.abubusoft.xenon.animations.TiledMapTimeline
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.mesh.MeshSprite
import com.abubusoft.xenon.mesh.modifiers.TextureModifier
import com.abubusoft.xenon.mesh.tiledmaps.Layer
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.texture.Texture
import com.abubusoft.xenon.texture.TextureRegion
import com.abubusoft.xenon.vbo.BufferAllocationType

/**
 * Consente di disegnare degli sprite in modo ottimizzato. Lo shader è sempre lo stesso. Se i vari parametri non vengono cambiati, il drawer evita di cambiarli.
 *
 * @author Francesco Benincasa
 */
class SpriteDrawerBatcher {
    /**
     * Usa di default uno shader del texturedAnimatorManager
     */
    constructor() {
        defaultShader = ShaderManager.instance().createShaderTexture()
    }

    /**
     * Usa di default uno shader del texturedAnimatorManager
     *
     * @param shaderValue
     */
    constructor(shaderValue: Shader) {
        defaultShader = shaderValue
    }

    /**
     * Imposta lo shader corrente
     *
     * @param shaderValue
     */
    fun setShader(shaderValue: Shader) {
        defaultShader = shaderValue
    }

    enum class DrawType {
        ANIMATED_SPRITE, STATIC_SPRITE, TILED_LAYER
    }

    var lastDrawType: DrawType? = null

    /**
     * shader corrente
     */
    var currentShader: Shader? = null

    /**
     * shader default
     */
    var defaultShader: Shader

    /**
     * texture atlas attualmente usata
     */
    var lastLayer: Layer? = null

    /**
     * ultima texture usata
     */
    var lastTexture: Texture? = null

    /**
     * texture usata adesso
     */
    var currentTexture: Texture? = null

    /**
     * ultimo frame usato
     */
    var lastTextureRegion: TextureRegion? = null

    /**
     * frame usato adesso
     */
    var currentTextureRegion: TextureRegion? = null

    /**
     * ultimo shape usato
     */
    var lastUsedMesh: MeshSprite? = null
    /**
     * Inizializza batch
     */
    /**
     * Inizializza batch
     */
    @JvmOverloads
    fun begin(shader: Shader = defaultShader) {
        defaultShader = shader
        currentShader = defaultShader
        currentShader!!.use()
        lastTexture = null
        lastUsedMesh = null
        lastTextureRegion = null
    }

    /**
     *
     *
     * @param clazz
     * @param matrixModelViewProjection
     * la matrice normalmente posizione il centro di origine ad una posizione z in modo tale da rendere visibile tutta l'area desiderata.f
     */
    fun draw(clazz: ObjClass, matrixModelViewProjection: Matrix4x4?) {
        resetIfNot(DrawType.TILED_LAYER)
        useShader(clazz.shapeLayer.tiledMap.shader)
        if (clazz.shapeLayer !== lastLayer) {
            for (i in clazz.shapeLayer.textureList.indices) {
                clazz.shapeLayer.tiledMap.shader.setTexture(i, clazz.shapeLayer.textureList[i])
            }
            lastLayer = clazz.shapeLayer
        }
        clazz.shapeLayer.handler.drawLayerPart(clazz.shapeLayer.tiledMap.shader, 0, clazz, matrixModelViewProjection)
    }

    /**
     *
     *
     * Se lo shader proposto è diverso da quello corrente, viene impostato a corrente e viene invocato il metodo use.
     *
     *
     * @param shaderNew
     */
    private fun useShader(shaderNew: Shader) {
        if (shaderNew != currentShader) {
            currentShader = shaderNew
            shaderNew.use()
        }
    }

    /**
     * disegna uno sprite prendendo la texture e le coordinate da una timeline
     *
     * @param mesh
     * @param timeline
     * @param matrixModelViewProjection
     */
    fun draw(mesh: MeshSprite, timeline: TiledMapTimeline, matrixModelViewProjection: Matrix4x4) {
        draw(mesh, timeline.handler!!.value1(), matrixModelViewProjection)
    }

    fun draw(mesh: MeshSprite, handler: TextureAnimationHandler, matrixModelViewProjection: Matrix4x4) {
        draw(mesh, handler.currentFrame, matrixModelViewProjection)
    }

    fun draw(mesh: MeshSprite, timeline: TextureTimeline, matrixModelViewProjection: Matrix4x4) {
        draw(mesh, timeline.value(), matrixModelViewProjection)
    }

    /**
     * Disegna in modalità batch uno sprite.
     *
     * @param mesh
     * @param timeline
     * @param matrixModelViewProjection
     */
    private fun draw(mesh: MeshSprite, frame: TextureKeyFrame?, matrixModelViewProjection: Matrix4x4) {
        resetIfNot(DrawType.ANIMATED_SPRITE)
        useShader(defaultShader)

        // shape
        if (mesh !== lastUsedMesh) {
            currentShader!!.setVertexCoordinatesArray(mesh.vertices)
            lastUsedMesh = mesh
        }

        // texture coordinate (indice 0)
        currentTextureRegion = frame!!.textureRegion
        if (currentTextureRegion !== lastTextureRegion) {
            TextureModifier.setTextureCoords(mesh, currentTextureRegion)
            // shape.setTextureCoords(currentValue.frame);
            currentShader!!.setTextureCoordinatesArray(0, mesh.textures[0])
            lastTextureRegion = currentTextureRegion
        }

        // texture binder
        currentTexture = frame.texture
        if (currentTexture !== lastTexture) {
            currentShader!!.setTexture(0, currentTexture)
            lastTexture = currentTexture
        }

        // matrice di proiezione
        currentShader!!.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer())
        if (mesh.indexes.allocation === BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(mesh.drawMode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexes.buffer)
        } else {
            currentShader!!.setIndexBuffer(mesh.indexes)
            GLES20.glDrawElements(mesh.drawMode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0)
            currentShader!!.unsetIndexBuffer(mesh.indexes)
        }
    }

    /**
     *
     *
     * Disegna uno sprite statico.
     *
     *
     *
     *
     * Imposta:
     * <dl>
     * <dt>vertex</dt>
     * <dd>vertice dei buffer</dd>
     * <dt>texture</dt>
     * <dd>texture 0</dd>
     * <dt>texture coordinate 0</dt>
     * <dd>coordinate texture 0</dd>
     * <dt>matrice mvp</dt>
     * <dd>matrice mvp</dd>
    </dl> *
     *
     *
     *
     * @param shape
     * @param texture
     * @param matrixModelViewProjection
     */
    fun draw(shape: MeshSprite, texture: Texture, matrixModelViewProjection: Matrix4x4) {
        resetIfNot(DrawType.STATIC_SPRITE)
        useShader(defaultShader)

        // shape
        if (shape !== lastUsedMesh) {
            currentShader!!.setVertexCoordinatesArray(shape.vertices)
            lastUsedMesh = shape
        }

        // texture binder
        if (texture !== lastTexture) {
            currentShader!!.setTexture(0, texture)
            lastTexture = texture // at.index;
        }

        // texture coordinate (default 0)
        currentShader!!.setTextureCoordinatesArray(0, shape.textures[0])

        // matrice di proiezione
        currentShader!!.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer())
        if (shape.indexes.allocation === BufferAllocationType.CLIENT) {
            GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, shape.indexes.buffer)
        } else {
            currentShader!!.setIndexBuffer(shape.indexes)
            GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0)
            currentShader!!.unsetIndexBuffer(shape.indexes)
        }
    }

    /**
     *
     *
     * Cancella i dati se il currentDrawType è diverso da quello corrente.
     *
     *
     * @param currentDrawType
     */
    private fun resetIfNot(currentDrawType: DrawType) {
        if (currentDrawType != lastDrawType) {
            currentTexture = null
            currentTextureRegion = null
            lastTexture = null
            lastTextureRegion = null
            lastUsedMesh = null
            lastLayer = null
        }
        lastDrawType = currentDrawType
    }

    /**
     * termina il current shader
     */
    fun end() {
        // currentShader.close();
        currentShader = null
    }
}