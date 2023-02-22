uniform mat4 u_mvpMatrix;                   

attribute vec4 a_position;
attribute vec2 a_textureCoordinate0;

// This will be passed into the fragment shader.
varying vec2 v_textureCoordinate0; // coord
        
void main()                                 
{
    // Pass through the texture coordinate. (da 0 a 1)
    v_textureCoordinate0 = a_textureCoordinate0;
    
    // mette in gl_position l'attuale posizione del vertice trasformato
    gl_Position = u_mvpMatrix * a_position;
}   