/**
 *
 */
package com.abubusoft.xenon.android;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.android.listener.XenonGestureDetector;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLView;
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer;
import com.abubusoft.xenon.opengl.XenonGLRenderer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

/**
 * <p>
 * Activity di base per la gestione dei game e dei wallpaper. Se non viene specifiato altro, questa classe viene visualizzata dopo lo splash screen in caso di game.
 * </p>
 * <p>
 * <p>
 * Può essere usata direttamente anche senza splash screen.
 * </p>
 * <p>
 * <p>
 * Il nome del file è:
 * </p>
 * <p>
 * <pre>
 * res / raw / argonSettings.xml
 * </pre>
 * <p>
 * </p>
 *
 * @author Francesco Benincasa
 */
public class XenonActivity4OpenGL extends Activity {

    public XenonGestureDetector gestureDetector;

    /**
     * Consente di effettuare il build di un renderer
     *
     * @return istanza di render da usare
     */
    public XenonGLRenderer createRenderer() {
        return new XenonGLDefaultRenderer();
    }

    /**
     * context xenon
     */
    public Xenon4OpenGL xenon;

    /**
     * view utilizzata per il rendering
     */
    protected ArgonGLView glView;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            // recuperiamo xenon dal contesto
            xenon = (Xenon4OpenGL) XenonBeanContext.getBean(XenonBeanType.XENON);
            xenon.onActivityCreated(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Imposta il glSurfaceView nel quale viene renderizzato il context opengl
     * </p>
     *
     * @param view
     */
    public void setArgonGLSurfaceView(ArgonGLView view) {
        // nostro riferimento
        glView = view;

        setContentView(view);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        xenon.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.argon_activity, menu);

        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, ArgonSettingsActivity.class));
            return true;
        }*/

        return (super.onOptionsItemSelected(item));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();


        Logger.info("XenonActivity4OpenGL - onResume");

        glView.onResume();

        xenon.onResume(this);

        // XENON-39, impostiamo il flag surfaceCreated
        // la superficie non è ancora pronta
        //xenon.surfaceReady = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        glView.onPause();

        Logger.debug("XenonActivity4OpenGL - onPause");


        xenon.onPause(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // come da
        // http://stackoverflow.com/questions/15309743/use-scalegesturedetector-with-gesturedetector
        if (xenon.isSceneReady()) {
            return gestureDetector.onTouchEvent(event);
        } else {
            Logger.debug("onTouchEvent but surface is not ready");
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

}
