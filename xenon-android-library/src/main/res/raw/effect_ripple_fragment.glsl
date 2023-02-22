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

uniform float   u_time0;
uniform float   u_time1;
uniform float   u_time2;
uniform float   u_time3;
uniform float   u_time4;
uniform float   u_time5;
uniform float   u_time6;
uniform float   u_time7;

uniform float 	u_wave_amount;
uniform float   u_wave_distortion;
uniform float   u_wave_speed;

uniform vec3    u_touch0;
uniform vec3    u_touch1;
uniform vec3    u_touch2;
uniform vec3    u_touch3;
uniform vec3    u_touch4;
uniform vec3    u_touch5;
uniform vec3    u_touch6;
uniform vec3    u_touch7;

// se sono 0 sono disabilitati, 1 se abilitati
uniform float     u_touch_enabled0;
uniform float     u_touch_enabled1;
uniform float     u_touch_enabled2;
uniform float     u_touch_enabled3;
uniform float     u_touch_enabled4;
uniform float     u_touch_enabled5;
uniform float     u_touch_enabled6;
uniform float     u_touch_enabled7;

uniform sampler2D u_texture0;    // The input texture 0.

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;

// 
//vec2 computeRipple(in vec2 resolution, in vec2 mouse_pos, in vec2 textureCoordinate, in float time)
vec2 computeRipple(in vec2 mouse_pos, in vec2 textureCoordinate, in float time)
{
	vec2 offset=vec2(0.0, 0.0);
	 
	if (time>0.0) {
	  vec2 uv = textureCoordinate;
	  
	  // sappiamo che è quadrata la texture, quindi questo passo lo evitiamo
	  //uv.x *= (resolution.x/resolution.y);	  
	  //float centre_x = (mouse_pos.x / resolution.x) * (resolution.x/resolution.y);
	  
	  float centre_x = mouse_pos.x * ${INV_RESOLUTION_X} ;
	  float centre_y = mouse_pos.y * ${INV_RESOLUTION_Y} ;
  
	  vec2 dir = textureCoordinate - vec2(0.5);
	  float dist = distance(uv, vec2(centre_x,centre_y));
	  dist=max(0.03, dist);
	  
	  // la parte "  * cos(u_time / 4.0) " serve a smorzare l'onda fino alla chiusura	   
	 // offset = (dir * (sin(dist * u_wave_amount - time * u_wave_speed)) / u_wave_distortion) * cos(time / 4.0);
	  //offset = (dir*sin(@2PI*dist*u_wave_amount-time*u_wave_speed)* u_wave_distortion * cos(time / 4.0))/dist;
	    offset = (uv * sin( ${2PI} * dist * u_wave_amount - time * u_wave_speed) * u_wave_distortion * cos(time / 4.0))/dist;
	  //offset = (uv/dist)*cos(dist*12.0-time*4.0)*0.03 * cos(time / 4.0);
	}
	
	return offset;
}

// Ripple effect
// Il sistema di riferimento del mouse è 
//
//  ^
//  |
//  |
//  +--->
//
void main(void)
{
	//vec2 resolutionA=vec2( 512.0 , 512.0 );
	
	//vec2 mouse_posB=vec2( 128.0 , 128.0 );
	//vec2 mouse_posC=vec2( 512.0 , 512.0 );
	
	vec2 offset=vec2(0.0, 0.0);
	
	//if (u_touch0==1.0)
	{
         vec2 mouse_pos0 = vec2( u_touch0.x , u_touch0.y );
	     offset = offset + computeRipple(mouse_pos0, v_textureCoordinate0, u_time0);
	}
//	if (u_touch1==1.0)
//	{
         vec2 mouse_pos1 = vec2( u_touch1.x , u_touch1.y );
	     offset = offset + computeRipple(mouse_pos1, v_textureCoordinate0, u_time1);
//	}
//	if (u_touch2==1.0)
//	{
         vec2 mouse_pos2 = vec2( u_touch2.x , u_touch2.y );
	     offset = offset + computeRipple(mouse_pos2, v_textureCoordinate0, u_time2);
//	}
//	if (u_touch3==1.0)
//	{
        vec2 mouse_pos3 = vec2( u_touch3.x , u_touch3.y );
	     offset = offset + computeRipple(mouse_pos3, v_textureCoordinate0, u_time3);
//	}

vec2 mouse_pos4 = vec2( u_touch4.x , u_touch4.y );
offset = offset + computeRipple(mouse_pos4, v_textureCoordinate0, u_time4);

vec2 mouse_pos5 = vec2( u_touch5.x , u_touch5.y );
offset = offset + computeRipple(mouse_pos5, v_textureCoordinate0, u_time5);

vec2 mouse_pos6 = vec2( u_touch6.x , u_touch6.y );
offset = offset + computeRipple(mouse_pos6, v_textureCoordinate0, u_time6);

vec2 mouse_pos7 = vec2( u_touch7.x , u_touch7.y );
offset = offset + computeRipple(mouse_pos7, v_textureCoordinate0, u_time7);
	
	//offset=offset+computeRipple(resolutionA, mouse_posA, v_textureCoordinate0, u_time);
	//offset=offset+computeRipple(resolutionA, mouse_posB, v_textureCoordinate0, u_time);
	//offset=offset+computeRipple(resolutionA, mouse_posC, v_textureCoordinate0, u_time);
 
    gl_FragColor = texture2D(u_texture0, v_textureCoordinate0 + offset);
    //gl_FragColor=gl_FragColoret;
    
    gl_FragColor.a=1.0;
}
