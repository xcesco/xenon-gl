precision highp float;

// Effect Library 1.0 - 14/07/2014
// -------------------------------
// Costanti disponibili:
//
// ${RESOLUTION_X}: larghezza della texture
// ${RESOLUTION_Y}: altezza della texture
// ${INV_RESOLUTION_X}: 1 / larghezza della texture
// ${INV_RESOLUTION_Y}: 1 / altezza della texture
// ${PI}: pi greco
// ${2PI}: 2*pi
//

uniform float u_time;
uniform mat4 u_mvpMatrix;                   

attribute vec4 a_position;
attribute vec2 a_textureCoordinate0;

// This will be passed into the fragment shader.
varying vec2 v_textureCoordinate0;
        
void main()                                 
{
	// Pass through the texture coordinate.
    v_textureCoordinate0 = a_textureCoordinate0;
    
	gl_Position = u_mvpMatrix * a_position;
}                                             
