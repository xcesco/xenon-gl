package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

import com.abubusoft.xenon.mesh.tiledmaps.TileSet
import org.xml.sax.Attributes
import org.xml.sax.SAXException
import org.xml.sax.helpers.DefaultHandler

class TMXTileSetLoader : DefaultHandler() {
    /**
     * tileSet da caricare
     */
    var tileSet: TileSet? = null
    @Throws(SAXException::class)
    override fun startDocument() {
    }

    @Throws(SAXException::class)
    override fun endDocument() {
        super.endDocument()
    }

    @Throws(SAXException::class)
    override fun startElement(uri: String, localName: String, qName: String, attributes: Attributes) {
        super.startElement(uri, localName, qName, attributes)
    }

    @Throws(SAXException::class)
    override fun endElement(uri: String, localName: String, qName: String) {
        super.endElement(uri, localName, qName)
    }

    @Throws(SAXException::class)
    override fun characters(ch: CharArray, start: Int, length: Int) {
        super.characters(ch, start, length)
    }
}