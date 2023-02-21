package com.abubusoft.xenon.android.surfaceview

import android.graphics.PixelFormat

class ConfigOptions {
    /**
     * Indica il tipo di risoluzione da adottare. L'<bold>high</bold> è tipicamente la risoluzione RGBA_8888. La bassa risoluzione è quella che permette di consumare di meno in termini di memoria.
     *
     */
    enum class DisplayFormatType(val r: Int, val g: Int, val b: Int, val a: Int, val pixelFormat: Int) {
        DONT_CARE(0, 0, 0, 0, PixelFormat.OPAQUE), RGB_565(5, 6, 5, 0, PixelFormat.RGB_565), RGBA_8888(8, 8, 8, 8, PixelFormat.RGBA_8888);
    }

    enum class DepthSizeType(val value: Int) {
        DONT_CARE(-1), NONE(0), DEPTH_SIZE_16(16), DEPTH_SIZE_24(24);
    }

    enum class StencilSizeType(val value: Int) {
        DONT_CARE(-1), NONE(0), STENCIL_SIZE_8(8);
    }

    enum class ClientVersionType {
        OPENGL_ES_2, OPENGL_ES_3, OPENGL_ES_3_1
    }

    enum class MultiSampleType {
        DONT_CARE, ENABLED, NONE
    }

    /**
     * Configurazione del display. Si può lasciare che sia il sistema a decidere quale risoluzione utilizzare
     * o si può forzare la risoluzione impostando un valore diverso da DONT_CARE
     */
    var displayFormat = DisplayFormatType.DONT_CARE
    var depthSize = DepthSizeType.DONT_CARE
    var stencilSize = StencilSizeType.NONE
    var clientVersion = ClientVersionType.OPENGL_ES_2
    var multiSample = MultiSampleType.NONE

    /**
     * Configurazione del display. Si può lasciare che sia il sistema a decidere quale risoluzione utilizzare
     * o si può forzare la risoluzione impostando un valore diverso da DONT_CARE
     * @param value
     * opzione lato client
     * @return
     * format del display
     */
    fun displayFormat(value: DisplayFormatType): ConfigOptions {
        displayFormat = value
        return this
    }

    fun depthSize(value: DepthSizeType): ConfigOptions {
        depthSize = value
        return this
    }

    fun stencilSize(value: StencilSizeType): ConfigOptions {
        stencilSize = value
        return this
    }

    fun clientVersion(value: ClientVersionType): ConfigOptions {
        clientVersion = value
        return this
    }

    fun multiSample(value: MultiSampleType): ConfigOptions {
        multiSample = value
        return this
    }

    companion object {
        /**
         *
         *  * DisplayFormat = DisplayFormat.DONT_CARE
         *  * DepthSize = DepthSize.DONT_CARE
         *  * StencilSize = StencilSize.DONT_CARE
         *  * ClientVersion = ClientVersion.OPENGL_ES_2
         *  * MultiSample = MultiSample.DONT_CARE,
         *
         *
         * @return
         * istanza delle opzioni da usare per creare le surfaceView di XenonGL
         */
        fun build(): ConfigOptions {
            return ConfigOptions()
        }
    }
}