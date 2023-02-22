package com.abubusoft.xenon.scenemanagement.quadtree

import java.util.*

/** */ /*
 PROGETTO DI RECUPERO DEL CORSO DI "INTERFACCE UTENTE" A.A.2003-2004
 Paola Magillo, luglio 2004
 QuadTree.java
 */
/** */
/** */
internal class QuadTree(kk: Int, x0: Double, y0: Double, edgeLength: Double) : QuadNode() {
    /* massimo numero di punti ammissibili in una foglia */
    var K: Int

    /* dimensione dell'universo */
    var lato: Double

    /* foglia contenente il punto che sara' inserito o cancellato */
    var old_node: QuadNode?

    /* foglia contenente il punto appena inserito o cancellato */
    var new_node: QuadNode?
    var last_tried = NOTHING

    /* foglie */
    var num_leaves = 0

    /* numero punti presenti */
    var num_pts = 0
    /** */ /* METODI PUBBLICI */ /** */ /*
	 * Inizializza una gerarchia con capacita' kk, universo il quadrato con
	 * vertice di startX,startY minime in (x0,y0) e lato lungo edgeLength. L'insieme di
	 * punti e' vuoto.
	 */
    init {
        minX = x0
        minY = y0
        livello = 0
        padre = null
        contesto = this
        K = kk
        x_punti = DoubleArray(contesto!!.K + 1)
        y_punti = DoubleArray(contesto!!.K + 1)
        num_punti = 0
        lato = edgeLength
        new_node = null
        old_node = new_node
        last_tried = NOTHING
        num_leaves = 1
        num_pts = 0
    }

    fun capacity(): Int {
        return K
    }

    /*
	 * Ritorna la foglia che contiene geometricamente il punto. Non e' detto che
	 * vi sia memorizzato. Ritorna null se il punto cade fuori dal dominio.
	 */
    public override fun searchPoint(x: Double, y: Double): QuadNode? {
        if (!containsPoint(x, y)) {
            println("Warning: point $x,$y is outside!")
            return null
        }
        return super.searchPoint(x, y)
    }

    /*
	 * Controlla se il punto (startX,startY) puo' essere inserito (cioe' se cade nel
	 * dominio e non e' gia' presente). Mette in oldNode il nodo che contiene
	 * (startX,startY) oppure NULL se il punto cade fuori dal dominio.
	 */
    fun tryAddPoint(x: Double, y: Double): Boolean {
        old_node = searchPoint(x, y)
        last_tried = NOTHING
        if (old_node == null) return false
        if (old_node!!.hasPoint(x, y) >= 0) return false
        last_tried = ADD
        return true
    }

    /*
	 * Controlla se il punto (startX,startY) puo' essere cancellato (cioe' se e'
	 * presente). Mette in oldNode il nodo che contiene (startX,startY) oppure NULL se il
	 * punto cade fuori dal dominio.
	 */
    fun tryDeletePoint(x: Double, y: Double): Boolean {
        old_node = searchPoint(x, y)
        last_tried = NOTHING
        if (old_node == null) return false
        if (old_node!!.hasPoint(x, y) >= 0) {
            last_tried = DELETE
            return true
        }
        return false
    }

    /*
	 * Aggiunge veramente il punto, crea i quadrati nuovi, mette in new_node il
	 * nuovo quadrato che contiene il punto.
	 */
    fun doAddPoint(xx: Double, yy: Double): QuadNode? {
        // System.out.println("TEST doAddPoint(" + xx + "," + yy + ")");
        if (last_tried != ADD) {
            new_node = null
            return null
        }
        new_node = old_node!!.addPoint(xx, yy)
        old_node = null
        last_tried = NOTHING
        num_pts++
        return new_node
    }

    /*
	 * Cancella veramente il punto, crea i quadrati nuovi, mette in new_node il
	 * nuovo quadrato che contiene il punto.
	 */
    fun doDeletePoint(xx: Double, yy: Double): QuadNode? {
        // System.out.println("TEST doDeletePoint(" + xx + "," + yy + ")");
        if (last_tried != DELETE) {
            new_node = null
            return null
        }
        new_node = old_node!!.deletePoint(xx, yy)
        old_node = null
        last_tried = NOTHING
        num_pts--
        return new_node
    }

    /*
	 * Restituisce il numero di punti presenti in S.
	 */
    fun numAllPoints(): Int {
        return num_pts
    }

    val squares: Array<QuadNode>
        get() {
            val aux: Vector<QuadNode> = Vector<QuadNode>()
            collectLeaves(aux)
            val res = Array(aux.size) { index -> aux.elementAt(index) as QuadNode }
            if (num_leaves != aux.size) println("NUM_LEAVES NON E' AGGIORNATO BENE!!! " + num_leaves + " invece di " + aux.size)
            return res
        }

    fun numSquares(): Int {
        return num_leaves
    }
    /** */ /* METODI PER DEBUG */
    /** */
    override fun write() {
        println("Quadree lato=$lato K=$K")
        super.write()
    }

    companion object {
        /* il quadtree e' identificato con il suo nodo radice */ /* For remembering the last operation */
        const val NOTHING = 0
        const val ADD = 1
        const val DELETE = 2
    }
}