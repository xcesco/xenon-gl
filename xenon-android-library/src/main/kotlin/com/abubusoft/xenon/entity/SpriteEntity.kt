/**
 *
 */
package com.abubusoft.xenon.entity

import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.mesh.MeshSprite

/**
 * Entità associata ad uno sprite.
 *
 * @author Francesco Benincasa
 */
class SpriteEntity : Entity<MeshSprite?>() {
    override val boundingRadius: Float
        get() = XenonMath.max(mesh!!.boundingBox.width, mesh!!.boundingBox.height) / 2.0f

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.core.util.Copy#copy()
	 */
    override fun copy(): SpriteEntity? {
        var copy: SpriteEntity? = null
        try {
            // cloniamo
            copy = javaClass.newInstance()
            copyInto(copy)
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        return copy
    }

    /**
     * Effettua la copia nell'oggetto destinazione
     *
     * @param destination
     */
    fun copyInto(destination: SpriteEntity?) {
        super.copyInto(destination)
    }

    companion object {
        private const val serialVersionUID = -7603400179112407353L
    }
}