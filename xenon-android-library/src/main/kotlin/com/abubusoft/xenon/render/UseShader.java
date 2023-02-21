package com.abubusoft.xenon.render;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.abubusoft.xenon.shader.Shader;

/**
 * <p>Usa lo shader passato come .</p> 
 * @author Francesco Benincasa
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface UseShader {

	/**
	 * <p>
	 * Indica il tipo di injection da inserire.
	 * </p>
	 * 
	 * @return
	 */
	Class<? extends Shader> value();
	
}
