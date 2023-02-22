/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 *
 * @author Francesco Benincasa
 */
@UseShader(PixelateShader::class)
class PixelateEffect : AbstractEffect<PixelateShader?>() {
    var pixelAmount = 200f
    protected override fun updateShader(shader: PixelateShader, enlapsedTime: Long, speedAdapter: Float) {
        shader.setPixelAmount(pixelAmount)
    }
}