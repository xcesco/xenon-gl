package com.abubusoft.xenon

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 *
 *
 * Questa annotazione indica che la classe contiene delle annotazioni injection
 * che consentono di iniettare alcuni oggetti dentro.
 *
 *
 * @author Francesco Benincasa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class XenonBean 