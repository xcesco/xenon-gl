package com.abubusoft.xenon.mesh.persistence.androidxml;

import java.io.IOException;
import java.io.InputStream;

import com.abubusoft.xenon.core.XenonRuntimeException;

import com.abubusoft.kripton.KriptonBinder;

import android.content.Context;

public class AndroidXmlLoader {
	public static XmlDataModel parse(InputStream stream) throws IOException, Exception {
		XmlDataModel mesh = KriptonBinder.xmlBind().parse(stream, XmlDataModel.class);
		/*BinderReader binder = BinderFactory.getXMLReader();
		XmlDataModel mesh = binder.read(XmlDataModel.class, stream);*/

		return mesh;
	}

	public static XmlDataModel loadFromAsset(Context context, String fileName) {
		try {
			return parse(context.getAssets().open(fileName));
		} catch (Exception e) {
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}

	public static XmlDataModel loadFromResources(Context context, int resourceId) {
		try {
			return parse(context.getResources().openRawResource(resourceId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new XenonRuntimeException(e);
		}
	}
}
