package com.abubusoft.xenon.scenemanagement.quadtree;

/***********************************************************************/
/*
 PROGETTO DI RECUPERO DEL CORSO DI "INTERFACCE UTENTE" A.A.2003-2004
 Paola Magillo, luglio 2004
 QuadNode.java
 */
/***********************************************************************/

import java.util.*;

/***********************************************************************/

class QuadNode {
	/* indici dei figli */
	static final int MINX_MINY = 0;
	static final int MAXX_MINY = 1;
	static final int MINX_MAXY = 2;
	static final int MAXX_MAXY = 3;

	QuadTree contesto; /* la radice dell'albero */

	QuadNode[] figli;
	QuadNode padre;
	int livello;
	double minX;
	double minY;

	double x_punti[];
	double y_punti[];
	int num_punti;

	/***********************************************************************/
	/* METODI PUBBLICI */
	/***********************************************************************/

	public double getMinX() {
		return minX;
	}

	public double getMinY() {
		return minY;
	}

	protected double getLevel() {
		return livello;
	}

	public double getEdgeLength() {
		if (livello == 0)
			return contesto.lato;
		return contesto.lato / (Math.pow(2.0, livello));
	}

	/*
	 * Restituisce true se e solo se il nodo e' foglia. Se non e' foglia allora
	 * ha 4 figli.
	 */
	protected boolean isLeaf() {
		return (figli == null);
	}

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
	public int numPoints() {
		return num_punti;
	}

	/*
	 * Restituiscono le coordinate startX ed startY del punto i-esimo associato al nodo.
	 */
	public double getPointX(int i) {
		return x_punti[i];
	}

	public double getPointY(int i) {
		return y_punti[i];
	}

	/***********************************************************************/
	/* METODI NON PUBBLICI */
	/***********************************************************************/

	protected QuadNode() {
	}

	protected QuadNode(QuadTree qt, QuadNode p, double mX, double mY, int l) {
		contesto = qt;
		minX = mX;
		minY = mY;
		livello = l;
		padre = p;
		x_punti = new double[contesto.K];
		y_punti = new double[contesto.K];
		num_punti = 0;
	}

	/*
	 * Inserisce, tra i punti della lista, quelli che cadono nel quadrato del
	 * nodo. Il nodo deve essere foglia.
	 */
	private void takePoints(int n, double xp[], double yp[]) {
		int i;
		for (i = 0; i < n; i++) {
			if (containsPoint(xp[i], yp[i])) {
				x_punti[num_punti] = xp[i];
				y_punti[num_punti] = yp[i];
				num_punti++;
			}
		}
	}

	/*
	 * Inserisce tutti i punti della lista.
	 */
	private void takeAllPoints(int n, double xp[], double yp[]) {
		int i;
		for (i = 0; i < n; i++) {
			x_punti[num_punti] = xp[i];
			y_punti[num_punti] = yp[i];
			num_punti++;
		}
	}

	/*
	 * Divide il nodo in 4 figli. Il nodo deve essere una foglia.
	 */
	protected void splitNode() {
		int i;
		figli = new QuadNode[4];
		double centroX = minX + getEdgeLength() * 0.5;
		double centroY = minY + getEdgeLength() * 0.5;
		figli[MINX_MINY] = new QuadNode(contesto, this, minX, minY, livello + 1);
		figli[MAXX_MINY] = new QuadNode(contesto, this, centroX, minY, livello + 1);
		figli[MINX_MAXY] = new QuadNode(contesto, this, minX, centroY, livello + 1);
		figli[MAXX_MAXY] = new QuadNode(contesto, this, centroX, centroY, livello + 1);
		contesto.num_leaves += 3;
		for (i = 0; i < 4; i++) {
			figli[i].takePoints(num_punti, x_punti, y_punti);
		}
		num_punti = 0;
		x_punti = y_punti = null;
	}

