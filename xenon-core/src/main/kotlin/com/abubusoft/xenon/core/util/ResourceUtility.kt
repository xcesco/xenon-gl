/**
 *
 */
package com.abubusoft.xenon.core.util

import android.content.Context
import android.graphics.Paint
import android.util.TypedValue

/**
 * @author Francesco Benincasa
 */
object ResourceUtility {
    /**
     *
     * Dato un context ed una stringa con il nome di una risorsa, ricaviamo
     * l'address. Supporta tutti i tipi di risorsa. Può avere davanti il @, ma anche no.
     *
     *
     * Se non trova l'elemento, viene restituito 0.
     *
     *
     * @param context
     * @param resourceName
     * @return
     */
    fun resolveAddress(context: Context, resourceName: String): Int {
        var resourceName = resourceName
        if (resourceName.startsWith("@")) {
            resourceName = resourceName.substring(1)
        } else if (resourceName.startsWith("res")) {
            // nel caso in cui sia /res/drawble, lo trasforma in una stringa utilizzabile					
            resourceName = resourceName.substring(4)
            //resourceName=resourceName.replace("drawable-nodpi","drawable");
            if (resourceName.indexOf("-") > -1) {
                resourceName = resourceName.substring(0, resourceName.indexOf("-")) + resourceName.substring(resourceName.indexOf('/'))
            }
        }
        var valueName = resourceName.substring(resourceName.lastIndexOf('/') + 1)
        if (valueName.indexOf(".") > 0) {
            valueName = valueName.substring(0, valueName.indexOf("."))
        }
        val valueType = resourceName.substring(0, resourceName.lastIndexOf('/'))
        return context.resources.getIdentifier(valueName, valueType, context.packageName)
    }

    /**
     * Recupera il valore della stringa ricercata
     *
     * @param context
     * @param address
     * @return
     */
    fun resolveString(context: Context, address: Int): String {
        return context.resources.getString(address)
    }

    /**
     *
     * Recupera l'elenco delle stringhe
     *
     * @param context
     * @param address
     * @return
     */
    fun resolveArrayOfString(context: Context, address: Int): Array<String> {
        return context.resources.getStringArray(address)
    }

    /**
     * Recupera il valore della stringa ricercata partendo dalla stringa che rappresenta
     * l'indirizzo della risorsa. Se non esiste, restituisce null
     *
     * @param context
     * @param address
     * @return
     */
    fun resolveString(context: Context, namedAddress: String): String? {
        val address = resolveAddress(context, namedAddress)
        return if (address != 0) context.resources.getString(address) else null
    }

    /**
     * Converte una dimensione in dp nei relativi pixel
     * @param context
     * @param dp
     * @return
     */
    fun convertDp2Pixels(context: Context, dp: Int): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), r.displayMetrics)
    }

    /**
     * Misura le dimensioni di un testo con una determinata fonte
     * @param input
     * @param fontSizeInPixel
     * @return
     */
    fun measureTextWidth(input: String?, fontSizeInPixel: Float): Float {
        var paint: Paint? = Paint()
        paint!!.textSize = fontSizeInPixel
        val res = paint.measureText(input)
        //"xxxxxxxxxxxxxxxxxx"
        paint = null
        return res
    }

    /**
     * Converte degli sp in pixels. Il text small ad esempio sono 14sp.
     *
     * Vedi
     * http://stackoverflow.com/questions/6263250/convert-pixels-to-sp
     *
     * @param context
     * @param sp
     * @return
     */
    fun convertSpToPixels(context: Context, sp: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (sp * scaledDensity).toInt()
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(context: Context, dp: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return dp * (metrics.densityDpi / 160f)
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    fun convertPixelsToDp(context: Context, px: Float): Float {
        val resources = context.resources
        val metrics = resources.displayMetrics
        return px / (metrics.densityDpi / 160f)
    }
}