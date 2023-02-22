/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.misc.NormalizedTimer
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer
import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 * @author Francesco Benincasa
 */
@UseShader(WaveShader::class)
class WaveEffect : AbstractEffect<WaveShader?>() {
    var waveAmount: Float
    var waveDistortion: Float

    /**
     * Speed. Maggiore è la velocità e più le onde si muoveranno velocemente.
     * da 1 .. +n.
     */
    var waveSpeed: Float
    var timer: NormalizedTimer

    init {
        timer = NormalizedTimer(TypeNormalizedTimer.REPEAT_FOREVER)
        timer.setMaxTime((4f * NormalizedTimer.SECOND_IN_MILLISECONDS).toLong())
        timer.start()
        waveAmount = 20f
        waveDistortion = 0.005f
        waveSpeed = 4f
    }

    /* (non-Javadoc)
	 * @see com.abubusoft.xenon.render.AbstractEffect#updateShader(com.abubusoft.xenon.shader.Shader, long, float)
	 */
    protected override fun updateShader(shader: WaveShader, enlapsedTime: Long, speedAdapter: Float) {
        shader.setWaveAmount(waveAmount)
        shader.setWaveDistortion(waveDistortion)
        shader.setWaveSpeed(waveSpeed)
        timer.update(enlapsedTime)
        shader.setTime(2f * XenonMath.PI * timer.normalizedEnlapsedTime)
    }
}