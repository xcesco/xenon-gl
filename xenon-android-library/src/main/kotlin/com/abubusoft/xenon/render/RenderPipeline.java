package com.abubusoft.xenon.render;

import java.util.ArrayList;

import com.abubusoft.xenon.camera.Camera;
import com.abubusoft.xenon.camera.CameraManager;
import com.abubusoft.xenon.math.XenonMath;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.Mesh;
import com.abubusoft.xenon.mesh.MeshFactory;
import com.abubusoft.xenon.mesh.MeshOptions;
import com.abubusoft.xenon.mesh.TextureCoordRect;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.shader.ArgonShaderOptions;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.shader.ShaderTexture;
import com.abubusoft.xenon.shader.drawers.ShaderDrawer;
import com.abubusoft.xenon.texture.RenderedTexture;
import com.abubusoft.xenon.texture.RenderedTextureOptions;
import com.abubusoft.xenon.texture.TextureManager;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.kripton.android.Logger;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;

public class RenderPipeline {

	int clearColor;

	AbstractEffect<?> currentEffect;

	public final ArrayList<AbstractEffect<?>> effects;

	public final int index;

	/**
	 * rendered texture usata come input di un passo
	 */
	private RenderedTexture inputTexture;

	private Matrix4x4 matrixModelview;

	public final String name;

	/**
	 * rendered texture come output di un passo
	 */
	private RenderedTexture outputTexture;

	/**
	 * camera usata per il rendering della scena
	 */
	protected Camera sceneCamera;

	protected Mesh sceneMesh;

	/**
	 * distanza della scena dal viewport
	 */
	private float sceneZDistance;

	RenderedTexture swapTextureTemp;

	protected SceneDrawer sceneDrawer;

	protected Shader sceneShader;

