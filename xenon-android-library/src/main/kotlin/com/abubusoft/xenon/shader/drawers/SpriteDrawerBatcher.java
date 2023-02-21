package com.abubusoft.xenon.shader.drawers;

import com.abubusoft.xenon.animations.TextureAnimationHandler;
import com.abubusoft.xenon.animations.TextureKeyFrame;
import com.abubusoft.xenon.animations.TextureTimeline;
import com.abubusoft.xenon.animations.TiledMapTimeline;
import com.abubusoft.xenon.math.Matrix4x4;
import com.abubusoft.xenon.mesh.MeshSprite;
import com.abubusoft.xenon.mesh.modifiers.TextureModifier;
import com.abubusoft.xenon.mesh.tiledmaps.Layer;
import com.abubusoft.xenon.mesh.tiledmaps.ObjClass;
import com.abubusoft.xenon.shader.Shader;
import com.abubusoft.xenon.shader.ShaderManager;
import com.abubusoft.xenon.texture.Texture;
import com.abubusoft.xenon.texture.TextureRegion;
import com.abubusoft.xenon.vbo.BufferAllocationType;

import android.opengl.GLES20;

/**
 * Consente di disegnare degli sprite in modo ottimizzato. Lo shader è sempre lo stesso. Se i vari parametri non vengono cambiati, il drawer evita di cambiarli.
 * 
 * @author Francesco Benincasa
 * 
 */
public class SpriteDrawerBatcher {

	/**
	 * Usa di default uno shader del texturedAnimatorManager
	 */
	public SpriteDrawerBatcher() {
		defaultShader = ShaderManager.instance().createShaderTexture();
	}

	/**
	 * Usa di default uno shader del texturedAnimatorManager
	 * 
	 * @param shaderValue
	 */
	public SpriteDrawerBatcher(Shader shaderValue) {
		defaultShader = shaderValue;
	}

	/**
	 * Imposta lo shader corrente
	 * 
	 * @param shaderValue
	 */
	public void setShader(Shader shaderValue) {
		defaultShader = shaderValue;
	}

	private enum DrawType {
			ANIMATED_SPRITE,
			STATIC_SPRITE,
			TILED_LAYER
	};

	DrawType lastDrawType;

	/**
	 * shader corrente
	 */
	Shader currentShader;

	/**
	 * shader default
	 */
	Shader defaultShader;

	/**
	 * texture atlas attualmente usata
	 */
	Layer lastLayer;

	/**
	 * ultima texture usata
	 */
	Texture lastTexture;

	/**
	 * texture usata adesso
	 */
	Texture currentTexture;

	/**
	 * ultimo frame usato
	 */
	TextureRegion lastTextureRegion;

	/**
	 * frame usato adesso
	 */
	TextureRegion currentTextureRegion;

	/**
	 * ultimo shape usato
	 */
	MeshSprite lastUsedMesh;

	/**
	 * Inizializza batch
	 */
	public void begin() {
		begin(defaultShader);
	}

	/**
	 * Inizializza batch
	 */
	public void begin(Shader shader) {
		defaultShader = shader;
		currentShader = defaultShader;
		currentShader.use();

		lastTexture = null;
		lastUsedMesh = null;
		lastTextureRegion = null;
	}

	/**
	 * 
	 * 
	 * @param clazz
	 * @param matrixModelViewProjection
	 *            la matrice normalmente posizione il centro di origine ad una posizione z in modo tale da rendere visibile tutta l'area desiderata.f
	 */
	public void draw(ObjClass clazz, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.TILED_LAYER);
		useShader(clazz.shapeLayer.tiledMap.shader);

		if (clazz.shapeLayer != lastLayer) {
			for (int i = 0; i < clazz.shapeLayer.textureList.size(); i++) {
				clazz.shapeLayer.tiledMap.shader.setTexture(i, clazz.shapeLayer.textureList.get(i));
			}
			lastLayer = clazz.shapeLayer;
		}

