/**
 * 
 */
package com.abubusoft.xenon.shader;

/**
 * @author Francesco Benincasa
 * 
 */
public class ShaderBuilder {

	/**
	 * <p>
	 * </p>
	 * 
	 * @param vertexProgramIdValue
	 * @param fragmentProgramIdValue
	 */
	private ShaderBuilder(int vertexProgramIdValue, int fragmentProgramIdValue, ArgonShaderOptions optionsValue) {
		vertexProgramId = vertexProgramIdValue;
		fragmentProgramId = fragmentProgramIdValue;
		options = optionsValue;
	}
	
	/**
	 * <p>Creatore di builder</p>
	 * 
	 * @param vertexProgramIdValue
	 * @param fragmentProgramIdValue
	 * @param optionsValue
	 * @return
	 * 		builder
	 */
	public static ShaderBuilder build(int vertexProgramIdValue, int fragmentProgramIdValue, ArgonShaderOptions optionsValue)
	{
		ShaderBuilder builder=new ShaderBuilder(vertexProgramIdValue, fragmentProgramIdValue, optionsValue);
		
		return builder;
	}

	/**
	 * <p>
	 * resourceId del vertex program, se esiste. Non viene usata per la
	 * rigenerazione dello shader.
	 * </p>
	 */
	public int vertexProgramId;

	/**
	 * <p>
	 * resourceId del fragment program, se esiste. Non viene usata per la
	 * rigenerazione dello shader.
	 * </p>
	 */
	public int fragmentProgramId;

	/**
	 * <p>
	 * opzioni per lo shader.
	 * </p>
	 */
	public ArgonShaderOptions options;
}
