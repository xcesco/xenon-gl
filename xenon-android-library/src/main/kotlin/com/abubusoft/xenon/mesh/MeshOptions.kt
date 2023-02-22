package com.abubusoft.xenon.mesh;

import com.abubusoft.xenon.texture.TextureAspectRatioType;
import com.abubusoft.xenon.vbo.AttributeBuffer.AttributeDimensionType;
import com.abubusoft.xenon.vbo.BufferAllocationType;
import com.abubusoft.xenon.vbo.BufferAllocationOptions;

/**
 * Opzioni per la creazione di uno shape. Di default
 * <dl>
 * <dt>texture</dt>
 * <dd>enabled. 1 texture e con aspect ratio 1:1</dd>
 * <dt>indici</dt>
 * <dd>disabled</dd>
 * <dt>normali</dt>
 * <dd>disabled</dd>
 * <dt>colori</dt>
 * <dd>disabled</dd>
 * <dt>attributes</dt>
 * <dd>disabled</dd>
 * </dl>
 * 
 * @author Francesco Benincasa
 * 
 */
public class MeshOptions {

	/**
	 * <p>
	 * Costruisce una configurazione con le impostazioni di base.
	 * </p>
	 * 
	 * <ul>
	 * 
	 * <li>attributesEnabled = <b>disabled</b></li>
	 * <li>bufferAllocationOptions = <b>CLIENT</b></li>
	 * <li>meshClazz = <b>Mesh</b></li>
	 * <li>colorsEnabled = <b>false</b></li>
	 * <li>indexesEnabled = <b>false</b></li>
	 * <li>normalsEnabled = <b>false</b></li>
	 * <li>textureAspectRatio = <b>RATIO1_1</b></li>
	 * <li>textureInverseY = <b>false</b></li>
	 * <li>textureEnabled = <b>true</b></li>
	 * <li>texturesCount = <b>1</b></li>
	 * <li>texturesCoordRect = <b>[0 .. 1, 0 .. 1]</b></li>
	 * <li>updateAfterCreation = <b>true</b></li>
	 * 
	 * </ul>
	 * 
	 * @return this
	 * 
	 */
	public static MeshOptions build() {
		return (new MeshOptions())
		// tipo di oggetto da creare
		.meshClazz(Mesh.class)
		// texutre
		.textureEnabled(true).texturesCount(1).textureAspectRatio(TextureAspectRatioType.RATIO1_1).textureInverseY(false).textureCoordRect(TextureCoordRect.build())
		// indici
		.indicesEnabled(false)		
		// normali
		.normalsEnabled(false)
		// aggiorna dopo la creazione
		.updateAfterCreation(true)
		// colori
		.colorEnabled(false)
		.attributesEnabled(false)
		.bufferAllocationOptions(BufferAllocationOptions.build());
	}

	public MeshOptions meshClazz(Class<Mesh> clazz) {
		meshClazz=clazz;
		return this;
	}

	/**
	 * coordinate da usare per le texture
	 */
	public TextureCoordRect textureCoordRect;

	public MeshOptions textureCoordRect(TextureCoordRect value) {
		textureCoordRect = value;
		return this;
	}

	public MeshOptions textureInverseY(boolean value) {
		textureInverseY = value;
		return this;
	}
	
	public Class<? extends Mesh> meshClazz;

	/**
	 * <p>
	 * Aggiorna i vari vertexBuffer subito dopo la creazione.
	 * </p>
	 */
	public boolean updateAfterCreation;

	/**
	 * colore
	 */
	public int color;

	/**
	 * indica se i colori sono abilitati
	 */
	public boolean colorsEnabled;

	/**
	 * indica gli indici sono stati abilitati
	 */
	public boolean indicesEnabled;

	/**
	 * indica se le normali sono state abilitate
	 */
	public boolean normalsEnabled;

	/**
	 * indica se gli attributi sono abilitati
	 */
	public boolean attributesEnabled;

	/**
	 * numero di attributi
	 */
	public int attributesCount;

	/**
	 * indica se le texture sono abilitate
	 */
	public boolean textureEnabled;

	/**
	 * numero di texture
	 */
	public int texturesCount;

	/**
	 * Se true inverte l'asse Y. Di solito l'asse punta verso l'alto. Con questo parametro invertito a true, L'asse Y va verso il basso (l'origine quindi va verso il basso).
	 */
	public boolean textureInverseY;

	/**
	 * opzioni da adottare per il vertex buffer
	 */
	public BufferAllocationOptions bufferOptions;

