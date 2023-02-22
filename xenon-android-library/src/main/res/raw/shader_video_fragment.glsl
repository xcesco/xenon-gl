#extension GL_OES_EGL_image_external : require
precision mediump float;

uniform samplerExternalOES u_texture0;

varying vec2 v_textureCoordinate0;

void main() {
	gl_FragColor = texture2D(u_texture0, v_textureCoordinate0);
}