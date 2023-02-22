/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 *
 * http://www.gamerendering.com/2008/10/11/gaussian-blur-filter-shader/
 *
 * @author Francesco Benincasa
 */
@UseShader(BlurVerticalShader::class)
class BlurVerticalEffect : AbstractEffect<BlurVerticalShader?>() {
    protected override fun updateShader(shader: BlurVerticalShader, enlapsedTime: Long, speedAdapter: Float) {
        // TODO Auto-generated method stub
    }
}