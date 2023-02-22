/**
 *
 */
package com.abubusoft.xenon.texture

import android.opengl.GLES20

/**
 *
 *
 * Tipo di ripetizione della texture.
 *
 *
 *
 * [Link delle specifiche
](http://www.khronos.org/opengles/sdk/docs/man/xhtml/glTexParameter.xml) *
 *
 * @author Francesco Benincasa
 */
enum class TextureRepeatType(var value: Int) {
    /**
     *
     *
     * Non effettua la ripetizione. Prima e dopo il range [0..1], viene lasciato il valore 0 o 1. E' il valore di default
     *
     *
     * <pre>
     * GL_CLAMP_TO_EDGE causes s coordinates to be clamped to the range 1 2N 1 - 1 2N , where N is the size of the texture in the direction of clamping. GL_REPEAT causes the
     * integer part of the s coordinate to be ignored
    </pre> *
     */
    NO_REPEAT(GLES20.GL_CLAMP_TO_EDGE),

    /**
     *
     *
     * La parte intera della coordinata viene ignorata. La texture viene ripetuta
     *
     *
     * <pre>
     * GL_REPEAT causes the integer part of the s coordinate to be ignored
    </pre> *
     */
    REPEAT(GLES20.GL_REPEAT),

    /**
     *
     * La texture viene ripetuta. Se la parte intera è pari, range [0 .. 1].. se la parte intera è dispari,
     * la texture viene invertita nella forma `1 - frac`.
     * <pre>
     * GL_MIRRORED_REPEAT causes the s coordinate to be set to the fractional part of the texture coordinate
     * if the integer part of s is even; if the integer part of s is odd, then the s texture coordinate is
     * set to 1 - frac ⁡ s , where frac ⁡ s represents the fractional part of s.
    </pre> *
     */
    MIRRORED_REPEAT(GLES20.GL_MIRRORED_REPEAT);
}