package com.abubusoft.xenon.render

import com.abubusoft.xenon.shader.Shader
import kotlin.reflect.KClass

/**
 *
 * Usa lo shader passato come .
 * @author Francesco Benincasa
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class UseShader(
    /**
     *
     *
     * Indica il tipo di injection da inserire.
     *
     *
     * @return
     */
    val value: KClass<out Shader>
)