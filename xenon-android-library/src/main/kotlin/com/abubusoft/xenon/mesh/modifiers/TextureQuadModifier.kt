package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.mesh.tiledmaps.Tile
import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.TextureBuffer
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 *
 *
 *
 *
 * @author Francesco Benincasa
 */
object TextureQuadModifier {
    /**
     * @param spriteShape
     * @param tile
     */
    /*
	 * public static void setTextureCoords(MeshSprite spriteShape, Tile tile) { setTextureCoords(spriteShape, TEXTURE_INDEX_DEFAULT, tile); }
	 */
    /**
     * Imposta la tile come texture nella texutre [CURRENT_TEXTURE_INDEX]
     *
     * @param tile
     */
    /*
	 * public static void setTextureCoords(MeshSprite spriteShape, int textureIndex, Tile tile) { int basePtr = 0;
	 * 
	 * spriteShape.texturesCoords[textureIndex][SPRITE_SHAPE_OFFSET_X] = tile.textureLowX; spriteShape.texturesCoords[textureIndex][SPRITE_SHAPE_OFFSET_Y] = tile.textureLowY;
	 * 
	 * basePtr += SPRITE_SHAPE_TEXTURE_DIMENSIONS; spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_X] = tile.textureLowX;
	 * spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_Y] = tile.textureHighY;
	 * 
	 * basePtr += SPRITE_SHAPE_TEXTURE_DIMENSIONS; spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_X] = tile.textureHighX;
	 * spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_Y] = tile.textureHighY;
	 * 
	 * basePtr += SPRITE_SHAPE_TEXTURE_DIMENSIONS; spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_X] = tile.textureHighX;
	 * spriteShape.texturesCoords[textureIndex][basePtr + SPRITE_SHAPE_OFFSET_Y] = tile.textureLowY;
	 * 
	 * // vertici spriteShape.textures[textureIndex].put(spriteShape.texturesCoords[textureIndex]).position(0); }
	 */
    /**
     * @param shape
     * @param lowX
     * @param highX
     * @param lowY
     * @param highY
     */
    /*
	 * public static void setTextureCoords(QuadMesh shape, float lowX, float highX, float lowY, float highY) { setTextureCoords(shape, 0, lowX, highX, lowY, highY); }
	 */
    /**
     * Imposta le coordinate mediante l'animazione
     *
     * @param timeline
     */
    /*
	 * public static void setTextureCoords(MeshSprite spriteShape, TextureTimeline timeline) { TexturedAnimationFrame currentValue = timeline.getCurrentFrame();
	 * setTextureCoords(spriteShape, currentValue.frame); }
	 */
    /**
     * Imposta le coordinate mediante l'animazione
     *
     * @param timeline
     */
    /*
	 * public static void setTextureCoords(MeshSprite spriteShape, int textureIndex, TextureTimeline timeline) { TexturedAnimationFrame currentValue = timeline.getCurrentFrame();
	 * setTextureCoords(spriteShape, textureIndex, currentValue.frame); }
	 * 
	 * public static void setTextureCoords(QuadMesh shape, int textureIndex, float lowX, float highX, float lowY, float highY) {
	 * 
	 * int i = 0; shape.texturesCoords[textureIndex][i + 0] = lowX; shape.texturesCoords[textureIndex][i + 1] = lowY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 2] = lowX; shape.texturesCoords[textureIndex][i + 3] = highY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 4] = highX; shape.texturesCoords[textureIndex][i + 5] = highY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 6] = highX; shape.texturesCoords[textureIndex][i + 7] = lowY;
	 * 
	 * // Quando passo un array ad un direct buffer devo poi riposizionare a 0 shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0); }
	 */
    /**
     *
     *
     * Imposta per uno sprite shape le texture coordinate per la texture #0, ovvero la prima.
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
    /*
	 * public static void setTextureCoords(MeshTile shape, float lowU, float highU, float lowV, float highV) { setTextureCoords(shape, 0, lowU, highU, lowV, highV); }
	 */
    /**
     *
     *
     * Imposta per uno sprite shape le texture coordinate per la texture #0, ovvero la prima.
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
     * @param textureIndex
     * indice della texture
     * @param lowU
     * lower U
     * @param highU
     * higher U
     * @param lowV
     * lower V
     * @param highV
     * higher V
     */
    /*
	 * public static void setTextureCoords(MeshTile shape, int textureIndex, float lowX, float highX, float lowY, float highY) {
	 * 
	 * float deltaX = (highX - lowX) / (shape.boundingBox.width / shape.tileWidth); float currentX = lowX; int n = shape.texturesCoords[textureIndex].length;
	 * 
	 * for (int i = 0; i < n; i += XmlDataModel.TEXTURE_DIMENSIONS * VertexBuffer.VERTEX_IN_QUAD_TILE) { shape.texturesCoords[textureIndex][i + 0] = currentX;
	 * shape.texturesCoords[textureIndex][i + 1] = lowY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 2] = currentX; shape.texturesCoords[textureIndex][i + 3] = highY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 4] = currentX + deltaX; shape.texturesCoords[textureIndex][i + 5] = highY;
	 * 
	 * shape.texturesCoords[textureIndex][i + 8] = currentX + deltaX; shape.texturesCoords[textureIndex][i + 9] = lowY;
	 * 
	 * currentX += deltaX; }
	 * 
	 * // Quando passo un array ad un direct buffer devo poi riposizionare a 0 shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0); }
	 */
    /**
     *
     *
     * Indica il verso sul quale operare.
     *
     *
     * @author Francesco Benincasa
     */
    /*
	 * public enum OperationVersus { HORIZONTAL, VERTICAL };
	 * 
	 * public enum OperationAlign { CENTER }
	 */
    /**
     * Dato uno sprite, provvede ad invertire le coordinate della texture associata. Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale), dopo
     * l'applicazione di questo metodo andrà da 1 a 0.
     *
     * @param shape
     * @param lowX
     * @param deltaX
     * @param lowY
     * @param highY
     */
    /*
	 * public static void swapTextureCoords(XmlDataModel shape, int textureIndex, OperationVersus allocation, boolean update) { if (!shape.indexesEnabled) { int n =
	 * shape.texturesCoords[textureIndex].length; switch (allocation) { case HORIZONTAL: for (int i = 0; i < n; i += TEXTURE_DIMENSION) { shape.texturesCoords[textureIndex][i + 0] = 1.0f
	 * - shape.texturesCoords[textureIndex][i + 0]; } break; case VERTICAL: for (int i = 0; i < n; i += TEXTURE_DIMENSION) { shape.texturesCoords[textureIndex][i + 1] = 1.0f -
	 * shape.texturesCoords[textureIndex][i + 1]; } break;
	 * 
	 * }
	 * 
	 * // Quando passo un array ad un direct buffer devo poi riposizionare // a 0 shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0); } else { //
	 * TODO: da sistemare throw new XenonRuntimeException("swapTextureCoords not supported for indexed shape"); } }
	 * 
	 * public static void changeAspectRatioTextureCoords(XmlDataModel shape, int textureIndex, float newAspectRatio, OperationVersus allocation, OperationAlign align, boolean update) { if
	 * (!shape.indexesEnabled) { int n = shape.texturesCoords[textureIndex].length; switch (allocation) { case VERTICAL: for (int i = 0; i < n; i += 2 * TEXTURE_DIMENSION) {
	 * shape.texturesCoords[textureIndex][i + 1] -= 0.5f * newAspectRatio; shape.texturesCoords[textureIndex][i + 1 + TEXTURE_DIMENSION] += 0.5f * newAspectRatio; } break; case
	 * HORIZONTAL: for (int i = 0; i < n / 2; i += TEXTURE_DIMENSION) { shape.texturesCoords[textureIndex][i + 0] -= 0.5f * newAspectRatio; shape.texturesCoords[textureIndex][n - 1
	 * - i] += 0.5f * newAspectRatio; } break;
	 * 
	 * }
	 * 
	 * if (update) { // Quando passo un array ad un direct buffer devo poi riposizionare // a 0 shape.textures[textureIndex].put(shape.texturesCoords[textureIndex]).position(0); }
	 * 
	 * } else { // TODO: da sistemare throw new XenonRuntimeException("changeAspectRatioTextureCoords not supported for indexed shape"); } }
	 */
    /**
     * Dato uno sprite, provvede ad invertire le coordinate della texture associata (num 0). Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale),
     * dopo l'applicazione di questo metodo andrà da 1 a 0.
     *
     * @param shape
     * @param lowX
     * @param deltaX
     * @param lowY
     * @param highY
     */
    /*
	 * public static void swapTextureCoords(XmlDataModel shape, OperationVersus allocation, boolean update) { swapTextureCoords(shape, 0, allocation, update); }
	 */
    /**
     *
     *
     * Imposta le coordinate texture della tile attualmente sotto il cursore.
     *
     *
     * <pre>
     *
     * 2--- 3       4
     * |   /       /|
     * |  /       / |
     * | /       /  |
     * |/       /   |
     * 1       6----5
     *
    </pre> *
     *
     *
     *
     * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
     *
     *
     *
     *
     * In caso di diagonal flip, questo metodo provvede a cambiare le coordinate in modo opportuno. Da ricordare che questa operazione non può essere fatta prima, ma solo nella
     * definizione delle coordinate uv.
     *
     *
     * @param buffer
     * buffer a quad da inserire.
     * @param current
     * indice del quad da aggiornare
     * @param tile
     * tile di partenza da
     * @param update
     * se true provvede ad aggiornare immediatamente il buffer.
     */
    fun setTextureCoords(
        buffer: TextureBuffer,
        current: Int,
        textureLowX: Float,
        textureHighX: Float,
        textureLowY: Float,
        textureHighY: Float,
        diagonalFlip: Boolean,
        update: Boolean
    ) {
        // cursor rappresenta il numero di vertici. Per ogni vertice ci sono 2
        // coordinate per le texture
        var basePtr: Int = current * AbstractBuffer.VERTEX_IN_QUAD_TILE * TextureBuffer.TEXTURE_DIMENSIONS
        if (!diagonalFlip) {
            buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureLowX
            buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureLowY
        } else {
            buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureHighX
            buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureHighY
        }
        basePtr += TextureBuffer.TEXTURE_DIMENSIONS
        buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureLowX
        buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureHighY
        if (!diagonalFlip) {
            basePtr += TextureBuffer.TEXTURE_DIMENSIONS
            buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureHighX
            buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureHighY
        } else {
            basePtr += TextureBuffer.TEXTURE_DIMENSIONS
            buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureLowX
            buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureLowY
        }
        basePtr += TextureBuffer.TEXTURE_DIMENSIONS
        buffer.coords!![basePtr + TextureBuffer.OFFSET_S] = textureHighX
        buffer.coords!![basePtr + TextureBuffer.OFFSET_T] = textureLowY
        if (update) {
            buffer.update()
        }
    }

    /**
     *
     *
     * Imposta le coordinate texture della tile attualmente sotto il cursore.
     *
     *
     * <pre>
     *
     * 2--- 3       4
     * |   /       /|
     * |  /       / |
     * | /       /  |
     * |/       /   |
     * 1       6----5
     *
    </pre> *
     *
     *
     *
     * Il controllo validità del tile si presume essere stato fatto prima di invocare questo metodo.
     *
     *
     *
     *
     * In caso di diagonal flip, questo metodo provvede a cambiare le coordinate in modo opportuno. Da ricordare che questa operazione non può essere fatta prima, ma solo nella
     * definizione delle coordinate uv.
     *
     *
     * @param buffer
     * buffer a quad da inserire.
     * @param current
     * indice del quad da aggiornare
     * @param tile
     * tile di partenza da
     * @param update
     * se true provvede ad aggiornare immediatamente il buffer.
     */
    fun setTextureCoords(buffer: TextureBuffer, current: Int, tile: Tile, update: Boolean) {
        setTextureCoords(buffer, current, tile.lowX, tile.highX, tile.lowY, tile.highY, tile.diagonalFlip, update)
    }
}