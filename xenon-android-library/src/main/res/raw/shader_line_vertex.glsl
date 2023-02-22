//
//	Line vertex shader
//
//	Versione 1.0.0
//
//	Data: 01/08/2013
//
// Vertex shader per disegnare uno shape semplicemente colorato
precision highp float;

// Matrice di proiezione model * view * projection
uniform mat4 u_mvpMatrix;
uniform vec4 u_color;                   

// Per-vertex texture coordinate information we will pass in.
attribute vec4 a_position;

void main()                                 
{
    // mette in gl_position l'attuale posizione del vertice trasformato
	gl_Position = u_mvpMatrix * a_position;  
}                                              
