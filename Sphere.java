/**
 * Class for storing vertices/indices data for a sphere object.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - createVerticesPosOnly(): generate array of vertices with only position (for the spotlight)
 */
public final class Sphere {  
    // ***************************************************
    /* THE DATA */
    // anticlockwise/counterclockwise ordering
    private static final int XLONG = 30;
    private static final int YLAT = 30;
    
    public static final float[] vertices = createVertices();
    public static final int[] indices = createIndices();

	/**
	 * Generate the vertices of the sphere (position, normals and texture).
	 * 
	 * @return An array of vertices.
	 */
    private static float[] createVertices() {
		double r = 0.5;
		int step = 8;
		float[] vertices = new float[XLONG*YLAT*step];
		for (int j = 0; j<YLAT; ++j) {
			double b = Math.toRadians(-90+180*(double)(j)/(YLAT-1));
			for (int i = 0; i<XLONG; ++i) {
				double a = Math.toRadians(360*(double)(i)/(XLONG-1));
				double z = Math.cos(b) * Math.cos(a);
				double x = Math.cos(b) * Math.sin(a);
				double y = Math.sin(b);
				int base = j*XLONG*step;
				vertices[base + i*step+0] = (float)(r*x);
				vertices[base + i*step+1] = (float)(r*y);
				vertices[base + i*step+2] = (float)(r*z); 
				vertices[base + i*step+3] = (float)x;
				vertices[base + i*step+4] = (float)y;
				vertices[base + i*step+5] = (float)z;
				vertices[base + i*step+6] = (float)(i)/(float)(XLONG-1);
				vertices[base + i*step+7] = (float)(j)/(float)(YLAT-1);
			}
		}
		return vertices;
    }
    
	/**
	 * Generate the indices of the sphere.
	 * 
	 * @return An array of indices.
	 */
    private static int[] createIndices() {
		int[] indices = new int[(XLONG-1)*(YLAT-1)*6];
		for (int j = 0; j<YLAT-1; ++j) {
			for (int i = 0; i<XLONG-1; ++i) {
				int base = j*(XLONG-1)*6;
				indices[base + i*6+0] = j*XLONG+i;
				indices[base + i*6+1] = j*XLONG+i+1;
				indices[base + i*6+2] = (j+1)*XLONG+i+1;
				indices[base + i*6+3] = j*XLONG+i;
				indices[base + i*6+4] = (j+1)*XLONG+i+1;
				indices[base + i*6+5] = (j+1)*XLONG+i;
			}
		}
		return indices;
    }
    
    public static final float[] verticesPosOnly = createVerticesPosOnly();

	/**
	 * Generate the vertices of a sphere (position only; used for the spotlight).
	 * 
	 * @return An array of vertices.
	 */
    private static float[] createVerticesPosOnly() {
		double r = 0.5;
		int step = 3;
		float[] vertices = new float[XLONG*YLAT*step];
		for (int j = 0; j<YLAT; ++j) {
			double b = Math.toRadians(-90+180*(double)(j)/(YLAT-1));
			for (int i = 0; i<XLONG; ++i) {
				double a = Math.toRadians(360*(double)(i)/(XLONG-1));
				double z = Math.cos(b) * Math.cos(a);
				double x = Math.cos(b) * Math.sin(a);
				double y = Math.sin(b);
				int base = j*XLONG*step;
				vertices[base + i*step+0] = (float)(r*x);
				vertices[base + i*step+1] = (float)(r*y);
				vertices[base + i*step+2] = (float)(r*z); 
			}
		}
		return vertices;
    }
}