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
uniform float   u_pixel_amount;

uniform sampler2D u_texture0;    // The input texture 0.

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;


// Scanlines effect
void main(void)
{		
	//vec2 resolution=vec2( 512.0 , 512.0 );	
    //vec2 res = vec2(1.0, resolution.x/resolution.y);    
    //vec2 size = vec2(res.x/u_pixel_amount, res.y/u_pixel_amount);
    vec2 size = vec2( 1.0 / u_pixel_amount, 1.0 / u_pixel_amount);
    
    vec2 uv = v_textureCoordinate0 - mod(v_textureCoordinate0,size);
    
    gl_FragColor = texture2D( u_texture0, uv );
    
    gl_FragColor.a=1.0;
}
