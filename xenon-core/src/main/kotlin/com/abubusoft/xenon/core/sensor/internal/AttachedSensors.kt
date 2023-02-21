package com.abubusoft.xenon.core.sensor.internal

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Annotazione che consente di associare un particolare set di sensori ad un particolare SensorDetector.
 *
 * @author xcesco
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class AttachedSensors(vararg val value: Int)