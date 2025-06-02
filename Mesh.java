import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

/**
 * Class for representing the shape defined by vertices/indices and managing the buffers associated
 * with them.
 */
public class Mesh {
	private float[] vertices;
	private int[] indices;
	private int vertexStride = 8;
	private int vertexXYZFloats = 3;
	private int vertexNormalFloats = 3;
	private int vertexTexFloats = 2;
	private int[] vertexBufferId = new int[1];
	private int[] vertexArrayId = new int[1];
	private int[] elementBufferId = new int[1];
	
	/**
	 * Constructor. Set the vertices and indices and fill the associated buffers.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param vertices The vertices of the shape.
	 * @param indices The indices of the shape.
	 */
	public Mesh(GL3 gl, float[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
		fillBuffers(gl);
	}
	
	/**
	 * Draw the mesh by binding its vertex array and drawing.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void render(GL3 gl) {
		gl.glBindVertexArray(vertexArrayId[0]);
		gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);
	}

	/**
	 * Initialise and fill the OpenGL buffers with position, normal and texture data.
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
	
		// x, y, z for each vertex
		int numNormalFloats = vertexNormalFloats;
		// Normal values are the three floats after the x,y,z values, so offset needs to be changed
		offset = numXYZFloats*Float.BYTES;
		// Vertex shader uses location 1 for normal information
		gl.glVertexAttribPointer(1, numNormalFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
		// Enable vertex attribute array at location 1
		gl.glEnableVertexAttribArray(1);

		// Only 2 values stored for each texture coordinate
		int numTexFloats = vertexTexFloats;
		offset = (numXYZFloats+numNormalFloats)*Float.BYTES;
		gl.glVertexAttribPointer(2, numTexFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
		gl.glEnableVertexAttribArray(2);
		
		gl.glGenBuffers(1, elementBufferId, 0);
		IntBuffer ib = Buffers.newDirectIntBuffer(indices);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
	}
	
	/**
	 * Discard the mesh and free up the buffer-related resources it used.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		gl.glDeleteBuffers(1, vertexBufferId, 0);
		gl.glDeleteVertexArrays(1, vertexArrayId, 0);
		gl.glDeleteBuffers(1, elementBufferId, 0);
	}
}