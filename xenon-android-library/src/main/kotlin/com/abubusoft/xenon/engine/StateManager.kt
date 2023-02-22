/**
 *
 */
package com.abubusoft.xenon.engine

import com.abubusoft.xenon.Xenon4OpenGL
import com.abubusoft.xenon.misc.Clock

/**
 * State manager. Quando si parla di frame nell'ambito dello stateManager, si indica il tempo
 * che intercorre tra il metodo [.frameStart] e [.frameStop].
 *
 * @author Francesco Benincasa
 */
class StateManager {
    /**
     * riferimento ad xenon
     */
    private var argon: Xenon4OpenGL? = null

    /**
     * @param argon
     * the xenon to set
     */
    fun setArgon(argon: Xenon4OpenGL?) {
        this.argon = argon
    }

    /**
     * elenco degli shared data modificati
     */
    private val sharedData = ArrayList<SharedData>()

    /**
     * durata dell'ultimo frame.
     */
    private var duration: Long = 0
    private var n = 0
    fun frameStart() {
        //Logger.info("StateManager -- frameStart");

        // iniziamo il disegno della scena
        argon!!.onDrawFrameBegin()

        // ora impostiamo nuovamente il clock
        duration = Clock.now()

        // aggiorniamo tutti gli shared data
        n = sharedData.size
        for (i in 0 until n) {
            sharedData[i].update()
        }

        // e alla fine puliamo la lista
        sharedData.clear()
    }

    /**
     *
     *
     * Viene invocato nella fase LOGIC e consente di tener traccia degli shared data che sono stati modificati./
     *
     *
     * @param <E>
     *
     * @param value
    </E> */
    fun <E : SharedData?> touch(value: E): E {
        sharedData.add(value)
        return value
    }

    fun frameStop() {
        UpdateManager.Companion.instance().isReady()
        duration = Clock.now() - duration
        //Logger.info("StateManager -- frameStop (%s ms)", duration);

        // termine del disegno della scena
        argon!!.onDrawFrameEnd()
    }

    companion object {
        private val instance = StateManager()
        fun instance(): StateManager {
            return instance
        }
    }
}