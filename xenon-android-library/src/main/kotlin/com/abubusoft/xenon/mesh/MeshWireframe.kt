/**
 * 
 */
package com.abubusoft.xenon.mesh;

import java.io.Serializable;

import com.abubusoft.kripton.annotation.BindType;

/**
 * Rappresenta un oggetto wireframe
 * 
 * @author Francesco Benincasa
 *
 */
@BindType
public class MeshWireframe extends Mesh implements Serializable {
	
	MeshWireframe()
	{
		
	}
	
	MeshWireframe(Mesh input)
	{
		type=input.type;
		
		// impostiamo boundingbox
		boundingBox.set(input.boundingBox.width, input.boundingBox.height, input.boundingBox.depth);
		
		this.boundingSphereRadius=input.boundingSphereRadius;
		
		// impostiamo vertici e texture
		this.vertexCount=input.vertexCount;
		this.vertices=input.vertices;
		
		this.texturesCount=input.texturesCount;
		this.textures=input.textures;
	}

	private static final long serialVersionUID = -8580829785311186419L;

}
