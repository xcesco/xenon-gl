package com.abubusoft.xenon.animations

import com.abubusoft.xenon.math.Vector3

class TranslationHandler : AnimationHandler<TranslationFrame?>() {
    public override fun value(current: TranslationFrame?, enlapsedTime: Long, next: TranslationFrame?): TranslationFrame? {
        return if (next != null) {
            val perc = current!!.interpolation.getPercentage(enlapsedTime.toFloat(), current.duration * rate)

            // temp viene sempre scritto, non importa cosa c'è prima
            Vector3.multiply(next.translation, perc, temp!!.translation)
            temp
        } else {
            // siamo sull'ultimo frame, dopo non c'è niente. La traslazione è 0
            temp!!.translation.setCoords(0f, 0f, 0f)
            temp
        }
    }

    public override fun buildFrame(): TranslationFrame? {
        return TranslationFrame()
    }
}