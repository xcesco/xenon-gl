//
//	Tiled map vertex shader
//
//	Versione 1.0.0
//
//	Data: 01/08/2013
//
precision highp float;

// Matrice di proiezione model * view * projection
uniform	mat4	u_mvpMatrix;
// opacita' del layer (0..1)
uniform float	u_opacity;         

// Per-vertex texture coordinate information we will pass in.
attribute	vec4	a_position;

// Per-vertex coordinate delle texture
attribute	vec2	a_textureCoordinate0;

//@ifdef ( MORE_TEXTURES )
attribute	float	a_textureIndex;
//@endif

// Valori passati ai fragment shader
varying vec2	v_textureCoordinate0;
varying float	v_textureIndex;
        
void main()                                 
{
    // Pass through the texture coordinate.
    v_textureCoordinate0 = a_textureCoordinate0;
    
    //@ifdef ( MORE_TEXTURES )
    v_textureIndex=a_textureIndex;
    //@endif
    
    // mette in gl_position l'attuale posizione del vertice trasformato
    vec4 pos= a_position;
	gl_Position = u_mvpMatrix * pos;  
}                                              
