package com.abubusoft.xenon.opengl;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import com.abubusoft.xenon.misc.Clock;
import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureAsyncLoaderListener;
import com.abubusoft.xenon.texture.TextureInfo;
import com.abubusoft.kripton.android.Logger;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * <p>
 * Si occupa di gestire le operazioni opengl async in un secondo contesto. Nel caso in cui non sia possibile a causa del device o da impostazioni dell'applicazione, questo manager
 * consente di effettuare le stesse operazioni in modo sincrono, bloccando e quindi introducendo dei lag, ma almeno l'operazione non viene impedita.
 * </p>
 * 
 * <p>
 * This class holds the shared context magic and is based on informations gathered from the following sources:
 * </p>
 * <ul>
 * <li><a href="http://www.khronos.org/message_boards/showthread.php/9029-Loading-textures-in-a-background-thread-on-Android">Loading-textures-in-a-background-thread-on-Android</link></li>
 * <li><a href="http://www.khronos.org/message_boards/showthread.php/5843-Texture-Sharing">5843-Texture-Sharing</a></li>
 * <li><a href="http://stackoverflow.com/questions/14062803/why-is-eglmakecurrent-failing-with-egl-bad-match">why-is-eglmakecurrent-failing-with-egl-bad-match</a></li>
 * </ul>
 * 
 * @author Francesco Benincasa
 * 
 */
public class AsyncOperationManager {

	/**
	 * indica se le operazioni sono fattibili o meno
	 */
	public boolean asyncMode;

	public interface AsyncTextureInfoLoader {
		TextureInfo load(Texture texture);
	}

	public class TextureLoaderThread extends Thread {
		public TextureLoaderThread() {
			super("GLThread-AsyncOperation");
		}

		public Handler handler;

		@SuppressLint("HandlerLeak")
		public void run() {
			Looper.prepare();

			int pbufferAttribs[] = { EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL_TEXTURE_TARGET, EGL_NO_TEXTURE, EGL_TEXTURE_FORMAT, EGL_NO_TEXTURE, EGL10.EGL_NONE };

			surfaceForTextureLoad = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);
			egl.eglMakeCurrent(display, surfaceForTextureLoad, surfaceForTextureLoad, textureContext);

			handler = new Handler() {
				public void handleMessage(Message msg) {
					MessageContent content = (MessageContent) msg.obj;
					long start2 = Clock.now();
					Logger.debug("context switch for async texture load stopped ");

					// EGLSurface defaultSurface =
					// egl.eglGetCurrentSurface(EGL10.EGL_DRAW);

					long start1 = Clock.now();
					Logger.debug("async texture load stopped ");
					content.texture.updateInfo(content.execute.load(content.texture));
					Logger.debug("async texture load ended in %s ms", (Clock.now() - start1));

					// egl.eglMakeCurrent(display, defaultSurface,
					// defaultSurface, screenContext);

					// distruggiamo subiot
					// egl.eglDestroySurface(display, surfaceForTextureLoad);

					Logger.debug("context switch for async texture load ended in %s ms", (Clock.now() - start2));

					if (content.listener != null)
						content.listener.onTextureReady(content.texture);
				}
			};

