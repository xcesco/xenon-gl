package com.abubusoft.xenon.mesh.modifiers

import com.abubusoft.xenon.math.Point3
import com.abubusoft.xenon.math.XenonMath.squareDistanceFromOrigin
import com.abubusoft.xenon.mesh.Mesh
import com.abubusoft.xenon.mesh.MeshFactory.VERTEX_DIMENSION
import com.abubusoft.xenon.mesh.MeshHelper
import com.abubusoft.xenon.mesh.MeshSprite
import com.abubusoft.xenon.vbo.AbstractBuffer
import com.abubusoft.xenon.vbo.VertexBuffer

/**
 *
 *
 * Modificatore dei vertici.
 *
 *
 * @author Francesco Benincasa
 */
object VertexModifier {
    /**
     * Itera su tutti i vertici della mesh e vi applica una trasformazione di scaling. Alla fine dell'iterazione,
     * aggiorna anche il boundingBox e la boundingSphere.
     *
     * @param mesh
     * mesh sulla quale applicare la modifica
     * @param scaleFactor
     * fattore di scala da applicare
     * @param update
     * se true indica di aggiornare direttament anche i native buffer.
     */
    fun scale(mesh: Mesh, scaleFactor: Float, update: Boolean) {
        iterate(mesh, object : VertexListener {
            override fun onApply(index: Int, inputVertex: Point3, outputVertex: Point3) {
                outputVertex.setCoords(scaleFactor * inputVertex.x, scaleFactor * inputVertex.y, scaleFactor * inputVertex.z)
            }
        }, update)
    }

    /**
     * Itera su tutti i vertici della mesh e vi applica la trasformazione desiderata. Alla fine dell'iterazione,
     * aggiorna anche il boundingBox e la boundingSphere.
     *
     * @param mesh
     * mesh sulla quale applicare la modifica
     * @param listener
     * modifica da applicare
     * @param update
     * se true indica di aggiornare direttament anche i native buffer.
     */
    fun iterate(mesh: Mesh, listener: VertexListener, update: Boolean) {
        val mins = FloatArray(3)
        val maxs = FloatArray(3)
        val result = Point3()
        val input = Point3()
        var vertexIndex = 0
        val coords = mesh.vertices!!.coords
        val n = coords!!.size
        var i = 0
        while (i < n) {
            input.setCoords(coords[i + 0], coords[i + 1], coords[i + 2])
            input.copyInto(result)
            listener.onApply(vertexIndex, input, result)
            coords[i + 0] = result.x
            coords[i + 1] = result.y
            coords[i + 2] = result.z
            MeshHelper.buildMinMaxArray(input.x, input.y, input.z, mins, maxs)
            if (mins[0] > result.x) mins[0] = result.x
            if (mins[1] > result.y) mins[1] = result.y
            if (mins[2] > result.z) mins[2] = result.z
            if (maxs[0] < result.x) maxs[0] = result.x
            if (maxs[1] < result.y) maxs[1] = result.y
            if (maxs[2] < result.z) maxs[2] = result.z
            vertexIndex++
            i += VertexBuffer.POSITION_DIMENSIONS
        }

        // impostiamo boundingbox
        mesh.boundingBox[Math.abs(maxs[0] - mins[0]), Math.abs(maxs[1] - mins[1])] = Math.abs(maxs[2] - mins[2])

        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        // Se parti da una sfera avente un raggio di 5 cm, il cubo inscritto è
        // quel cubo che può essere inserito esattamente nella tua sfera, in
        // modo tale che la distanza tra due vertici del cubo che siano opposti
        // tra loro misuri in lunghezza 10 cm (che è il diametro della sfera di
        // partenza). Per calcolare con precisione la lunghezza dello spigolo
        // del cubo inscritto devi dividere il diametro della sfera per la
        // radice quadrata di 3 (1,732 circa).
        // http://vivalascuola.studenti.it/come-determinare-le-misure-di-cubi-legati-a-sfere-140075.html#steps_2
        mesh.boundingSphereRadius = (0.8660254037844386 * mesh.boundingBox.width).toFloat()
        if (update) mesh.vertices!!.update()
    }

