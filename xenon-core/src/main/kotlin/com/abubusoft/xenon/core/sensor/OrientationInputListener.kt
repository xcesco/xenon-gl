package com.abubusoft.xenon.core.sensor

import com.abubusoft.xenon.core.sensor.internal.InputListener

/**
 *
 *
 * Interfaccia atta a rilevare i movimenti. Ricordiamo che:
 *
 * <pre>
 * ^ Roll (z)
 * |
 * +---------+
 * |         |
 * | Heading |
 * |   (x)   |
 * |    x    | ---> Pitch (y)
 * |         |
 * +---------+
 *
</pre> *
 *
 * @author Francesco Benincasa
 */
interface OrientationInputListener : InputListener {
    /**
     *
     *
     * Esegue il listener del rilevatore del sensore.
     *
     *
     * <pre>
     * ^ Roll (z)
     * |
     * +---------+
     * |         |
     * | Heading |
     * |   (x)   | ---> Pitch (y)
     * |    x    |
     * |         |
     * +---------+
     *
    </pre> *
     *
     * @param heading
     * valore attuale in gradi (0 - 360)
     * @param pitch
     * valore attuale in gradi (0 - 360)
     * @param roll
     * valore attuale in gradi (0 - 360)
     * @param deltaHeading
     * delta head in gradi rispetto all'ultima misurazione (current - old)
     * @param deltaPitch
     * delta pitch in gradi rispetto all'ultima misurazione (current - old)
     * @param deltaRoll
     * delta roll in gradi rispetto all'ultima misurazione (current - old)
     * @param somethingIsChanged
     * se true indica se qualcosa è cambiato
     */
    fun update(heading: Double, pitch: Double, roll: Double, deltaHeading: Double, deltaPitch: Double, deltaRoll: Double, somethingIsChanged: Boolean)
}