package com.abubusoft.xenon.shader;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

/**
 * <p>
 * Serve a preprocessare gli shader.
 * </p>
 * <code>
 * if
 * else 
 * else
 * 
 * </code>
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressLint("DefaultLocale")
public class ShaderPreprocessor {

	public static final String REGEX_0 = "//\\s*\\@(ifdef)\\s*(\\w[\\w_]*)|";
	public static final String REGEX_1 = "//\\s*\\@(ifndef)\\s*(\\w[\\w_]*)|";
	public static final String REGEX_2="(//\\s*@(endif)\\s*)|(//\\s*@(else)\\s*)|";
	public static final String REGEX_3="(\\$\\{\\s*([\\w_\\.]+)\\s*\\})";
	
	public static final String REGEX_ALL=REGEX_0+REGEX_1+REGEX_2+REGEX_3;	

	public static final Pattern pattern = Pattern.compile(REGEX_ALL);

	private static final int IF_POSITION = 1;
	private static final int IF_POSITION_VALUE = 2;
	
	private static final int IF_NOT_POSITION = 3;
	private static final int IF_NOT_POSITION_VALUE = 4;

	private static final int ELSE_POSITION = 7;

	private static final int ENDIF_POSITION = 5;

	private static final int PARAMETER_POSITION = 9;
	
	private static final int PARAMETER_NAME = 10;

	/**
	 * indica se finora lo stack è valido
	 * 
	 * @param stack
	 * @return
	 */
	private static boolean isStatementValid(ArrayList<Boolean> stack, int offset) {
		for (int i = 0; i < stack.size() + offset; i++) {
			if (!stack.get(i))
				return false;
		}

		return true;
	}

	/**
	 * indica se finora lo stack è valido
	 * 
	 * @param stack
	 * @return
	 */
	private static boolean isStatementValid(ArrayList<Boolean> stack) {
		return isStatementValid(stack, 0);
	}

	/**
	 * Effettua il preprocessamento dello shader:
	 * 
	 * <ul>
	 * <li>sostituzione costanti</li>
	 * <li>rimozione, abilitazione regioni
	 * </ul>
	 * 
	 * @param options
	 */
	public static String preprocessorSource(String source, ArgonShaderOptions options) {
		StringBuilder builder = new StringBuilder();
		ArrayList<Boolean> defineStack = new ArrayList<Boolean>();
		String define;
		Boolean defineValue;
		boolean lastDefineValue;
		String[] lines = source.split("\n");
		Matcher matcher;

		String strLine;

		// per default ricopiamo tutto
		lastDefineValue = true;

		for (int i = 0; i < lines.length; i++) {
			strLine = lines[i];

			matcher = pattern.matcher(strLine);

			while (matcher.find()) {
				if (matcher.group(IF_POSITION) != null) {
					if (matcher.group(IF_POSITION_VALUE)==null)
					{
						throw new ShaderPreprocessorException("@ifdef without expression");
					}
					define = matcher.group(IF_POSITION_VALUE).toLowerCase(Locale.ENGLISH);

					defineValue = options.pragmaDefinitions.get(define);
					
					if (defineValue == null)
						defineValue = false;

					defineValue = defineValue && isStatementValid(defineStack);

					defineStack.add(defineValue);
					lastDefineValue = defineValue;
				} else if (matcher.group(IF_NOT_POSITION) != null) {
					if (matcher.group(IF_NOT_POSITION_VALUE)==null)
					{
						throw new ShaderPreprocessorException("@ifndef without expression");
					}
					define = matcher.group(IF_NOT_POSITION_VALUE).toLowerCase(Locale.ENGLISH);

					defineValue = options.pragmaDefinitions.get(define);

					if (defineValue == null)
						defineValue = true;
					// neghiamolo
					defineValue=!defineValue;

					defineValue = defineValue && isStatementValid(defineStack);

					defineStack.add(defineValue);
					lastDefineValue = defineValue;
				
				} else if (matcher.group(ELSE_POSITION) != null) {
					lastDefineValue = !lastDefineValue && isStatementValid(defineStack, -1);
				} else if (matcher.group(ENDIF_POSITION) != null) {
					if (defineStack.size() - 1 >= 0) {
						defineStack.remove(defineStack.size() - 1);
					}

					lastDefineValue = isStatementValid(defineStack);
				} else if (matcher.group(PARAMETER_POSITION) != null) {
					boolean found=false;
					String constantSentence=matcher.group(PARAMETER_POSITION);
					String constantName=matcher.group(PARAMETER_NAME);
			
					for (int j = 0; j < options.pragmaCostants.size(); j++) {
						if (constantName.equalsIgnoreCase(options.pragmaCostants.get(j).first))
						{
							strLine = strLine.replace(constantSentence, options.pragmaCostants.get(j).second);
							found=true;
							break;
						}
					}
					
					if (!found)
					{					
						throw new ShaderPreprocessorException("constant "+constantName+" is not defined ");	
					}
				}
			}
			
			if (lastDefineValue) {
				builder.append(strLine + "\n");
			}
//			if (lastDefineValue) {
//				for (int j = 0; j < options.pragmaCostants.size(); j++) {
//					strLine = strLine.replace("@" + options.pragmaCostants.get(j).first, options.pragmaCostants.get(j).second);
//				}
//			}
		}

		if (defineStack.size() > 0)
			throw new ShaderPreprocessorException("//@if //@else //@endif not structured correctly");

		return builder.toString();
	}
}
