/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 * @author Francesco Benincasa
 */
@UseShader(LedShader::class)
class LedEffect : AbstractEffect<LedShader?>() {
    var ledSize = 128.0f
    var brightness = 1.0f

    init {
        ledSize = 128.0f
        brightness = 1.0f
    }

    protected override fun updateShader(shader: LedShader, enlapsedTime: Long, speedAdapter: Float) {
        shader.setLedSize(ledSize)
        shader.setBrightness(brightness)
    }
}