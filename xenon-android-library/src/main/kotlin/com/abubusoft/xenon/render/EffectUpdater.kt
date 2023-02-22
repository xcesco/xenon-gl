package com.abubusoft.xenon.render

import com.abubusoft.xenon.shader.Shader

/**
 *
 *
 * @author Francesco Benincasa
 *
 * @param <P>
 * @param <E>
</E></P> */
interface EffectUpdater<P : AbstractEffect<E>, E : Shader> {
    /**
     * @param effect
     * @param shader
     * @param enlapsedTime
     * @param speedAdapter
     */
    fun update(effect: P, shader: E, enlapsedTime: Long, speedAdapter: Float)
}