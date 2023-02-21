package com.abubusoft.xenon.core.sensor.compass

import java.util.*

/**
 * Gestore per la media di una serie di angoli espressi in forma di radianti.
 * Questo usa la trigonetria per gestire ad esempio quei casi in cui si oltrepassa il limite:
 * ad esempio 360 + 40 non fa 200 di media. Con la trigonometria si oltrepassa questo limite.
 *
 * @author xcesco
 */
class AngleLowpassFilter {
    /**
     * numero di valori con cui elaborare la media
     */
    private val LENGTH = 10
    private var sumSin = 0f
    private var sumCos = 0f

    /**
     * coda di valori
     */
    private val queue = ArrayDeque<Float>()

    /**
     * aggiunge un valore alla media
     *
     * @param radians
     */
    fun add(radians: Float) {
        sumSin += Math.sin(radians.toDouble()).toFloat()
        sumCos += Math.cos(radians.toDouble()).toFloat()
        queue.add(radians)
        if (queue.size > LENGTH) {
            val old = queue.poll()
            sumSin -= Math.sin(old.toDouble()).toFloat()
            sumCos -= Math.cos(old.toDouble()).toFloat()
        }
    }

    /**
     * calcola la media dei valori calcolati finora
     *
     * @return
     * media
     */
    fun average(): Float {
        val size = queue.size
        return Math.atan2((sumSin / size).toDouble(), (sumCos / size).toDouble()).toFloat()
    }

    /**
     * aggiunge un valore e restituisce la media
     *
     * @param radians
     * nuovo valore da sommare alla media
     * @return
     * media
     */
    fun average(radians: Float): Float {
        add(radians)
        return average()
    }
}