package com.abubusoft.xenon.animations;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public abstract class AbstractAnimationManager<T extends Timeline, A extends Animation> {

	protected AbstractAnimationManager()
	{
		timelineMap = new HashMap<>();
		timelineList = new ArrayList<>();
		animationMap = new HashMap<>();
		animationList = new ArrayList<>();
	}
	
	public abstract void clear();
	
	/**
	 * lista delle animazioni
	 */
	protected ArrayList<A> animationList;

	/**
	 * mappa delle animazioni
	 */
	protected HashMap<String, A> animationMap;

	/**
	 * lista delle timeline
	 */
	protected ArrayList<T> timelineList;

	/**
	 * mappa delle timeline
	 */
	protected HashMap<String, T> timelineMap;
	
	/**
	 * Recupera un timeline in base al suo index.
	 * 
	 * @param index
	 * @return
	 */
	public T getTimeline(int index) {
		return timelineList.get(index);
	}

	/**
	 * Recupera un timeline in base al suo nome.
	 * 
	 * @param animatorId
	 * @return
	 */
	public T getTimeline(String animatorId) {
		return timelineMap.get(animatorId);
	}

	public A getAnimation(String animationName) {
		return animationMap.get(animationName);
	}

	public A getAnimation(int index) {
		return animationList.get(index);
	}
	
	public int getTimelineCount() {
		return timelineList.size();
	}

	public int getAnimationCount() {
		return animationList.size();
	}
}
