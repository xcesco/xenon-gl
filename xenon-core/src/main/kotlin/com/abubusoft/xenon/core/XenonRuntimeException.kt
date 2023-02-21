/**
 *
 */
package com.abubusoft.xenon.core

/**
 * @author Francesco Benincasa
 */
open class XenonRuntimeException : RuntimeException {
    constructor(msg: String?) : super(msg) {}
    constructor(origin: Throwable?) : super(origin) {}
    constructor(msg: String?, origin: Throwable?) : super(msg, origin) {}

    companion object {
        /**
         *
         */
        private const val serialVersionUID = -7885443692058739678L
    }
}