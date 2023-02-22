/**
 *
 */
package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.math.Vector3

/**
 * @author Francesco Benincasa
 */
@BindType
class TranslationFrame : KeyFrame() {
    @Bind
    var translation = Vector3()

    companion object {
        fun build(duration: Long): TranslationFrame {
            return build(0f, 0f, 0f, duration)
        }

        fun build(x: Float, y: Float, z: Float, duration: Long): TranslationFrame {
            val frame = TranslationFrame()
            frame.translation.setCoords(x, y, z)
            frame.duration = duration
            return frame
        }
    }
}