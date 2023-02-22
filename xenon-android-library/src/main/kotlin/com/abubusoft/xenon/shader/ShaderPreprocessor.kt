package com.abubusoft.xenon.shader

import android.annotation.SuppressLint
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 *
 * Serve a preprocessare gli shader.
 *
 * `
 * if
 * else
 * else
 *
` *
 *
 * @author Francesco Benincasa
 */
@SuppressLint("DefaultLocale")
object ShaderPreprocessor {
    const val REGEX_0 = "//\\s*\\@(ifdef)\\s*(\\w[\\w_]*)|"
    const val REGEX_1 = "//\\s*\\@(ifndef)\\s*(\\w[\\w_]*)|"
    const val REGEX_2 = "(//\\s*@(endif)\\s*)|(//\\s*@(else)\\s*)|"
    const val REGEX_3 = "(\\$\\{\\s*([\\w_\\.]+)\\s*\\})"
    const val REGEX_ALL = REGEX_0 + REGEX_1 + REGEX_2 + REGEX_3
    val pattern = Pattern.compile(REGEX_ALL)
    private const val IF_POSITION = 1
    private const val IF_POSITION_VALUE = 2
    private const val IF_NOT_POSITION = 3
    private const val IF_NOT_POSITION_VALUE = 4
    private const val ELSE_POSITION = 7
    private const val ENDIF_POSITION = 5
    private const val PARAMETER_POSITION = 9
    private const val PARAMETER_NAME = 10

    /**
     * indica se finora lo stack è valido
     *
     * @param stack
     * @return
     */
    private fun isStatementValid(stack: ArrayList<Boolean>, offset: Int): Boolean {
        for (i in 0 until stack.size + offset) {
            if (!stack[i]) return false
        }
        return true
    }

    /**
     * indica se finora lo stack è valido
     *
     * @param stack
     * @return
     */
    private fun isStatementValid(stack: ArrayList<Boolean>): Boolean {
        return isStatementValid(stack, 0)
    }

    /**
     * Effettua il preprocessamento dello shader:
     *
     *
     *  * sostituzione costanti
     *  * rimozione, abilitazione regioni
     *
     *
     * @param options
     */
    fun preprocessorSource(source: String?, options: ArgonShaderOptions?): String {
        val builder = StringBuilder()
        val defineStack = ArrayList<Boolean>()
        var define: String
        var defineValue: Boolean?
        var lastDefineValue: Boolean
        val lines = source!!.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var matcher: Matcher
        var strLine: String

        // per default ricopiamo tutto
        lastDefineValue = true
        for (i in lines.indices) {
            strLine = lines[i]
            matcher = pattern.matcher(strLine)
            while (matcher.find()) {
                if (matcher.group(IF_POSITION) != null) {
                    if (matcher.group(IF_POSITION_VALUE) == null) {
                        throw ShaderPreprocessorException("@ifdef without expression")
                    }
                    define = matcher.group(IF_POSITION_VALUE).lowercase()
                    defineValue = options!!.pragmaDefinitions[define]
                    if (defineValue == null) defineValue = false
                    defineValue = defineValue && isStatementValid(defineStack)
                    defineStack.add(defineValue)
                    lastDefineValue = defineValue
                } else if (matcher.group(IF_NOT_POSITION) != null) {
                    if (matcher.group(IF_NOT_POSITION_VALUE) == null) {
                        throw ShaderPreprocessorException("@ifndef without expression")
                    }
                    define = matcher.group(IF_NOT_POSITION_VALUE).lowercase()
                    defineValue = options!!.pragmaDefinitions[define]
                    if (defineValue == null) defineValue = true
                    // neghiamolo
                    defineValue = !defineValue
                    defineValue = defineValue && isStatementValid(defineStack)
                    defineStack.add(defineValue)
                    lastDefineValue = defineValue
                } else if (matcher.group(ELSE_POSITION) != null) {
                    lastDefineValue = !lastDefineValue && isStatementValid(defineStack, -1)
                } else if (matcher.group(ENDIF_POSITION) != null) {
                    if (defineStack.size - 1 >= 0) {
                        defineStack.removeAt(defineStack.size - 1)
                    }
                    lastDefineValue = isStatementValid(defineStack)
                } else if (matcher.group(PARAMETER_POSITION) != null) {
                    var found = false
                    val constantSentence = matcher.group(PARAMETER_POSITION)
                    val constantName = matcher.group(PARAMETER_NAME)
                    for (j in options!!.pragmaCostants.indices) {
                        if (constantName.equals(options.pragmaCostants[j].first, ignoreCase = true)) {
                            strLine = strLine.replace(constantSentence, options.pragmaCostants[j].second)
                            found = true
                            break
                        }
                    }
                    if (!found) {
                        throw ShaderPreprocessorException("constant $constantName is not defined ")
                    }
                }
            }
            if (lastDefineValue) {
                builder.append(
                    """
    $strLine
    
    """.trimIndent()
                )
            }
            //			if (lastDefineValue) {
//				for (int j = 0; j < options.pragmaCostants.size(); j++) {
//					strLine = strLine.replace("@" + options.pragmaCostants.get(j).first, options.pragmaCostants.get(j).second);
//				}
//			}
        }
        if (defineStack.size > 0) throw ShaderPreprocessorException("//@if //@else //@endif not structured correctly")
        return builder.toString()
    }
}