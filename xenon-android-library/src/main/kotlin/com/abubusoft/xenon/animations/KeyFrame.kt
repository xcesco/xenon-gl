package com.abubusoft.xenon.animations

import com.abubusoft.kripton.annotation.Bind
import com.abubusoft.kripton.annotation.BindDisabled
import com.abubusoft.kripton.annotation.BindType
import com.abubusoft.kripton.annotation.BindXml
import com.abubusoft.kripton.xml.XmlType
import com.abubusoft.xenon.interpolations.Interpolation
import com.abubusoft.xenon.interpolations.InterpolationLinear

/**
 * key frame delle animazioni. Ogni frame contiene un valore. A seconda del tipo di animazione il valore cambia. Ogni frame può avere anche un nome. Sempre e comunque deve avere
 * una durata.
 *
 * @author Francesco Benincasa
 */
@BindType
open class KeyFrame {
    /**
     * nome del keyframe
     */
    @Bind(order = 0)
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var name: String? = null

    /**
     *
     *
     * Durata del frame in millisecondi.
     *
     */
    @Bind(order = 1)
    @BindXml(xmlType = XmlType.ATTRIBUTE)
    var duration: Long = 0

    /**
     * interpolazione da usare nel momento in cui questo value diventa quello corrente.
     */
    @BindDisabled
    var interpolation: Interpolation = InterpolationLinear.instance()
}