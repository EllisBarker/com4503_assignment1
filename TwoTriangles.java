/**
 * Class for storing vertices/indices data for a structure made from two triangles.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - vertices_mipmap stores different texture coordinates for texture looping on the right wall
 */
public final class TwoTriangles {  
	// ***************************************************
	/* THE DATA */
	// anticlockwise/counterclockwise ordering
	public static final float[] vertices = {      // position, colour, tex coords
		-0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
		-0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
		 0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
		 0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 1.0f   // top right
	};
	
	public static final int[] indices = {
		0, 1, 2,
		0, 2, 3
	};

	// Texture coordinates changed for right wall of room
	public static final float[] vertices_mipmap = {      // position, colour, tex coords
		-0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 4.0f,  // top left
		-0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
		 0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  4.0f, 0.0f,  // bottom right
		 0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  4.0f, 4.0f   // top right
	};
}