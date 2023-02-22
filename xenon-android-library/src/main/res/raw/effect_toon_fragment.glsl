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
// https://github.com/yulu/GLtext/blob/master/res/raw/toon_fragment_shader.glsl

uniform float	u_time;
uniform sampler2D u_texture0;    // The input texture 0.

//uniform vec2 resolution;
//uniform float led_size;
//uniform float brightness;

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;

void main(void)
{
    float ResS = ${RESOLUTION_X};
	float ResT = ${RESOLUTION_Y};
	float MagTol = 0.5;
	float Quantize = 10.;

	vec3 irgb = texture2D(u_texture0, v_textureCoordinate0).rgb;
	vec2 stp0 = vec2(${INV_RESOLUTION_X}, 0.);
	vec2 st0p = vec2(0., ${INV_RESOLUTION_Y});
	vec2 stpp = vec2(${INV_RESOLUTION_X}, ${INV_RESOLUTION_Y});
	vec2 stpm = vec2(${INV_RESOLUTION_X}, -${INV_RESOLUTION_Y});

	const vec3 W = vec3(0.2125, 0.7154, 0.0721);
	float i00 = 	dot(texture2D(u_texture0, v_textureCoordinate0).rgb, W);
	float im1m1 =	dot(texture2D(u_texture0, v_textureCoordinate0-stpp).rgb, W);
	float ip1p1 = 	dot(texture2D(u_texture0, v_textureCoordinate0+stpp).rgb, W);
	float im1p1 = 	dot(texture2D(u_texture0, v_textureCoordinate0-stpm).rgb, W);
	float ip1m1 = 	dot(texture2D(u_texture0, v_textureCoordinate0+stpm).rgb, W);
	float im10 = 	dot(texture2D(u_texture0, v_textureCoordinate0-stp0).rgb, W);
	float ip10 = 	dot(texture2D(u_texture0, v_textureCoordinate0+stp0).rgb, W);
	float i0m1 = 	dot(texture2D(u_texture0, v_textureCoordinate0-st0p).rgb, W);
	float i0p1 = 	dot(texture2D(u_texture0, v_textureCoordinate0+st0p).rgb, W);

	//H and V sobel filters
	float h = -1.*im1p1 - 2.*i0p1 - 1.*ip1p1 + 1.*im1m1 + 2.*i0m1 + 1.*ip1m1;
	float v = -1.*im1m1 - 2.*im10 - 1.*im1p1 + 1.*ip1m1 + 2.*ip10 + 1.*ip1p1;
	float mag = length(vec2(h, v));

	if(mag > MagTol){
		gl_FragColor = vec4(0., 0., 0., 1.0);
	}else{
		irgb.rgb *= Quantize;
		irgb.rgb += vec3(.5,.5,.5);
		ivec3 intrgb = ivec3(irgb.rgb);
		irgb.rgb = vec3(intrgb)/Quantize;
		gl_FragColor = vec4(irgb, 1.0);
	}
	
	// dobbiamo impostare l'alpha channel a 1. Altrimenti si va a mischiare con il fondo.
	// Il codice non serve, dato che l'alpha è già impostato a 1.0
	// gl_FragColor.a=1.0;
}
