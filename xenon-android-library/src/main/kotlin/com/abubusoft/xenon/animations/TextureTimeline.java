package com.abubusoft.xenon.animations;


/**
 * <p>
 * Definiamo come <b>animation</b> la sequenza di frame caratterizzati da una durata. Ci possono essere
 * diversi tipi di animazioni. 
 * </p>
 * 
 * <p>
 * Le animazioni possono essere accodate, stando attenti al fatto che le animazioni a loop non finiranno mai.
 * </p>
 * 
 * 
 * @author Francesco Benincasa
 * @param <F>
 * 
 */
public class TextureTimeline extends Timeline<TextureAnimation, TextureKeyFrame, TextureAnimationHandler> {
	
	public TextureTimeline()
	{
		handler=new TextureAnimationHandler();
	}

}
