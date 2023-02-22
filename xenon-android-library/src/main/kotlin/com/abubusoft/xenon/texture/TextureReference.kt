package com.abubusoft.xenon.texture;

/**
 * Rappresenta un puntatore ad una texture. E' sempre bene utilizzare le referenze alle texture piuttosto che
 * le texture direttamente.
 * 
 * @author Francesco Benincasa
 *
 */
public class TextureReference {
	
	/**
	 * indice della texture referenziata.
	 */
	public int index;
		
	/**
	 * costante per indicare valore invalido
	 */
	public static final int INVALID_REFERENCE=-1;

	/**
	 * Costruttore
	 * 
	 * @param textureIndexValue
	 */
	public TextureReference()
	{
		update(INVALID_REFERENCE);
	}
	
	/**
	 * Costruttore
	 * 
	 * @param textureIndexValue
	 */
	public TextureReference(int textureIndexValue)
	{
		update(textureIndexValue);
	}
	
	public TextureReference copy() {
		return new TextureReference(index);
	}
	
	public void copyInto(TextureReference destination) {
		destination.update(index);		
	}
	
	/**
	 * Restituisce la texture referenziata
	 * @return
	 */
	public Texture get()
	{
		return TextureManager.instance().getTexture(index);		
	}
	
	/**
	 * @return
	 */
	public boolean isValid()
	{
		return index!=INVALID_REFERENCE;
	}
	
	/**
	 * Aggiorniamo texture referenziata.
	 *  
	 * @param textureIndexValue
	 */
	public void update(int value)
	{
		index=value;
	}

	
	/**
	 * Aggiorniamo texture referenziata.
	 *  
	 * @param textureIndexValue
	 */
	public void update(Texture value)
	{
		index=value.index;
	}

	/**
	 * Aggiorniamo texture referenziata.
	 *  
	 * @param textureIndexValue
	 */
	public void update(TextureReference value)
	{
		index=value.index;
	}
}
