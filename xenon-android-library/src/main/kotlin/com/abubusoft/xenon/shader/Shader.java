/**
 * Represents a shader object
 */

package com.abubusoft.xenon.shader;

import java.nio.FloatBuffer;

import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.opengl.XenonGL;
import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureReference;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.ColorBuffer;
import com.abubusoft.xenon.vbo.IndexBuffer;
import com.abubusoft.xenon.vbo.TextureBuffer;
import com.abubusoft.xenon.vbo.VertexBuffer;
import com.abubusoft.xenon.core.Uncryptable;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.IOUtility;

import android.content.Context;
import android.opengl.GLES20;

/**
 * <p>
 * Gestore degli share. Il suo compito consiste nel compilare uno shader e dargli la possibilità di bindare le varie proprità a runtime.
 * </p>
 * 
 * <p>
 * Le regole per definire le variabili negli shader:
 * </p>
 * 
 * <ul>
 * <li><b>Attributi (cambiano per vertex)</b>: gli attributi negli shader iniziano con "a_"</li>
 * <li><b>Uniform (costanti per lo shader)</b>: gli attributi negli shader iniziano con "u_"</li>
 * <li><b>Variant (costanti per lo shader)</b>: le varianti (passano da vertex a fragment) "v_"</li>
 * </ul>
 * 
 * <p>
 * Di base gli shader hanno
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@Uncryptable
public class Shader {

	/**
	 * indica se verificare la presenza di errori dopo aver eseguito un comando.
	 */
	public boolean checkErrors = true;

	/**
	 * Nome dello shader
	 */
	public String name;

	/**
	 * <p>
	 * Consente di derivare degli shader da questa classe. Nel costruttore è sufficiente definire il builder con tutte le opzioni.
	 * </p>
	 */
	protected ShaderBuilder builder;

	/**
	 * <p>
	 * uid opengl del programma
	 * </p>
	 */
	protected int programId;

	/**
	 * pointer del vettore delle coordinate dei vertici
	 */
	protected int positionPtr = -1;

	/**
	 * pointer del vettore dei colori
	 */
	protected int colorPtr = -1;

	/**
	 * pointer valore iniziale
	 */
	protected int initialValuePtr = -1;

	/**
	 * pointer delle texture
	 */
	protected int[] texturePtr;

	/**
	 * pointer del vettore delle coordinate delle texture
	 */
	protected int[] textureCoordinatePtr;

	/**
	 * texture attivabili
	 */
	protected static int[] textureActivation = { GLES20.GL_TEXTURE0, GLES20.GL_TEXTURE1, GLES20.GL_TEXTURE2, GLES20.GL_TEXTURE3, GLES20.GL_TEXTURE4, GLES20.GL_TEXTURE5, GLES20.GL_TEXTURE6, GLES20.GL_TEXTURE7 };

	/**
	 * pointer della percentuale
	 */
	protected int percentagePtr = -1;

	/**
	 * pointer della velocità
	 */
	protected int velocityPtr = -1;

	/**
	 * pointer della matrice di proiezione
	 */
	protected int modelViewProjectionMatrixPtr = -1;

	private int timePtr;

	/**
	 * <p>
	 * Assegna i pointer alle variabili dello shader.
	 * </p>
	 */
	protected void assignPtrs() {
		// attribute
		positionPtr = GLES20.glGetAttribLocation(programId, "a_position");
		colorPtr = GLES20.glGetAttribLocation(programId, "a_color");

		// texture
		for (int i = 0; i < numTextures; i++) {
			texturePtr[i] = GLES20.glGetUniformLocation(programId, "u_texture" + i);
			textureCoordinatePtr[i] = GLES20.glGetAttribLocation(programId, "a_textureCoordinate" + i);
		}

		// uniform (currentFramePercentage, mvp)
		percentagePtr = GLES20.glGetUniformLocation(programId, "u_percentage");
		velocityPtr = GLES20.glGetUniformLocation(programId, "u_velocity");
		initialValuePtr = GLES20.glGetUniformLocation(programId, "u_initialValue");
		timePtr = GLES20.glGetUniformLocation(programId, "u_time");

		modelViewProjectionMatrixPtr = GLES20.glGetUniformLocation(programId, "u_mvpMatrix");

		// attributi
		for (int i = 0; i < numUniformAttributes; i++) {
			uniformAttributePtr[i] = GLES20.glGetUniformLocation(programId, "u_attribute" + i);
		}

	}

	/**
	 * numero di texture presenti
	 */
	private int numTextures;
	private int numUniformAttributes;

	// numero di attributi
	private int[] uniformAttributePtr;

	/************************
	 * CONSTRUCTOR(S)
	 *************************/
	/**
	 * <p>
	 * Costruttore usato mediante reflection.
	 * </p>
	 */
	protected Shader() {

	}

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
	protected void setupFromFiles(Context context, int vertexProgramId, int fragmentProgramId, ArgonShaderOptions options) {
		String vertex = "";
		String fragment = "";

		// read the files
		try {
			vertex = IOUtility.readRawTextFile(context, vertexProgramId);
			fragment = IOUtility.readRawTextFile(context, fragmentProgramId);
		} catch (Exception e) {
			Logger.error("ERROR-readingShader - could not read shader - %s", e.getLocalizedMessage());
		}

		// Setup everything
		setup(vertex, fragment, options);
	}

	/**
	 * <p>
	 * Esegue l'inizializzazione dello shader in base al builder definito. Se options è null viene preso l'options del builder.
	 * </p>
	 * 
	 * @param context
	 */
	protected void setupFromBuilder(Context context, ArgonShaderOptions options) {

		if (builder != null) {
			setupFromFiles(context, builder.vertexProgramId, builder.fragmentProgramId, options != null ? options : builder.options);
		} else {
			throw (new XenonRuntimeException("Shader builder not defined!"));
		}
	}

	/**
	 * <p>
	 * Setup dello shader
	 * </p>
	 * 
	 * @param vertexSourceValue
	 * @param fragmentSourceValue
	 * @param options
	 */
	protected void setup(String vertexSourceValue, String fragmentSourceValue, ArgonShaderOptions options) {
		options = options == null ? ArgonShaderOptions.build() : options;

		// create the program
		vertexSource = vertexSourceValue;
		fragmentSource = fragmentSourceValue;

		// eventualmente lo prepariamo per le texture esterne.
		if (options.useForExternalTexture) {
			fragmentSource = "#extension GL_OES_EGL_image_external : require\n" + fragmentSource;
			fragmentSource = fragmentSource.replace("sampler2D u_texture0", "samplerExternalOES u_texture0");
		}

		setupOptions(options);

		// preprocessore
		preprocessor(options);

		// crea il programma
		createProgram();

		// se richiesto, mettiamo nella cartella cache gli shader
		if (options.debugOnFile) {
			String name = this.getClass().getSimpleName();
			IOUtility.writeTempRawTextFile((Context) XenonBeanContext.getBean(XenonBeanType.CONTEXT), name + "_vertex.glsl", vertexSource);
			IOUtility.writeTempRawTextFile((Context) XenonBeanContext.getBean(XenonBeanType.CONTEXT), name + "_fragment.glsl", fragmentSource);
		}

		// texture variables
		numTextures = options.numberOfTextures;

		texturePtr = new int[numTextures];
		textureCoordinatePtr = new int[numTextures];

		// attributes
		numUniformAttributes = options.numberOfUniformAttributes;
		uniformAttributePtr = new int[numUniformAttributes];

		assignPtrs();
	}

	/**
	 * rigenera lo shader, ridefinendo solo i ptr e ricaricando i sorgenti.
	 */
	public void reload() {
		unbind();

		// create the program
		createProgram();

		assignPtrs();
	}

	protected String vertexSource;

	protected String fragmentSource;

	private int vertexProgramId;

	private int fragmentProgramId;

	/**
	 * Creates a shader program.
	 * 
	 * @param vertexSource
	 * @param fragmentSource
	 */
	private void createProgram() {
		// Vertex shader
		vertexProgramId = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexProgramId == 0) {
			Logger.error("Shader %s - Could create vertexShaderPtr:\n%s ", this.getClass().getSimpleName(), vertexSource);
			throw (new XenonRuntimeException("Shader - Could create vertexShaderPtr"));
		}

		// pixel shader
		fragmentProgramId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (fragmentProgramId == 0) {
			Logger.error("Shader %s - Could create fragmentShaderPtr:\n%s ", this.getClass().getSimpleName(), fragmentSource);
			throw (new XenonRuntimeException("Shader - Could create fragmentShaderPtr"));
		}

		// Create the program
		programId = GLES20.glCreateProgram();
		if (programId != 0) {
			GLES20.glAttachShader(programId, vertexProgramId);
			GLES20.glAttachShader(programId, fragmentProgramId);
			GLES20.glLinkProgram(programId);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programId, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Logger.error("Shader %s - Could not link _program: %s", this.getClass().getSimpleName(), GLES20.glGetProgramInfoLog(programId));
				GLES20.glDeleteProgram(programId);
				programId = 0;

				throw (new XenonRuntimeException(String.format("Shader - Could not link _program: %s", GLES20.glGetProgramInfoLog(programId))));
			}
			
			GLES20.glDetachShader(programId, vertexProgramId);
			GLES20.glDetachShader(programId, fragmentProgramId);
			
			GLES20.glDeleteShader(vertexProgramId);			
			GLES20.glDeleteShader(fragmentProgramId);
			
			Logger.info("Shader - Create program %s", programId);
			
		} else {
			Logger.error("Shader - Could not create program");
			throw (new XenonRuntimeException("Shader - Could not create program"));
		}

	}

	/**
	 * Loads a shader (either vertex or pixel) given the source
	 * 
	 * @param shaderType
	 *            VERTEX or PIXEL
	 * @param source
	 *            The string data representing the shader code
	 * @return handle for shader
	 */
	private int loadShader(int shaderType, String source) {
		int shader = GLES20.glCreateShader(shaderType);
		if (shader != 0) {
			GLES20.glShaderSource(shader, source);
			GLES20.glCompileShader(shader);
			int[] compiled = new int[1];
			GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
			Logger.info("Results of compiling shader: %s message: %s", shader, GLES20.glGetShaderInfoLog(shader));
			if (compiled[0] == 0) {
				Logger.error("Shader - Could not compile shader %s :", shaderType);
				Logger.error("Shader %s", GLES20.glGetShaderInfoLog(shader));
				GLES20.glDeleteShader(shader);
				shader = 0;
			}
		}
		return shader;
	}

	/**
	 * Attiva lo shader
	 * 
	 * @return
	 */
	public boolean use() {
		// Use the program object

		GLES20.glUseProgram(programId);

		// GLES20.glGetShaderInfoLog(programId);
		// Logger.debug(">>>>>>>>>>>>>>>> "+GLES20.glGetProgramInfoLog(programId));

		if (checkErrors)
			XenonGL.checkGlError("Shader (id="+programId+") use");

		return true;
	}

	public void close() {
		GLES20.glDisableVertexAttribArray(positionPtr);

		if (colorPtr != -1)
			GLES20.glDisableVertexAttribArray(colorPtr);

		for (int i = 0; i < numTextures; i++) {
			GLES20.glDisableVertexAttribArray(textureCoordinatePtr[i]);
		}

		for (int i = 0; i < numUniformAttributes; i++) {
			GLES20.glDisableVertexAttribArray(textureCoordinatePtr[i]);
		}

	}

	public int getProgramId() {
		return programId;
	}

	/**
	 * Imposta l'array dei vertici.
	 * 
	 * @param vertices
	 */
	public void setVertexCoordinatesArray(VertexBuffer vbo) {
		if (vbo.allocation == BufferAllocationType.CLIENT) {
			GLES20.glVertexAttribPointer(positionPtr, 3, GLES20.GL_FLOAT, false, 0, vbo.buffer);
			GLES20.glEnableVertexAttribArray(positionPtr);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setVertexCoordinatesArray");
		} else {

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo.bindingId);

			GLES20.glVertexAttribPointer(positionPtr, 3, GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glEnableVertexAttribArray(positionPtr);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setVertexCoordinatesArray");

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertexBuffer.BINDING_ID_INVALID);
		}
	}

	/**
	 * Imposta l'array dei vertici.
	 * 
	 * @param vertices
	 */
	public void setColorsArray(ColorBuffer value) {
		if (value.allocation == BufferAllocationType.CLIENT) {
			GLES20.glVertexAttribPointer(colorPtr, value.vertexDimension(), GLES20.GL_FLOAT, false, 0, value.buffer);
			GLES20.glEnableVertexAttribArray(colorPtr);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setColorsArray");
		} else {

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, value.bindingId);

			GLES20.glVertexAttribPointer(colorPtr, value.vertexDimension(), GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glEnableVertexAttribArray(colorPtr);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setColorsArray");

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertexBuffer.BINDING_ID_INVALID);
		}
	}

	/**
	 * Consente di modificare le opzioni prima del preprocessing.
	 * 
	 * @param options
	 */
	public void setupOptions(ArgonShaderOptions options) {

	}

	/**
	 * Attiva la texture i-esima
	 * 
	 * @param i
	 * @param textureReference
	 *            reference della texture
	 */
	public void setTexture(int i, TextureReference textureReference) {
		GLES20.glActiveTexture(textureActivation[i]);
		GLES20.glUniform1i(texturePtr[i], i);
		GLES20.glBindTexture(textureReference.get().info.type.value, textureReference.get().bindingId);

		if (checkErrors)
			XenonGL.checkGlError("Shader (id="+programId+") setTextureBinding ", ""+i);
	}

	/**
	 * Attiva la texture i-esima
	 * 
	 * @param i
	 * @param texture
	 */
	public void setTexture(int i, Texture texture) {
		GLES20.glActiveTexture(textureActivation[i]);
		GLES20.glUniform1i(texturePtr[i], i);
		GLES20.glBindTexture(texture.info.type.value, texture.bindingId);

		if (checkErrors)
			XenonGL.checkGlError("Shader (id="+programId+") setTextureBinding ", ""+i);
	}

	/**
	 * Imposta la matrice MVP
	 * 
	 * @param matrixFloatBuffer
	 */
	public void setModelViewProjectionMatrix(FloatBuffer matrixFloatBuffer) {
		GLES20.glUniformMatrix4fv(modelViewProjectionMatrixPtr, 1, false, matrixFloatBuffer);

		if (checkErrors)
			XenonGL.checkGlError("Shader (id="+programId+") setModelViewProjectionMatrix");

	}

	/**
	 * <p>
	 * Definisce l'index buffer da usare.
	 * </p>
	 * 
	 * @param indexBuffer
	 */
	public void setIndexBuffer(IndexBuffer indexBuffer) {
		if (indexBuffer.allocation != BufferAllocationType.CLIENT) {
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bindingId);
		}
	}

	/**
	 * <p>
	 * Rimuove l'index buffer usato.
	 * </p>
	 * 
	 * @param indexBuffer
	 */
	public void unsetIndexBuffer(IndexBuffer indexBuffer) {
		if (indexBuffer.allocation != BufferAllocationType.CLIENT) {
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, IndexBuffer.BINDING_ID_INVALID);
		}
	}

	/**
	 * Imposta l'array delle coordinate della texture i.
	 * 
	 * @param i
	 * @param textureCoords
	 */
	public void setTextureCoordinatesArray(int i, TextureBuffer textureCoords) {
		if (textureCoords.allocation == BufferAllocationType.CLIENT) {
			GLES20.glVertexAttribPointer(textureCoordinatePtr[i], textureCoords.vertexDimension(), GLES20.GL_FLOAT, false, 0, textureCoords.buffer);
			GLES20.glEnableVertexAttribArray(textureCoordinatePtr[i]);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setTextureCoordinatesArray[" + i + "]");
		} else {
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, textureCoords.bindingId);

			GLES20.glVertexAttribPointer(textureCoordinatePtr[i], textureCoords.vertexDimension(), GLES20.GL_FLOAT, false, 0, 0);
			GLES20.glEnableVertexAttribArray(textureCoordinatePtr[i]);

			if (checkErrors)
				XenonGL.checkGlError("Shader (id="+programId+") setTextureCoordinatesArray[" + i + "]");

			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, VertexBuffer.BINDING_ID_INVALID);
		}
	}

	/**
	 * Imposta una percentuale, da 0f a 1f
	 * 
	 * @param currentFramePercentage
	 */
	public void setPercentage(float value) {
		GLES20.glUniform1f(percentagePtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setPercentage");
	}

	/**
	 * Imposta un initial value, da 0f a 1f
	 * 
	 * @param initialValue
	 */
	public void setInitialValue(float value) {
		GLES20.glUniform1f(initialValuePtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setInitialValue");
	}

	/**
	 * Imposta una velocità, da 0f a 1f
	 * 
	 * @param currentFramePercentage
	 */
	public void setVelocity(float value) {
		GLES20.glUniform1f(velocityPtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setVelocity");
	}

	/**
	 * Imposta il time, in millisecondi
	 * 
	 * @param currentFramePercentage
	 */
	public void setTime(float value) {
		GLES20.glUniform1f(timePtr, value);
		XenonGL.checkGlError("Shader (id="+programId+") setTime");
	}

	/**
	 * Imposta un attributo uniforme di tipo float
	 * 
	 * @param attributo
	 */
	public void setUniformAttribute(int attributeIndex, float value) {
		GLES20.glUniform1f(uniformAttributePtr[attributeIndex], value);
		XenonGL.checkGlError("Shader (id="+programId+") setUniformAttribute " + attributeIndex);
	}

	/**
	 * Imposta un attributo uniforme di tipo float[3]
	 * 
	 * @param attributo
	 */
	public void setUniformAttribute(int attributeIndex, float xvalue, float yvalue, float zvalue) {
		GLES20.glUniform3f(uniformAttributePtr[attributeIndex], xvalue, yvalue, zvalue);
		XenonGL.checkGlError("Shader (id="+programId+") setUniformAttribute " + attributeIndex);
	}

	/**
	 * <p>
	 * Imposta un vector 4 f
	 * </p>
	 * 
	 * @param ptr
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param v4
	 */
	protected void setUniform4f(int ptr, float v1, float v2, float v3, float v4) {
		GLES20.glUniform4f(ptr, v1, v2, v3, v4);
		XenonGL.checkGlError("Shader (id="+programId+") setUniform4f");
	}

	/**
	 * Imposta un attributo uniforme di tipo int
	 * 
	 * @param attributo
	 */
	public void setUniformAttribute(int attributeIndex, int value) {
		GLES20.glUniform1i(uniformAttributePtr[attributeIndex], value);
		XenonGL.checkGlError("Shader (id="+programId+") setUniformAttribute " + attributeIndex);
	}

	/**
	 * Gestisce le costanti e gli ifdef
	 * 
	 * @param options
	 */
	protected void preprocessor(ArgonShaderOptions options) {
		/*
		 * if (Logger.isEnabledFor(LoggerLevelType.DEBUG)) { //Logger.debug ("========== PREPROCESSOR SHADER %s - BEGIN ==========" ,getClass().getCanonicalName());
		 * 
		 * for (int i=0;i<options.pragmaCostants.size();i++) { Logger.debug(" Costant %s = %s" ,options.pragmaCostants.get(i).first, options.pragmaCostants.get(i).second); }
		 * 
		 * for (Map.Entry<String, Boolean> item: options.pragmaDefinitions.entrySet()) { Logger.debug(" Definition %s = %s",item.getKey(), item.getValue()); } }
		 */

		vertexSource = ShaderPreprocessor.preprocessorSource(vertexSource, options);
		fragmentSource = ShaderPreprocessor.preprocessorSource(fragmentSource, options);

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
	public void unbind() {
		Logger.debug("Unbind shader programId " + programId);
		
		GLES20.glDetachShader(programId, vertexProgramId);
		GLES20.glDetachShader(programId, fragmentProgramId);
		
		GLES20.glDeleteShader(vertexProgramId);			
		GLES20.glDeleteShader(fragmentProgramId);
		
		GLES20.glDeleteProgram(programId);
		
		//checkGlError("glDeleteProgram");

		// GLES20.glDeleteShader(vertexProgramId);
		// checkGlError("glDeleteShader vertex");
		// GLES20.glDeleteShader(fragmentProgramId);
		// checkGlError("glDeleteShader fragment");

	}

}
