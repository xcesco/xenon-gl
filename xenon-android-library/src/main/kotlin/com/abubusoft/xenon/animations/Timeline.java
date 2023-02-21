/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.abubusoft.xenon.animations;

import com.abubusoft.xenon.animations.AnimationHandler.StatusType;
import com.abubusoft.xenon.core.collections.SmartQueue;
import com.abubusoft.kripton.android.Logger;

/**
 * A {@code Timeline} can be used to define a free from animation of any {@link javafx.beans.value.WritableValue}, e.g. all {@link javafx.beans.property.Property JavaFX Properties}
 * .
 * <p>
 * A {@code Timeline}, defined by one or more {@link KeyFrame}s, processes individual {@code KeyFrame} sequentially, in the order specified by {@code KeyFrame.time}. The animated
 * properties, defined as key values in {@code KeyFrame.values}, are interpolated to/from the targeted key values at the specified time of the {@code KeyFrame} to {@code Timeline}
 * 's initial position, depends on {@code Timeline}'s direction.
 * <p>
 * {@code Timeline} processes individual {@code KeyFrame} at or after specified time interval elapsed, it does not guarantee the timing when {@code KeyFrame} is processed.
 * <p>
 * The {@link #cycleDurationProperty()} will be set to the largest time value of Timeline's keyFrames.
 * <p>
 * If a {@code KeyFrame} is not provided for the {@code time==0s} instant, one will be synthesized using the target values that are current at the time {@link #play()} or
 * {@link #playFromStart()} is called.
 * <p>
 * It is not possible to change the {@code keyFrames} of a running {@code Timeline}. If the value of {@code keyFrames} is changed for a running {@code Timeline}, it has to be
 * stopped and started again to pick up the new value.
 * 
 * @see TextureTimeline
 * @see KeyFrame
 * @see KeyValue
 * 
 * @since JavaFX 2.0
 */
/**
 * @author Francesco Benincasa
 * 
 * @param <V>
 * @param <A>
 * @param <M>
 */
public class Timeline<A extends Animation<K>, K extends KeyFrame, H extends AnimationHandler<K>> {

	/**
	 * Avvia l'animazione
	 * 
	 * @param name
	 * @param enlapsedTimeValue
	 * @return
	 */
	public void play() {
		// immettiamo animation e la avviamo
		if (channel.size() == 0) {
			// assert: non abbiamo alcun elemento
			Logger.error("No animation to play on timeline %s!", id);
		} else {
			handler.set(channel.pop());
			handler.play();
		}
	}

	/**
	 * <p>
	 * Aggiunge un elemento alla coda.
	 * </p>
	 * 
	 * @param animationId
	 */
	public void add(A animation) {
		add(animation, false);
	}
	
	/**
	 * <p>
	 * Aggiunge un elemento alla coda.
	 * </p>
	 * 
	 * @param animationId
	 */
	public void add(A animation, boolean forceRun) {
		channel.add(animation);
		
		if (forceRun && handler.status==StatusType.STOPPED)
		{
			// se è bloccato, lo facciamo partire
			play();
		}
	}

	/**
	 * 
	 */
	public void oneMoreTime() {
		if (handler != null && handler.animation != null) {
			handler.oneMoreTime();
		} else {
			Logger.warn("No oneMoreTime, because no animation is associated to timeline.");
		}

	}

	/**
	 * rimuove tutti gli elementi accodati. Se un'animazione è in esecuzione, viene mantenuta
	 * 
	 */
	public void removeQueue() {
		channel.clear();
	}

	/**
	 * facciamo in modo che l'attuale animazione, se in loop, venga terminata (con il suo scorrere normale). Se in stato di running viene posto il loop dell'handler a false.
	 * 
	 * Se l'animazione invece finirà di suo, allora non viene fatto niente.
	 */
	public void forceNext() {
		if (handler.status == StatusType.RUNNING) {
			handler.loop = false;
		}
	}

	/**
	 * <p>
	 * Se l'animazione corrente è diversa da quella selezionata, la sostituisce e cancella tutte le altre animazioni che sono state accodate.
	 * </p>
	 * 
	 * @param animationId
	 * 
	 * @return
	 */
	/*
	 * public boolean replaceWith(A animation) { return replaceWith(animation, true); }
	 */

	/**
	 * <p>
	 * Sostituisce l'animazione corrente con quella il cui nome è passato come parametro. Il secondo parametro indica se cancellare o meno la coda di animazioni. Se l'animazione
	 * corrente corrisponde già a quella selezionata, non fa nulla.
	 * </p>
	 * 
	 * @param animationId
	 * @return
	 */
	/*
	 * public boolean replaceWith(String animationId, boolean clearQueue) { // se lo abbiamo già come animazione corrente, lo lasciamo in pace. // questo per evitare che in caso di
	 * animazione infinita, l'animazione venga interrotta. if (!animationId.equals(animation.name)) { start(animationId); }
	 * 
	 * if (clearQueue) channel.clear();
	 * 
	 * return false; }
	 */

	/**
	 * <p>
	 * Costruttore con l'animazione da far partire subito.
	 * </p>
	 * 
	 * @param timelineName
	 *            nome della timeline
	 * @param animationName
	 *            animazione da far partir subito
	 * @param manager
	 *            manager delle animazioni
	 */
	public Timeline() {
		channel = new SmartQueue<A>(16);
	}

	public void setHandler(H value) {
		handler = value;
	}

	/**
	 * indice dell'animator
	 */
	public int index;

	public boolean isAnimationFinished() {
		return handler.isFinished();
	}

	/**
	 * Durata dell'animazione. Usa il cursor
	 * 
	 * @return
	 */
	public long duration() {
		long duration = 0;

		channel.cursorReset();
		while (channel.cursorHasNext()) {
			duration += channel.cursorValue().duration();
			channel.cursorNext();
		}

		return duration;
	}

	/**
	 * <p>
	 * Nome dell'animazione corrente.
	 * </p>
	 * 
	 * @return
	 */
	public String getAnimationName() {
		return handler.getAnimationName();
	}

	/**
	 * handler dell'animazione
	 */
	protected H handler;

	/**
	 * @return the handler
	 */
	public H getHandler() {
		return handler;
	}

	/**
	 * id
	 */
	public String id;

	/**
	 * <p>
	 * Blocca l'animazione.
	 * </p>
	 */
	public void stop() {
		handler.stop();
	}

	/**
	 * <p>
	 * Sequenza delle animazioni da eseguire.
	 * </p>
	 */
	protected SmartQueue<A> channel;

	/**
	 * Restituisce il valore corrente dell'animazione.
	 * 
	 * @return valore corrente
	 */
	public K value() {
		return handler.value();
	}

	/**
	 * <p>
	 * Aggiorna lo stato dell'animazione.
	 * </p>
	 * 
	 * @param enlapsedTime
	 *            tempo trascorso dall'inizio dell'animazione
	 * @return
	 */
	public K update(long enlapsedTime) {
		K value;
		long exceedTime;
		value = handler.update(enlapsedTime);

		// andiamo avanti finchè abbiamo esaurito il tempo
		while (handler.isFinished() && channel.size() > 0) {
			exceedTime = handler.remaingTime;

			handler.set(channel.pop());
			handler.play();
			handler.update(exceedTime);
		}

		return value;
	}

	public boolean isFinished() {
		return handler.isFinished();
	}
	
	public boolean isAnimationPlaying() {
		return handler.isPlaying();
	}

}