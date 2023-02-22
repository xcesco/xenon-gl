package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.xenon.texture.Texture
import com.abubusoft.xenon.texture.TextureRegion

@BindType
class TextureKeyFrame : KeyFrame() {
    /**
     * regione della texture
     */
    @Bind
    var textureRegion: TextureRegion? = null

    /**
     * texture da usare. Non può essere reso persistente
     */
    @BindDisabled
    var texture: Texture? = null

    companion object {
        fun build(texture: Texture?, textureRegion: TextureRegion?, duration: Long): TextureKeyFrame {
            val frame = TextureKeyFrame()
            frame.texture = texture
            frame.textureRegion = textureRegion
            return frame
        }
    }
}