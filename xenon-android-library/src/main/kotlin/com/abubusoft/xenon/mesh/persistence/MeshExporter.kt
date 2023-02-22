/**
 *
 */
package com.abubusoft.xenon.mesh.persistence

import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.persistence.wavefront.WavefrontExporter
import java.io.File

/**
 * @author Francesco Benincasa
 */
object MeshExporter {
    fun exportWavefront(file: File?, mesh: Mesh?): Boolean {
        return WavefrontExporter.export(file, mesh!!)
    }
}