package com.abubusoft.xenon.render;

import java.util.ArrayList;

import android.content.Context;

/**
 * <p>
 * Questo è un sistema per il rendering di una scena offscreen mediante frame buffer. Ogni {@link RenderPipeline} ha un viewport di dimensioni pari a quello delle texture, quindi
 * 256, 512 o 1024.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class RenderPipelineManager {

	/**
	 * 
	 */
	private RenderPipelineManager() {
		pipelines = new ArrayList<>();
	}

	/**
	 * 
	 */
	private final static RenderPipelineManager instance = new RenderPipelineManager();

	public static RenderPipelineManager instance() {
		return instance;
	}

	/**
	 * elenco delle pipeline
	 */
	private ArrayList<RenderPipeline> pipelines;

	/**
	 * <p>
	 * Crea una pipeline.
	 * </p>
	 * 
	 * @param name
	 * @return render pipeline
	 */
	public RenderPipeline createPipeline(Context context, String name, SceneDrawer sceneDrawer, RenderPipelineOptions options) {
		RenderPipeline pipeline = new RenderPipeline(context, name, pipelines.size(), sceneDrawer, options);
		
		pipelines.add(pipeline);

		return pipeline;
	}
}
