/**
 *
 */
package com.abubusoft.xenon.core

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Interfaccia che serve a marcare come non cryptabili (con proguard)
 * @author Francesco Benincasa
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Uncryptable 