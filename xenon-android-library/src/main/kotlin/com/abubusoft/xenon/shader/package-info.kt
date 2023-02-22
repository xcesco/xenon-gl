/**
 *
 * Contiene le informazioni necessarie a gestire gli shader.
 *
 * Ricordiamo che per semplificare lo sviluppo degli shader sono stati introdotti delle istruzioni
 * per il preprocessore degli shader:
 *
 * Se definisco nelle opzioni dello shader:
 * <pre>
 * ArgonShaderOptions opts = ArgonShaderOptions.build();
 * opts.define("VADI", true);
</pre> *
 *
 * Il define deve impostarlo a TRUE, altrimenti sarà come non averlo definito.
 * Allora potrò usare questa condizione per far compilare una parte di codice
 * <pre>
 * //@ifdef VADI
 * [codice da eseguire]
 * //@endif
</pre> *
 *
 * La definizione dovrà impostare il rispettivo valore a true per poter essere incluso nello shader.
 *
 * Altre istruzioni per il preprocessore sono:
 *
 *  * //@ifndef :  quando voglio eseguire il codice solo quando la variabile non è definita
 *  * //@else
 *  * //@endif
 *
 *
 */
package com.abubusoft.xenon.shader

import com.abubusoft.xenon.core.util.IOUtility.readRawTextFile
import com.abubusoft.xenon.core.util.IOUtility.writeTempRawTextFile
import com.abubusoft.xenon.context.XenonBeanContext.getBean
import com.abubusoft.xenon.opengl.XenonGL.checkGlError
import com.abubusoft.xenon.vbo.AbstractBuffer.vertexDimension
import com.abubusoft.xenon.opengl.XenonGL.clearGlError
import android.annotation.SuppressLint
import com.abubusoft.xenon.shader.ArgonShaderOptions
import com.abubusoft.xenon.core.Uncryptable
import com.abubusoft.xenon.shader.ShaderBuilder
import android.opengl.GLES20
import com.abubusoft.xenon.core.util.IOUtility
import com.abubusoft.xenon.core.XenonRuntimeException
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.XenonGL
import com.abubusoft.xenon.vbo.VertexBuffer
import com.abubusoft.xenon.vbo.BufferAllocationType
import com.abubusoft.xenon.vbo.ColorBuffer
import com.abubusoft.xenon.texture.TextureReference
import com.abubusoft.xenon.vbo.IndexBuffer
import com.abubusoft.xenon.vbo.TextureBuffer
import com.abubusoft.xenon.shader.ShaderPreprocessor
import com.abubusoft.xenon.R
import android.util.SparseArray
import com.abubusoft.xenon.shader.VideoShader
import com.abubusoft.xenon.shader.ShaderTexture
import com.abubusoft.xenon.shader.ShaderLine
import com.abubusoft.xenon.shader.ShaderTiledMap
import com.abubusoft.xenon.shader.ShaderManager
import com.abubusoft.xenon.shader.ShaderPreprocessorException
