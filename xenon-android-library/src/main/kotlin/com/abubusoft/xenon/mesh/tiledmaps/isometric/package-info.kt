/**
 * <p>Questo package si occupa di gestire le mappe di tipo isometrico. Tralasciando
 * buona parte della teoria che si trova in rete:</p>
 * 
 * <image src="./doc-files/IsoOnScreen.png"/>
 * 
 * <p>La forma a rombo o diamante è tipica di questo tipo di mappe.</p>
 * <p>In memoria la rappresentazione delle celle è la seguente:</p>
 * 
 * <image style="width: 100%"  src="./doc-files/Map2Window.png"/>
 * 
 * <p>La cella (0,0) è posizionata nell'angolo superiore del diamante. La cella (0,1) 
 * viene posizionata in basso a dx e così via.</p>
 * 
 * <p>Le dimensioni in memoria di una cella sono uguali, quindi abbiamo in memoria un quadrato. Sulla view,
 * a causa della proiezione isometrica, l'altezza viene dimezzata.</p>
 * 
 * <p>L'ordine di draw è basato sempre sulle righe (una riga alla volta).</p>
 * 
 * <h2>Adattamento allo schermo e costruzione della view</h2>
 * 
 *  <p>Per ovviare agli spazi vuoti che la visualizzazione a diamante comporta, si è deciso di 
 *  utilizzare una mask da riempire da una texture a piacimento.</p>
 *  
 *  <p>Inoltre, si è deciso di rendere determinare automaticamente il fill screen semplicemente in 
 *  base all'orientamento dello schermo e non di quanto scritto nelle opzioni.</p>
 * 
 *  <img style="width: 50%" src="./doc-files/2016-02-13 10.55.45.jpg"/>
 *  
 *  <p>L'immagine di sopra sono degli appunti che ho scritto a riguardo.</p>
 * 
 * @author xcesco
 *
 */
package com.abubusoft.xenon.mesh.tiledmaps.isometric;