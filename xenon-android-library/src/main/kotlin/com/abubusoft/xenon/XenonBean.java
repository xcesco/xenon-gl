package com.abubusoft.xenon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Questa annotazione indica che la classe contiene delle annotazioni injection
 * che consentono di iniettare alcuni oggetti dentro.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface XenonBean {

}
