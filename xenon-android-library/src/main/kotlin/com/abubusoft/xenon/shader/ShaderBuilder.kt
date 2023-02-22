/**
 *
 */
package com.abubusoft.xenon.shader

/**
 * @author Francesco Benincasa
 */
class ShaderBuilder
/**
 *
 *
 *
 *
 * @param vertexProgramIdValue
 * @param fragmentProgramIdValue
 */ private constructor(
    /**
     *
     *
     * resourceId del vertex program, se esiste. Non viene usata per la
     * rigenerazione dello shader.
     *
     */
    var vertexProgramId: Int,
    /**
     *
     *
     * resourceId del fragment program, se esiste. Non viene usata per la
     * rigenerazione dello shader.
     *
     */
    var fragmentProgramId: Int,
    /**
     *
     *
     * opzioni per lo shader.
     *
     */
    var options: ArgonShaderOptions?
) {
    companion object {
        /**
         *
         * Creatore di builder
         *
         * @param vertexProgramIdValue
         * @param fragmentProgramIdValue
         * @param optionsValue
         * @return
         * builder
         */
        @JvmStatic
        fun build(vertexProgramIdValue: Int, fragmentProgramIdValue: Int, optionsValue: ArgonShaderOptions?): ShaderBuilder {
            return ShaderBuilder(vertexProgramIdValue, fragmentProgramIdValue, optionsValue)
        }
    }
}