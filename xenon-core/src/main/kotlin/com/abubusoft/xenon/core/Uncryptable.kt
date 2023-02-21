/**
 *
 */
package com.abubusoft.xenon.core

/**
 * Interfaccia che serve a marcare come non cryptabili (con proguard)
 * @author Francesco Benincasa
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Uncryptable 