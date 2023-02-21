package com.abubusoft.xenon.android;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.xenon.settings.LoggerLevelType;

/**
 * Created by xcesco on 15/12/2017.
 */

public abstract class XenonLogger {

    /**
     * livello
     */
    public static LoggerLevelType level = LoggerLevelType.NONE;

    public static void debug(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.DEBUG.ordinal()) {
            Logger.debug(msg, args);
        }
    }

    public static void info(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.INFO.ordinal()) {
            Logger.info(msg, args);
        }
    }

    public static void error(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.ERROR.ordinal()) {
            Logger.error(msg, args);
        }
    }

    public static void fatal(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.FATAL.ordinal()) {
            Logger.fatal(msg, args);
        }
    }

    public static void verbose(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.VERBOSE.ordinal()) {
            Logger.verbose(msg, args);
        }
    }

    public static void warn(String msg, Object... args) {
        if (level.ordinal() <= LoggerLevelType.WARN.ordinal()) {
            Logger.warn(msg, args);
        }
    }


}