	/*
	 * Se i 4 figli di questo nodo sono tutti foglie e se non ci sono troppi
	 * punti, fonde i 4 figli e questo nodo diventa una foglia. Restituisce true
	 * se fonde.
	 */
	protected boolean mergeNode() {
		int i;
		if (figli[MINX_MINY].isLeaf() && figli[MAXX_MINY].isLeaf() && figli[MINX_MAXY].isLeaf()
				&& figli[MAXX_MAXY].isLeaf()) {
			if (figli[MINX_MINY].num_punti + figli[MAXX_MINY].num_punti + figli[MINX_MAXY].num_punti
					+ figli[MAXX_MAXY].num_punti <= contesto.K) {
				x_punti = new double[contesto.K];
				y_punti = new double[contesto.K];
				num_punti = 0;
				for (i = 0; i < 4; i++)
					takeAllPoints(figli[i].num_punti, figli[i].x_punti, figli[i].y_punti);
				/* sgancia i figli dall'albero */
				for (i = 0; i < 4; i++)
					figli[i].padre = null;
				figli = null;
				contesto.num_leaves -= 3;
				return true;
			}
		}
		return false;
	}

	/*
	 * Fonde ricorsivamente finche' puo' risalendo nella gerarchia. Restituisce
	 * il nodo su cui ha smesso di fondere.
	 */
	protected QuadNode mergeNodeUp() {
		QuadNode aux = this;
		QuadNode p = aux.padre;
		while ((p != null) && (p.mergeNode())) {
			aux = p;
			p = aux.padre;
		}
		return aux;
	}

	/*
	 * Controlla se punto sta geometricamente nel quadrato. Non significa che
	 * sia memorizzato nel nodo (potrebbe non essere stato ancora inserito). Un
	 * nodo si intende comprendere solo i suoi bordi in basso e a sinistra, non
	 * quelli in alto e a destra. Eccezione i nodi che confinano coi lati in
	 * alto e/o a destra dell'intero dominio.
	 */
	public boolean containsPoint(double x, double y) {
		if (x < minX)
			return false;
		if (x > minX + getEdgeLength())
			return false;
		if (x == minX + getEdgeLength()) {
			if (x < contesto.minX + contesto.getEdgeLength())
				return false;
		}
		if (y < minY)
			return false;
		if (y > minY + getEdgeLength())
			return false;
		if (y == minY + getEdgeLength()) {
			if (y < contesto.minY + contesto.getEdgeLength())
				return false;
		}
		return true;
	}

	/*
	 * Controlla se il punto e' memorizzato in questo nodo. Ritorna l'indice se
	 * presente, -1 altrimenti.
	 */
	public int hasPoint(double x, double y) {
		int i;
		for (i = 0; i < num_punti; i++) {
			if ((x == x_punti[i]) && (y == y_punti[i]))
				return i;
		}
		return -1;
	}

	/*
	 * Cerca tra i punti memorizzati in questo nodo uno che abbia coordinate
	 * (startX,startY) a meno di una tolleranza toll e ne restituisce l'indice. Se sono
	 * piu' di uno sceglie il piu' vicino. Se non ce ne sono ritorna -1.
	 */
	public int hasNearPoint(double x, double y, double toll) {
		int r = -1;
		double dmin = contesto.lato * contesto.lato;
		double dx, dy, d;
		int i;
		for (i = 0; i < num_punti; i++) {
			dx = x - x_punti[i];
			dy = y - y_punti[i];
			d = dx * dx + dy * dy;
			if (d < dmin) {
				r = i;
				dmin = d;
			}
		}
		if (dmin < toll * toll)
			return r;
		return -1;
	}

	/*
	 * Cerca la foglia che contiene (geometricamente) il punto startX,startY. Non
	 * significa che il punto debba essere memorizzato nella foglia.
	 */
	protected QuadNode searchPoint(double x, double y) {
		/* per come e' chiamata dal quadtree questo non dovrebbe mai accadere */
		if (!containsPoint(x, y))
			return null;
		if (isLeaf())
			return this;
		double centroX = minX + getEdgeLength() * 0.5;
		double centroY = minY + getEdgeLength() * 0.5;
		if (x < centroX)
			if (y < centroY)
				return figli[MINX_MINY].searchPoint(x, y);
			else
				return figli[MINX_MAXY].searchPoint(x, y);
		else if (y < centroY)
			return figli[MAXX_MINY].searchPoint(x, y);
		else
			return figli[MAXX_MAXY].searchPoint(x, y);
	}

