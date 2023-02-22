/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 * @author Francesco Benincasa
 */
@UseShader(ScanlinesShader::class)
class ScanlinesEffect : AbstractEffect<ScanlinesShader?>() {
    protected override fun updateShader(shader: ScanlinesShader, enlapsedTime: Long, speedAdapter: Float) {
        // TODO Auto-generated method stub
    }
}