	public RenderPipeline(Context context, String name, int index, SceneDrawer sceneDrawer, RenderPipelineOptions options) {
		this.name = name;
		this.index = index;
		this.matrixModelview = new Matrix4x4();
		this.effects = new ArrayList<AbstractEffect<?>>();

		TextureManager tm = TextureManager.instance();

		// la texture è quadrata
		inputTexture = tm.createRenderedTexture(context, options.viewportDimensions, RenderedTextureOptions.build());
		outputTexture = tm.createRenderedTexture(context, options.viewportDimensions, RenderedTextureOptions.build());

		this.sceneDrawer = sceneDrawer;
		sceneCamera = CameraManager.instance().createCamera(options.viewportDimensions.width, options.viewportDimensions.height);
		// distanza da usare per il draw dentro la texture
		sceneZDistance = XenonMath.zDistanceForSquare(sceneCamera, options.viewportDimensions.width);

		//
		sceneShader = ShaderManager.instance().createShader(ShaderTexture.class, ArgonShaderOptions.build());

		// creiamo lo shape per la scena
		if (options.optimized) {
			if (XenonGL.screenInfo.isPortraitMode()) {
				TextureCoordRect rect = TextureCoordRect.buildFromCenter(XenonGL.screenInfo.aspectRatio, 1f);
				sceneMesh = MeshFactory.createPlaneMesh(options.viewportDimensions.width * XenonGL.screenInfo.aspectRatio, options.viewportDimensions.height, 1, 1,
						MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true).textureCoordRect(rect));
			} else {
				TextureCoordRect rect = TextureCoordRect.buildFromCenter(1f, 1f / XenonGL.screenInfo.aspectRatio);
				sceneMesh = MeshFactory.createPlaneMesh(options.viewportDimensions.width, options.viewportDimensions.height / XenonGL.screenInfo.aspectRatio, 1, 1,
						MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true).textureCoordRect(rect));
			}

		} else {
			sceneMesh = MeshFactory.createPlaneMesh(options.viewportDimensions.width, options.viewportDimensions.height, 1, 1, MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true));
		}
	}

	/**
	 * <p>
	 * Aggiunge un effetto alla pipeline grafica.
	 * </p>
	 * 
	 * @param effect
	 * @return
	 */
	public <E extends AbstractEffect<?>> E addEffect(Context context, Class<E> clazzEffect, ArgonShaderOptions options) {
		E effect = null;
		try {
			effect = clazzEffect.newInstance();
			effect.setup(context, inputTexture.info.dimension.width, inputTexture.info.dimension.height, options, this);
			effects.add(effect);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.error(e.getMessage());
		}

		return effect;
	}

	/**
	 * <p>
	 * Esegue il rendering su una texture.
	 * </p>
	 * 
	 * @param camera
	 * @param enlapsedTime
	 * @param speedAdapter
	 * @return texture ottenuta
	 */
	public RenderedTexture executeOnTexture(Camera camera, long enlapsedTime, float speedAdapter) {
		int n = effects.size();

		outputTexture.activate();
		sceneClear();
		sceneDrawer.drawScene(sceneCamera, enlapsedTime, speedAdapter);

		// prepariamo matrice
		matrixModelview.buildIdentityMatrix();
		matrixModelview.translate(0, 0, -this.sceneZDistance);
		matrixModelview.multiply(sceneCamera.info.projection4CameraMatrix, matrixModelview);

		for (int i = 0; i < n; i++) {
			swapTexture();
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
			currentEffect = effects.get(i);
			outputTexture.activate();

			currentEffect.clear();
			currentEffect.execute(inputTexture, sceneMesh, matrixModelview, enlapsedTime, speedAdapter);
		}

		// disabilitiamo il framebuffer utilizzato
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

		return outputTexture;
	}

	/**
	 * <p>
	 * Esegue la pipeline e disegna il risultato direttamente sullo schermo.
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso dall'ultimo frame
	 * @param speedAdapter
	 *            fattore di moltiplicazione per adattare una velocità al secondo
	 */
	public void executeOnScreen(Camera camera, long enlapsedTime, float speedAdapter) {
		sceneClear();
		if (effects.size() == 0) {
			sceneDrawer.drawScene(camera, enlapsedTime, speedAdapter);
		} else {
			executeOnTexture(camera, enlapsedTime, speedAdapter);

			// torniamo a disegnare sullo schermo
			GLES20.glViewport(0, 0, XenonGL.screenInfo.width, XenonGL.screenInfo.height);

			matrixModelview.buildIdentityMatrix();
			matrixModelview.translate(0, 0, -sceneZDistance);
			matrixModelview.multiply(camera.info.projection4CameraMatrix, matrixModelview);

			sceneShader.use();
			sceneShader.setVertexCoordinatesArray(sceneMesh.vertices);
			sceneShader.setTextureCoordinatesArray(0, sceneMesh.textures[0]);
			sceneShader.setTexture(0, outputTexture);

			ShaderDrawer.draw(sceneShader, sceneMesh, matrixModelview);
		}

		/*
		 * 
		 * // prepariamo matrice matrixModelview.buildIdentityMatrix(); matrixModelview.translate(0, 0, -this.sceneZDistance);
		 * matrixModelview.multiply(sceneCamera.info.projection4CameraMatrix, matrixModelview);
		 * 
		 * 
		 * 
		 * if (n >= 1) { // attiviamo la texture per il rendering della scena principale. Impostiamo anche il viewport outputTexture.activate(); }
		 * 
		 * sceneClear(); sceneDrawer.drawScene(matrixModelview, enlapsedTime, speedAdapter);
		 * 
		 * if (n <= 1) { // ASSERT: abbiamo in outputTexture l'immagine disegnata
		 * 
		 * // torniamo a disegnare sullo schermo returnToScreen(RenderPipelineManager.instance().screenInfo);
		 * 
		 * for (int i = 0; i < n; i++) { swapTexture(); currentEffect = effects.get(i); currentEffect.clear(); currentEffect.execute(inputTexture, matrixModelview, enlapsedTime,
		 * speedAdapter); } } else { for (int i = 0; i < n - 1; i++) { // l'output dello step di prima, ora diventa l'input swapTexture();
		 * 
		 * outputTexture.activateWithSameViewport(); currentEffect = effects.get(i); currentEffect.clear(); currentEffect.execute(inputTexture, matrixModelview, enlapsedTime,
		 * speedAdapter); } }
		 */

	}

	/**
	 * <p>
	 * Attiviamo texture per disegnare scena.
	 * </p>
	 */
	protected void sceneClear() {
		clearColor = Color.BLACK;
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		GLES20.glClearColor(Color.red(clearColor), Color.green(clearColor), Color.blue(clearColor), 0);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * <p>
	 * Invertiamo le texture.
	 * </p>
	 */
	protected void swapTexture() {
		swapTextureTemp = inputTexture;
		inputTexture = outputTexture;
		outputTexture = swapTextureTemp;
	}

}
