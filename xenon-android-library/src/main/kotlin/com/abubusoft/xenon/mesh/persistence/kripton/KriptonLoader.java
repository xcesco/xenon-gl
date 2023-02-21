package com.abubusoft.xenon.mesh.persistence.kripton;

import java.io.IOException;
import java.io.InputStream;

import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.vbo.AbstractBuffer;
import com.abubusoft.xenon.vbo.BufferHelper;
import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;

import com.abubusoft.kripton.BinderType;
import com.abubusoft.kripton.KriptonBinder;

public class KriptonLoader {


	/**
	 * Effettua il build, il bind per opengl e l'update del buffer
	 * @param buffer
	 */
	private static void buildAndBindAndUpdate(AbstractBuffer buffer) {
		BufferHelper.buildBuffer(buffer);
		BufferHelper.bindBuffer(buffer);
		buffer.update();
	}

	/**
	 * carica un file usando il binderReader specificato come parametro in una mesh il cui tipo è stato specificato
	 * 
	 * @param input
	 * @param reader
	 * @param options
	 * @return
	 */
	static Mesh load(InputStream input, BinderType binderType, MeshOptions options) {
		try {
			Mesh mesh=KriptonBinder.bind(binderType).parse(input, options.meshClazz);			

			// vertici (ci sono sempre)
			buildAndBindAndUpdate(mesh.vertices);

			// vertici (ci sono sempre)
			if (mesh.normalsEnabled) {
				buildAndBindAndUpdate(mesh.normals);
			}

			// texture
			if (mesh.texturesEnabled) {
				for (int i = 0; i < mesh.texturesCount; i++) {
					buildAndBindAndUpdate(mesh.textures[i]);
				}
			}

			// attributi
			if (mesh.attributesEnabled) {
				for (int i = 0; i < mesh.attributesCount; i++) {
					buildAndBindAndUpdate(mesh.attributes[i]);
				}
			}

			// indici
			if (mesh.indexesEnabled) {
				buildAndBindAndUpdate(mesh.indexes);
			}

			return mesh;
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

	/**
	 * Carica un file da un asset in un context di applicazione android.
	 * 
	 * @param context
	 * @param fileName
	 * @param reader
	 * @param options
	 * @return
	 */
	static Mesh loadFromAsset(Context context, String fileName, BinderType bindType, MeshOptions options) {
		try {
			return load(context.getAssets().open(fileName), bindType, options);
		} catch (IOException e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

	private static Mesh loadFromResource(Context context, int resourceId, BinderType bindType, MeshOptions options) {
		try {
			return load(context.getResources().openRawResource(resourceId), bindType, options);
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
				
	}

	public static Mesh loadMeshFromJSON(Context context, int resourceId, MeshOptions options) {		
		return loadFromResource(context, resourceId, BinderType.JSON, options);
	}

	public static Mesh loadMeshFromJSON(Context context, String fileName, MeshOptions options) {
		return loadFromAsset(context, fileName, BinderType.JSON, options);
	}

	public static Mesh loadMeshFromXML(Context context, String fileName, MeshOptions options) {
		return loadFromAsset(context, fileName, BinderType.XML, options);
	}
	
	public static Mesh loadMeshFromXML(Context context, int resourceId, MeshOptions options) {
		return loadFromResource(context, resourceId, BinderType.XML, options);
	}

}
