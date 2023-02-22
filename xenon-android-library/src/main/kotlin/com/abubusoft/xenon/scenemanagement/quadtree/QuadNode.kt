package com.abubusoft.xenon.scenemanagement.quadtree

import java.util.*

internal open class QuadNode {
    var contesto /* la radice dell'albero */: QuadTree? = null
    var figli: Array<QuadNode?>? = null
    var padre: QuadNode? = null
    var livello = 0

    /** */
    var minX = 0.0
    var minY = 0.0
    var x_punti: DoubleArray? = null
    var y_punti: DoubleArray? = null
    var num_punti = 0

    /** */ /* METODI PUBBLICI */
    protected val level: Double
        protected get() = livello.toDouble()
    val edgeLength: Double
        get() = if (livello == 0) contesto!!.lato else contesto!!.lato / Math.pow(2.0, livello.toDouble())

    /*
	 * Restituisce true se e solo se il nodo e' foglia. Se non e' foglia allora
	 * ha 4 figli.
	 */
    protected val isLeaf: Boolean
        protected get() = figli == null

    /*
	 * Restituiscono i 4 figli del nodo. Vanno chiamati solo se il nodo non e'
	 * foglia.
	 */
    // public QuadNode bottomLeftChild() { return (figli[MINX_MINY]); }
    // public QuadNode bottomRightChild() { return (figli[MAXX_MINY]); }
    // public QuadNode topLeftChild() { return (figli[MINX_MAXY]); }
    // public QuadNode topRightChild() { return (figli[MAXX_MAXY]); }
    /*
	 * Restituisce il nodo padre.
	 */
    // public QuadNode parent() { return padre; }
    /*
	 * Restituisce il numero di punti associati al nodo. Puo' essere diverso da
	 * zero solo se il nodo e' foglia.
	 */
    fun numPoints(): Int {
        return num_punti
    }

    /*
	 * Restituiscono le coordinate startX ed startY del punto i-esimo associato al nodo.
	 */
    fun getPointX(i: Int): Double {
        return x_punti!![i]
    }

    fun getPointY(i: Int): Double {
        return y_punti!![i]
    }
    /** */ /* METODI NON PUBBLICI */
    /** */
    protected constructor() {}
    protected constructor(qt: QuadTree?, p: QuadNode?, mX: Double, mY: Double, l: Int) {
        contesto = qt
        minX = mX
        minY = mY
        livello = l
        padre = p
        x_punti = DoubleArray(contesto!!.K)
        y_punti = DoubleArray(contesto!!.K)
        num_punti = 0
    }

    /*
	 * Inserisce, tra i punti della lista, quelli che cadono nel quadrato del
	 * nodo. Il nodo deve essere foglia.
	 */
    private fun takePoints(n: Int, xp: DoubleArray, yp: DoubleArray) {
        var i: Int
        i = 0
        while (i < n) {
            if (containsPoint(xp[i], yp[i])) {
                x_punti!![num_punti] = xp[i]
                y_punti!![num_punti] = yp[i]
                num_punti++
            }
            i++
        }
    }

    /*
	 * Inserisce tutti i punti della lista.
	 */
    private fun takeAllPoints(n: Int, xp: DoubleArray?, yp: DoubleArray?) {
        var i: Int
        i = 0
        while (i < n) {
            x_punti!![num_punti] = xp!![i]
            y_punti!![num_punti] = yp!![i]
            num_punti++
            i++
        }
    }

    /*
	 * Divide il nodo in 4 figli. Il nodo deve essere una foglia.
	 */
    protected fun splitNode() {
        var i: Int
        figli = arrayOfNulls(4)
        val centroX = minX + edgeLength * 0.5
        val centroY = minY + edgeLength * 0.5
        figli!![MINX_MINY] = QuadNode(contesto, this, minX, minY, livello + 1)
        figli!![MAXX_MINY] = QuadNode(contesto, this, centroX, minY, livello + 1)
        figli!![MINX_MAXY] = QuadNode(contesto, this, minX, centroY, livello + 1)
        figli!![MAXX_MAXY] = QuadNode(contesto, this, centroX, centroY, livello + 1)
        contesto!!.num_leaves += 3
        i = 0
        while (i < 4) {
            figli!![i]!!.takePoints(num_punti, x_punti!!, y_punti!!)
            i++
        }
        num_punti = 0
        y_punti = null
        x_punti = y_punti
    }

