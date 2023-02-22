/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 * @author Francesco Benincasa
 */
@UseShader(GreyscaleShader::class)
class GreyscaleEffect : AbstractEffect<GreyscaleShader?>() {
    protected override fun updateShader(shader: GreyscaleShader, enlapsedTime: Long, speedAdapter: Float) {
        // TODO Auto-generated method stub
    }
}