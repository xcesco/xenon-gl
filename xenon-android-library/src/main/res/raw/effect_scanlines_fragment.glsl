precision mediump float;

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
// http://coding-experiments.blogspot.it/2010/06/edge-detection.html

uniform float	u_time;
uniform float 	u_wave_amount;
uniform float	u_wave_distortion;
uniform float	u_wave_speed;

uniform sampler2D u_texture0;    // The input texture 0.

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;


// Scanlines effect
void main(void)
{
	vec2 resolution=vec2( ${RESOLUTION_X} , ${RESOLUTION_Y} );
	
    vec4 colour = texture2D(u_texture0, v_textureCoordinate0);
    
    float half_y = v_textureCoordinate0.y * resolution.y * 0.5;
    float delta = floor(half_y) - half_y;
    if (delta * delta < 0.1) { colour.rgb = vec3(0.0); }
   
    gl_FragColor = colour;
    
    // dobbiamo impostare l'alpha channel a 1. Altrimenti si va a mischiare con il fondo.
    gl_FragColor.a=1.0;
}
