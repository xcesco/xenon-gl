package com.abubusoft.xenon.context

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 * Effettua l'inject del campo con l'entità argon specificata.
 * @author Francesco Benincasa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class XenonBeanInject(
    /**
     *
     *
     * Indica il tipo di injection da inserire.
     *
     *
     * @return
     */
    val value: XenonBeanType
)