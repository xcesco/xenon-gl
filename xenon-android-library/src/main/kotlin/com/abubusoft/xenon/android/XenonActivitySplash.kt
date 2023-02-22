package com.abubusoft.xenon.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.Xenon4BaseImpl
import com.abubusoft.xenon.XenonStartupTask
import com.abubusoft.xenon.context.XenonBeanContext
import com.abubusoft.xenon.context.XenonBeanType
import com.abubusoft.xenon.core.util.ResourceUtility.resolveAddress
import com.abubusoft.xenon.settings.XenonSettings

/**
 *
 *
 * Splash screen. Serve a visualizzare una pagina di info e poi partire con
 * l'applicazione vera e propria
 *
 *
 * @author Francesco Benincasa
 */
class XenonActivitySplash : Activity() {
    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // finestra senza titolo
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //setContentView(R.layout.argon_splash_layout);

        // costruiamo settings per timeout splash screen
        //ApplicationInfo info = ApplicationManager.instance().info;

        //String version = info.version.toString();
        val resVersion = resolveAddress(
            this,
            "id/argon_splash_version"
        )
        if (resVersion != 0) {
            val txtVersion = findViewById<View>(resVersion) as TextView
            //txtVersion.setText(version);
        } else {
            Logger
                .warn("argon_splash_version is not present in argon_splash_layout")
        }
        val resApplication = resolveAddress(
            this,
            "id/argon_splash_application"
        )
        if (resApplication != 0) {
            val txtApplication = findViewById<View>(resApplication) as TextView
            //txtApplication.setText(info.name);
        }
        //		} else if (Logger.isEnabledFor(LoggerLevelType.WARN)) {
//			Logger
//					.warn("argon_splash_application is not present in argon_splash_layout");
//		}

        // XenonSettings settings = (XenonSettings)
        // ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.SETTINGS);
        val settings = XenonBeanContext
            .getBean<XenonSettings>(XenonBeanType.XENON_SETTINGS)
        Handler().postDelayed({ // TODO mettere del codice qua
            // This method will be executed once the touchTimer is over
            // Start your app main activity
            val argon = XenonBeanContext
                .getBean<Any>(XenonBeanType.XENON) as Xenon4BaseImpl<*>
            // Xenon4BaseImpl<?> xenon = (Xenon4BaseImpl)
            // ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.MODE);
            /*
                     * try { Xenon4BaseImpl xenon = (Xenon4BaseImpl)
                     * ApplicationManager
                     * .getInstance().attributes.get(ApplicationManagerAttributeKeys
                     * .MODE); xenon.
                     * xenon.onActivityCreated(ArgonActivity4App.this); } catch
                     * (Exception e) { e.printStackTrace(); }
                     */
            val i: Intent
            try {
                i = Intent(this@XenonActivitySplash, Class
                    .forName(argon.settings.application.activityClazz.trim { it <= ' ' })
                )
                startActivity(i)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            // close this activity
            finish()
        }, settings.application.splashScreenTimeout.toLong())
        try {
            // esegue operazione di inizializzazione
            if (settings.application.startupTaskClazz != null) {
                val task = Class.forName(settings.application.startupTaskClazz.trim { it <= ' ' }).newInstance() as XenonStartupTask
                Logger.info("Execute startup task %s", settings.application.startupTaskClazz)
                task.doTask(this)
            }
        } catch (e: Exception) {
            Logger.fatal("Error during startup: %s", e.message)
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
    override fun onResume() {
        super.onResume()

        // questa parte non ha necessariamente xenon sistemato
        /*
		 * if (Logger.isEnabledFor(LoggerLevelType.INFO)) {
		 * Logger.info("ArgonActivity4App - onResume %s",
		 * this.getClass().getName()); }
		 * 
		 * xenon.application.onResume(this);
		 */
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
    override fun onPause() {
        super.onPause()

        // questa parte non ha necessariamente xenon sistemato
        /*
		 * if (Logger.isEnabledFor(LoggerLevelType.INFO)) {
		 * Logger.info("ArgonActivity4App - onPause %s",
		 * this.getClass().getName()); }
		 * 
		 * xenon.application.onPause(this);
		 */
    }
}