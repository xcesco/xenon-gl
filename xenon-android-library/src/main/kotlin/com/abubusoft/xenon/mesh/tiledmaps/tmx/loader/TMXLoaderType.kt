package com.abubusoft.xenon.mesh.tiledmaps.tmx.loader

/**
 * Tipo di loader. Indica se il loader lavora nella cartella asset o nella cartella
 * @author Francesco Benincasa
 */
enum class TMXLoaderType {
    /**
     * carica dalla cartella asset
     */
    ASSET_LOADER,

    /**
     * carica dalla cartella res
     */
    RES_LOADER
}