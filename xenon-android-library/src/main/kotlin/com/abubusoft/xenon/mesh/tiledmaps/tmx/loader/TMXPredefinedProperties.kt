package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

/**
 * Proprietà definite per i layer e startX la mappa.
 *
 * @author Francesco Benincasa
 */
object TMXPredefinedProperties {
    /**
     *
     *
     * instanceOf. Attributo che indica per un object dentro un object layer che
     * classe implementa questa istanza.
     *
     */
    const val INSTANCE_OF = "instanceOf"

    /**
     *
     *
     * Se impostato a true, indica che il layer deve essere rimosso
     *
     *
     * Usato nei **LAYER**
     */
    const val PREVIEW = "preview"

    /**
     * Indica la percentuale di spostamento su startX per un layer. Utile per
     * l'effetto parallasse. Va da 0 a 1.
     *
     * Se non definito è 1.
     */
    const val SPEED_PERCENTAGE_X = "speedPercentageX"

    /**
     * Indica la percentuale di spostamento su startY per un layer. Utile per
     * l'effetto parallasse. Va da 0 a 1.
     *
     * Se non definito è 1.
     */
    const val SPEED_PERCENTAGE_Y = "speedPercentageY"

    /**
     * Proprietà a livello di mappa che indica le animazioni. Es: animationOnda.
     *
     * I vari frame avranno come nome onda1, onda2, onda3 etc.
     */
    const val ANIMATION_PREFIX = "animation"

    /**
     * parametro di mappa relativo alla durata di default dei frame
     */
    const val ANIMATION_FRAME_DEFAULT_DURATION = "animationFrameDefaultDuration"

    /**
     * Gruppo di animazione di appartenenza. Es: prova
     */
    const val ANIMATION_ID = "animationId"

    /**
     * indice di posizione: da 0 a n
     */
    const val ANIMATION_FRAME = "animationFrame"

    /**
     * durante del frame in ms
     */
    const val ANIMATION_FRAME_DURATION = "animationFrameDuration"
    const val ANIMATION_ENABLED = "animationFrameDuration"

    /**
     *
     * Indica se un'entità è visibile (1) o invisibile (0).
     */
    const val VISIBLE = "visible"

    /**
     *
     * Indica se l'oggetto è un sensore o meno.
     */
    const val SENSOR = "sensor"
}