//
//	Colored fragment shader
//
//	Versione 1.0.0
//
//	Data: 01/08/2013
//
// Fragment shader per disegnare uno shape semplicemente colorato
precision highp float;


// input vertex color from vertex shader
varying vec4 v_color; 
			 

void main()                                  
{                   
	gl_FragColor = v_color;
}                                         