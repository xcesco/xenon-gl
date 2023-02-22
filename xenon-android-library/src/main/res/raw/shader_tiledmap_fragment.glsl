//
//	Tiled map fragment shader
//
//	Versione 1.1.0
//
//	Data: 09/03/2013
//
// Fragment shader per le tiledMap. Questo shader consente di gestire 4 texture contemporaneamente.
// La texture utilizzata viene impostata dal vertex shader.
//
// Accorpato con lo shader ad una texture, grazie agli ifdef
precision highp float;

// Texture sampler 0
 uniform sampler2D   u_texture0;
//@ifdef MORE_TEXTURES
 uniform sampler2D   u_texture1;
 uniform sampler2D   u_texture2;
 uniform sampler2D   u_texture3;
//@endif
 
// opacita' del layer (0..1)
 uniform float		u_opacity;      

// Valori interpolati dai vertex shader.
 varying vec2	v_textureCoordinate0;   
//@ifdef MORE_TEXTURES
 varying float	v_textureIndex;
//@endif

void main()                                  
{		
	vec4 color;
//@ifdef MORE_TEXTURES
	// in base al selettore prendiamo una texture piuttosto che un'altra
	if (v_textureIndex==0.0) {
		color = texture2D(u_texture0, v_textureCoordinate0);
	} else if (v_textureIndex==1.0) {
		color = texture2D(u_texture1, v_textureCoordinate0);
	} else if (v_textureIndex==2.0) {
		color = texture2D(u_texture2, v_textureCoordinate0);
	} else if (v_textureIndex==3.0) {
		color = texture2D(u_texture3, v_textureCoordinate0);
	}
//@else
	color= texture2D(u_texture0, v_textureCoordinate0);
//@endif
	color.a = color.a * u_opacity;
	gl_FragColor = color;

}                                         