/**
 *
 */
package com.abubusoft.xenon.core.util

import java.util.*

/**
 * @author Francesco Benincasa
 */
object StringUtility {
    fun leftPad(source: String, size: Int, padder: Char): String {
        var pad = size - source.length
        if (pad < 0) pad = 0
        val temp = (String(CharArray(pad)) + source).toCharArray()
        Arrays.fill(temp, 0, pad, padder)
        return String(temp)
    }

    /**
     * Check if a String has text. More specifically, returns true if the string
     * not null, it's length is > 0, and it has at least one non-whitespace
     * character.
     *
     * <pre>
     * StringUtility.hasText(null) = false
     * StringUtility.hasText("") = false
     * StringUtility.hasText(" ") = false
     * StringUtility.hasText("12345") = true
     * StringUtility.hasText(" 12345 ") = true
    </pre> *
     *
     * Parameters: str - the String to check, may be null
     *
     * true if the String is not null, length > 0, and not whitespace only
     *
     * @param source
     * @param defaultValue
     *
     * @return
     */
    fun nvl(source: String?, defaultValue: String): String {
        return if (source == null || source.trim { it <= ' ' }.length == 0) {
            defaultValue
        } else source
    }

    /**
     * True se la stringa è vuota
     * @param source
     * @return
     */
    fun isEmpty(source: String?): Boolean {
        return if (source == null) true else false
    }

    /**
     * true se stringa blank o nulla
     * @param source
     * @return
     */
    fun isBlank(source: String?): Boolean {
        return if (source == null || source.trim { it <= ' ' }.length == 0) {
            true
        } else false
    } /*
	 * IsEmpty/IsBlank
	 * 
	 * DefaultString
	 */
}