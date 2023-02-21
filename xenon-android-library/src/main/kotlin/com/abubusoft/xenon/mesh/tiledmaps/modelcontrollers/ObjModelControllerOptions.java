package com.abubusoft.xenon.mesh.tiledmaps.modelcontrollers;

import com.abubusoft.xenon.R;

public class ObjModelControllerOptions {

	private ObjModelControllerOptions() {

	}

	/**
	 * elenco dei movimenti
	 */
	public int resourceKeys;

	/**
	 * elenco delle animazioni
	 */
	public int resourceValues;

	/**
	 * movimento iniziale
	 */
	public ObjActionType status;

	/**
	 * <ul>
	 * <li><b>resourceKeys</b>: array di stringhe dei movimenti definiti - R.array.controller_obj_key</li>
	 * <li><b>resourceValues</b>: array di stringhe delle animazioni associate - R.array.controller_obj_value</li>
	 * <li><b>status</b>:stato iniziale - <code>null</code></li>
	 * </ul>
	 * 
	 * @return
	 */
	public static ObjModelControllerOptions build() {
		ObjModelControllerOptions ret = new ObjModelControllerOptions();
		ret.resourceKeys = R.array.tiledmap_obj_action_keys;
		ret.resourceValues = R.array.tiledmap_obj_action_values;
		ret.status = null;

		return ret;
	}

	public ObjModelControllerOptions resourceKeys(int value) {
		resourceKeys = value;
		return this;
	}

	public ObjModelControllerOptions resourceValues(int value) {
		resourceValues = value;
		return this;
	}

	public ObjModelControllerOptions status(ObjActionType value) {
		status=value;
		
		return this;
		
	}

}
