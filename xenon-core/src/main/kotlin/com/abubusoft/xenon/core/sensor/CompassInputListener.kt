package com.abubusoft.xenon.core.sensor

import com.abubusoft.xenon.core.sensor.internal.InputListener

interface CompassInputListener : InputListener {
    fun update(heading: Double, pitch: Double, roll: Double, deltaHeading: Double, deltaPitch: Double, deltaRoll: Double, somethingIsChanged: Boolean)
}