			Looper.loop();
		}
	}

	final static int EGL_TEXTURE_TARGET = 12417;
	final static int EGL_NO_TEXTURE = 12380;
	final static int EGL_TEXTURE_FORMAT = 12416;

	/**
	 * <p>
	 * </p>
	 */
	private static AsyncOperationManager instance = new AsyncOperationManager();

	public static AsyncOperationManager instance() {
		return instance;
	}

	private EGLContext textureContext;
	private EGL10 egl;
	private EGLDisplay display;
	private EGLConfig eglConfig;
	protected EGLSurface surfaceForTextureLoad;
	private TextureLoaderThread textureLoaderThread;

	public AsyncOperationManager() {
	}

	/**
	 * <p>
	 * Inizializza l'async loader. Se non è possibile impostare le operazioni asyncrone, tutte le operazioni successive verranno fatte in modo sincrono.
	 * </p>
	 * 
	 * @param egl
	 * @param renderContext
	 * @param display
	 * @param eglConfig
	 */
	public void init(EGL10 egl, EGLContext renderContext, EGLDisplay display, EGLConfig eglConfig) {
		// la versione usata è la 2!
		int[] attrib_list = { XenonEGL.EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };

		this.egl = egl;
		this.display = display;
		this.eglConfig = eglConfig;

		// screenContext = renderContext;
		textureContext = egl.eglCreateContext(display, eglConfig, renderContext, attrib_list);

		if (textureContext != EGL10.EGL_NO_CONTEXT) {
			Logger.info("Context for async operation asyncMode.");
			asyncMode = true;
			// creiamo il thread per le operazioni async su opengl
			textureLoaderThread = new TextureLoaderThread();
			textureLoaderThread.start();
		} else {
			asyncMode = false;
			Logger.fatal("Try to enable context for async operation, but failed.");
		}
	}
	
	public int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
	
	/**
	 * <p>
	 * Inizializza l'async loader. Se non è possibile impostare le operazioni asyncrone, tutte le operazioni successive verranno fatte in modo sincrono.
	 * </p>
	 * 
	 * @param egl
	 * @param renderContext
	 * @param display
	 * @param eglConfig
	 */
	//TODO da fixare
	public void init(android.opengl.EGLContext renderContext, android.opengl.EGLDisplay display, android.opengl.EGLConfig eglConfig) {
		// la versione usata è la 2!
		int[] attrib_list = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };

	//	this.display = display;
	//	this.eglConfig = eglConfig;

		// screenContext = renderContext;
	//	textureContext = EGL14.eglCreateContext(display, eglConfig, renderContext, attrib_list);

		if (textureContext != EGL10.EGL_NO_CONTEXT) {
			Logger.info("Context for async operation asyncMode.");
			asyncMode = true;
			// creiamo il thread per le operazioni async su opengl
			textureLoaderThread = new TextureLoaderThread();
			textureLoaderThread.start();
		} else {
			asyncMode = false;
			Logger.fatal("Try to enable context for async operation, but failed.");
		}
	}


	public boolean destroy(EGL10 egl) {
		return egl.eglDestroyContext(display, textureContext);
	}
	
	//TODO da fixare
	public boolean destroy() {
		//return EGL14.eglDestroyContext(display, textureContext);
		return false;
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @author Francesco Benincasa
	 * 
	 */
	public class MessageContent {
		public MessageContent(Texture textureValue, AsyncTextureInfoLoader executeValue, TextureAsyncLoaderListener listenerValue) {
			texture = textureValue;
			execute = executeValue;
			listener = listenerValue;
		}

		public Texture texture;

		public AsyncTextureInfoLoader execute;

		public TextureAsyncLoaderListener listener;
	}

	/**
	 * <p>
	 * Indica se il sistema è su.
	 * </p>
	 * 
	 * @return
	 */
	public boolean isEnabled() {
		return asyncMode;
	}

	/**
	 * <p>
	 * Se i.
	 * </p>
	 * 
	 * @param <T>
	 * 
	 * @param execute
	 * @param texture
	 * @param listener
	 */
	public TextureInfo load(final Texture texture, final AsyncTextureInfoLoader execute, final TextureAsyncLoaderListener listener) {
		if (asyncMode) {
			MessageContent content = new MessageContent(texture, execute, listener);
			Message msg = textureLoaderThread.handler.obtainMessage(25, content);
			textureLoaderThread.handler.sendMessage(msg);

			return null;
		} else {
			Logger.error("async operations on textures are disabled! This device support multiple opengl context?");
			Logger.warn("run texture update in single thread!");
			execute.load(texture);
			if (listener != null)
				listener.onTextureReady(texture);

			return texture.info;
		}
	}

	public void init() {
		asyncMode = false;
	}
}
