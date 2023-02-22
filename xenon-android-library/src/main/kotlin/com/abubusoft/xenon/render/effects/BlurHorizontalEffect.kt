/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 *
 * https://github.com/yulu/GLtext/blob/master/res/raw/edge_detect_fragment_shader.glsl
 *
 * @author Francesco Benincasa
 */
@UseShader(BlurHorizontalShader::class)
class BlurHorizontalEffect : AbstractEffect<BlurHorizontalShader>() {
    protected override fun updateShader(shader: BlurHorizontalShader, enlapsedTime: Long, speedAdapter: Float) {
        // TODO Auto-generated method stub
    }
}