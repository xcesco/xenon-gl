package com.abubusoft.xenon.context

import android.content.Context
import com.abubusoft.kripton.android.Logger

/**
 *
 *
 * Utilità per la creazione di componenti argon.
 *
 * @author Francesco Benincasa
 */
object XenonBeanContext {
    /**
     *
     *
     * Crea un'instanza di un determinato oggetto. E' richiesto che tale oggetto abbia il costruttore di default. Tutti i field con annotazione XenonBeanInject vengono risolti con gli oggetti già instanziati.
     *
     *
     * @param clazz
     * classe da istanziare
     * @return
     * crea una nuova istanza
     */
    fun <E> createInstance(clazz: Class<E>): E? {
        var `object`: E? = null
        `object` = try {
            // creiamo istanza oggetto
            Logger.debug("BeanInject - createInstance of allocation %s", clazz.simpleName)
            clazz.newInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            Logger.error(e.message)
            throw RuntimeException(e)
        }
        fillBean(`object`)
        return `object`
    }

    /**
     *
     *
     * Effettua nuovamente il binding delle proprietà se eventualmente ci sono.
     *
     *
     * @param bean
     */
    fun fillBean(bean: Any?) {
        // verifichiamo se è un argonComponent
        // if (clazz.getAnnotation(XenonBean.class) != null) {
        val fields = bean!!.javaClass.fields
        var inject: XenonBeanInject?
        val n = fields.size
        for (i in 0 until n) {
            inject = fields[i].getAnnotation(XenonBeanInject::class.java)
            if (inject != null) {
                try {
                    fields[i][bean] = inject.value().value
                    Logger.debug("BeanInject - Injected field %s with bean %s", fields[i].name, inject.value().toString())
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    Logger.error(e.message)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    Logger.error(e.message)
                }
            }
        }
        // }
    }

    /**
     *
     *
     * Imposta un bean predefinito.
     *
     * @param type
     * tipo da impostare
     * @param value
     * istanza
     */
    fun setBean(type: XenonBeanType, value: Any?) {
        if (value != null) Logger.debug("BeanInject - setBean %s of allocation %s", type.toString(), value.javaClass.name) else {
            Logger.debug("BeanInject - setBean %s as null", type.toString())
        }
        type.value = value
    }

    /**
     *
     *
     * Recupera un bean predefinito.
     *
     *
     * @param type
     * @return bean predefinito
     */
    fun <T> getBean(type: XenonBeanType): T? {
        return type.value as T
    }

    /**
     *
     *
     * Permette di recuperare da qualunque punto il context
     *
     *
     * @return
     * contesto android
     */
    val context: Context
        get() = getBean<Context>(XenonBeanType.CONTEXT)!!
}