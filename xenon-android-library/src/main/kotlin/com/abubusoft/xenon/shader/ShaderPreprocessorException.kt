/**
 *
 */
package com.abubusoft.xenon.shader

/**
 * @author Francesco Benincasa
 */
class ShaderPreprocessorException : RuntimeException {
    constructor(value: String?) : super(value) {}
    constructor() : super() {}

    companion object {
        private const val serialVersionUID = -4795122361975180575L
    }
}