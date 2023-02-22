/**
 * Represents a shader object
 */
package com.abubusoft.xenon.shader

import android.content.Context
import android.opengl.GLES20
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.core.util.IOUtility.readRawTextFile
import com.abubusoft.xenon.core.util.IOUtility.writeTempRawTextFile
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.texture.Texture
import com.abubusoft.xenon.texture.TextureReference
import com.abubusoft.xenon.vbo.*
import java.nio.FloatBuffer

/**
 *
 *
 * Gestore degli share. Il suo compito consiste nel compilare uno shader e dargli la possibilità di bindare le varie proprità a runtime.
 *
 *
 *
 *
 * Le regole per definire le variabili negli shader:
 *
 *
 *
 *  * **Attributi (cambiano per vertex)**: gli attributi negli shader iniziano con "a_"
 *  * **Uniform (costanti per lo shader)**: gli attributi negli shader iniziano con "u_"
 *  * **Variant (costanti per lo shader)**: le varianti (passano da vertex a fragment) "v_"
 *
 *
 *
 *
 * Di base gli shader hanno
 *
 *
 * @author Francesco Benincasa
 */
@Uncryptable
open class Shader
protected constructor() {
    /**
     * indica se verificare la presenza di errori dopo aver eseguito un comando.
     */
    var checkErrors = true

    /**
     * Nome dello shader
     */
    var name: String? = null

    /**
     *
     *
     * Consente di derivare degli shader da questa classe. Nel costruttore è sufficiente definire il builder con tutte le opzioni.
     *
     */
    protected lateinit var builder: ShaderBuilder

    /**
     *
     *
     * uid opengl del programma
     *
     */
    var programId = 0

    /**
     * pointer del vettore delle coordinate dei vertici
     */
    protected var positionPtr = -1

    /**
     * pointer del vettore dei colori
     */
    protected var colorPtr = -1

    /**
     * pointer valore iniziale
     */
    protected var initialValuePtr = -1

    /**
     * pointer delle texture
     */
    protected var texturePtr: IntArray? = null

    /**
     * pointer del vettore delle coordinate delle texture
     */
    protected var textureCoordinatePtr: IntArray? = null

    /**
     * pointer della percentuale
     */
    protected var percentagePtr = -1

    /**
     * pointer della velocità
     */
    protected var velocityPtr = -1

    /**
     * pointer della matrice di proiezione
     */
    protected var modelViewProjectionMatrixPtr = -1
    private var timePtr = 0

    /**
     *
     *
     * Assegna i pointer alle variabili dello shader.
     *
     */
    protected open fun assignPtrs() {
        // attribute
        positionPtr = GLES20.glGetAttribLocation(programId, "a_position")
        colorPtr = GLES20.glGetAttribLocation(programId, "a_color")

        if (texturePtr != null && textureCoordinatePtr != null) {
            // texture
            for (i in 0 until numTextures) {
                texturePtr!![i] = GLES20.glGetUniformLocation(programId, "u_texture$i")
                textureCoordinatePtr!![i] = GLES20.glGetAttribLocation(programId, "a_textureCoordinate$i")
            }
        }

        // uniform (currentFramePercentage, mvp)
        percentagePtr = GLES20.glGetUniformLocation(programId, "u_percentage")
        velocityPtr = GLES20.glGetUniformLocation(programId, "u_velocity")
        initialValuePtr = GLES20.glGetUniformLocation(programId, "u_initialValue")
        timePtr = GLES20.glGetUniformLocation(programId, "u_time")
        modelViewProjectionMatrixPtr = GLES20.glGetUniformLocation(programId, "u_mvpMatrix")

        // attributi
        for (i in 0 until numUniformAttributes) {
            uniformAttributePtr?.set(i, GLES20.glGetUniformLocation(programId, "u_attribute$i"))
        }
    }

    /**
     * numero di texture presenti
     */
    private var numTextures = 0
    private var numUniformAttributes = 0

    // numero di attributi
    private var uniformAttributePtr: IntArray? = null
    /*
	 * protected Shader(String vertexProgram, String fragmentProgram, ArgonShaderOptions options) { setup(vertexProgram, fragmentProgram, options); }
	 */
    /**
     * @param context
     * @param vertexProgramId
     * @param fragmentProgramId
     * @param options
     */
    /*
	 * protected Shader(Context context, int vertexProgramId, int fragmentProgramId, ArgonShaderOptions options) { setupFromFiles(context, vertexProgramId, fragmentProgramId, options); }
	 */
    /**
     * @param context
     * @param vertexProgramId
     * @param fragmentProgramId
     * @param options
     */
    fun setupFromFiles(context: Context?, vertexProgramId: Int, fragmentProgramId: Int, options: ArgonShaderOptions?) {
        var vertex = ""
        var fragment = ""

        // read the files
        try {
            vertex = readRawTextFile(context!!, vertexProgramId)
            fragment = readRawTextFile(context, fragmentProgramId)
        } catch (e: Exception) {
            Logger.error("ERROR-readingShader - could not read shader - %s", e.localizedMessage)
        }

        // Setup everything
        setup(vertex, fragment, options)
    }

    /**
     *
     *
     * Esegue l'inizializzazione dello shader in base al builder definito. Se options è null viene preso l'options del builder.
     *
     *
     * @param context
     */
    fun setupFromBuilder(context: Context?, options: ArgonShaderOptions?) {
        if (builder != null) {
            setupFromFiles(context, builder!!.vertexProgramId, builder!!.fragmentProgramId, options ?: builder!!.options)
        } else {
            throw XenonRuntimeException("Shader builder not defined!")
        }
    }

    /**
     *
     *
     * Setup dello shader
     *
     *
     * @param vertexSourceValue
     * @param fragmentSourceValue
     * @param options
     */
    protected fun setup(vertexSourceValue: String?, fragmentSourceValue: String?, options: ArgonShaderOptions?) {
        var options = options
        options = options ?: ArgonShaderOptions.Companion.build()

        // create the program
        vertexSource = vertexSourceValue
        fragmentSource = fragmentSourceValue

        // eventualmente lo prepariamo per le texture esterne.
        if (options.useForExternalTexture) {
            fragmentSource = "#extension GL_OES_EGL_image_external : require\n$fragmentSource"
            fragmentSource = fragmentSource!!.replace("sampler2D u_texture0", "samplerExternalOES u_texture0")
        }
        setupOptions(options)

        // preprocessore
        preprocessor(options)

        // crea il programma
        createProgram()

        // se richiesto, mettiamo nella cartella cache gli shader
        if (options.debugOnFile) {
            val name = this.javaClass.simpleName
            writeTempRawTextFile((getBean<Any>(XenonBeanType.CONTEXT) as Context?)!!, name + "_vertex.glsl", vertexSource)
            writeTempRawTextFile((getBean<Any>(XenonBeanType.CONTEXT) as Context?)!!, name + "_fragment.glsl", fragmentSource)
        }

        // texture variables
        numTextures = options.numberOfTextures
        texturePtr = IntArray(numTextures)
        textureCoordinatePtr = IntArray(numTextures)

        // attributes
        numUniformAttributes = options.numberOfUniformAttributes
        uniformAttributePtr = IntArray(numUniformAttributes)
        assignPtrs()
    }

    /**
     * rigenera lo shader, ridefinendo solo i ptr e ricaricando i sorgenti.
     */
    fun reload() {
        unbind()

        // create the program
        createProgram()
        assignPtrs()
    }

    protected var vertexSource: String? = null
    protected var fragmentSource: String? = null
    private var vertexProgramId = 0
    private var fragmentProgramId = 0

    /**
     * Creates a shader program.
     *
     * @param vertexSource
     * @param fragmentSource
     */
    private fun createProgram() {
        // Vertex shader
        vertexProgramId = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource)
        if (vertexProgramId == 0) {
            Logger.error("Shader %s - Could create vertexShaderPtr:\n%s ", this.javaClass.simpleName, vertexSource)
            throw XenonRuntimeException("Shader - Could create vertexShaderPtr")
        }

        // pixel shader
        fragmentProgramId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource)
        if (fragmentProgramId == 0) {
            Logger.error("Shader %s - Could create fragmentShaderPtr:\n%s ", this.javaClass.simpleName, fragmentSource)
            throw XenonRuntimeException("Shader - Could create fragmentShaderPtr")
        }

        // Create the program
        programId = GLES20.glCreateProgram()
        if (programId != 0) {
            GLES20.glAttachShader(programId, vertexProgramId)
            GLES20.glAttachShader(programId, fragmentProgramId)
            GLES20.glLinkProgram(programId)
            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Logger.error("Shader %s - Could not link _program: %s", this.javaClass.simpleName, GLES20.glGetProgramInfoLog(programId))
                GLES20.glDeleteProgram(programId)
                programId = 0
                throw XenonRuntimeException(String.format("Shader - Could not link _program: %s", GLES20.glGetProgramInfoLog(programId)))
            }
            GLES20.glDetachShader(programId, vertexProgramId)
            GLES20.glDetachShader(programId, fragmentProgramId)
            GLES20.glDeleteShader(vertexProgramId)
            GLES20.glDeleteShader(fragmentProgramId)
            Logger.info("Shader - Create program %s", programId)
        } else {
            Logger.error("Shader - Could not create program")
            throw XenonRuntimeException("Shader - Could not create program")
        }
    }

    /**
     * Loads a shader (either vertex or pixel) given the source
     *
     * @param shaderType
     * VERTEX or PIXEL
     * @param source
     * The string data representing the shader code
     * @return handle for shader
     */
    private fun loadShader(shaderType: Int, source: String?): Int {
        var shader = GLES20.glCreateShader(shaderType)
        if (shader != 0) {
            GLES20.glShaderSource(shader, source)
            GLES20.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
            Logger.info("Results of compiling shader: %s message: %s", shader, GLES20.glGetShaderInfoLog(shader))
            if (compiled[0] == 0) {
                Logger.error("Shader - Could not compile shader %s :", shaderType)
                Logger.error("Shader %s", GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    /**
     * Attiva lo shader
     *
     * @return
     */
    fun use(): Boolean {
        // Use the program object
        GLES20.glUseProgram(programId)

        // GLES20.glGetShaderInfoLog(programId);
        // Logger.debug(">>>>>>>>>>>>>>>> "+GLES20.glGetProgramInfoLog(programId));
        if (checkErrors) checkGlError("Shader (id=$programId) use")
        return true
    }

    fun close() {
        GLES20.glDisableVertexAttribArray(positionPtr)
        if (colorPtr != -1) GLES20.glDisableVertexAttribArray(colorPtr)
        for (i in 0 until numTextures) {
            GLES20.glDisableVertexAttribArray(textureCoordinatePtr!![i])
        }
        for (i in 0 until numUniformAttributes) {
            GLES20.glDisableVertexAttribArray(textureCoordinatePtr!![i])
        }
    }

    /**
     * Imposta l'array dei vertici.
     *
     * @param vertices
     */
    fun setVertexCoordinatesArray(vbo: VertexBuffer) {
        if (vbo.allocation === BufferAllocationType.CLIENT) {
            GLES20.glVertexAttribPointer(positionPtr, 3, GLES20.GL_FLOAT, false, 0, vbo.buffer)
            GLES20.glEnableVertexAttribArray(positionPtr)
            if (checkErrors) checkGlError("Shader (id=$programId) setVertexCoordinatesArray")
        } else {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo.bindingId)
            GLES20.glVertexAttribPointer(positionPtr, 3, GLES20.GL_FLOAT, false, 0, 0)
            GLES20.glEnableVertexAttribArray(positionPtr)
            if (checkErrors) checkGlError("Shader (id=$programId) setVertexCoordinatesArray")
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, AbstractBuffer.BINDING_ID_INVALID)
        }
    }

    /**
     * Imposta l'array dei vertici.
     *
     * @param vertices
     */
    fun setColorsArray(value: ColorBuffer) {
        if (value.allocation === BufferAllocationType.CLIENT) {
            GLES20.glVertexAttribPointer(colorPtr, value.vertexDimension(), GLES20.GL_FLOAT, false, 0, value.buffer)
            GLES20.glEnableVertexAttribArray(colorPtr)
            if (checkErrors) checkGlError("Shader (id=$programId) setColorsArray")
        } else {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, value.bindingId)
            GLES20.glVertexAttribPointer(colorPtr, value.vertexDimension(), GLES20.GL_FLOAT, false, 0, 0)
            GLES20.glEnableVertexAttribArray(colorPtr)
            if (checkErrors) checkGlError("Shader (id=$programId) setColorsArray")
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, AbstractBuffer.BINDING_ID_INVALID)
        }
    }

    /**
     * Consente di modificare le opzioni prima del preprocessing.
     *
     * @param options
     */
    fun setupOptions(options: ArgonShaderOptions?) {}

    /**
     * Attiva la texture i-esima
     *
     * @param i
     * @param textureReference
     * reference della texture
     */
    fun setTexture(i: Int, textureReference: TextureReference) {
        GLES20.glActiveTexture(textureActivation[i])
        GLES20.glUniform1i(texturePtr!![i], i)
        GLES20.glBindTexture(textureReference.get().info.type.value, textureReference.get().bindingId)
        if (checkErrors) checkGlError("Shader (id=$programId) setTextureBinding ", "" + i)
    }

    /**
     * Attiva la texture i-esima
     *
     * @param i
     * @param texture
     */
    fun setTexture(i: Int, texture: Texture) {
        GLES20.glActiveTexture(textureActivation[i])
        GLES20.glUniform1i(texturePtr!![i], i)
        GLES20.glBindTexture(texture.info.type.value, texture.bindingId)
        if (checkErrors) checkGlError("Shader (id=$programId) setTextureBinding ", "" + i)
    }

    /**
     * Imposta la matrice MVP
     *
     * @param matrixFloatBuffer
     */
    fun setModelViewProjectionMatrix(matrixFloatBuffer: FloatBuffer?) {
        GLES20.glUniformMatrix4fv(modelViewProjectionMatrixPtr, 1, false, matrixFloatBuffer)
        if (checkErrors) checkGlError("Shader (id=$programId) setModelViewProjectionMatrix")
    }

    /**
     *
     *
     * Definisce l'index buffer da usare.
     *
     *
     * @param indexBuffer
     */
    fun setIndexBuffer(indexBuffer: IndexBuffer) {
        if (indexBuffer.allocation !== BufferAllocationType.CLIENT) {
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bindingId)
        }
    }

    /**
     *
     *
     * Rimuove l'index buffer usato.
     *
     *
     * @param indexBuffer
     */
    fun unsetIndexBuffer(indexBuffer: IndexBuffer) {
        if (indexBuffer.allocation !== BufferAllocationType.CLIENT) {
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, AbstractBuffer.BINDING_ID_INVALID)
        }
    }

    /**
     * Imposta l'array delle coordinate della texture i.
     *
     * @param i
     * @param textureCoords
     */
    fun setTextureCoordinatesArray(i: Int, textureCoords: TextureBuffer) {
        if (textureCoords.allocation === BufferAllocationType.CLIENT) {
            GLES20.glVertexAttribPointer(textureCoordinatePtr!![i], textureCoords.vertexDimension(), GLES20.GL_FLOAT, false, 0, textureCoords.buffer)
            GLES20.glEnableVertexAttribArray(textureCoordinatePtr!![i])
            if (checkErrors) checkGlError("Shader (id=$programId) setTextureCoordinatesArray[$i]")
        } else {
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCoords.bindingId)
            GLES20.glVertexAttribPointer(textureCoordinatePtr!![i], textureCoords.vertexDimension(), GLES20.GL_FLOAT, false, 0, 0)
            GLES20.glEnableVertexAttribArray(textureCoordinatePtr!![i])
            if (checkErrors) checkGlError("Shader (id=$programId) setTextureCoordinatesArray[$i]")
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, AbstractBuffer.BINDING_ID_INVALID)
        }
    }

    /**
     * Imposta una percentuale, da 0f a 1f
     *
     * @param currentFramePercentage
     */
    fun setPercentage(value: Float) {
        GLES20.glUniform1f(percentagePtr, value)
        checkGlError("Shader (id=$programId) setPercentage")
    }

    /**
     * Imposta un initial value, da 0f a 1f
     *
     * @param initialValue
     */
    fun setInitialValue(value: Float) {
        GLES20.glUniform1f(initialValuePtr, value)
        checkGlError("Shader (id=$programId) setInitialValue")
    }

    /**
     * Imposta una velocità, da 0f a 1f
     *
     * @param currentFramePercentage
     */
    fun setVelocity(value: Float) {
        GLES20.glUniform1f(velocityPtr, value)
        checkGlError("Shader (id=$programId) setVelocity")
    }

    /**
     * Imposta il time, in millisecondi
     *
     * @param currentFramePercentage
     */
    fun setTime(value: Float) {
        GLES20.glUniform1f(timePtr, value)
        checkGlError("Shader (id=$programId) setTime")
    }

    /**
     * Imposta un attributo uniforme di tipo float
     *
     * @param attributo
     */
    fun setUniformAttribute(attributeIndex: Int, value: Float) {
        GLES20.glUniform1f(uniformAttributePtr!![attributeIndex], value)
        checkGlError("Shader (id=$programId) setUniformAttribute $attributeIndex")
    }

    /**
     * Imposta un attributo uniforme di tipo float[3]
     *
     * @param attributo
     */
    fun setUniformAttribute(attributeIndex: Int, xvalue: Float, yvalue: Float, zvalue: Float) {
        GLES20.glUniform3f(uniformAttributePtr!![attributeIndex], xvalue, yvalue, zvalue)
        checkGlError("Shader (id=$programId) setUniformAttribute $attributeIndex")
    }

    /**
     *
     *
     * Imposta un vector 4 f
     *
     *
     * @param ptr
     * @param v1
     * @param v2
     * @param v3
     * @param v4
     */
    protected fun setUniform4f(ptr: Int, v1: Float, v2: Float, v3: Float, v4: Float) {
        GLES20.glUniform4f(ptr, v1, v2, v3, v4)
        checkGlError("Shader (id=$programId) setUniform4f")
    }

    /**
     * Imposta un attributo uniforme di tipo int
     *
     * @param attributo
     */
    fun setUniformAttribute(attributeIndex: Int, value: Int) {
        GLES20.glUniform1i(uniformAttributePtr!![attributeIndex], value)
        checkGlError("Shader (id=$programId) setUniformAttribute $attributeIndex")
    }

    /**
     * Gestisce le costanti e gli ifdef
     *
     * @param options
     */
    protected fun preprocessor(options: ArgonShaderOptions?) {
        /*
		 * if (Logger.isEnabledFor(LoggerLevelType.DEBUG)) { //Logger.debug ("========== PREPROCESSOR SHADER %s - BEGIN ==========" ,getClass().getCanonicalName());
		 * 
		 * for (int i=0;i<options.pragmaCostants.size();i++) { Logger.debug(" Costant %s = %s" ,options.pragmaCostants.get(i).first, options.pragmaCostants.get(i).second); }
		 * 
		 * for (Map.Entry<String, Boolean> item: options.pragmaDefinitions.entrySet()) { Logger.debug(" Definition %s = %s",item.getKey(), item.getValue()); } }
		 */
        vertexSource = ShaderPreprocessor.preprocessorSource(vertexSource, options)
        fragmentSource = ShaderPreprocessor.preprocessorSource(fragmentSource, options)

        // Logger.debug("========== SHADER %s - BEGIN ==========",
        // getClass().getCanonicalName());
        // if (Logger.isEnabledFor(LoggerLevelType.DEBUG)) {
        // Logger.debug("========== vertex Source ==========");
        // Logger.debug(vertexSource);
        //
        // Logger.debug("========== fragment Source ==========");
        // Logger.debug(fragmentSource);
        // }
    }

    /**
     * provvede a cancellare lo shader
     */
    fun unbind() {
        Logger.debug("Unbind shader programId $programId")
        GLES20.glDetachShader(programId, vertexProgramId)
        GLES20.glDetachShader(programId, fragmentProgramId)
        GLES20.glDeleteShader(vertexProgramId)
        GLES20.glDeleteShader(fragmentProgramId)
        GLES20.glDeleteProgram(programId)

        //checkGlError("glDeleteProgram");

        // GLES20.glDeleteShader(vertexProgramId);
        // checkGlError("glDeleteShader vertex");
        // GLES20.glDeleteShader(fragmentProgramId);
        // checkGlError("glDeleteShader fragment");
    }

    companion object {
        /**
         * texture attivabili
         */
        protected var textureActivation = intArrayOf(
            GLES20.GL_TEXTURE0,
            GLES20.GL_TEXTURE1,
            GLES20.GL_TEXTURE2,
            GLES20.GL_TEXTURE3,
            GLES20.GL_TEXTURE4,
            GLES20.GL_TEXTURE5,
            GLES20.GL_TEXTURE6,
            GLES20.GL_TEXTURE7
        )
    }
}