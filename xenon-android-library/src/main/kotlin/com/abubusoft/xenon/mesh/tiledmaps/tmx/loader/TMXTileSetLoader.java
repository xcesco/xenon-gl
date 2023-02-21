package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader;

import com.abubusoft.xenon.mesh.tiledmaps.TileSet;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TMXTileSetLoader extends DefaultHandler {

	/**
	 * tileSet da caricare
	 */
	public TileSet tileSet;
	
	@Override
	public void startDocument() throws SAXException {
		
	}

	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
	}

}
