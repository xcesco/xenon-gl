/**
 * 
 */
package com.abubusoft.xenon.shader;

import java.util.HashMap;

import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;
import android.util.SparseArray;

/**
 * @author Francesco Benincasa
 * 
 */
public class ShaderManager {

	/**
	 * Costruttore
	 */
	private ShaderManager() {
		shaders = new SparseArray<Shader>();
		mapShaders = new HashMap<String, Shader>();

	}

	/**
	 * Singleton
	 */
	private static final ShaderManager instance = new ShaderManager();

	/**
	 * Restituisce istanza dello shader manager
	 * 
	 * @return singleton dello shader manager
	 */
	public static ShaderManager instance() {
		return instance;
	}

	/**
	 * array delle texture
	 */
	private SparseArray<Shader> shaders;

	/**
	 * mappa degli shader in base al nome
	 */
	private HashMap<String, Shader> mapShaders;

	/**
	 * <p>
	 * Crea un video shader.
	 * </p>
	 * 
	 * @return
	 * 		video shader
	 */
	public VideoShader createShaderVideo() {
		return createShader(VideoShader.class);
	}

	/**
	 * Crea uno shader che si preoccupa semplicemente di disegnare mediante una texture
	 * 
	 * @return shader per disegnare una texture con un sistema di coordinate
	 */
	public ShaderTexture createShaderTexture() {
		return createShader(ShaderTexture.class);
	}

	/**
	 * Crea uno shader per le linee
	 * 
	 * @return shader per le linee
	 */
	public ShaderLine createShaderLine() {
		return createShader(ShaderLine.class);
	}

	public <E extends Shader> E createShader(int vertexProgramId, int fragmentProgramId, Class<E> shaderClazz, ArgonShaderOptions options) {
		E shader = null;
		try {
			Context context = XenonBeanContext.getBean(XenonBeanType.CONTEXT);

			Logger.info("Create shader %s", shaderClazz.toString());
			shader = shaderClazz.newInstance();
			shader.setupFromFiles(context, vertexProgramId, fragmentProgramId, options);
			addShader(shader);
			Logger.info("Builded shader kind: %s, programId: %s", shaderClazz.toString(), shader.programId);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return shader;
	}

	/**
	 * <p>
	 * Crea uno shader custom partendo dalla sua classe. Nel costruttore della classe deve essere inizializzato il builder.
	 * </p>
	 * 
	 * @param shaderClazz
	 *            class shader
	 * @param options
	 *            opzioni per la costruzione dello shader
	 * @return shader
	 */
	public <E extends Shader> E createShader(Class<E> shaderClazz, ArgonShaderOptions options) {
		E shader = null;
		try {
			Context context = XenonBeanContext.getBean(XenonBeanType.CONTEXT);

			Logger.info("Create shader %s", shaderClazz.toString());

			shader = shaderClazz.newInstance();
			shader.setupFromBuilder(context, options);
			addShader(shader);
			Logger.info("Builded shader kind: %s, programId: %s", shaderClazz.toString(), shader.programId);
		} catch (Exception e) {
			Logger.fatal(e.getMessage());
			e.printStackTrace();
		}

		return shader;
	}

	/**
	 * <p>
	 * Crea uno shader custom partendo dalla sua classe. Nel costruttore della classe deve essere inizializzato il builder.
	 * </p>
	 * 
	 * @param shaderClazz
	 *            class shader
	 * @return shader
	 */
	public <E extends Shader> E createShader(Class<E> shaderClazz) {
		return createShader(shaderClazz, null);
	}

	/**
	 * Creao uno shader custom, al quale bastano i parametri di base offerti dalla classe base shader.
	 * 
	 * @param vertexProgramId
	 * @param fragmentProgramId
	 * @param options
	 * @return
	 * 		shader
	 */
	public Shader createShader(int vertexProgramId, int fragmentProgramId, ArgonShaderOptions options) {
		return createShader(vertexProgramId, fragmentProgramId, Shader.class, options);
	}

	public ShaderTiledMap createShaderTiledMap(boolean oneTextureForLayer, ArgonShaderOptions options) {
		ShaderTiledMap shader = new ShaderTiledMap(oneTextureForLayer, options);
		addShader(shader);

		return shader;
	}

	public Shader getShader(String name) {
		return mapShaders.get(name);
	}

	/**
	 * Aggiunge uno shader
	 * 
	 * @param shader
	 */
	private void addShader(Shader shader) {
		shaders.append(shader.programId, shader);

		if (shader.name != null) {
			mapShaders.put(shader.name, shader);
		}
	}

	/**
	 * Rigenera gli shader, partendo dal loro source. Non possono essere fatte modifiche agli shader.
	 */
	public void reloadShaders() {
		Shader shader;
		for (int i = 0; i < shaders.size(); i++) {
			shader = shaders.valueAt(i);

			shader.reload();
		}
	}

	/**
	 * Azzera gli shader e dealloca le risorse utilizzate
	 */
	public void clearShaders() {
		Shader shader;
		XenonGL.clearGlError();
		Logger.debug("Clear " + shaders.size() + " old shaders, without deleting them ");

		for (int i = 0; i < shaders.size(); i++) {
			shader = shaders.valueAt(i);
			if (shader != null) {
				shader.unbind();
			}
		}

		shaders.clear();
		mapShaders.clear();

	}
}
