package com.abubusoft.xenon.context

/**
 *
 *
 * Sono i valori che possono essere trasmessi mediante reflection
 *
 *
 * @author Francesco Benincasa
 */
enum class XenonBeanType {
    /**
     * application
     */
    APPLICATION,

    /**
     *
     */
    XENON,

    /**
     *
     *
     * Impostazioni caricate da file xml. il bean è di tipo
     * [XenonSettings]
     *
     *
     */
    XENON_SETTINGS,

    /**
     * android context (application context)
     */
    CONTEXT,

    /**
     * configurazione dell'applicazione. Deve derivare da ConfigBase
     */
    CONFIG;

    /**
     * istanza di classe da associare
     */
    var value: Any? = null
}