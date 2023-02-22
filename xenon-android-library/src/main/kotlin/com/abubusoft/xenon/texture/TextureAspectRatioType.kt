package com.abubusoft.xenon.texture

/**
 *
 * Aspect ratio delle texture supportati. Normalmente una texture ricopre tutta l'area della bitmap usata per definirla.
 * <img src="doc-files/textureCoords.png"></img>
 *
 * In tal caso le coordiante U, V sono definite nell'intervallo (0, 1). In alcuni casi però può capitare che
 * la texture sia definita solo in una parte della bitmap. Questo è capitato nella realizzazione di Tiles Wallpaper.
 * <img src="doc-files/TypeTextureAspectRatio.png"></img>
 *
 * Per la larghezza non ci sono problemi, quello che cambia è l'altezza della texture. Questo valore rappresenta
 * un valore compreso nell'intervallo (0, 1) che rappresenta quanta percentuale (con 100% = 1) della bitmap devo considerare.
 *
 * Il valore massimo della coordinata V quindi è espresso dall'attributo `aspectXY`.
 *
 * @author Francesco Benincasa
 */
enum class TextureAspectRatioType(x: Int, y: Int) {
    /**
     * ratio=1, es: 1024 startX 1024
     */
    RATIO1_1(1, 1),

    /**
     * ratio=1.3 es: 1024x768
     */
    RATIO4_3(4, 3),

    /**
     * ratio=1.6 es: 1024x640
     */
    RATIO16_10(16, 10),

    /**
     * ratio=1.7 es: 1024x576
     */
    RATIO16_9(16, 9);

    val aspectXY: Double

    init {
        aspectXY = x.toDouble() / y
    }
}