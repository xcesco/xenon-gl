package com.abubusoft.xenon.shader;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.util.Pair;

/**
 * Definisce le opzioni per i vari shader. Di default vengono definite due costanti: <ld> <dt>@PI</dt><dd>Pi greco</dd> <dt>@2PI</dt><dd>Pi greco * 2</dd> </ld>
 * 
 * <p>Le definizioni sono quelle che possono cambiare la definizione degli shader.</p>
 * 
 * @author Francesco Benincasa
 * 
 */
@SuppressLint("DefaultLocale")
public class ArgonShaderOptions {
	
	static float PI_2=(float) (2.0 * Math.PI);

	/**
	 * costruttore.
	 */
	private ArgonShaderOptions() {
		// definisce le costanti valide sempre
		costant("PI", floatFormatter.format((float) Math.PI));
		costant("2PI", floatFormatter.format(PI_2));
	}

	/**
	 * salva i due shader su file
	 */
	public boolean debugOnFile;
	
	/**
	 * Se true indica che lo shader sarà modificato automaticamente al fine di ospitare
	 * come texture_0 una texture esterna.
	 */
	public boolean useForExternalTexture;

	/**
	 * nome dello shader
	 */
	public String name;

	/**
	 * numero di texture utilizzate contemporaneamente dallo shader.
	 */
	public int numberOfTextures;

	/**
	 * numero di attributi uniformi, ovvero validi per tutti i vertici.
	 */
	public int numberOfUniformAttributes;

	/**
	 * <p>
	 * Mappa delle costanti che devono essere sostituite con un @[nome costante].
	 * </p>
	 * 
	 * Se mettiamo in uno dei due programmi il seguente codice:
	 * 
	 * <pre>
	 *  	i=@cod;
	 * </pre>
	 * 
	 * E impostiamo la variabile da programma, il codice compilato diventa quindi
	 * 
	 */
	public ArrayList<Pair<String, String>> pragmaCostants = new ArrayList<Pair<String, String>>();

	public HashMap<String, Boolean> pragmaDefinitions = new HashMap<String, Boolean>();

	/**
	 * Parte con una texture e 0 attributi
	 * 
	 * @return
	 */
	public static ArgonShaderOptions build() {
		return (new ArgonShaderOptions()).numberOfTextures(1).numberOfUniformAttributes(0).useForExternalTexture(false);
	}

	public ArgonShaderOptions numberOfTextures(int value) {
		numberOfTextures = value;
		return this;
	}

	public ArgonShaderOptions numberOfUniformAttributes(int value) {
		numberOfUniformAttributes = value;
		return this;
	}

	public ArgonShaderOptions name(String value) {
		name = value;
		return this;
	}

	/**
	 * <p>
	 * salva i due shader su file
	 * </p>
	 * 
	 * @param value
	 * @return
	 */
	public ArgonShaderOptions debugOnFile(boolean value) {
		debugOnFile = value;
		return this;
	}

	/**
	 * <p>
	 * Definisce una costante. Il nome della costante all'interno dello shader è del tipo @NOME, in uppercase.
	 * </p>
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public ArgonShaderOptions costant(String name, String value) {
		pragmaCostants.add(new Pair<String, String>(name.toUpperCase(Locale.ENGLISH), value));
		return this;
	}
	
	
	/**
	 * Facility per definire le costanti @RESOLUTION_X, @RESOLUTION_Y, @INV_RESOLUTION_X e @INV_RESOLUTION_Y
	 * @param name
	 * @param value
	 * @return
	 */
	public ArgonShaderOptions costantResolution(float resolutionX, float resolutionY) {		
		costant("RESOLUTION_X", floatFormatter.format(resolutionX));
		costant("RESOLUTION_Y", floatFormatter.format(resolutionY));
        costant("INV_RESOLUTION_X", floatFormatter.format(1f/resolutionX));
        costant("INV_RESOLUTION_Y", floatFormatter.format(1f /resolutionY));
		
		return this;
	}
	
	private static DecimalFormat floatFormatter = new DecimalFormat("#.0###########");

	/**
	 * Definizione. viene salvata in lowercase.
	 * 
	 * @param name
	 * @param enabled
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public ArgonShaderOptions define(String name, boolean enabled) {
		pragmaDefinitions.put(name.toLowerCase(), enabled);
		return this;
	}
	
	/**
	 * Se true indica che lo shader sarà modificato automaticamente al fine di ospitare
	 * come texture_0 una texture esterna.
	 */
	public ArgonShaderOptions useForExternalTexture(boolean enabled) {
		useForExternalTexture=enabled;
		return this;
	}
}
