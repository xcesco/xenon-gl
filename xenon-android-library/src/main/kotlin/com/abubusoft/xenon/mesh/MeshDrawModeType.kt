package com.abubusoft.xenon.mesh

import android.opengl.GLES20

/**
 *
 * Tipo di drawing supportati
 *
 * [Vedi wiki](https://github.com/xcesco/xenon-gl/wiki/MeshDrawModeType)
 *
 *
 * In base al tipo di stile, cambiano inoltre i modi in cui gli attributi dello shape possono essere assegnati.
 *
 * @author Francesco Benincasa
 */
enum class MeshDrawModeType(
    /**
     * valore associato
     */
    var value: Int
) {
    /**
     *
     * Disegno con indici triangoli, scollegati tra loro. Utile per disegnare i quad o le mesh genericamente calcolate.
     *
     */
    INDEXED_TRIANGLES(GLES20.GL_TRIANGLES),

    /**
     *
     */
    LINES(GLES20.GL_LINES),

    /**
     *
     * Disegno semplici triangoli, scollegati tra loro. Serve per gli oggetti caricati dal formato .obj
     *
     */
    TRIANGLES(GLES20.GL_TRIANGLES),

    /**
     *
     * Triangle strip
     *
     */
    TRIANGLE_STRIP(GLES20.GL_TRIANGLE_STRIP);
}