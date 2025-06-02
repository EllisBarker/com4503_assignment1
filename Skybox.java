import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Class for setting up and rendering the skybox of the scene.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - A new class for storing vertex/buffer/etc. information for creating and rendering the skybox
 */
public class Skybox {
    private Camera camera;
    private Shader shader;
    private double startTime;
    private Texture skyboxTexture, movingTexture;
    private int numXYZFloats = 3;
    private int numTexFloats = 2;
    private int[] vertexBufferId = new int[1];
	private int[] vertexArrayId = new int[1];

    float skyboxVertices[] = {
        // x,y,z,s,t

        // Front face
        -1.0f, -1.0f, -1.0f,   0.0f, 1.0f,
        -1.0f,  1.0f, -1.0f,   0.0f, 0.0f,
         1.0f,  1.0f, -1.0f,   1.0f, 0.0f,
         1.0f,  1.0f, -1.0f,   1.0f, 0.0f,
         1.0f, -1.0f, -1.0f,   1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,   0.0f, 1.0f,

        // Back face
        -1.0f,  1.0f,  1.0f,   0.0f, 0.0f,
        -1.0f, -1.0f,  1.0f,   0.0f, 1.0f, 
         1.0f, -1.0f,  1.0f,   1.0f, 1.0f,
         1.0f, -1.0f,  1.0f,   1.0f, 1.0f,
         1.0f,  1.0f,  1.0f,   1.0f, 0.0f,
        -1.0f,  1.0f,  1.0f,   0.0f, 0.0f,

        // Right face
         1.0f, -1.0f, -1.0f,   0.0f, 1.0f,
         1.0f, -1.0f,  1.0f,   1.0f, 1.0f,
         1.0f,  1.0f,  1.0f,   1.0f, 0.0f,
         1.0f,  1.0f,  1.0f,   1.0f, 0.0f,
         1.0f,  1.0f, -1.0f,   0.0f, 0.0f,
         1.0f, -1.0f, -1.0f,   0.0f, 1.0f,

        // Left face
        -1.0f, -1.0f,  1.0f,   1.0f, 1.0f,
        -1.0f, -1.0f, -1.0f,   0.0f, 1.0f,
        -1.0f,  1.0f, -1.0f,   0.0f, 0.0f,
        -1.0f,  1.0f, -1.0f,   0.0f, 0.0f,
        -1.0f,  1.0f,  1.0f,   1.0f, 0.0f,
        -1.0f, -1.0f,  1.0f,   1.0f, 1.0f,

        // Top face
        -1.0f,  1.0f, -1.0f,   0.0f, 0.0f,
         1.0f,  1.0f, -1.0f,   0.0f, 1.0f,
         1.0f,  1.0f,  1.0f,   1.0f, 1.0f,
         1.0f,  1.0f,  1.0f,   1.0f, 1.0f,
        -1.0f,  1.0f,  1.0f,   1.0f, 0.0f,
        -1.0f,  1.0f, -1.0f,   0.0f, 0.0f,

        // Bottom face
        -1.0f, -1.0f, -1.0f,   0.0f, 1.0f,
        -1.0f, -1.0f,  1.0f,   0.0f, 0.0f,
         1.0f, -1.0f, -1.0f,   1.0f, 1.0f,
         1.0f, -1.0f, -1.0f,   1.0f, 1.0f,
        -1.0f, -1.0f,  1.0f,   0.0f, 0.0f,
         1.0f, -1.0f,  1.0f,   1.0f, 0.0f
    };

    /**
     * Constructor. Initialise the buffers, shaders and textures for the skybox.
     * 
     * @param gl The OpenGL context used for rendering.
     * @param c The camera object in the world.
     * @param s The skybox texture (a cube map).
     * @param t The texture that will be animated across the skybox.
     */
    public Skybox(GL3 gl, Camera c, Texture s, Texture t) {
        this.camera = c;
        shader = new Shader(gl, "assets/shaders/vs_skybox.txt", "assets/shaders/fs_skybox.txt");
        this.skyboxTexture = s;
        this.movingTexture = t;
        startTime = getSeconds();

        // Vertex buffers
        gl.glGenVertexArrays(1, vertexArrayId, 0);
        gl.glGenBuffers(1, vertexBufferId, 0);
        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
        FloatBuffer fb = Buffers.newDirectFloatBuffer(skyboxVertices);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * skyboxVertices.length, fb, GL.GL_STATIC_DRAW);
        gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, 5*Float.BYTES, 0);
		gl.glEnableVertexAttribArray(0);

        // Texture buffers
        gl.glVertexAttribPointer(1, numTexFloats, GL.GL_FLOAT, false, 5*Float.BYTES, numXYZFloats*Float.BYTES);
		gl.glEnableVertexAttribArray(1);
    }

    /**
     * Draw the skybox and update its textures over time (and update the buffers).
     * 
     * @param gl The OpenGL context used for rendering.
     */
    public void render(GL3 gl) {
        // Shader-related operations
        double elapsedTime = getSeconds() - startTime;
        Mat4 viewMatrix = camera.getViewMatrix();
        viewMatrix.set(0,3,0);
        viewMatrix.set(1,3,0);
        viewMatrix.set(2,3,0);
        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), viewMatrix);
        shader.use(gl);
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        // Set the offset for the moving non-cube map texture
        float offsetX = (float)elapsedTime*0.02f;
        float offsetY = -(float)elapsedTime*0.01f;
        shader.setFloat(gl, "offset", offsetX, offsetY);

        // Set the uniforms for the two textures
        shader.setInt(gl, "skybox", 0);
        shader.setInt(gl, "moving_texture", 1);

        // Buffer alterations (depth function changed so that skybox is always behind everything)
        gl.glDepthFunc(GL3.GL_LEQUAL);
        gl.glBindVertexArray(vertexArrayId[0]);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        skyboxTexture.bind(gl);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        movingTexture.bind(gl);
		gl.glDrawArrays(GL.GL_TRIANGLES, 0, skyboxVertices.length / 5);
		gl.glBindVertexArray(0);
        gl.glDepthFunc(GL3.GL_LESS);
    }

	/**
	 * Get the current time in seconds.
	 * 
	 * @return The current time in seconds in a double format.
	 */
    private double getSeconds() {
    	return System.currentTimeMillis()/1000.0;
  	}
}