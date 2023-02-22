precision highp float;

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
