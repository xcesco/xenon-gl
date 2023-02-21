package com.abubusoft.xenon.render;

import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.shader.ArgonShaderOptions;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.shader.drawers.ShaderDrawer;
import com.abubusoft.xenon.texture.RenderedTexture;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

/**
 * <p>
 * Gli effetti che derivano da questa classe devono avere l'annotazione UseShader, pena eccezione.
 * </p>
 * 
 * <p>
 * Ogni shader che viene utilizzato ha a disposizione le seguenti constanti:
 * </p>
 * 
 * <ul>
 * <li>RESOLUTION_X: larghezza della texture</li>
 * <li>RESOLUTION_Y: altezza della texture</li>
 * <li>INV_RESOLUTION_X: 1 / larghezza della texture</li>
 * <li>INV_RESOLUTION_Y: 1 / altezza della texture</li>
 * </ul>
 * 
 * @author Francesco Benincasa
 * @param <P>
 * 
 */
public abstract class AbstractEffect<E extends Shader> {

	public AbstractEffect() {

	}

	/**
	 * indica se l'effetto è abilitato o meno.
	 */
	public boolean enabled;

	/**
	 * indice dell'effetto
	 */
	public int index;

	protected E shader;

	@SuppressWarnings("rawtypes")
	protected EffectUpdater updater;

	public void setUpdater(EffectUpdater<?, E> updater) {
		this.updater = updater;
	}

	int clearColor = Color.BLACK;

	protected void execute(RenderedTexture texture, Mesh shape, Matrix4x4 matrix, long enlapsedTime, float speedAdapter) {
		shader.use();
		shader.setVertexCoordinatesArray(shape.vertices);
		shader.setTexture(0, texture);
		shader.setTextureCoordinatesArray(0, shape.textures[0]);

		update(shader, enlapsedTime, speedAdapter);

		ShaderDrawer.draw(shader, shape, matrix);
	}

	/**
	 * <p>
	 * Serve ad aggiornare gli attributi dello shader che devono essere definite dalla classe effect.
	 * </p>
	 * 
	 * @param shader
	 * @param enlapsedTime
	 * @param speedAdapter
	 */
	protected abstract void updateShader(E shader, long enlapsedTime, float speedAdapter);

	/**
	 * <p>
	 * Aggiorna lo shader.
	 * </p>
	 * 
	 * @param shader
	 * @param enlapsedTime
	 * @param speedAdapter
	 */
	@SuppressWarnings("unchecked")
	protected void update(E shader, long enlapsedTime, float speedAdapter) {

		updateShader(this.shader, enlapsedTime, speedAdapter);

		if (updater != null) {
			updater.update(this, this.shader, enlapsedTime, speedAdapter);
		}
	}

	/**
	 * <p>
	 * Pulisce il color buffer e lo depth buffer.
	 * </p>
	 */
	protected void clear() {
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		GLES20.glClearColor(Color.red(clearColor), Color.green(clearColor), Color.blue(clearColor), 0);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * <p>
	 * Effettua il setup dell'effetto.
	 * </p>
	 * 
	 * @param context
	 * @param options
	 * @param pipeline
	 */
	@SuppressWarnings("unchecked")
	void setup(Context context, float screenWidth, float screenHeight, ArgonShaderOptions options, RenderPipeline pipeline) {
		UseShader ann = this.getClass().getAnnotation(UseShader.class);

		if (ann == null) {
			throw (new RuntimeException("No @UseShader defined for effect"));
		}
		Class<E> shaderClazz = ((Class<E>) ann.value());

		index = pipeline.effects.size();

		options.costant("RESOLUTION_X", Float.toString(screenWidth));
		options.costant("RESOLUTION_Y", Float.toString(screenHeight));

		options.costant("INV_RESOLUTION_X", Float.toString(1f / screenWidth));
		options.costant("INV_RESOLUTION_Y", Float.toString(1f / screenHeight));

		setShaderOptions(options);

		shader = ShaderManager.instance().createShader(shaderClazz, options);
	}

	/**
	 * <p>
	 * Permette di configurare i parametri dello shader
	 * </p>
	 * 
	 * @param options
	 */
	public void setShaderOptions(ArgonShaderOptions options) {

	}

}
