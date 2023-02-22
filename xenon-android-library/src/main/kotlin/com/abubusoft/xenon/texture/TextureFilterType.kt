package com.abubusoft.xenon.texture

import android.opengl.GLES20

/**
 *
 *
 * Imposta il tipo di filtro da applicare alla texture.
 *
 *
 *
 *
 * GL_TEXTURE_MIN_FILTER accepts the following options:
 *
 *
 * <pre>
 * GL_NEAREST
 * GL_LINEAR
 * GL_NEAREST_MIPMAP_NEAREST
 * GL_NEAREST_MIPMAP_LINEAR
 * GL_LINEAR_MIPMAP_NEAREST
 * GL_LINEAR_MIPMAP_LINEAR
</pre> *
 *
 *
 * GL_TEXTURE_MAG_FILTER accepts the following options:
 *
 *
 * <pre>
 * GL_NEAREST
 * GL_LINEAR
</pre> *
 *
 *
 *
 * Vedi [qui](http://www.learnopengles.com/android-lesson-six-an-introduction-to-texture-filtering/)
 *
 *
 * @author Francesco Benincasa
 */
enum class TextureFilterType(
    /**
     * filtro da applicare per ridurre la texture (la vedo da più lontano)
     */
    var minifier: Int,
    /**
     * filtro da applicare per avvicinare la texture (la vedo da più vicino)
     *
     */
    var magnifier: Int,
    /**
     * genero o meno la mipmap
     */
    var generateMipmap: Boolean
) {
    /**
     *
     * tutto linear
     */
    LINEAR(GLES20.GL_LINEAR, GLES20.GL_LINEAR, false),

    /**
     *
     *
     * **Nearest-neighbour rendering**
     *
     *
     *
     * This mode is reminiscent of older software-rendered 3D games.
     *
     *
     * <pre>
     * GL_TEXTURE_MIN_FILTER = GL_NEAREST
     * GL_TEXTURE_MAG_FILTER = GL_NEAREST
    </pre> *
     *
     * <img src="doc-files/nearest-neighbour-rendering.png"></img>
     *
     *
     * This texture option is applied to texture atlases by default. This is the
     * fastest-performing texture option we can apply to a texture atlas, but
     * also the poorest in quality. This option means that the texture will
     * apply blending of pixels that make up the display by obtaining the
     * nearest texel color to a pixel. Similar to how a pixel represents the
     * smallest element of a digital image, a texel represents the smallest
     * element of a texture.
     *
     */
    NEAREST(GLES20.GL_NEAREST, GLES20.GL_NEAREST, false),

    /**
     *
     *
     * **Bilinear filtering, with mipmaps**
     *
     *
     *
     * This mode was used by many of the first games that supported 3D
     * acceleration and is an efficient way of smoothing textures on Android
     * phones today.
     *
     *
     * <pre>
     * GL_TEXTURE_MIN_FILTER = GL_LINEAR_MIPMAP_NEAREST
     * GL_TEXTURE_MAG_FILTER = GL_LINEAR
    </pre> *
     *
     * <img src="doc-files/bilinear-mipmap-filtering.png"></img>
     *
     *
     * This approach takes a hit performance-wise, but the quality of scaled
     * sprites will increase. Bilinear filtering obtains the four nearest texels
     * per pixel in order to provide smoother blending to an onscreen image.
     *
     */
    BILINEAR(GLES20.GL_LINEAR_MIPMAP_NEAREST, GLES20.GL_LINEAR, true),

    /**
     *
     *
     * **Trilinear filtering**
     *
     *
     *
     * This mode improves on the render quality of bilinear filtering with
     * mipmaps, by interpolating between the mipmap levels.
     *
     *
     * <pre>
     * GL_TEXTURE_MIN_FILTER = GL_LINEAR_MIPMAP_LINEAR
     * GL_TEXTURE_MAG_FILTER = GL_LINEAR
    </pre> *
     *
     * <img src="doc-files/trilinear-filtering.png"></img>
     */
    TRILINEAR(GLES20.GL_LINEAR_MIPMAP_LINEAR, GLES20.GL_LINEAR, true);
}