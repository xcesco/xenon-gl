package com.abubusoft.xenon.core.util

import android.content.Context
import android.graphics.Bitmap
import java.io.*

/**
 * Utility varie
 *
 * @author Francesco Benincasa
 */
object IOUtility {
    /**
     * Legge un file di testo a partire dal suo percorso.
     *
     * @param fullfilename
     * @return
     */
    fun readTextFile(fullfilename: String?): String? {
        var everything: String? = null
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(fullfilename))
            val sb = StringBuilder()
            var line = br.readLine()
            while (line != null) {
                sb.append(line)
                sb.append('\n')
                line = br.readLine()
            }
            everything = sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (br != null) try {
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return everything
    }

    /**
     * Legge un file di testo a partire dal suo percorso.
     *
     * @param fullfilename
     * @return
     */
    fun readTextFileAsStrings(fullfilename: String?): ArrayList<String?>? {
        var everything: ArrayList<String?>? = null
        var br: BufferedReader? = null
        try {
            br = BufferedReader(FileReader(fullfilename))
            var line = br.readLine()
            everything = ArrayList()
            while (line != null) {
                everything.add(line)
                line = br.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (br != null) try {
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return everything
    }

    /**
     * Legge un file di testo da una risorsa raw.
     *
     * @param context
     * @param resourceId
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readTextFileFromAssets(context: Context, fileName: String?): String {
        val buffer = StringBuilder()
        val inputStream = context.assets.open(fileName!!)
        val `in` = BufferedReader(InputStreamReader(inputStream))
        var read = `in`.readLine()
        while (read != null) {
            buffer.append(
                """
    $read
    
    """.trimIndent()
            )
            read = `in`.readLine()
        }
        inputStream.close()
        buffer.deleteCharAt(buffer.length - 1)
        return buffer.toString()
    }

    /**
     * Legge un file di testo da una risorsa raw.
     *
     * @param context
     * @param resourceId
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun readRawTextFile(context: Context, rawResourceId: Int): String {
        val buffer = StringBuilder()
        val inputStream = context.resources.openRawResource(rawResourceId)
        val `in` = BufferedReader(InputStreamReader(inputStream))
        var read = `in`.readLine()
        while (read != null) {
            buffer.append(
                """
    $read
    
    """.trimIndent()
            )
            read = `in`.readLine()
        }
        inputStream.close()
        buffer.deleteCharAt(buffer.length - 1)
        return buffer.toString()
    }

    /**
     * Crea un file vuoto nella cartella cache dell'applicazione con l'estensione desiderata e con un
     *
     * @param ctx
     * @param extension
     * @return
     */
    fun createEmptyFile(ctx: Context, prefix: String?, extension: String): File? {
        var prefix = prefix
        val outputDir = ctx.cacheDir
        var outputFile: File? = null
        try {
            prefix = prefix ?: ""
            outputFile = File.createTempFile(prefix, ".$extension", outputDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputFile
    }

    /**
     * Salva una bitmap in formato png nella cartella cache dell'applicazione.
     *
     * @param photo
     * @return
     */
    fun saveTempPngFile(context: Context, prefix: String?, photo: Bitmap): String? {
        val outputDir = context.cacheDir
        return saveTempPngFile(context, prefix, outputDir, photo)
    }

    /**
     * Cancella tutti i file temporanei che iniziano con un dato prefisso
     *
     * @param context
     * @param prefix
     * @return numero di file cancellati
     */
    fun deleteTempFiles(context: Context, prefix: String?): Int {
        var i = 0
        val cacheDir = context.cacheDir
        var file: File
        val filesToDelete = cacheDir.list { dir, filename ->
            if (filename.startsWith(prefix!!)) {
                true
            } else false
        }
        for (item in filesToDelete) {
            file = File("$cacheDir/$item")

            //	Logger.debug("Delete cached file %s", file.getAbsolutePath());
            file.delete()
            i++
        }
        return i
    }

    /**
     * Cancella tutt le preference fatte dall'utente
     *
     * @param context
     * @return
     */
    fun deleteAllSavedUserPreferences(context: Context): Int {
        val listName = ArrayList<String>()
        val sharedPrefsDir = File(context.cacheDir.absoluteFile.toString() + "/../shared_prefs/")
        sharedPrefsDir.list { dir, filename ->
            if (filename.startsWith("userprefs")) {
                listName.add(filename.substring(0, filename.lastIndexOf(".")))
            }
            false
        }
        var file: File
        for (item in listName) {
            file = File(sharedPrefsDir.absolutePath + "/" + item + ".xml")

            //     Logger.debug("Delete SavedUserPreference file %s", file.getAbsolutePath());
            file.delete()
        }
        return listName.size
    }

    /**
     * cancella il salvataggio fatto dall'utente
     *
     * @param context
     * @param preferenceName
     * @return
     */
    fun deleteSavedUserPreference(context: Context, preferenceName: String): Boolean {
        val cacheDir = context.cacheDir
        val sharedPrefsDir = File(cacheDir.absoluteFile.toString() + "/../shared_prefs")
        var file: File? = null
        try {
            file = File(sharedPrefsDir.absolutePath + "/" + preferenceName + ".xml")
            file.delete()
            //     Logger.debug("Deleted SavedUserPreference file %s", file.getAbsolutePath());
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            // Logger.warn("Can not delete user-preference-file %s", file.getAbsolutePath());
        }
        return false
    }

    /**
     * Recupera l'elenco delle preferenze utente.
     *
     * @param context
     * @return
     */
    fun readSavedUserPreference(context: Context): ArrayList<String> {
        val listName = ArrayList<String>()
        val cacheDir = context.cacheDir
        val sharedPrefsDir = File(cacheDir.absoluteFile.toString() + "/../shared_prefs/")
        sharedPrefsDir.list { dir, filename ->
            if (filename.startsWith("user")) {
                listName.add(filename.substring(0, filename.lastIndexOf(".")))
            }
            false
        }
        return listName
    }

    /**
     * Salva un png in un dato percorso dato sottoforma di file.
     *
     * @param photo
     * @return
     */
    fun saveTempPngFile(context: Context?, prefix: String?, outputDir: File?, photo: Bitmap): String? {
        val outputFile: File
        var outputFileName: String? = null
        try {
            outputFile = File.createTempFile(prefix, ".png", outputDir)
            val out = FileOutputStream(outputFile)
            photo.compress(Bitmap.CompressFormat.PNG, 90, out)
            outputFileName = outputFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return outputFileName
    }

    /**
     * Salva un file di testo nella cartella temporanea
     *
     * @param photo
     * @return
     */
    fun writeTempRawTextFile(context: Context, filename: String?, text: String?): Boolean {
        val outputDir = context.cacheDir
        return writeRawTextFile(context, filename, outputDir, text)
    }

    /**
     * Salva un file di testo in un dato percorso dato sottoforma di file.
     *
     * @param photo
     * @return
     */
    fun writeRawTextFile(context: Context?, filename: String?, outputDir: File?, text: String?): Boolean {
        val outputFile: File
        var writer: BufferedWriter? = null
        try {
            outputFile = File(outputDir, filename)
            val fw = FileWriter(outputFile)
            writer = BufferedWriter(fw)
            writer.write(text)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        } finally {
            if (writer != null) try {
                writer.close()
            } catch (e: IOException) {
            }
        }
        return true
    }

    /**
     * Salva un'immagine in formato
     *
     * @param ctx
     * @param outputFileName
     * @param photo
     * @return
     */
    fun savePngFile(ctx: Context?, outputFileName: String?, photo: Bitmap): Boolean {
        var outputFile: File? = null
        try {
            outputFile = File(outputFileName)
            val out = FileOutputStream(outputFile)
            photo.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                outputFile?.delete()
            } catch (f: Exception) {
            }
            return false
        }
        return true
    }

    /**
     * Copia un file
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(src: File?, dst: File?) {
        val inChannel = FileInputStream(src).channel
        val outChannel = FileOutputStream(dst).channel
        try {
            inChannel!!.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
        }
    }
}