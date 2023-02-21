package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureRegion;

import com.abubusoft.kripton.annotation.Bind;
import com.abubusoft.kripton.annotation.BindDisabled;
import com.abubusoft.kripton.annotation.BindType;

@BindType
public class TextureKeyFrame extends KeyFrame {

	public TextureKeyFrame() {
	}
	
	public static TextureKeyFrame build(Texture texture, TextureRegion textureRegion, long duration)
	{
		TextureKeyFrame frame = new TextureKeyFrame();
		
		frame.texture=texture;
		frame.textureRegion=textureRegion;	
		
		return frame;
	}
	
	/**
	 * regione della texture
	 */
	@Bind
	public TextureRegion textureRegion;

	/**
	 * texture da usare. Non può essere reso persistente
	 */
	@BindDisabled
	public Texture texture;


}
