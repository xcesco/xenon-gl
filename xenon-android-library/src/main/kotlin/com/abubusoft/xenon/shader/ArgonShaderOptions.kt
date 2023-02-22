package com.abubusoft.xenon.shader

import android.annotation.SuppressLint
import android.util.Pair
import java.text.DecimalFormat
import java.util.*

/**
 * Definisce le opzioni per i vari shader. Di default vengono definite due costanti: <ld> <dt>@PI</dt><dd>Pi greco</dd> <dt>@2PI</dt><dd>Pi greco * 2</dd> </ld>
 *
 *
 * Le definizioni sono quelle che possono cambiare la definizione degli shader.
 *
 * @author Francesco Benincasa
 */
@SuppressLint("DefaultLocale")
class ArgonShaderOptions private constructor() {
    /**
     * salva i due shader su file
     */
    var debugOnFile = false

    /**
     * Se true indica che lo shader sarà modificato automaticamente al fine di ospitare
     * come texture_0 una texture esterna.
     */
    var useForExternalTexture = false

    /**
     * nome dello shader
     */
    var name: String? = null

    /**
     * numero di texture utilizzate contemporaneamente dallo shader.
     */
    var numberOfTextures = 0

    /**
     * numero di attributi uniformi, ovvero validi per tutti i vertici.
     */
    var numberOfUniformAttributes = 0

    /**
     *
     *
     * Mappa delle costanti che devono essere sostituite con un @[nome costante].
     *
     *
     * Se mettiamo in uno dei due programmi il seguente codice:
     *
     * <pre>
     * i=@cod;
    </pre> *
     *
     * E impostiamo la variabile da programma, il codice compilato diventa quindi
     *
     */
    var pragmaCostants = ArrayList<Pair<String, String>>()
    var pragmaDefinitions = HashMap<String?, Boolean?>()
    fun numberOfTextures(value: Int): ArgonShaderOptions {
        numberOfTextures = value
        return this
    }

    fun numberOfUniformAttributes(value: Int): ArgonShaderOptions {
        numberOfUniformAttributes = value
        return this
    }

    fun name(value: String?): ArgonShaderOptions {
        name = value
        return this
    }

    /**
     *
     *
     * salva i due shader su file
     *
     *
     * @param value
     * @return
     */
    fun debugOnFile(value: Boolean): ArgonShaderOptions {
        debugOnFile = value
        return this
    }

    /**
     *
     *
     * Definisce una costante. Il nome della costante all'interno dello shader è del tipo @NOME, in uppercase.
     *
     *
     * @param name
     * @param value
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun costant(name: String, value: String): ArgonShaderOptions {
        pragmaCostants.add(Pair(name.uppercase(), value))
        return this
    }

    /**
     * Facility per definire le costanti @RESOLUTION_X, @RESOLUTION_Y, @INV_RESOLUTION_X e @INV_RESOLUTION_Y
     * @param name
     * @param value
     * @return
     */
    fun costantResolution(resolutionX: Float, resolutionY: Float): ArgonShaderOptions {
        costant("RESOLUTION_X", floatFormatter.format(resolutionX.toDouble()))
        costant("RESOLUTION_Y", floatFormatter.format(resolutionY.toDouble()))
        costant("INV_RESOLUTION_X", floatFormatter.format((1f / resolutionX).toDouble()))
        costant("INV_RESOLUTION_Y", floatFormatter.format((1f / resolutionY).toDouble()))
        return this
    }

    /**
     * costruttore.
     */
    init {
        // definisce le costanti valide sempre
        costant("PI", floatFormatter.format(Math.PI.toFloat().toDouble()))
        costant("2PI", floatFormatter.format(PI_2.toDouble()))
    }

    /**
     * Definizione. viene salvata in lowercase.
     *
     * @param name
     * @param enabled
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun define(name: String, enabled: Boolean): ArgonShaderOptions {
        pragmaDefinitions[name.lowercase(Locale.getDefault())] = enabled
        return this
    }

    /**
     * Se true indica che lo shader sarà modificato automaticamente al fine di ospitare
     * come texture_0 una texture esterna.
     */
    fun useForExternalTexture(enabled: Boolean): ArgonShaderOptions {
        useForExternalTexture = enabled
        return this
    }

    companion object {
        var PI_2 = (2.0 * Math.PI).toFloat()

        /**
         * Parte con una texture e 0 attributi
         *
         * @return
         */
        fun build(): ArgonShaderOptions {
            return ArgonShaderOptions().numberOfTextures(1).numberOfUniformAttributes(0).useForExternalTexture(false)
        }

        private val floatFormatter = DecimalFormat("#.0###########")
    }
}