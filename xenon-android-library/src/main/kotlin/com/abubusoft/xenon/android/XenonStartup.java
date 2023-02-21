package com.abubusoft.xenon.android;

import com.abubusoft.kripton.android.KriptonLibrary;
import com.abubusoft.xenon.Xenon;
import com.abubusoft.xenon.Xenon4App;
import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.XenonLibrary;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.R;
import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.settings.XenonSettings;
import com.abubusoft.xenon.settings.XenonSettingsFactory;
import com.abubusoft.xenon.settings.ArgonSettingsReader;
import com.abubusoft.kripton.android.Logger;

import android.app.Application;

/**
 * <p>
 * Rappresenta l'application di base di tutte le applicazioni che si basano su argon.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class XenonStartup extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		KriptonLibrary.init(this);
		XenonLibrary.init(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		Logger.info("XenonStartup - onLowMemory");
	}

}
