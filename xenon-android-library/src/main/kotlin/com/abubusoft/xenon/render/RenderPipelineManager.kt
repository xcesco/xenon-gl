package com.abubusoft.xenon.render

import android.content.Context

/**
 *
 *
 * Questo è un sistema per il rendering di una scena offscreen mediante frame buffer. Ogni [RenderPipeline] ha un viewport di dimensioni pari a quello delle texture, quindi
 * 256, 512 o 1024.
 *
 *
 * @author Francesco Benincasa
 */
class RenderPipelineManager private constructor() {
    /**
     * elenco delle pipeline
     */
    private val pipelines: ArrayList<RenderPipeline>

    /**
     *
     */
    init {
        pipelines = ArrayList()
    }

    /**
     *
     *
     * Crea una pipeline.
     *
     *
     * @param name
     * @return render pipeline
     */
    fun createPipeline(context: Context?, name: String, sceneDrawer: SceneDrawer, options: RenderPipelineOptions): RenderPipeline {
        val pipeline = RenderPipeline(context, name, pipelines.size, sceneDrawer, options)
        pipelines.add(pipeline)
        return pipeline
    }

    companion object {
        /**
         *
         */
        private val instance = RenderPipelineManager()
        fun instance(): RenderPipelineManager {
            return instance
        }
    }
}