	/**
	 * dimensioni degli attributi
	 */
	public AttributeDimensionType attributesDimension;

	/**
	 * Fluent interface.
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions colorEnabled(boolean value) {
		colorsEnabled = value;

		return this;
	}

	/**
	 * Fluent interface. Se valorizzato con !=null allora impostiamo anche colorsEnabled a true.
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions colorEnabled(int value) {
		color = value;
		colorsEnabled = true;
		return this;
	}

	/**
	 * Fluent interface
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions indicesEnabled(boolean value) {
		indicesEnabled = value;
		return this;
	}

	/**
	 * Fluent interface
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions normalsEnabled(boolean value) {
		normalsEnabled = value;
		return this;
	}

	/**
	 * Fluent interface per attributesEnabled
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions attributesEnabled(boolean value) {
		attributesEnabled = value;
		return this;
	}

	/**
	 * Fluent interface per attributesDimension. Definisce la dimensione dell'attribute
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions attributesDimension(AttributeDimensionType value) {
		attributesDimension = value;
		return this;
	}

	/**
	 * Fluent interface per attributesCount. Se = 0 disabilita gli attributes
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions attributesCount(int value) {
		// per sicurezza
		attributesEnabled = value > 0 ? true : false;
		attributesCount = value;
		return this;
	}

	/**
	 * <p>
	 * Fluent interface per textureRatio: ovvero il rapporto tra le dimensioni in larghezza ed in altezza della texture.
	 * </p>
	 * 
	 * <p>
	 * Indica quanta parte della texture deve essere considerata come valida. Le coordinate in una texture vanno da 0 a 1, ma nel caso in cui la texture è stata ridotta, le
	 * coordinate, quelle verticali, dovranno avere un limite inferiore di 1.
	 * </p>
	 * 
	 * <p>
	 * Equivale ad utilizzare il metodo:
	 * </p>
	 * 
	 * <pre>
	 * TextureCoordRect.buildFromOrigin(1.f, (float) (1f / value.aspectXY));
	 * </pre>
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions textureAspectRatio(TextureAspectRatioType value) {
		textureCoordRect = TextureCoordRect.buildFromTopLeft(0f, 0f, 1.f, (float) (1f / value.aspectXY));
		return this;
	}
	
	/**
	 * <p>
	 * Fluent interface per textureRatio: ovvero il rapporto tra le dimensioni in larghezza ed in altezza della texture.
	 * </p>
	 * 
	 * <p>
	 * Indica quanta parte della texture deve essere considerata come valida. Le coordinate in una texture vanno da 0 a 1, ma nel caso in cui la texture è stata ridotta, le
	 * coordinate, quelle verticali, dovranno avere un limite inferiore di 1.
	 * </p>
	 * 
	 * <p>
	 * Equivale ad utilizzare il metodo:
	 * </p>
	 * 
	 * <pre>
	 * TextureCoordRect.buildFromOrigin(1.f, (float) (1f / value.aspectXY));
	 * </pre>
	 * 
	 * @param aspectXY
	 * 		rapporto da width e height
	 * @return this
	 */
	public MeshOptions textureAspectRatio(float aspectXY) {
		textureCoordRect = TextureCoordRect.buildFromTopLeft(0f, 0f, 1.f, (float) (1f / aspectXY));
		return this;
	}

	/**
	 * Fluent interface per textureEnabled
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions textureEnabled(boolean value) {
		textureEnabled = value;
		return this;
	}

	/**
	 * Fluent interface per textureRatio. Se = 0 disabilita le texture
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions texturesCount(int value) {
		// per sicurezza
		textureEnabled = value > 0 ? true : false;
		texturesCount = value;
		return this;
	}

	/**
	 * Fluent interface per l'allocation type
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions bufferAllocationOptions(BufferAllocationOptions value) {
		// per sicurezza
		bufferOptions = value;
		return this;
	}

	/**
	 * Fluent interface per l'allocation type
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions bufferAllocation(BufferAllocationType value) {
		// per sicurezza
		bufferOptions = BufferAllocationOptions.build().allocation(value);
		return this;
	}

	/**
	 * Fluent interface per updateAfterCreation. Se true viene fatto l'update dei vari vertexbuffer durante la creazione
	 * 
	 * @param value
	 * @return this
	 */
	public MeshOptions updateAfterCreation(boolean value) {
		// per sicurezza
		updateAfterCreation = value;
		return this;
	}

}
