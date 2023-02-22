package com.abubusoft.xenon.render

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.camera.Camera
import com.abubusoft.xenon.camera.CameraManager
import com.abubusoft.xenon.math.Matrix4x4
import com.abubusoft.xenon.math.XenonMath
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshFactory
import com.abubusoft.xenon.mesh.MeshOptions
import com.abubusoft.xenon.mesh.TextureCoordRect
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.shader.ArgonShaderOptions
import com.abubusoft.xenon.shader.Shader
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.shader.ShaderTexture
import com.abubusoft.xenon.shader.drawers.ShaderDrawer
import com.abubusoft.xenon.texture.RenderedTexture
import com.abubusoft.xenon.texture.RenderedTextureOptions
import com.abubusoft.xenon.texture.TextureManager
import com.abubusoft.xenon.vbo.BufferAllocationType

class RenderPipeline(context: Context?, val name: String, val index: Int, sceneDrawer: SceneDrawer, options: RenderPipelineOptions) {
    var clearColor = 0
    var currentEffect: AbstractEffect<*>? = null
    val effects: ArrayList<AbstractEffect<*>?>

    /**
     * rendered texture usata come input di un passo
     */
    private var inputTexture: RenderedTexture?
    private val matrixModelview: Matrix4x4

    /**
     * rendered texture come output di un passo
     */
    private var outputTexture: RenderedTexture?

    /**
     * camera usata per il rendering della scena
     */
    protected var sceneCamera: Camera
    protected var sceneMesh: Mesh? = null

    /**
     * distanza della scena dal viewport
     */
    private val sceneZDistance: Float
    var swapTextureTemp: RenderedTexture? = null
    protected var sceneDrawer: SceneDrawer
    protected var sceneShader: Shader

    init {
        matrixModelview = Matrix4x4()
        effects = ArrayList()
        val tm = TextureManager.instance()

        // la texture è quadrata
        inputTexture = tm.createRenderedTexture(context, options.viewportDimensions, RenderedTextureOptions.build())
        outputTexture = tm.createRenderedTexture(context, options.viewportDimensions, RenderedTextureOptions.build())
        this.sceneDrawer = sceneDrawer
        sceneCamera = CameraManager.instance().createCamera(options.viewportDimensions!!.width, options.viewportDimensions!!.height)
        // distanza da usare per il draw dentro la texture
        sceneZDistance = XenonMath.zDistanceForSquare(sceneCamera, options.viewportDimensions!!.width.toFloat())

        //
        sceneShader = ShaderManager.instance().createShader(ShaderTexture::class.java, ArgonShaderOptions.build())

        // creiamo lo shape per la scena
        sceneMesh = if (options.optimized) {
            if (XenonGL.screenInfo.isPortraitMode) {
                val rect = TextureCoordRect.buildFromCenter(XenonGL.screenInfo.aspectRatio, 1f)
                MeshFactory.createPlaneMesh(
                    options.viewportDimensions!!.width * XenonGL.screenInfo.aspectRatio, options.viewportDimensions!!.height.toFloat(), 1, 1,
                    MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true).textureCoordRect(rect)
                )
            } else {
                val rect = TextureCoordRect.buildFromCenter(1f, 1f / XenonGL.screenInfo.aspectRatio)
                MeshFactory.createPlaneMesh(
                    options.viewportDimensions!!.width.toFloat(), options.viewportDimensions!!.height / XenonGL.screenInfo.aspectRatio, 1, 1,
                    MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true).textureCoordRect(rect)
                )
            }
        } else {
            MeshFactory.createPlaneMesh(
                options.viewportDimensions!!.width.toFloat(),
                options.viewportDimensions!!.height.toFloat(),
                1,
                1,
                MeshOptions.build().texturesCount(1).bufferAllocation(BufferAllocationType.STATIC).textureInverseY(true)
            )
        }
    }

    /**
     *
     *
     * Aggiunge un effetto alla pipeline grafica.
     *
     *
     * @param effect
     * @return
     */
    fun <E : AbstractEffect<*>?> addEffect(context: Context?, clazzEffect: Class<E>, options: ArgonShaderOptions?): E? {
        var effect: E? = null
        try {
            effect = clazzEffect.newInstance()
            effect!!.setup(context, inputTexture!!.info.dimension.width.toFloat(), inputTexture!!.info.dimension.height.toFloat(), options!!, this)
            effects.add(effect)
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.error(e.message)
        }
        return effect
    }

    /**
     *
     *
     * Esegue il rendering su una texture.
     *
     *
     * @param camera
     * @param enlapsedTime
     * @param speedAdapter
     * @return texture ottenuta
     */
    fun executeOnTexture(camera: Camera?, enlapsedTime: Long, speedAdapter: Float): RenderedTexture? {
        val n = effects.size
        outputTexture!!.activate()
        sceneClear()
        sceneDrawer.drawScene(sceneCamera, enlapsedTime, speedAdapter)

        // prepariamo matrice
        matrixModelview.buildIdentityMatrix()
        matrixModelview.translate(0f, 0f, -sceneZDistance)
        matrixModelview.multiply(sceneCamera.info.projection4CameraMatrix, matrixModelview)
        for (i in 0 until n) {
            swapTexture()
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            currentEffect = effects[i]
            outputTexture!!.activate()
            currentEffect!!.clear()
            currentEffect!!.execute(inputTexture, sceneMesh!!, matrixModelview, enlapsedTime, speedAdapter)
        }

        // disabilitiamo il framebuffer utilizzato
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        return outputTexture
    }

    /**
     *
     *
     * Esegue la pipeline e disegna il risultato direttamente sullo schermo.
     *
     *
     * @param enlapsedTime
     * tempo trascorso dall'ultimo frame
     * @param speedAdapter
     * fattore di moltiplicazione per adattare una velocità al secondo
     */
    fun executeOnScreen(camera: Camera, enlapsedTime: Long, speedAdapter: Float) {
        sceneClear()
        if (effects.size == 0) {
            sceneDrawer.drawScene(camera, enlapsedTime, speedAdapter)
        } else {
            executeOnTexture(camera, enlapsedTime, speedAdapter)

            // torniamo a disegnare sullo schermo
            GLES20.glViewport(0, 0, XenonGL.screenInfo.width, XenonGL.screenInfo.height)
            matrixModelview.buildIdentityMatrix()
            matrixModelview.translate(0f, 0f, -sceneZDistance)
            matrixModelview.multiply(camera.info.projection4CameraMatrix, matrixModelview)
            sceneShader.use()
            sceneShader.setVertexCoordinatesArray(sceneMesh!!.vertices)
            sceneShader.setTextureCoordinatesArray(0, sceneMesh!!.textures[0])
            sceneShader.setTexture(0, outputTexture)
            ShaderDrawer.draw(sceneShader, sceneMesh, matrixModelview)
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
     *
     *
     * Attiviamo texture per disegnare scena.
     *
     */
    protected fun sceneClear() {
        clearColor = Color.BLACK
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glClearColor(Color.red(clearColor).toFloat(), Color.green(clearColor).toFloat(), Color.blue(clearColor).toFloat(), 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
    }

    /**
     *
     *
     * Invertiamo le texture.
     *
     */
    protected fun swapTexture() {
        swapTextureTemp = inputTexture
        inputTexture = outputTexture
        outputTexture = swapTextureTemp
    }
}