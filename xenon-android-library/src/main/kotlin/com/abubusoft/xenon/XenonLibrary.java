package com.abubusoft.xenon;

import android.content.Context;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.android.XenonActivity4OpenGL;
import com.abubusoft.xenon.android.XenonLogger;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.core.XenonRuntimeException;
import com.abubusoft.xenon.settings.ArgonSettingsReader;
import com.abubusoft.xenon.settings.XenonSettings;
import com.abubusoft.xenon.settings.XenonSettingsFactory;

/**
 * Created by xcesco on 15/12/2017.
 */

public abstract class XenonLibrary {

    static class ResourceSettings implements XenonSettingsFactory {

        @Override
        public XenonSettings buildSettings() {
            return ArgonSettingsReader.readFromRawXml(XenonLibrary.context, R.raw.xenon_settings);
        }
    }

    /**
     * <p>
     * Effettua la configurazione di avvio di argon relativa all'upgrade policy
     * </p>
     */
    //protected void applyStartupSettings(ApplicationManager am, XenonSettings settings) {
    static void applyStartupSettings(XenonSettings settings) {
        // impostiamo i settings tra gli attributi
        XenonBeanContext.setBean(XenonBeanType.XENON_SETTINGS, settings);
        // am.attributes.put(ApplicationManagerAttributeKeys.SETTINGS, settings);
        try {
            //	am.upgradePolicy = (ApplicationUpgradePolicy) Class.forName(settings.application.upgradePolicyClazz.trim()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Configurazione del logger
     * </p>
     */
    static void applyLoggerSettings(XenonSettings settings) {
        // impostiamo il logger
        XenonLogger.level = settings.logger.level;
    }

    private static Context context;

    public static Context context() {
        return context;
    }


    public static void init(Context contextValue) {
        context = contextValue;

        // 1 - carichiamo settings da file xml
        XenonSettings settings = (new ResourceSettings()).buildSettings();

        // 2 - definiamo il log
        applyLoggerSettings(settings);

        // 3 - sistemiamo applicationManager
//		ApplicationManager am = ApplicationManager.instance();
        applyStartupSettings(settings);

        // 4 - startup dell'application manager
//		ApplicationInfo info = am.startup(this);

        Logger.info("XenonStartup - onCreate, mode %s", settings.application.mode);

        // avvio applicazione
        //Logger.info("Application %s ver. %s stopped, execution counter %s ", info.name, info.version, info.executionNumber);

        // 5 - cancelliamo la parte relativa ai file xml che rendono persistente
        // la configurazione
        if (settings.application.resetConfig) {
            // avvio applicazione
            Logger.info("Both system and application preferences are reset by configuration");
            // pulisce la configurazione di sistema
            //am.resetSystemPreferences();
            //am.resetApplicationPreferences();
        }

        // 6 - avvia la parte mode e l'application
        Xenon xenon = null;

        switch (settings.application.mode) {
            case APP:
                xenon = new Xenon4App();
                break;
            case OPENGL:
                xenon = new Xenon4OpenGL();
                if (settings.application.activityClazz == null) {
                    settings.application.activityClazz = XenonActivity4OpenGL.class.getName();
                }
                break;
            default:
                throw (new XenonRuntimeException("No valid application type was defined with settings.application.mode parameter"));
        }

        // impostiamo il bean context
        XenonBeanContext.setBean(XenonBeanType.CONTEXT, context);
        // impostiamo il bean xenon
        XenonBeanContext.setBean(XenonBeanType.XENON, xenon);
        xenon.init(XenonLibrary.context, settings);

        // imposta il config storage
        //am.configStorage = xenon.getConfigStorage();

        //
        // <p>lo dobbiamo mettere qua perchè l'application deve essere
        // inizializzata.</p>
//		if (am.isConfigReset()) {
//			xenon.onConfigReset();
//			am.setConfigReset(false);
//		}
    }
}
