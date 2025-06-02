/**
 * Class for storing vertices/indices data for a triangle object.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - Adapted from TwoTriangles to only create 1 triangle (for use in Room class for window pieces)
 */
public final class Triangle {  
	// ***************************************************
	/* THE DATA */
	// anticlockwise/counterclockwise ordering
	public static final float[] vertices = {      // position, colour, tex coords
		-0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f,  // top left
		-0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
		 0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  1.0f, 0.0f,  // bottom right
	};

	public static final int[] indices = {
		0, 1, 2
	};
}