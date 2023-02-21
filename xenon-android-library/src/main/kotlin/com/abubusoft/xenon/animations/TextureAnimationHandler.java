/**
 * 
 */
package com.abubusoft.xenon.animations;

/**
 * @author Francesco Benincasa
 * 
 */
public class TextureAnimationHandler extends AnimationHandler<TextureKeyFrame> {

	@Override
	protected TextureKeyFrame value(TextureKeyFrame current, long enlapsedTime, TextureKeyFrame next) {
		// questa è un'animazione di tipo discrete, quindi viene preso sempre il valore del frame corente
		return current;
	}

	@Override
	protected TextureKeyFrame buildFrame() {		
		return new TextureKeyFrame();
	}

}
