/**
 * 
 */
package com.abubusoft.xenon.shader;

import com.abubusoft.xenon.R;

/**
 * This only works for API Level 15 and higher.
 * Thanks to Lubomir Panak (@drakh)
 * <p>
 * How to use:
 * <pre>
 * protected void initScene() {
 * 		super.initScene();
 * 		mLight = new DirectionalLight(0, 0, 1);
 * 		mCamera.setPosition(0, 0, -17);
 * 		
 * 		VideoMaterial material = new VideoMaterial();
 * 		TextureInfo tInfo = mTextureManager.addVideoTexture();
 * 		
 * 		mTexture = new SurfaceTexture(tInfo.getTextureId());
 * 		
 * 		mMediaPlayer = MediaPlayer.create(context(), R.raw.nemo);
 * 		mMediaPlayer.setSurface(new Surface(mTexture));
 * 		mMediaPlayer.start();
 * 		
 * 		BaseObject3D cube = new Plane(2, 2, 1, 1);
 * 		cube.setMaterial(material);
 * 		cube.addTexture(tInfo);
 * 		cube.addLight(mLight);
 * 		addChild(cube);
 * 	}
 * 
 * 	public void onDrawFrame(GL10 glUnused) {
 * 		mTexture.updateTexImage();
 * 		super.onDrawFrame(glUnused);
 * 	}
 * </pre>
 *  
 * @author dennis.ippel
 * @author Lubomir Panak (@drakh)
 * @author Francesco Benincasa
 * 
 */
public class VideoShader extends Shader {
	public VideoShader() {
		builder=ShaderBuilder.build(R.raw.shader_video_vertex, R.raw.shader_video_fragment, null);
	}
}
