import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
  
/**
 * Class for storing the properties of a light object and its associated buffers.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - 'shape' parameter in the constructor for determining the light's shape
 * - getDirection(): used for spotlight functionality
 */
public class Light {
	private Material material;
	private Vec3 position;
	private Vec3 direction;
	private Mat4 model;
	private Shader shader;
	private Camera camera;
		
	/**
	 * Constructor. Create a light object and set its default material values, position and 
	 * direction.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param shape The shape of the light to be made (sphere or cube).
	 */
	public Light(GL3 gl, String shape) {
		material = new Material();
		material.setAmbient(0.4f, 0.4f, 0.4f);
		material.setDiffuse(0.7f, 0.7f, 0.7f);
		material.setSpecular(0.7f, 0.7f, 0.7f);
		position = new Vec3(3f,2f,1f);
		direction = new Vec3(-0.6f, -1.0f, -0.6f);
		model = new Mat4(1);
		if (shape == "sphere") {
			this.vertices = Sphere.verticesPosOnly.clone();
			this.indices = Sphere.indices.clone();
		}
		
		fillBuffers(gl);
		shader = new Shader(
			gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
	}
	
	/**
	 * Set the position of the light in the world.
	 * 
	 * @param v The new position of the light as a vector.
	 */
	public void setPosition(Vec3 v) {
		position.x = v.x;
		position.y = v.y;
		position.z = v.z;
	}
	
	/** 
	 * Set the position of the light in the world (3 floats rather than 1 vector).
	 * 
	 * @param x The new x position of the light.
	 * @param y The new y position of the light.
	 * @param z The new z position of the light.
	 */
	public void setPosition(float x, float y, float z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}
	
	/**
	 * Get the current position of the light in the world.
	 * 
	 * @return The position of the light in vector format.
	 */
	public Vec3 getPosition() {
		return position;
	}

	/** 
	 * Set the direction of the light in the world.
	 * 
	 * @param v The new direction of the light in vector format.
	 */
	public void setDirection(Vec3 v) {
		direction = v;
	}

	/**
	 * Get the current direction of the light in the world.
	 * 
	 * @return The direction of the light in vector format.
	 */
	public Vec3 getDirection() {
		return direction;
	}
	
	/**
	 * Set the material of the light (ambient, diffuse and specular properties).
	 * 
	 * @param m The new material for the light.
	 */
	public void setMaterial(Material m) {
		material = m;
	}
	
	/**
	 * Get the current material of the light.
	 * 
	 * @return The material of the light.
	 */
	public Material getMaterial() {
		return material;
	}
	
	/**
	 * Set the camera object associated with the light.
	 * 
	 * @param camera The camera object.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Draw the light in the world and its effects on surrounding objects.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void render(GL3 gl) {
		Mat4 model = new Mat4(1);
		model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
		model = Mat4.multiply(Mat4Transform.translate(position), model);
		
		Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));
		
		shader.use(gl);
		shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
	
		gl.glBindVertexArray(vertexArrayId[0]);
		
		gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);
	}

	/**
	 * Discard the camera and free the resources it used.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		gl.glDeleteBuffers(1, vertexBufferId, 0);
		gl.glDeleteVertexArrays(1, vertexArrayId, 0);
		gl.glDeleteBuffers(1, elementBufferId, 0);
	}

	// ***************************************************
	/* THE DATA */
	// anticlockwise/counterclockwise ordering
	private float[] vertices = new float[] {  // x,y,z
		-0.5f, -0.5f, -0.5f,  // 0
		-0.5f, -0.5f,  0.5f,  // 1
		-0.5f,  0.5f, -0.5f,  // 2
		-0.5f,  0.5f,  0.5f,  // 3
		0.5f, -0.5f, -0.5f,  // 4
		0.5f, -0.5f,  0.5f,  // 5
		0.5f,  0.5f, -0.5f,  // 6
		0.5f,  0.5f,  0.5f   // 7
	};
		
	private int[] indices =  new int[] {
		0,1,3, // x -ve 
		3,2,0, // x -ve
		4,6,7, // x +ve
		7,5,4, // x +ve
		1,5,7, // z +ve
		7,3,1, // z +ve
		6,4,0, // z -ve
		0,2,6, // z -ve
		0,4,5, // y -ve
		5,1,0, // y -ve
		2,3,7, // y +ve
		7,6,2  // y +ve
	};
		
	private int vertexStride = 3;
	private int vertexXYZFloats = 3;
	
	// ***************************************************
	/* THE LIGHT BUFFERS */
	private int[] vertexBufferId = new int[1];
	private int[] vertexArrayId = new int[1];
	private int[] elementBufferId = new int[1];
		
	/**
	 * Initialises and fills the OpenGL buffers for the light's vertices and indices.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	private void fillBuffers(GL3 gl) {
		gl.glGenVertexArrays(1, vertexArrayId, 0);
		gl.glBindVertexArray(vertexArrayId[0]);
		gl.glGenBuffers(1, vertexBufferId, 0);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
		FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
		
		gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
		
		int stride = vertexStride;
		int numXYZFloats = vertexXYZFloats;
		int offset = 0;
		gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
		gl.glEnableVertexAttribArray(0);
		
		gl.glGenBuffers(1, elementBufferId, 0);
		IntBuffer ib = Buffers.newDirectIntBuffer(indices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
	} 
}