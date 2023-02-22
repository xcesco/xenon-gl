package com.abubusoft.xenon.mesh

/**
 *
 * Tipi di formati per la lettura degli shape supportati.
 * @author Francesco Benincasa
 */
enum class MeshFileFormatType {
    /**
     * Formato Obj o Wavefront.
     *
     *  * [Online version](http://en.wikipedia.org/w/index.php?title=Wavefront_.obj_file)
     *  * [PDF Version](./doc-files/Wavefront.pdf)
     *
     */
    WAVEFRONT,

    /**
     * Formato 3D studio max.
     *
     *  * [Online version](http://en.wikipedia.org/wiki/.3ds)
     *  * [Online version 1](http://www.spacesimulator.net/wiki/index.php?title=Tutorials:3ds_Loader)
     *  * [Online version 2](http://www.the-labs.com/Blender/3dsspec.html)
     *  * [PDF Version](./doc-files/3ds.pdf)
     *  * [PDF Version 2](./doc-files/3ds1.pdf)
     *
     */
    MAX3D, ANDROID_XML,

    /**
     * Formato JSON ottenuto mediante la mia libreria kripton
     */
    KRIPTON_JSON,

    /**
     * Formato XML ottenuto mediante la mia libreria kripton
     */
    KRIPTON_XML
}