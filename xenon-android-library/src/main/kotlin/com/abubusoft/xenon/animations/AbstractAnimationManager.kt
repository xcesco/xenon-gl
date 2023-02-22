package com.abubusoft.xenon.animations

abstract class AbstractAnimationManager<T : Timeline<*, *, *>?, A : Animation<*>?> protected constructor() {
    abstract fun clear()

    /**
     * lista delle animazioni
     */
    protected var animationList: ArrayList<A>

    /**
     * mappa delle animazioni
     */
    protected var animationMap: HashMap<String, A>

    /**
     * lista delle timeline
     */
    protected var timelineList: ArrayList<T>

    /**
     * mappa delle timeline
     */
    protected var timelineMap: HashMap<String, T>

    init {
        timelineMap = HashMap()
        timelineList = ArrayList()
        animationMap = HashMap()
        animationList = ArrayList()
    }

    /**
     * Recupera un timeline in base al suo index.
     *
     * @param index
     * @return
     */
    fun getTimeline(index: Int): T {
        return timelineList[index]
    }

    /**
     * Recupera un timeline in base al suo nome.
     *
     * @param animatorId
     * @return
     */
    fun getTimeline(animatorId: String): T? {
        return timelineMap[animatorId]
    }

    fun getAnimation(animationName: String): A? {
        return animationMap[animationName]
    }

    fun getAnimation(index: Int): A {
        return animationList[index]
    }

    val timelineCount: Int
        get() = timelineList.size
    val animationCount: Int
        get() = animationList.size
}