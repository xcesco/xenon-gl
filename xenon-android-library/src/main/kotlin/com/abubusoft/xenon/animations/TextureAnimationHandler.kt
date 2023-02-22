/**
 *
 */
package com.abubusoft.xenon.animations

/**
 * @author Francesco Benincasa
 */
class TextureAnimationHandler : AnimationHandler<TextureKeyFrame>() {
    override fun value(current: TextureKeyFrame, enlapsedTime: Long, next: TextureKeyFrame): TextureKeyFrame {
        // questa è un'animazione di tipo discrete, quindi viene preso sempre il valore del frame corente
        return current
    }

    override fun buildFrame(): TextureKeyFrame {
        return TextureKeyFrame()
    }
}