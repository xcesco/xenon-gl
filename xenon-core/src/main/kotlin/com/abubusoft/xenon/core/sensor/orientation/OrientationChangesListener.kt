package com.abubusoft.xenon.core.sensor.orientation

interface OrientationChangesListener {
    fun onChange(x: Float, y: Float, z: Float)
}