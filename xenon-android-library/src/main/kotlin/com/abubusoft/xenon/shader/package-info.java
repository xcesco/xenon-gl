/**
 * <p>Contiene le informazioni necessarie a gestire gli shader.</p>
 * <p>Ricordiamo che per semplificare lo sviluppo degli shader sono stati introdotti delle istruzioni 
 * per il preprocessore degli shader:</p>
 * <p>Se definisco nelle opzioni dello shader:</p>
 * <pre>
 * ArgonShaderOptions opts = ArgonShaderOptions.build();
 * opts.define("VADI", true);
 * </pre>
 * <p>Il define deve impostarlo a TRUE, altrimenti sarà come non averlo definito.
 * Allora potrò usare questa condizione per far compilare una parte di codice</p>
 * <pre>
 * //@ifdef VADI
 * 		[codice da eseguire]
 * //@endif
 * </pre>
 * <p>La definizione dovrà impostare il rispettivo valore a true per poter essere incluso nello shader.</p>
 * <p>Altre istruzioni per il preprocessore sono:</p>
 * <ul>
 * 		<li>//@ifndef :  quando voglio eseguire il codice solo quando la variabile non è definita</li>
 * 		<li>//@else</li>
 * 		<li>//@endif</li>
 * </ul>
 * 
 */
package com.abubusoft.xenon.shader;