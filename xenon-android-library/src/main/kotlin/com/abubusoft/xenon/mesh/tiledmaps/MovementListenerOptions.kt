package com.abubusoft.xenon.mesh.tiledmaps

/**
 *
 *
 * Opzioni relative al listener sugli eventi di spostamento relativi alla mappa.
 *
 *
 * @author Francesco Benincasa
 */
class MovementListenerOptions private constructor() {
    /**
     * numero di aree in orizzontale in cui suddividere logicamente la mappa
     */
    var horizontalAreaCount = 0
    var horizontalAreaInvSize = 0f

    /**
     * numero di aree in verticale in cui suddividere logicamente la mappa
     */
    var verticalAreaCount = 0
    var verticalAreaInvSize = 0f

    /**
     * numero di aree in orizzontale in cui suddividere logicamente la mappa
     */
    fun horizontalAreaCount(value: Int): MovementListenerOptions {
        horizontalAreaCount = value
        return this
    }

    /**
     * numero di aree in verticale in cui suddividere logicamente la mappa
     */
    fun verticalAreaCount(value: Int): MovementListenerOptions {
        verticalAreaCount = value
        return this
    }

    companion object {
        /**
         *
         *
         * Opzioni di default:
         *
         *
         *  * horizontalAreaCount = 1
         *  * verticalAreaCount = 1
         *
         *
         * @return
         * this
         */
        fun build(): MovementListenerOptions {
            return MovementListenerOptions().horizontalAreaCount(1).verticalAreaCount(1)
        }
    }
}