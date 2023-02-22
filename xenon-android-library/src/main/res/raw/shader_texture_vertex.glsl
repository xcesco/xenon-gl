//
//  Sprite vertex shader
//
//  Versione 1.0.0
//
//  Data: 01/08/2013
//
// Vertex shader di default per gli sprite
precision highp float;

uniform mat4 u_mvpMatrix;                   

// Per-vertex texture coordinate information we will pass in.
attribute vec4 a_position;
attribute vec2 a_textureCoordinate0;

// This will be passed into the fragment shader.
varying vec2 v_textureCoordinate0;
        
void main()                                 
{
    // Pass through the texture coordinate.
    v_textureCoordinate0 = a_textureCoordinate0;
    
    // mette in gl_position l'attuale posizione del vertice trasformato
    vec4 pos= a_position;
	gl_Position = u_mvpMatrix * pos;  
}                                              