		clazz.shapeLayer.handler.drawLayerPart(clazz.shapeLayer.tiledMap.shader, 0, clazz, matrixModelViewProjection);
	}

	/**
	 * <p>
	 * Se lo shader proposto è diverso da quello corrente, viene impostato a corrente e viene invocato il metodo use.
	 * </p>
	 * 
	 * @param shaderNew
	 * 
	 */
	private void useShader(Shader shaderNew) {
		if (!shaderNew.equals(currentShader)) {
			currentShader = shaderNew;
			shaderNew.use();
		}
	}

	/**
	 * disegna uno sprite prendendo la texture e le coordinate da una timeline
	 * 
	 * @param mesh
	 * @param timeline
	 * @param matrixModelViewProjection
	 */
	public void draw(MeshSprite mesh, TiledMapTimeline timeline, Matrix4x4 matrixModelViewProjection) {
		draw(mesh, timeline.getHandler().value1(), matrixModelViewProjection);
	}

	public void draw(MeshSprite mesh, TextureAnimationHandler handler, Matrix4x4 matrixModelViewProjection) {
		draw(mesh, handler.currentFrame, matrixModelViewProjection);
	}

	public void draw(MeshSprite mesh, TextureTimeline timeline, Matrix4x4 matrixModelViewProjection) {
		draw(mesh, timeline.value(), matrixModelViewProjection);
	}

	/**
	 * Disegna in modalità batch uno sprite.
	 * 
	 * @param mesh
	 * @param timeline
	 * @param matrixModelViewProjection
	 */
	private void draw(MeshSprite mesh, TextureKeyFrame frame, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.ANIMATED_SPRITE);
		useShader(defaultShader);

		// shape
		if (mesh != lastUsedMesh) {
			currentShader.setVertexCoordinatesArray(mesh.vertices);
			lastUsedMesh = mesh;
		}

		// texture coordinate (indice 0)
		currentTextureRegion = frame.textureRegion;
		if (currentTextureRegion != lastTextureRegion) {
			TextureModifier.setTextureCoords(mesh, currentTextureRegion);
			// shape.setTextureCoords(currentValue.frame);
			currentShader.setTextureCoordinatesArray(0, mesh.textures[0]);
			lastTextureRegion = currentTextureRegion;
		}

		// texture binder
		currentTexture = frame.texture;
		if (currentTexture != lastTexture) {
			currentShader.setTexture(0, currentTexture);
			lastTexture = currentTexture;
		}

		// matrice di proiezione
		currentShader.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer());

		if (mesh.indexes.allocation == BufferAllocationType.CLIENT) {
			GLES20.glDrawElements(mesh.drawMode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, mesh.indexes.buffer);
		} else {
			currentShader.setIndexBuffer(mesh.indexes);
			GLES20.glDrawElements(mesh.drawMode.value, mesh.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0);
			currentShader.unsetIndexBuffer(mesh.indexes);
		}
	}

	/**
	 * <p>
	 * Disegna uno sprite statico.
	 * </p>
	 * 
	 * <p>
	 * Imposta:
	 * <dl>
	 * <dt>vertex</dt>
	 * <dd>vertice dei buffer</dd>
	 * <dt>texture</dt>
	 * <dd>texture 0</dd>
	 * <dt>texture coordinate 0</dt>
	 * <dd>coordinate texture 0</dd>
	 * <dt>matrice mvp</dt>
	 * <dd>matrice mvp</dd>
	 * </dl>
	 * </p>
	 * 
	 * 
	 * @param shape
	 * @param texture
	 * @param matrixModelViewProjection
	 */
	public void draw(MeshSprite shape, Texture texture, Matrix4x4 matrixModelViewProjection) {
		resetIfNot(DrawType.STATIC_SPRITE);
		useShader(defaultShader);

		// shape
		if (shape != lastUsedMesh) {
			currentShader.setVertexCoordinatesArray(shape.vertices);
			lastUsedMesh = shape;
		}

		// texture binder
		if (texture != lastTexture) {
			currentShader.setTexture(0, texture);
			lastTexture = texture;// at.index;
		}

		// texture coordinate (default 0)
		currentShader.setTextureCoordinatesArray(0, shape.textures[0]);

		// matrice di proiezione
		currentShader.setModelViewProjectionMatrix(matrixModelViewProjection.asFloatBuffer());

		if (shape.indexes.allocation == BufferAllocationType.CLIENT) {
			GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, shape.indexes.buffer);
		} else {
			currentShader.setIndexBuffer(shape.indexes);
			GLES20.glDrawElements(shape.drawMode.value, shape.indexesCount, GLES20.GL_UNSIGNED_SHORT, 0);
			currentShader.unsetIndexBuffer(shape.indexes);
		}
	}

	/**
	 * <p>
	 * Cancella i dati se il currentDrawType è diverso da quello corrente.
	 * </p>
	 * 
	 * @param currentDrawType
	 */
	private void resetIfNot(DrawType currentDrawType) {
		if (currentDrawType != lastDrawType) {
			currentTexture = null;
			currentTextureRegion = null;

			lastTexture = null;
			lastTextureRegion = null;

			lastUsedMesh = null;
			lastLayer = null;
		}
		lastDrawType = currentDrawType;
	}

	/**
	 * termina il current shader
	 */
	public void end() {
		// currentShader.close();
		currentShader = null;
	}
}
