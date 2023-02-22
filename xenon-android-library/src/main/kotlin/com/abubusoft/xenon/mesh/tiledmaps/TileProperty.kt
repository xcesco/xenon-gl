package com.abubusoft.xenon.mesh.tiledmaps;

/**
 * Proprietà
 * 
 * @author Francesco Benincasa
 * 
 */
public class TileProperty {

	private String name;
	
	private String value;

	public TileProperty(String nameValue, String valueValue) {
		this.name = nameValue;
		this.value = valueValue;
	}

	public String getName() {
		return this.name;
	}

	public String getValue() {
		return this.value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
}