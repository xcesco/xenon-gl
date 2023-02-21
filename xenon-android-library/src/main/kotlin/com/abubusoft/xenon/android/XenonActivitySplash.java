package com.abubusoft.xenon.android;

import com.abubusoft.xenon.Xenon4BaseImpl;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.XenonStartupTask;
import com.abubusoft.xenon.settings.XenonSettings;
import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.core.util.ResourceUtility;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * <p>
 * Splash screen. Serve a visualizzare una pagina di info e poi partire con
 * l'applicazione vera e propria
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class XenonActivitySplash extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// finestra senza titolo
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//setContentView(R.layout.argon_splash_layout);

		// costruiamo settings per timeout splash screen
		//ApplicationInfo info = ApplicationManager.instance().info;

		//String version = info.version.toString();

		int resVersion = ResourceUtility.resolveAddress(this,
				"id/argon_splash_version");
		if (resVersion != 0) {
			TextView txtVersion = (TextView) findViewById(resVersion);
			//txtVersion.setText(version);
		} else {
			Logger
					.warn("argon_splash_version is not present in argon_splash_layout");
		}

		int resApplication = ResourceUtility.resolveAddress(this,
				"id/argon_splash_application");
		if (resApplication != 0) {
			TextView txtApplication = (TextView) findViewById(resApplication);
			//txtApplication.setText(info.name);
		}
//		} else if (Logger.isEnabledFor(LoggerLevelType.WARN)) {
//			Logger
//					.warn("argon_splash_application is not present in argon_splash_layout");
//		}

		// XenonSettings settings = (XenonSettings)
		// ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.SETTINGS);
		XenonSettings settings = XenonBeanContext
				.getBean(XenonBeanType.XENON_SETTINGS);
		new Handler().postDelayed(new Runnable() {

			/*
			 * Showing splash screen with a touchTimer. This will be useful when
			 * you want to show case your app logo / company
			 */
			@Override
			public void run() {

				// TODO mettere del codice qua
				// This method will be executed once the touchTimer is over
				// Start your app main activity
				@SuppressWarnings("rawtypes")
				Xenon4BaseImpl<?> argon = (Xenon4BaseImpl) XenonBeanContext
						.getBean(XenonBeanType.XENON);
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

				Intent i;
				try {
					i = new Intent(XenonActivitySplash.this, Class
							.forName(argon.settings.application.activityClazz.trim()));
					startActivity(i);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}

				// close this activity
				finish();
			}
		}, settings.application.splashScreenTimeout);

		try {
			// esegue operazione di inizializzazione
			if (settings.application.startupTaskClazz != null) {				
				XenonStartupTask task = (XenonStartupTask) Class.forName(settings.application.startupTaskClazz.trim()).newInstance();
				Logger.info("Execute startup task %s", settings.application.startupTaskClazz);
				task.doTask(this);
			}

		} catch (Exception e) {
			Logger.fatal("Error during startup: %s", e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

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
	@Override
	protected void onPause() {
		super.onPause();

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
