/**
 *
 */
package com.abubusoft.xenon.android

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.Xenon4OpenGL
import com.abubusoft.xenon.android.listener.XenonGestureDetector
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer
import com.abubusoft.xenon.opengl.XenonGLRenderer

/**
 *
 *
 * Activity di base per la gestione dei game e dei wallpaper. Se non viene specifiato altro, questa classe viene visualizzata dopo lo splash screen in caso di game.
 *
 *
 *
 *
 *
 * Può essere usata direttamente anche senza splash screen.
 *
 *
 *
 *
 *
 * Il nome del file è:
 *
 *
 *
 * <pre>
 * res / raw / argonSettings.xml
</pre> *
 *
 *
 *
 *
 * @author Francesco Benincasa
 */
class XenonActivity4OpenGL : Activity() {
    var gestureDetector: XenonGestureDetector? = null

    /**
     * Consente di effettuare il build di un renderer
     *
     * @return istanza di render da usare
     */
    fun createRenderer(): XenonGLRenderer {
        return XenonGLDefaultRenderer()
    }

    /**
     * context xenon
     */
    var xenon: Xenon4OpenGL? = null

    /**
     * view utilizzata per il rendering
     */
    protected var glView: ArgonGLView? = null

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // recuperiamo xenon dal contesto
            xenon = XenonBeanContext.getBean<Any>(XenonBeanType.XENON) as Xenon4OpenGL
            xenon!!.onActivityCreated(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *
     *
     * Imposta il glSurfaceView nel quale viene renderizzato il context opengl
     *
     *
     * @param view
     */
    fun setArgonGLSurfaceView(view: ArgonGLView?) {
        // nostro riferimento
        glView = view
        setContentView(view)
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    override fun onDestroy() {
        super.onDestroy()
        xenon!!.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //getMenuInflater().inflate(R.menu.argon_activity, menu);
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /*if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, ArgonSettingsActivity.class));
            return true;
        }*/
        return super.onOptionsItemSelected(item)
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    override fun onResume() {
        super.onResume()
        Logger.info("XenonActivity4OpenGL - onResume")
        glView!!.onResume()
        xenon!!.onResume(this)

        // XENON-39, impostiamo il flag surfaceCreated
        // la superficie non è ancora pronta
        //xenon.surfaceReady = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    override fun onPause() {
        super.onPause()
        glView!!.onPause()
        Logger.debug("XenonActivity4OpenGL - onPause")
        xenon!!.onPause(this)
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // come da
        // http://stackoverflow.com/questions/15309743/use-scalegesturedetector-with-gesturedetector
        if (xenon!!.isSceneReady) {
            return gestureDetector!!.onTouchEvent(event)
        } else {
            Logger.debug("onTouchEvent but surface is not ready")
        }
        return false
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStop()
     */
    override fun onStop() {
        super.onStop()
    }
}