    /*
	 * Se i 4 figli di questo nodo sono tutti foglie e se non ci sono troppi
	 * punti, fonde i 4 figli e questo nodo diventa una foglia. Restituisce true
	 * se fonde.
	 */
    protected fun mergeNode(): Boolean {
        var i: Int
        if (figli!![MINX_MINY]!!.isLeaf && figli!![MAXX_MINY]!!.isLeaf && figli!![MINX_MAXY]!!.isLeaf
            && figli!![MAXX_MAXY]!!.isLeaf
        ) {
            if ((figli!![MINX_MINY]!!.num_punti + figli!![MAXX_MINY]!!.num_punti + figli!![MINX_MAXY]!!.num_punti
                        + figli!![MAXX_MAXY]!!.num_punti) <= contesto!!.K
            ) {
                x_punti = DoubleArray(contesto!!.K)
                y_punti = DoubleArray(contesto!!.K)
                num_punti = 0
                i = 0
                while (i < 4) {
                    takeAllPoints(figli!![i]!!.num_punti, figli!![i]!!.x_punti, figli!![i]!!.y_punti)
                    i++
                }
                /* sgancia i figli dall'albero */i = 0
                while (i < 4) {
                    figli!![i]!!.padre = null
                    i++
                }
                figli = null
                contesto!!.num_leaves -= 3
                return true
            }
        }
        return false
    }

    /*
	 * Fonde ricorsivamente finche' puo' risalendo nella gerarchia. Restituisce
	 * il nodo su cui ha smesso di fondere.
	 */
    protected fun mergeNodeUp(): QuadNode {
        var aux = this
        var p = aux.padre
        while (p != null && p.mergeNode()) {
            aux = p
            p = aux.padre
        }
        return aux
    }

    /*
	 * Controlla se punto sta geometricamente nel quadrato. Non significa che
	 * sia memorizzato nel nodo (potrebbe non essere stato ancora inserito). Un
	 * nodo si intende comprendere solo i suoi bordi in basso e a sinistra, non
	 * quelli in alto e a destra. Eccezione i nodi che confinano coi lati in
	 * alto e/o a destra dell'intero dominio.
	 */
    fun containsPoint(x: Double, y: Double): Boolean {
        if (x < minX) return false
        if (x > minX + edgeLength) return false
        if (x == minX + edgeLength) {
            if (x < contesto!!.minX + contesto!!.edgeLength) return false
        }
        if (y < minY) return false
        if (y > minY + edgeLength) return false
        if (y == minY + edgeLength) {
            if (y < contesto!!.minY + contesto!!.edgeLength) return false
        }
        return true
    }

    /*
	 * Controlla se il punto e' memorizzato in questo nodo. Ritorna l'indice se
	 * presente, -1 altrimenti.
	 */
    fun hasPoint(x: Double, y: Double): Int {
        var i: Int
        i = 0
        while (i < num_punti) {
            if (x == x_punti!![i] && y == y_punti!![i]) return i
            i++
        }
        return -1
    }

    /*
	 * Cerca tra i punti memorizzati in questo nodo uno che abbia coordinate
	 * (startX,startY) a meno di una tolleranza toll e ne restituisce l'indice. Se sono
	 * piu' di uno sceglie il piu' vicino. Se non ce ne sono ritorna -1.
	 */
    fun hasNearPoint(x: Double, y: Double, toll: Double): Int {
        var r = -1
        var dmin = contesto!!.lato * contesto!!.lato
        var dx: Double
        var dy: Double
        var d: Double
        var i: Int
        i = 0
        while (i < num_punti) {
            dx = x - x_punti!![i]
            dy = y - y_punti!![i]
            d = dx * dx + dy * dy
            if (d < dmin) {
                r = i
                dmin = d
            }
            i++
        }
        return if (dmin < toll * toll) r else -1
    }

    /*
	 * Cerca la foglia che contiene (geometricamente) il punto startX,startY. Non
	 * significa che il punto debba essere memorizzato nella foglia.
	 */
    protected open fun searchPoint(x: Double, y: Double): QuadNode? {
        /* per come e' chiamata dal quadtree questo non dovrebbe mai accadere */
        if (!containsPoint(x, y)) return null
        if (isLeaf) return this
        val centroX = minX + edgeLength * 0.5
        val centroY = minY + edgeLength * 0.5
        return if (x < centroX) if (y < centroY) figli!![MINX_MINY]!!.searchPoint(x, y) else figli!![MINX_MAXY]!!
            .searchPoint(x, y) else if (y < centroY) figli!![MAXX_MINY]!!.searchPoint(x, y) else figli!![MAXX_MAXY]!!
            .searchPoint(x, y)
    }

