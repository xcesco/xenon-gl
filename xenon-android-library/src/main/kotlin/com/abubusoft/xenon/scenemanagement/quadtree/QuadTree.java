package com.abubusoft.xenon.scenemanagement.quadtree;

/***********************************************************************/
/*
 PROGETTO DI RECUPERO DEL CORSO DI "INTERFACCE UTENTE" A.A.2003-2004
 Paola Magillo, luglio 2004
 QuadTree.java
 */
/***********************************************************************/

import java.util.*;

/***********************************************************************/

class QuadTree extends QuadNode {
	/* massimo numero di punti ammissibili in una foglia */
	int K;

	/* dimensione dell'universo */
	double lato;

	/* foglia contenente il punto che sara' inserito o cancellato */
	QuadNode old_node;
	/* foglia contenente il punto appena inserito o cancellato */
	QuadNode new_node;

	/* il quadtree e' identificato con il suo nodo radice */

	/* For remembering the last operation */
	static final int NOTHING = 0;
	static final int ADD = 1;
	static final int DELETE = 2;
	int last_tried = NOTHING;

	/* foglie */
	int num_leaves = 0;

	/* numero punti presenti */
	int num_pts = 0;

	/***********************************************************************/
	/* METODI PUBBLICI */
	/***********************************************************************/

	/*
	 * Inizializza una gerarchia con capacita' kk, universo il quadrato con
	 * vertice di startX,startY minime in (x0,y0) e lato lungo edgeLength. L'insieme di
	 * punti e' vuoto.
	 */
	public QuadTree(int kk, double x0, double y0, double edgeLength) {
		minX = x0;
		minY = y0;
		livello = 0;
		padre = null;
		contesto = this;
		K = kk;
		x_punti = new double[contesto.K + 1];
		y_punti = new double[contesto.K + 1];
		num_punti = 0;
		lato = edgeLength;
		old_node = new_node = null;
		last_tried = NOTHING;
		num_leaves = 1;
		num_pts = 0;
	}

	public int capacity() {
		return K;
	}

	/*
	 * Ritorna la foglia che contiene geometricamente il punto. Non e' detto che
	 * vi sia memorizzato. Ritorna null se il punto cade fuori dal dominio.
	 */
	public QuadNode searchPoint(double x, double y) {
		if (!containsPoint(x, y)) {
			System.out.println("Warning: point " + x + "," + y + " is outside!");
			return null;
		}
		return super.searchPoint(x, y);
	}

	/*
	 * Controlla se il punto (startX,startY) puo' essere inserito (cioe' se cade nel
	 * dominio e non e' gia' presente). Mette in oldNode il nodo che contiene
	 * (startX,startY) oppure NULL se il punto cade fuori dal dominio.
	 */
	public boolean tryAddPoint(double x, double y) {
		old_node = searchPoint(x, y);
		last_tried = NOTHING;
		if (old_node == null)
			return false;
		if (old_node.hasPoint(x, y) >= 0)
			return false;
		last_tried = ADD;
		return true;
	}

	/*
	 * Controlla se il punto (startX,startY) puo' essere cancellato (cioe' se e'
	 * presente). Mette in oldNode il nodo che contiene (startX,startY) oppure NULL se il
	 * punto cade fuori dal dominio.
	 */
	public boolean tryDeletePoint(double x, double y) {
		old_node = searchPoint(x, y);
		last_tried = NOTHING;
		if (old_node == null)
			return false;
		if (old_node.hasPoint(x, y) >= 0) {
			last_tried = DELETE;
			return true;
		}
		return false;
	}

	/*
	 * Aggiunge veramente il punto, crea i quadrati nuovi, mette in new_node il
	 * nuovo quadrato che contiene il punto.
	 */
	public QuadNode doAddPoint(double xx, double yy) {
		// System.out.println("TEST doAddPoint(" + xx + "," + yy + ")");
		if (last_tried != ADD) {
			new_node = null;
			return null;
		}
		new_node = old_node.addPoint(xx, yy);
		old_node = null;
		last_tried = NOTHING;
		num_pts++;
		return new_node;
	}

	/*
	 * Cancella veramente il punto, crea i quadrati nuovi, mette in new_node il
	 * nuovo quadrato che contiene il punto.
	 */
	public QuadNode doDeletePoint(double xx, double yy) {
		// System.out.println("TEST doDeletePoint(" + xx + "," + yy + ")");
		if (last_tried != DELETE) {
			new_node = null;
			return null;
		}
		new_node = old_node.deletePoint(xx, yy);
		old_node = null;
		last_tried = NOTHING;
		num_pts--;
		return new_node;
	}

	/*
	 * Restituisce il numero di punti presenti in S.
	 */
	public int numAllPoints() {
		return num_pts;
	}

	public QuadNode[] getSquares() {
		Vector aux = new Vector();
		collectLeaves(aux);
		QuadNode[] res = new QuadNode[aux.size()];
		int i;
		for (i = 0; i < aux.size(); i++) {
			res[i] = (QuadNode) aux.elementAt(i);
		}
		if (num_leaves != aux.size())
			System.out.println("NUM_LEAVES NON E' AGGIORNATO BENE!!! " + num_leaves + " invece di " + aux.size());

		return res;
	}

	public int numSquares() {
		return num_leaves;
	}

	/***********************************************************************/
	/* METODI PER DEBUG */
	/***********************************************************************/

	public void write() {
		System.out.println("Quadree lato=" + lato + " K=" + K);
		super.write();
	}

};

/***********************************************************************/
/* CLASSI AUSILIARIE */
/***********************************************************************/

// QuadNode
// definita in file separato

/***********************************************************************/
