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

uniform float	u_time;
uniform sampler2D u_texture0;    // The input texture 0.

// Interpolated texture coordinate per fragment.
varying vec2 v_textureCoordinate0;

float threshold(float thr1, float thr2 , float val) {
 if (val < thr1) {return 0.0;}
 if (val > thr2) {return 1.0;}
 return val;
}

// averaged pixel intensity from 3 color channels
float avg_intensity(vec4 pix) {
 return (pix.r + pix.g + pix.b)/3.;
}

vec4 get_pixel(vec2 coords, float dx, float dy) {
 return texture2D(u_texture0,coords + vec2(dx, dy));
}

// returns pixel color
float IsEdge(vec2 coords){
  float dxtex = ${INV_RESOLUTION_X} /*image width*/;
  float dytex = ${INV_RESOLUTION_Y} /*image height*/;
  float pix[9];
  int k = -1;
  float delta;

  // read neighboring pixel intensities
  for (int i=-1; i<2; i++) {
   for(int j=-1; j<2; j++) {
    k++;
    pix[k] = avg_intensity(get_pixel(coords,float(i)*dxtex,
                                          float(j)*dytex));
   }
  }

  // average color differences around neighboring pixels
  delta = (abs(pix[1]-pix[7])+
          abs(pix[5]-pix[3]) +
          abs(pix[0]-pix[8])+
          abs(pix[2]-pix[6])
           )/4.;

  return threshold(0.25,0.4,clamp(1.8*delta,0.0,1.0));
}

void main()
{
  vec4 color = vec4(0.0,0.0,0.0,1.0);
  color.g = IsEdge(v_textureCoordinate0.xy);
  gl_FragColor = color;
}
