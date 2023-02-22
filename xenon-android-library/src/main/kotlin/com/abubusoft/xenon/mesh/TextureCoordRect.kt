/**
 *
 */
package com.abubusoft.xenon.mesh

/**
 * Permette di definire un rettangolo nello spazio delle texture, che va da a [0 .. 1]
 *
 * @author Francesco Benincasa
 */
class TextureCoordRect private constructor(var x: Float, var y: Float, var width: Float, var height: Float) {
    companion object {
        /**
         * A partire dal centro (0.5, 0.5) definisce un rettangolo di dimensioni specificate.
         *
         * @param width da 0 a 1
         * @param height da 0 a 1
         * @return
         */
        fun buildFromCenter(width: Float, height: Float): TextureCoordRect {
            return TextureCoordRect(0.5f - width * 0.5f, 0.5f - height * 0.5f, width, height)
        }

        /**
         * A partire dal centro (0.5, 0.5) definisce un rettangolo di dimensioni specificate.
         *
         * @param width da 0 a 1
         * @param height da 0 a 1
         * @return
         */
        fun buildFromTopLeft(width: Float, height: Float): TextureCoordRect {
            return TextureCoordRect(0f, 0f, width, height)
        }

        /**
         * Posto il top left (origine) del sistema a (0, 0), definisce il range in termini di larghezza ed altezza
         *
         * @param width da 0 a 1
         * @param height da 0 a 1
         * @return
         */
        fun buildFromOrigin(width: Float, height: Float): TextureCoordRect {
            return TextureCoordRect(0f, 0f, width, height)
        }

        /**
         * A partire dal top left definisce un rettangolo di dimensioni specificate.
         *
         * @param startx
         * @param starty
         * @param width
         * @param height
         * @return
         */
        fun buildFromTopLeft(startx: Float, starty: Float, width: Float, height: Float): TextureCoordRect {
            return TextureCoordRect(startx, starty, width, height)
        }

        /**
         * Range [0 - 1] [0 - 1]
         *
         * @param width da 0 a 1
         * @param height da 0 a 1
         * @return
         */
        fun build(): TextureCoordRect {
            return TextureCoordRect(0f, 0f, 1f, 1f)
        }
    }
}