package com.abubusoft.xenon

import android.app.ActivityManager
import android.content.Context
import com.abubusoft.xenon.context.XenonBeanContext
import java.io.File
import java.io.FileFilter
import java.util.regex.Pattern

object DeviceInfo {
    /**
     * @return the avaiableRAM in mebibyte
     */
    /**
     * memoria del dispositivo
     */
    val availableRAM: Long
    /**
     * numero di cpu
     *
     * @return the cpuCores
     */
    /**
     * numero di core della cpu
     */
    val cpuCores: Long

    init {
        cpuCores = retrieveCPUCores()
        availableRAM = retrieveRAM()
    }


    /**
     * Gets the number of cores available in this device, across all processors. Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     *
     * da [qui](http://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code)
     *
     * @return The number of cores, or 1 if failed to get result
     */
    fun retrieveCPUCores(): Long {
        // Private Class to display only CPU devices in the directory listing
        class CpuFilter : FileFilter {
            override fun accept(pathname: File): Boolean {
                // Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.name)
            }
        }
        return try {
            // Get directory containing CPU info
            val dir = File("/sys/devices/system/cpu/")
            // Filter to only list the devices we care about
            val files = dir.listFiles(CpuFilter())
            // Return the number of cores (virtual CPU devices)
            files?.size?.toLong() ?: 1
        } catch (e: Exception) {
            // Default to return 1 core
            1
        }
    }

    fun retrieveRAM(): Long {
        val context = XenonBeanContext.getContext()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        return memoryInfo.totalMem / 1048576L
    }

}