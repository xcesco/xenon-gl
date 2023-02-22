package com.abubusoft.xenon.entity

class GridOptions {
    /**
     *
     * Definisce la percentuale della dimensione in orizzontale della tile da utilizzare come spaziatore
     * tra un centro della tile e quello della colonna successivo
     */
    var marginHorizontal = 0f
    var marginVertical = 0f
    var oddColumnsLower = false

    /**
     * fattore di scala della window. Nel caso di landscape sarà > 1
     */
    var windowScaleFactor = 0f
    fun oddColumnsLower(value: Boolean): GridOptions {
        oddColumnsLower = value
        return this
    }

    fun marginVertical(value: Float): GridOptions {
        marginVertical = value
        return this
    }

    fun marginHorizontal(value: Float): GridOptions {
        marginHorizontal = value
        return this
    }

    fun windowScaleFactor(value: Float): GridOptions {
        windowScaleFactor = value
        return this
    }

    companion object {
        fun build(): GridOptions {
            return GridOptions().marginHorizontal(1f).marginVertical(1f).oddColumnsLower(false).windowScaleFactor(1f)
        }
    }
}