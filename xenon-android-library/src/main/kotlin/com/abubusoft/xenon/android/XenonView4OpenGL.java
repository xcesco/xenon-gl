package com.abubusoft.xenon.android;

import com.abubusoft.xenon.Xenon4OpenGL;
import com.abubusoft.xenon.context.XenonBeanContext;
import com.abubusoft.xenon.context.XenonBeanType;
import com.abubusoft.xenon.android.listener.XenonGestureDetector;
import com.abubusoft.xenon.android.surfaceview16.ArgonGLSurfaceView16;
import com.abubusoft.xenon.opengl.XenonGLDefaultRenderer;
import com.abubusoft.xenon.opengl.XenonGLRenderer;
import com.abubusoft.xenon.core.Uncryptable;

import android.content.Context;

/**
 * View opengl
 * 
 * @author Francesco Benincasa
 * 
 */
@Uncryptable
public class XenonView4OpenGL extends ArgonGLSurfaceView16 {
	
	protected Xenon4OpenGL argon;

	public XenonView4OpenGL(Context context) {
		super(context);
	}

	/**
	 * Avvia il contesto xenon
	 */
	public void startArgonContext() {
		try {
			// crea l'applicazione
			argon = (Xenon4OpenGL) XenonBeanContext.getBean(XenonBeanType.XENON);
			//ApplicationManager.getInstance().attributes.get(ApplicationManagerAttributeKeys.MODE);
			argon.onViewCreated(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public XenonGestureDetector gestureDetector;

	/**
	 * Consente di effettuare il build di un renderer
	 * 
	 * @return istanza di render da usare
	 */
	public XenonGLRenderer createRenderer() {
		 return new XenonGLDefaultRenderer();
	}

}
