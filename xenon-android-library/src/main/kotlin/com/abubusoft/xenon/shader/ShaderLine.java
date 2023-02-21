/**
 * 
 */
package com.abubusoft.xenon.shader;

import com.abubusoft.xenon.R;

import android.graphics.Color;
import android.opengl.GLES20;

/**
 * @author Francesco Benincasa
 *
 */
public class ShaderLine extends Shader {
	/**
	 * Costruisce una tile
	 * 
	 * @param context
	 * @param options
	 */
	public ShaderLine() {
		builder=ShaderBuilder.build(R.raw.shader_line_vertex, R.raw.shader_line_fragment, ArgonShaderOptions.build().name("line").numberOfTextures(0).numberOfUniformAttributes(0));	
	}
	
	/**
	 * <p></p>
	 * @param a
	 * 			da 0 a 255
	 * @param r
	 * 			da 0 a 255
	 * @param g
	 * 			da 0 a 255
	 * @param b
	 * 			da 0 a 255
	 */
	public void setColor(int a, int r, int g, int b)
	{
		// il formato dei colori è rgba
		setUniform4f(colorPtr, r/255f, g/255f, b/255f, a/255f);
	}
	
	/**
	 * <p></p>
	 * @param r
	 * 			da 0 a 255
	 * @param g
	 * 			da 0 a 255
	 * @param b
	 * 			da 0 a 255
	 */
	public void setColor(int argb)
	{
		// il formato dei colori è rgba
		setUniform4f(colorPtr, Color.red(argb)/255f, Color.green(argb)/255f, Color.blue(argb)/255f, Color.alpha(argb)/255f);
	}

	@Override
	protected void assignPtrs() {
		super.assignPtrs();
		
		colorPtr = GLES20.glGetUniformLocation(programId, "u_color");		
	}
	
	
	
	
	
}