    /**
     *
     *
     *
     *
     * @param shape
     * @param widthValue
     * @param heightValue
     * @param update
     */
    fun setDimension(shape: MeshSprite, widthValue: Float, heightValue: Float, update: Boolean) {
        val startX = -widthValue / 2f
        // impostiamo boundingbox
        shape.boundingBox[widthValue, heightValue] = 0f
        var currentX = startX
        val highY = heightValue / 2.0f
        val lowY = -highY
        var i = 0
        while (i < AbstractBuffer.VERTEX_IN_QUAD_TILE * VertexBuffer.POSITION_DIMENSIONS) {

            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 0] = currentX
            shape.vertices!!.coords!![i + 1] = highY
            shape.vertices!!.coords!![i + 2] = 0.0f

            // vertex lower startX, startY, z
            shape.vertices!!.coords!![i + 3] = currentX
            shape.vertices!!.coords!![i + 4] = lowY
            shape.vertices!!.coords!![i + 5] = 0.0f

            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 6] = currentX + widthValue
            shape.vertices!!.coords!![i + 7] = lowY
            shape.vertices!!.coords!![i + 8] = 0.0f

            // triangolo 2

            // vertex higher startX, startY, z
            shape.vertices!!.coords!![i + 9] = currentX + widthValue
            shape.vertices!!.coords!![i + 10] = highY
            shape.vertices!!.coords!![i + 11] = 0.0f
            currentX += widthValue
            i += AbstractBuffer.VERTEX_IN_QUAD_TILE * VertexBuffer.POSITION_DIMENSIONS
        }
        // shape.vertices.put(shape.verticesCoords).position(0);
        if (update) {
            // ASSERT: aggiorno subito il buffer
            shape.vertices!!.update()
        }
        // calcoliamo boundingSphere radius, ovvero il raggio della sfera che
        // contiene lo shape
        shape.boundingSphereRadius =
            Math.sqrt(squareDistanceFromOrigin(shape.vertices!!.coords!![0], shape.vertices!!.coords!![1], shape.vertices!!.coords!![2]).toDouble()).toFloat()
    }

    /**
     * Swap delle coordinate
     *
     * @param shape
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flipVertical(shape: Mesh, updateBuffer: Boolean) {
        flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer)
    }

    /**
     * Swap delle coordinate
     *
     * @param shape
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flipHorizontal(shape: Mesh, updateBuffer: Boolean) {
        flip(shape, VerticesFlipType.HORIZONTAL, updateBuffer)
    }

    /**
     * Dato uno shape, provvede ad invertire le coordinate della texture associata. Questo vuol dire, ad esempio, che se prima la texture va da 0 a 1 (in orizzontale), dopo l'applicazione di questo metodo andrà da 1 a 0.
     *
     * @param shape
     * @param type
     * @param updateBuffer
     * se true indica che deve essere effettuato l'update del buffer dal float[]
     */
    fun flip(shape: Mesh, type: VerticesFlipType, updateBuffer: Boolean) {
        // bounding box non cambia

        // il ciclo cambia un vertice alla volta per fare in modo che il sistema
        // funzioni
        // su tutti i tipi di shape.
        val offset = when (type) {
            VerticesFlipType.HORIZONTAL ->            // offset startX
                0
            VerticesFlipType.VERTICAL ->            // offset startX
                1
        }
        val n = shape.vertexCount

        // n * VERTEX_ELEMENT: per ogni vertice abbiamo 3 coordinate
        var i = 0
        while (i < n * VERTEX_DIMENSION) {
            shape.vertices!!.coords!![i + offset] *= -1f
            i += VERTEX_DIMENSION
        }

        // Quando passo un array ad un direct buffer devo poi riposizionare a 0ù
        if (updateBuffer) shape.vertices!!.update()

        // non richiede il calcolo del bounding sphere dato che non cambia
    }

    interface VertexListener {
        fun onApply(index: Int, inputVertex: Point3, outputVertex: Point3)
    }

    /**
     * Indica il tipo di swap della texture.
     *
     * @author Francesco Benincasa
     */
    enum class VerticesFlipType {
        HORIZONTAL, VERTICAL
    }
}