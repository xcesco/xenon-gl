//
//  Sprite fragment shader
//
//  Versione 1.0.0
//
//  Data: 01/08/2013
//
// Fragment shader di default per gli sprite
precision highp float;

uniform sampler2D u_texture0;    // The input texture 0.

varying vec2 v_textureCoordinate0;   // Interpolated texture coordinate per fragment.
			 

void main()                                  
{                   
	gl_FragColor = texture2D(u_texture0, v_textureCoordinate0);
}                                         