	/*
	 * Aggiunge il punto nel sottoalbero di questo nodo. Lo aggiunge solo se non
	 * e' fuori dal quadrato e non e' gia' presente. Restituisce il nodo se non
	 * era fuori, null se era fuori.
	 */
	protected QuadNode addPoint(double x, double y) {
		if (!containsPoint(x, y)) {
			System.out.println("Warning: point " + x + "," + y + " is outside! Not added.");
			return null;
		}
		if (isLeaf()) {
			if (hasPoint(x, y) >= 0) {
				System.out.println("Warning: point " + x + "," + y + " is already present! Not Added");
				return this;
			}
			if (num_punti < contesto.K) {
				x_punti[num_punti] = x;
				y_punti[num_punti] = y;
				num_punti++;
				return this;
			} else
				splitNode();
			/*
			 * dopo di che questo nodo non e' piu' foglia e si va avanti
			 * applicando il caso generale
			 */
		}
		double centroX = minX + getEdgeLength() * 0.5;
		double centroY = minY + getEdgeLength() * 0.5;
		if (x < centroX)
			if (y < centroY)
				return figli[MINX_MINY].addPoint(x, y);
			else
				return figli[MINX_MAXY].addPoint(x, y);
		else if (y < centroY)
			return figli[MAXX_MINY].addPoint(x, y);
		else
			return figli[MAXX_MAXY].addPoint(x, y);
	}

	/*
	 * Cancella il punto dal sottoalbero di questo nodo. Lo cancella solo se e'
	 * presente. Restituisce la foglia che contiene il punto ormai cancellato.
	 */
	protected QuadNode deletePoint(double x, double y) {
		if (!containsPoint(x, y)) {
			System.out.println("Warning: point " + x + "," + y + " is outside! Not deleted.");
			return null;
		}
		if (isLeaf()) {
			int i = hasPoint(x, y);
			if (i >= 0) {
				int j;
				for (j = i; j < num_punti - 1; j++) {
					x_punti[j] = x_punti[j + 1];
					y_punti[j] = y_punti[j + 1];
				}
				num_punti--;
				return mergeNodeUp();
			}
			/* if not removed */
			System.out.println("Warning: point " + x + "," + y + " is not present! Not Removed");
			return this;
		} else {
			double centroX = minX + getEdgeLength() * 0.5;
			double centroY = minY + getEdgeLength() * 0.5;
			if (x < centroX)
				if (y < centroY)
					return figli[MINX_MINY].deletePoint(x, y);
				else
					return figli[MINX_MAXY].deletePoint(x, y);
			else if (y < centroY)
				return figli[MAXX_MINY].deletePoint(x, y);
			else
				return figli[MAXX_MAXY].deletePoint(x, y);
		}
	}

	/*
	 * Aggiunge al vettore le foglie presenti nel sottoalbero di questo nodo.
	 */
	protected void collectLeaves(Vector coll) {
		if (isLeaf())
			coll.add(this);
		else {
			figli[MINX_MINY].collectLeaves(coll);
			figli[MINX_MAXY].collectLeaves(coll);
			figli[MAXX_MINY].collectLeaves(coll);
			figli[MAXX_MAXY].collectLeaves(coll);
		}
	}

	/***********************************************************************/
	/* METODI PER DEBUG */
	/***********************************************************************/

	public void write() {
		if (isLeaf()) {
			System.out.println("Foglia (" + minX + "," + minY + ") lato=" + getEdgeLength() + " livello=" + livello);
			writePointList("punti interni");
		} else {
			System.out.println("Nodo interno (" + minX + "," + minY + ") lato=" + getEdgeLength() + " livello="
					+ livello);
			int i;
			for (i = 0; i < 4; i++) {
				figli[i].write();
			}
		}
	}

	public void writePointList(String titolo) {
		int i;
		System.out.print("Lista " + titolo + ": ");
		for (i = 0; i < num_punti; i++) {
			System.out.print(" (" + x_punti[i] + "," + y_punti[i] + ") ");
		}
		System.out.println(" ");
	}

};

/***********************************************************************/
