package com.abubusoft.xenon.android

import com.abubusoft.kripton.android.Logger
import com.abubusoft.xenon.settings.LoggerLevelType

/**
 * Created by xcesco on 15/12/2017.
 */
object XenonLogger {
    /**
     * livello
     */
    var level = LoggerLevelType.NONE
    fun debug(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.DEBUG.ordinal) {
            Logger.debug(msg, *args)
        }
    }

    fun info(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.INFO.ordinal) {
            Logger.info(msg, *args)
        }
    }

    fun error(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.ERROR.ordinal) {
            Logger.error(msg, *args)
        }
    }

    fun fatal(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.FATAL.ordinal) {
            Logger.fatal(msg, *args)
        }
    }

    fun verbose(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.VERBOSE.ordinal) {
            Logger.verbose(msg, *args)
        }
    }

    fun warn(msg: String?, vararg args: Any?) {
        if (level.ordinal <= LoggerLevelType.WARN.ordinal) {
            Logger.warn(msg, *args)
        }
    }
}