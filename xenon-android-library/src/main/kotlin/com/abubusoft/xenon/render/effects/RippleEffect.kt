/**
 *
 */
package com.abubusoft.xenon.render.effects

import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.misc.NormalizedTimer
import com.abubusoft.xenon.misc.NormalizedTimer.TypeNormalizedTimer
import com.abubusoft.xenon.render.AbstractEffect
import com.abubusoft.xenon.render.UseShader

/**
 *
 * Effetto onda. Supporta fino a 8 touch.
 *
 * <table>
 * <tr>
 * <td><img width="196px" src="doc-files/original.png"></img></td>
 * <td><img width="196px" src="doc-files/effectRipple.png"></img></td>
</tr> *
</table> *
 *
 * I parametri:
 * <dl>
 * <dt>waveAmount</dt><dd>.Valore di default 80.0</dd>
 * <dt>waveDistortion</dt><dd>. Valore di default 30.0</dd>
 * <dt>waveSpeed</dt><dd>. Valore di default 5.0</dd>
</dl> *
 *
 *
 * @author Francesco Benincasa
 */
@UseShader(RippleShader::class)
class RippleEffect : AbstractEffect<RippleShader?>() {
    var waveAmount: Float
    var waveDistortion: Float
    var waveSpeed: Float

    /**
     * durata in secondi del touch
     */
    protected var touchDuration: Float
    fun setTouchDuration(value: Float) {
        touchDuration = value
        for (i in 0 until MAX_TOUCH) {
            touchTimer[i]!!.setMaxTime((touchDuration * NormalizedTimer.SECOND_IN_MILLISECONDS).toLong())
        }
    }

    fun getTouchDuration(): Float {
        return touchDuration
    }

    var touchTimer: Array<NormalizedTimer?>
    var touch: Array<Point3?>
    private var currentTouch = 0

    init {
        touch = arrayOfNulls(MAX_TOUCH)
        touchTimer = arrayOfNulls(MAX_TOUCH)
        touchDuration = 4f
        for (i in 0 until MAX_TOUCH) {
            touchTimer[i] = NormalizedTimer(TypeNormalizedTimer.ONE_TIME)
            touchTimer[i]!!.setMaxTime((touchDuration * NormalizedTimer.SECOND_IN_MILLISECONDS).toLong())
        }

        //waveAmount = 20f;
        //waveDistortion = 30f;
        //waveSpeed = 5.0f;
        waveAmount = 8.0f
        waveDistortion = 0.01f
        waveSpeed = 10.0f

        //rippleEffect.waveAmount=20.0f;		
        //rippleEffect.waveDistortion=1.0f;
//		rippleEffect.waveSpeed=10.0f;
    }

    fun addTouch(center: Point3?) {
        touch[currentTouch] = center
        touchTimer[currentTouch]!!.start()
        currentTouch = (currentTouch + 1) % MAX_TOUCH
    }

    protected override fun updateShader(shader: RippleShader, enlapsedTime: Long, speedAdapter: Float) {
        shader.setWaveAmount(waveAmount)
        shader.setWaveDistortion(waveDistortion)
        shader.setWaveSpeed(waveSpeed)
        for (i in 0 until MAX_TOUCH) {
            touchTimer[i]!!.update(enlapsedTime)
            shader.setTouch(i, touch[i])
            shader.setTouchEnabled(i, touchTimer[i]!!.isStarted)
            shader.setTouchTime(i, 2f * XenonMath.PI * touchTimer[i]!!.normalizedEnlapsedTime)
        }
    }

    companion object {
        const val MAX_TOUCH = 2
    }
}