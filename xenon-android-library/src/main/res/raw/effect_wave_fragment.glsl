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


uniform float	u_time;
uniform float 	u_wave_amount;
uniform float	u_wave_distortion;
uniform float	u_wave_speed;

uniform sampler2D u_texture0;    // The input texture 0.

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;

// u_wave_amount 20
// u_wave_distortion 1/30
// u_wave_speed 2

// Wave effect
void main(void)
{	    
	vec2 uv = v_textureCoordinate0;
    uv.x = uv.x+sin(uv.y*u_wave_amount+u_time*u_wave_speed)*u_wave_distortion;
    uv.y = uv.y+cos(uv.x*u_wave_amount+u_time*u_wave_speed)*u_wave_distortion;
 
    gl_FragColor = texture2D(u_texture0, uv);
    
    // dobbiamo impostare l'alpha channel a 1. Altrimenti si va a mischiare con il fondo.
	// Il codice non serve, dato che l'alpha è già impostato a 1.0
	gl_FragColor.a=1.0;
}
