import gmaths.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.glsl.*;  

/**
 * Class for loading/linking/compiling shader files and setting their associated uniform variables.
 */
public class Shader {
    private static final boolean DISPLAY_SHADERS = false;
    
    private int ID;
    private String vertexShaderSource;
    private String fragmentShaderSource;
    
    /** 
	 * Constructor. Determine the path of the vertex and fragment shaders and try to compile and
	 * link them.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param vertexPath The file path of the vertex shader.
	 * @param fragmentPath The file path of the fragment shader.
	*/
    public Shader(GL3 gl, String vertexPath, String fragmentPath) {
		try {
			vertexShaderSource = new String(Files.readAllBytes(Paths.get(vertexPath)), Charset.defaultCharset());
			fragmentShaderSource = new String(Files.readAllBytes(Paths.get(fragmentPath)), Charset.defaultCharset());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		if (DISPLAY_SHADERS) display();
			ID = compileAndLink(gl);
    }
    
	/**
	 * Get the OpenGL ID of the shader.
	 * 
	 * @return The ID of the shader.
	 */
    public int getID() {
		return ID;
    }
    
	/**
	 * Specifically activate this shader in the OpenGL context.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
    public void use(GL3 gl) {
		gl.glUseProgram(ID);
    }
    
	/**
	 * Set an integer value for a uniform variable.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param value The integer value to set the uniform to.
	 */
    public void setInt(GL3 gl, String name, int value) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform1i(location, value);
    }
    
	/**
	 * Set a float value for a uniform variable.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param value The float value to set the uniform to.
	 */
    public void setFloat(GL3 gl, String name, float value) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform1f(location, value);
    }
    
	/**
	 * Set multiple float values for a uniform variable (in the form of a 2D vector).
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param f1 The first float value of the 2D vector.
	 * @param f2 The second float value of the 2D vector.
	 */
    public void setFloat(GL3 gl, String name, float f1, float f2) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform2f(location, f1, f2);
    }
    
	/**
	 * Set multiple float values for a uniform variable (in the form of a 3D vector).
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param f1 The first float value of the 3D vector.
	 * @param f2 The second float value of the 3D vector.
	 * @param f3 The third float value of the 3D vector.
	 */
    public void setFloat(GL3 gl, String name, float f1, float f2, float f3) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform3f(location, f1, f2, f3);
    }
    
	/**
	 * Set multiple float values for a uniform variable (in the form of a 4D vector).
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param f1 The first float value of the 4D vector.
	 * @param f2 The second float value of the 4D vector.
	 * @param f3 The third float value of the 4D vector.
	 * @param f4 The fourth float value of the 4D vector.
	 */
    public void setFloat(GL3 gl, String name, float f1, float f2, float f3, float f4) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform4f(location, f1, f2, f3, f4);
    }
    
	/**
	 * Set multiple float values for a uniform variable (in the form of a 4x4 matrix).
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param f The array of float values (representing the 4x4 matrix).
	 */
    public void setFloatArray(GL3 gl, String name, float[] f) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniformMatrix4fv(location, 1, false, f, 0);
    }
    
	/**
	 * Set a 3D vector for a uniform variable.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the uniform variable.
	 * @param v The Vec3 representing the vector of values.
	 */
    public void setVec3(GL3 gl, String name, Vec3 v) {
		int location = gl.glGetUniformLocation(ID, name);
		gl.glUniform3f(location, v.x, v.y, v.z);
    }
    
	/**
	 * Display the source code of the vertex and fragment shaders.
	 */
    private void display() {
		System.out.println("***Vertex shader***");
		System.out.println(vertexShaderSource);
		System.out.println("\n***Fragment shader***");
		System.out.println(fragmentShaderSource);
    }
    
	/**
	 * Compile the vertex and fragment shaders and link them to the program.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @return The OpenGL ID of the shader.
	 */
    private int compileAndLink(GL3 gl) {
		String[][] sources = new String[1][1];
		sources[0] = new String[]{ vertexShaderSource };
		ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
		boolean compiled = vertexShaderCode.compile(gl, System.err);
		if (!compiled)
			System.err.println("[error] Unable to compile vertex shader: " + sources);
		sources[0] = new String[]{ fragmentShaderSource };
		ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
		compiled = fragmentShaderCode.compile(gl, System.err);
		if (!compiled)
			System.err.println("[error] Unable to compile fragment shader: " + sources);
		ShaderProgram program = new ShaderProgram();
		program.init(gl);
		program.add(vertexShaderCode);
		program.add(fragmentShaderCode);
		program.link(gl, System.out);
		if (!program.validateProgram(gl, System.out))
			System.err.println("[error] Unable to link program");
		return program.program();
    }
}