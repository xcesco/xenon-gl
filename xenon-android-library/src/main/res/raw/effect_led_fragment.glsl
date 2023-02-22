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
// http://coding-experiments.blogspot.it/2010/06/edge-detection.html

uniform float u_time;
uniform float u_led_size;
uniform float u_brightness;

uniform sampler2D u_texture0;    // The input texture 0.

//uniform vec2 resolution;
//uniform float led_size;
//uniform float brightness;

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;


vec4 pixelize(in vec2 uv, in float sca) {
    float dx = 1.0/sca;
    float dy = 1.0/sca;
    vec2 coord = vec2(dx*ceil(uv.x/dx),dy*ceil(uv.y/dy));
    return texture2D(u_texture0, coord);
}


// Led effect
void main(void)
{
    vec2 uv = v_textureCoordinate0;
       
    vec2 coor = uv * u_led_size;
    //coor.x *= (resolution.x/resolution.y);
    
    vec4 resColor = pixelize(uv, u_led_size) * u_brightness;

    float mvx = abs(sin(coor.x * ${PI} )) * 1.5;
    float mvy = abs(sin(coor.y* ${PI}  )) * 1.5;
    
    if (mvx*mvy < 1.0)
    {
          //resColor=vec4(0.0, 0.0, 0.0, texture2D(u_texture0, v_textureCoordinate0).a);
          resColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        resColor = resColor*(mvx*mvy);
    }

	resColor.a = 1.0;

 	gl_FragColor = resColor;
}
