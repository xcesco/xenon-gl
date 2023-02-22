//
//	Colored fragment shader
//
//	Versione 1.0.0
//
//	Data: 01/08/2013
//
// Fragment shader per disegnare uno shape semplicemente colorato
precision highp float;

uniform vec4 u_color; 

// input vertex color from vertex shader
varying vec3 v_position; 

void main()                                  
{                       
	gl_FragColor = u_color;
}                                         