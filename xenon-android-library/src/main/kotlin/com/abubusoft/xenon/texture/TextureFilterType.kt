package com.abubusoft.xenon.texture;

import android.opengl.GLES20;

/**
 * <p>
 * Imposta il tipo di filtro da applicare alla texture.
 * </p>
 * 
 * <p>
 * GL_TEXTURE_MIN_FILTER accepts the following options:
 * </p>
 * 
 * <pre>
 * GL_NEAREST
 * GL_LINEAR
 * GL_NEAREST_MIPMAP_NEAREST
 * GL_NEAREST_MIPMAP_LINEAR
 * GL_LINEAR_MIPMAP_NEAREST
 * GL_LINEAR_MIPMAP_LINEAR
 * </pre>
 * <p>
 * GL_TEXTURE_MAG_FILTER accepts the following options:
 * </p>
 * 
 * <pre>
 * GL_NEAREST
 * GL_LINEAR
 * </pre>
 * 
 * <p>
 * Vedi <a href="http://www.learnopengles.com/android-lesson-six-an-introduction-to-texture-filtering/">qui</a>
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public enum TextureFilterType {

	/**
	 * <p>tutto linear</p>
	 */
	LINEAR(GLES20.GL_LINEAR, GLES20.GL_LINEAR, false),
	
	/**
	 * <p>
	 * <b>Nearest-neighbour rendering</b>
	 * </p>
	 * <p>
	 * This mode is reminiscent of older software-rendered 3D games.
	 * </p>
	 * 
	 * <pre>
	 * GL_TEXTURE_MIN_FILTER = GL_NEAREST
	 * GL_TEXTURE_MAG_FILTER = GL_NEAREST
	 * </pre>
	 * 
	 * <img src="doc-files/nearest-neighbour-rendering.png"/>
	 * <p>
	 * This texture option is applied to texture atlases by default. This is the
	 * fastest-performing texture option we can apply to a texture atlas, but
	 * also the poorest in quality. This option means that the texture will
	 * apply blending of pixels that make up the display by obtaining the
	 * nearest texel color to a pixel. Similar to how a pixel represents the
	 * smallest element of a digital image, a texel represents the smallest
	 * element of a texture.
	 * </p>
	 */
	NEAREST(GLES20.GL_NEAREST, GLES20.GL_NEAREST, false),

	/**
	 * <p>
	 * <b>Bilinear filtering, with mipmaps</b>
	 * </p>
	 * <p>
	 * This mode was used by many of the first games that supported 3D
	 * acceleration and is an efficient way of smoothing textures on Android
	 * phones today.
	 * </p>
	 * 
	 * <pre>
	 * GL_TEXTURE_MIN_FILTER = GL_LINEAR_MIPMAP_NEAREST
	 * GL_TEXTURE_MAG_FILTER = GL_LINEAR
	 * </pre>
	 * 
	 * <img src="doc-files/bilinear-mipmap-filtering.png"/>
	 * <p>
	 * This approach takes a hit performance-wise, but the quality of scaled
	 * sprites will increase. Bilinear filtering obtains the four nearest texels
	 * per pixel in order to provide smoother blending to an onscreen image.
	 * </p>
	 */
	BILINEAR(GLES20.GL_LINEAR_MIPMAP_NEAREST, GLES20.GL_LINEAR, true),

	/**
	 * <p>
	 * <b>Trilinear filtering</b>
	 * </p>
	 * <p>
	 * This mode improves on the render quality of bilinear filtering with
	 * mipmaps, by interpolating between the mipmap levels.
	 * </p>
	 * 
	 * <pre>
	 * 		GL_TEXTURE_MIN_FILTER = GL_LINEAR_MIPMAP_LINEAR
	 * 		GL_TEXTURE_MAG_FILTER = GL_LINEAR
	 * </pre>
	 * 
	 * <img src="doc-files/trilinear-filtering.png"/>
	 */
	TRILINEAR(GLES20.GL_LINEAR_MIPMAP_LINEAR, GLES20.GL_LINEAR, true), ;

	TextureFilterType(int minifierValue, int magnifierValue, boolean generateMipmapValue) {
		minifier = minifierValue;
		magnifier = magnifierValue;
		generateMipmap = generateMipmapValue;
	}

	/**
	 * filtro da applicare per ridurre la texture (la vedo da più lontano)
	 */
	int minifier;

	/**
	 * filtro da applicare per avvicinare la texture (la vedo da più vicino)
	 * 
	 */
	int magnifier;

	/**
	 * genero o meno la mipmap
	 */
	boolean generateMipmap;
}
