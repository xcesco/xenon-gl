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


// Grayscale effect
void main(void)
{
	float gray = dot(texture2D(u_texture0,v_textureCoordinate0).rgb, vec3(0.21, 0.71, 0.07));

    //gl_FragColor = vec4(vec3(gray), texture2D(u_texture0,v_textureCoordinate0).a);
    gl_FragColor = vec4(vec3(gray), 1.0);
}
