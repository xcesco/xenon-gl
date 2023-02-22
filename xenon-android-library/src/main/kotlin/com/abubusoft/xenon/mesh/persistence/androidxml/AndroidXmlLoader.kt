package com.abubusoft.xenon.mesh.persistence.androidxml

import android.content.Context
import com.abubusoft.kripton.KriptonBinder
import com.abubusoft.xenon.core.XenonRuntimeException
import java.io.IOException
import java.io.InputStream

object AndroidXmlLoader {
    @Throws(IOException::class, Exception::class)
    fun parse(stream: InputStream?): XmlDataModel {
        /*BinderReader binder = BinderFactory.getXMLReader();
		XmlDataModel mesh = binder.read(XmlDataModel.class, stream);*/return KriptonBinder.xmlBind().parse(stream, XmlDataModel::class.java)
    }

    fun loadFromAsset(context: Context, fileName: String?): XmlDataModel {
        return try {
            parse(context.assets.open(fileName!!))
        } catch (e: Exception) {
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }

    fun loadFromResources(context: Context, resourceId: Int): XmlDataModel {
        return try {
            parse(context.resources.openRawResource(resourceId))
        } catch (e: Exception) {
            e.printStackTrace()
            throw XenonRuntimeException(e)
        }
    }
}