/*
 * Copyright (c) 2010-2011 e3roid project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * * Neither the name of the project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import android.annotation.SuppressLint
import org.xml.sax.Attributes

/**
 * A utility class for parsing SAX objects.
 */
object SAXUtil {
    fun getInt(atts: Attributes, name: String): Int {
        val value = atts.getValue("", name)
        if (value != null) {
            return value.toInt()
        }
        throw IllegalArgumentException("No value found for attribute: $name")
    }

    fun getLong(atts: Attributes, name: String): Long {
        val value = atts.getValue("", name)
        if (value != null) {
            return value.toLong()
        }
        throw IllegalArgumentException("No value found for attribute: $name")
    }

    fun getInt(atts: Attributes, name: String?, defaultValue: Int): Int {
        val value = atts.getValue("", name)
        return value?.toInt() ?: defaultValue
    }

    /**
     *
     * Ottiene il valore dell'attributo in lowercase.
     *
     * @param atts
     * @param name
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun getString(atts: Attributes, name: String?): String {
        return atts.getValue("", name)
    }

    /**
     *
     * Recupera un attributo di tipo string.
     *
     * @param atts
     * @param name
     * @param defaultValue
     * @return
     */
    fun getString(atts: Attributes, name: String?, defaultValue: String): String {
        val value = atts.getValue("", name)
        return value ?: defaultValue
    }

    /**
     *
     * Recupera un attributo di tipo float.
     *
     * @param atts
     * @param name
     * @param defaultValue
     * @return
     */
    fun getFloat(atts: Attributes, name: String?, defaultValue: Float): Float {
        val value = atts.getValue("", name)
        return value?.toFloat() ?: defaultValue
    }
}