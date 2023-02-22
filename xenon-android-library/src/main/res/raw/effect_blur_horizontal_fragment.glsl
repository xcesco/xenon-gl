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
uniform float u_led_size;
uniform float u_brightness;

uniform sampler2D u_texture0;    // The input texture 0.

const float blurSize = 2.0/${RESOLUTION_X};

varying vec2 v_textureCoordinate0;

void main()
{
     vec4 sum = vec4(0.0);
 
   // blur in y (vertical)
   // take nine samples, with the distance blurSize between them
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x - 4.0*blurSize, v_textureCoordinate0.y)) * 0.05;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x - 3.0*blurSize, v_textureCoordinate0.y)) * 0.09;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x - 2.0*blurSize, v_textureCoordinate0.y)) * 0.12;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x - blurSize, v_textureCoordinate0.y)) * 0.15;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x, v_textureCoordinate0.y)) * 0.16;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x + blurSize, v_textureCoordinate0.y)) * 0.15;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x + 2.0*blurSize, v_textureCoordinate0.y)) * 0.12;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x + 3.0*blurSize, v_textureCoordinate0.y)) * 0.09;
   sum += texture2D(u_texture0, vec2(v_textureCoordinate0.x + 4.0*blurSize, v_textureCoordinate0.y)) * 0.05;
 
   gl_FragColor = vec4(sum.rgb , 1.0);
}