    /*
	 * Aggiunge il punto nel sottoalbero di questo nodo. Lo aggiunge solo se non
	 * e' fuori dal quadrato e non e' gia' presente. Restituisce il nodo se non
	 * era fuori, null se era fuori.
	 */
    fun addPoint(x: Double, y: Double): QuadNode? {
        if (!containsPoint(x, y)) {
            println("Warning: point $x,$y is outside! Not added.")
            return null
        }
        if (isLeaf) {
            if (hasPoint(x, y) >= 0) {
                println("Warning: point $x,$y is already present! Not Added")
                return this
            }
            if (num_punti < contesto!!.K) {
                x_punti!![num_punti] = x
                y_punti!![num_punti] = y
                num_punti++
                return this
            } else splitNode()
            /*
			 * dopo di che questo nodo non e' piu' foglia e si va avanti
			 * applicando il caso generale
			 */
        }
        val centroX = minX + edgeLength * 0.5
        val centroY = minY + edgeLength * 0.5
        return if (x < centroX) if (y < centroY) figli!![MINX_MINY]!!.addPoint(x, y) else figli!![MINX_MAXY]!!
            .addPoint(x, y) else if (y < centroY) figli!![MAXX_MINY]!!.addPoint(x, y) else figli!![MAXX_MAXY]!!
            .addPoint(x, y)
    }

    /*
	 * Cancella il punto dal sottoalbero di questo nodo. Lo cancella solo se e'
	 * presente. Restituisce la foglia che contiene il punto ormai cancellato.
	 */
    fun deletePoint(x: Double, y: Double): QuadNode? {
        if (!containsPoint(x, y)) {
            println("Warning: point $x,$y is outside! Not deleted.")
            return null
        }
        return if (isLeaf) {
            val i = hasPoint(x, y)
            if (i >= 0) {
                var j: Int
                j = i
                while (j < num_punti - 1) {
                    x_punti!![j] = x_punti!![j + 1]
                    y_punti!![j] = y_punti!![j + 1]
                    j++
                }
                num_punti--
                return mergeNodeUp()
            }
            /* if not removed */println("Warning: point $x,$y is not present! Not Removed")
            this
        } else {
            val centroX = minX + edgeLength * 0.5
            val centroY = minY + edgeLength * 0.5
            if (x < centroX) if (y < centroY) figli!![MINX_MINY]!!.deletePoint(x, y) else figli!![MINX_MAXY]!!
                .deletePoint(x, y) else if (y < centroY) figli!![MAXX_MINY]!!.deletePoint(x, y) else figli!![MAXX_MAXY]!!
                .deletePoint(x, y)
        }
    }

    /*
	 * Aggiunge al vettore le foglie presenti nel sottoalbero di questo nodo.
	 */
    protected fun collectLeaves(coll: Vector<QuadNode>) {
        if (isLeaf) coll.add(this) else {
            figli!![MINX_MINY]!!.collectLeaves(coll)
            figli!![MINX_MAXY]!!.collectLeaves(coll)
            figli!![MAXX_MINY]!!.collectLeaves(coll)
            figli!![MAXX_MAXY]!!.collectLeaves(coll)
        }
    }
    /** */ /* METODI PER DEBUG */
    /** */
    open fun write() {
        if (isLeaf) {
            println("Foglia (" + minX + "," + minY + ") lato=" + edgeLength + " livello=" + livello)
            writePointList("punti interni")
        } else {
            println(
                "Nodo interno (" + minX + "," + minY + ") lato=" + edgeLength + " livello="
                        + livello
            )
            var i: Int
            i = 0
            while (i < 4) {
                figli!![i]!!.write()
                i++
            }
        }
    }

    fun writePointList(titolo: String) {
        var i: Int
        print("Lista $titolo: ")
        i = 0
        while (i < num_punti) {
            print(" (" + x_punti!![i] + "," + y_punti!![i] + ") ")
            i++
        }
        println(" ")
    }

    companion object {
        /* indici dei figli */
        const val MINX_MINY = 0
        const val MAXX_MINY = 1
        const val MINX_MAXY = 2
        const val MAXX_MAXY = 3
    }
}