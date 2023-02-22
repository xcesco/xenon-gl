package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.animations.TextureTimeline
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshFactory.TEXTURE_DIMENSION
import com.abubusoft.xenon.mesh.MeshSprite
import com.abubusoft.xenon.mesh.MeshTile
import com.abubusoft.xenon.texture.TextureRegion
import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.TextureBuffer

/**
 *
 *
 *
 *
 * @author Francesco Benincasa
 */
object TextureModifier {
    const val TEXTURE_INDEX_DEFAULT = 0

    /**
     * @param spriteShape
     * @param tile
     */
    fun setTextureCoords(spriteShape: MeshSprite, tile: TextureRegion?) {
        setTextureCoords(spriteShape, TEXTURE_INDEX_DEFAULT, tile)
    }

    /**
     * Imposta la tile come texture nella texutre [CURRENT_TEXTURE_INDEX]
     *
     * @param tile
     */
    fun setTextureCoords(spriteShape: MeshSprite, textureIndex: Int, tile: TextureRegion?) {
        var basePtr = 0
        // triangle 1
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_S] = tile!!.lowX
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_T] = tile.lowY
        basePtr += TEXTURE_DIMENSION
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_S] = tile.lowX
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_T] = tile.highY
        basePtr += TEXTURE_DIMENSION
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_S] = tile.highX
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_T] = tile.highY

        // triangle 2
        basePtr += TEXTURE_DIMENSION
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_S] = tile.highX
        spriteShape.textures[textureIndex].coords!![basePtr + TextureBuffer.OFFSET_T] = tile.lowY

        // vertici
        spriteShape.textures[textureIndex].update()
        //spriteShape.textures[textureIndex].put(spriteShape.texturesCoords[textureIndex]).position(0);
    }

    /**
     * @param shape
     * @param lowX
     * @param highX
     * @param lowY
     * @param highY
     */
    fun setTextureCoords(shape: MeshSprite, lowX: Float, highX: Float, lowY: Float, highY: Float) {
        setTextureCoords(shape, 0, lowX, highX, lowY, highY)
    }

    /**
     * Imposta le coordinate mediante l'animazione
     *
     */
    fun setTextureCoords(spriteShape: MeshSprite, animator: TextureTimeline) {
        setTextureCoords(spriteShape, animator.value()!!.textureRegion)
    }

    /**
     * Imposta le coordinate mediante l'animazione
     *
     */
    fun setTextureCoords(spriteShape: MeshSprite, textureIndex: Int, animator: TextureTimeline) {
        setTextureCoords(spriteShape, textureIndex, animator.value()!!.textureRegion)
    }

    fun setTextureCoords(shape: MeshSprite, textureIndex: Int, lowX: Float, highX: Float, lowY: Float, highY: Float) {
        val i = 0
        shape.textures[textureIndex].coords!![i + 0] = lowX
        shape.textures[textureIndex].coords!![i + 1] = lowY
        shape.textures[textureIndex].coords!![i + 2] = lowX
        shape.textures[textureIndex].coords!![i + 3] = highY
        shape.textures[textureIndex].coords!![i + 4] = highX
        shape.textures[textureIndex].coords!![i + 5] = highY
        shape.textures[textureIndex].coords!![i + 6] = highX
        shape.textures[textureIndex].coords!![i + 7] = lowY

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0
        //shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0);
        shape.textures[textureIndex].update()
    }

    /**
     *
     *
     * Imposta per uno sprite shape le texture coordinate per la texture #0,
     * ovvero la prima.
     *
     *
     * <pre>
     * ---------------+ U
     * |
     * |  U low, V low
     * |  +------------------+
     * |  |                  |
     * |  |                  |
     * |  |                  |
     * |  +------------------+ U high, V high
     * |
     * + V
    </pre> *
     *
     * @param shape
     * shape
     * @param lowU
     * lower U
     * @param highU
     * higher U
     * @param lowV
     * lower V
     * @param highV
     * higher V
     */
    fun setTextureCoords(shape: MeshTile, lowU: Float, highU: Float, lowV: Float, highV: Float) {
        setTextureCoords(shape, 0, lowU, highU, lowV, highV)
    }

    /**
     *
     *
     * Imposta per uno sprite shape le texture coordinate per la texture #0,
     * ovvero la prima.
     *
     *
     * <pre>
     * ---------------+ U
     * |
     * |  U low, V low
     * |  +------------------+
     * |  |                  |
     * |  |                  |
     * |  |                  |
     * |  +------------------+ U high, V high
     * |
     * + V
    </pre> *
     *
     * @param shape
     * shape
     * @param index
     * indice della texture
     * @param lowX
     * lower U
     * @param highX
     * higher U
     * @param lowY
     * lower V
     * @param highY
     * higher V
     */
    fun setTextureCoords(shape: MeshTile, index: Int, lowX: Float, highX: Float, lowY: Float, highY: Float) {
        val deltaX = (highX - lowX) / (shape.boundingBox.width / shape.tileWidth)
        var currentX = lowX
        val n = shape.textures[index].coords!!.size
        var i = 0
        while (i < n) {
            shape.textures[index].coords!![i + 0] = currentX
            shape.textures[index].coords!![i + 1] = lowY
            shape.textures[index].coords!![i + 2] = currentX
            shape.textures[index].coords!![i + 3] = highY
            shape.textures[index].coords!![i + 4] = currentX + deltaX
            shape.textures[index].coords!![i + 5] = highY

            // triangolo 2
            shape.textures[index].coords!![i + 6] = currentX + deltaX
            shape.textures[index].coords!![i + 7] = lowY
            currentX += deltaX
            i += TEXTURE_DIMENSION * AbstractBuffer.VERTEX_IN_QUAD_TILE
        }
    }

    /**
     * Dato uno sprite, provvede ad invertire le coordinate della texture
     * associata. Questo vuol dire, ad esempio, che se prima la texture va da 0
     * a 1 (in orizzontale), dopo l'applicazione di questo metodo andrà da 1 a
     * 0.
     *
     * @param shape
     * @param textureIndex
     * @param type
     */
    fun swapTextureCoords(shape: Mesh, textureIndex: Int, type: OperationVersus?) {
        //if (!shape.indexesEnabled) {
        val n = shape.textures[textureIndex].coords!!.size
        when (type) {
            OperationVersus.HORIZONTAL -> {
                var i = 0
                while (i < n) {
                    shape.textures[textureIndex].coords!![i + 0] = 1.0f - shape.textures[textureIndex].coords!![i + 0]
                    i += TEXTURE_DIMENSION
                }
            }
            OperationVersus.VERTICAL -> {
                var i = 0
                while (i < n) {
                    shape.textures[textureIndex].coords!![i + 1] = 1.0f - shape.textures[textureIndex].coords!![i + 1]
                    i += TEXTURE_DIMENSION
                }
            }
            else -> {}
        }

        // Quando passo un array ad un direct buffer devo poi riposizionare
        // a 0
        shape.textures[textureIndex].update()
        //shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0);
        /*} else {
			// TODO: da sistemare
			throw new XenonRuntimeException("swapTextureCoords not supported for indexed shape");
		}*/
    }

    fun changeAspectRatioTextureCoords(shape: Mesh, textureIndex: Int, newAspectRatio: Float, type: OperationVersus?, align: OperationAlign?) {
        if (!shape.indexesEnabled) {
            val n = shape.textures[textureIndex].coords!!.size
            when (type) {
                OperationVersus.VERTICAL -> {
                    var i = 0
                    while (i < n) {
                        shape.textures[textureIndex].coords!![i + 1] -= 0.5f * newAspectRatio
                        shape.textures[textureIndex].coords!![i + 1 + TEXTURE_DIMENSION] += 0.5f * newAspectRatio
                        i += 2 * TEXTURE_DIMENSION
                    }
                }
                OperationVersus.HORIZONTAL -> {
                    var i = 0
                    while (i < n / 2) {
                        shape.textures[textureIndex].coords!![i + 0] -= 0.5f * newAspectRatio
                        shape.textures[textureIndex].coords!![n - 1 - i] += 0.5f * newAspectRatio
                        i += TEXTURE_DIMENSION
                    }
                }
                else -> {}
            }

            // Quando passo un array ad un direct buffer devo poi riposizionare
            // a 0
            shape.textures[textureIndex].update() // .put(shape.textures[textureIndex]).position(0);
        } else {
            // TODO: da sistemare
            throw XenonRuntimeException("changeAspectRatioTextureCoords not supported for indexed shape")
        }
    }

    /**
     * Dato uno sprite, provvede ad invertire le coordinate della texture
     * associata (num 0). Questo vuol dire, ad esempio, che se prima la texture
     * va da 0 a 1 (in orizzontale), dopo l'applicazione di questo metodo andrà
     * da 1 a 0.
     *
     * @param shape
     * @param lowX
     * @param deltaX
     * @param lowY
     * @param highY
     */
    fun swapTextureCoords(shape: Mesh, type: OperationVersus?) {
        swapTextureCoords(shape, 0, type)
    }
    /**
     * TopLeft - BottomRight
     *
     * @param percX1
     * @param percY1
     * @param percX2
     * @param percY2
     */
    /*
	 * public void setTextureCoords(float percX1, float percY1, float percX2,
	 * float percY2) { // 0 textureCoords[0] = percX1; textureCoords[1] =
	 * percY1;
	 * 
	 * // 1 textureCoords[2] = percX2; textureCoords[3] = percY1;
	 * 
	 * // 2 textureCoords[4] = percX1; textureCoords[5] = percY2;
	 * 
	 * // 3 textureCoords[6] = percX2; textureCoords[7] = percY2; }
	 */
    /**
     * TopLeft - BottomRight
     *
     * @param percX1
     * @param percY1
     * @param percX2
     * @param percY2
     */
    /*
	 * public void setTextureCoords(Point2 point0, Point2 point1, Point2
	 * point2, Point2 point3) { // 0 textureCoords[0] = point0.x;
	 * textureCoords[1] = point0.y;
	 * 
	 * // 1 textureCoords[2] = point1.x; textureCoords[3] = point1.y;
	 * 
	 * // 2 textureCoords[4] = point2.x; textureCoords[5] = point2.y;
	 * 
	 * // 3 textureCoords[6] = point3.x; textureCoords[7] = point3.y; }
	 */
    /**
     * copia i valori della texture
     */
    /*
	 * public void applyChangesOnTexture() { textureBuffer.position(0);
	 * textureBuffer.put(textureCoords); textureBuffer.position(0); }
	 */
    /**
     * @param left
     */
    /*
	 * public void setTextureOrientation(boolean left) { textureCoords =
	 * textureCoordsRight; if (left) { textureCoords = textureCoordsToLeft; }
	 * 
	 * textureBuffer.position(0); textureBuffer.put(textureCoords);
	 * textureBuffer.position(0); }
	 */
    /**
     *
     *
     * Indica il verso sul quale operare.
     *
     *
     * @author Francesco Benincasa
     */
    enum class OperationVersus {
        HORIZONTAL, VERTICAL
    }

    enum class OperationAlign {
        CENTER
    }
}