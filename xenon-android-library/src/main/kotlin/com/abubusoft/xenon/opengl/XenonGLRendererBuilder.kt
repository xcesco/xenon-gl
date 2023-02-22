/**
 *
 */
package com.abubusoft.xenon.opengl

/**
 * @author xcesco
 */
interface XenonGLRendererBuilder {
    /**
     * permette di creare un renderer
     *
     * @return
     * instanza di renderer
     */
    fun createRenderer(): XenonGLRenderer
}