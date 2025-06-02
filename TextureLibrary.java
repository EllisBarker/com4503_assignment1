import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.jogamp.opengl.*;

import com.jogamp.opengl.util.texture.*;

/**
 * Class to manage texture information (individual and cube maps) through loading files, binding
 * textures and setting individual parameters.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - loadCubemap(): load a cubemap texture from an array of 2D textures (for the skybox)
 */
public class TextureLibrary {	
	private Map<String,Texture> textures;

	/**
	 * Constructor. Initialises the hash map used to store texture information alongside an 
	 * associated identifying string for each.
	 */
	public TextureLibrary() {
		textures = new HashMap<String, Texture>();
	}

	/**
	 * Add a new texture to the texture hash map based on a file path.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name used to refer to the texture.
	 * @param filename The file name (or path) of the texture in the user's system.
	 */
	public void add(GL3 gl, String name, String filename) {
		Texture texture = loadTexture(gl, filename);
		textures.put(name, texture);
	}

	/**
	 * Add a new cube map texture to the texture hash map based on file paths.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name used to refer to the texture.
	 * @param filenames The selection of file names (or paths) of textures in the user's system.
	 */
	public void addCubemap(GL3 gl, String name, String[] filenames) {
		Texture cubemap = loadCubemap(gl, filenames);
		textures.put(name, cubemap);
	}

	/**
	 * Retrieve a specific texture from the hash map based on its specific name.
	 * 
	 * @param name The name of the texture to retrieve.
	 * @return The desired texture.
	 */
	public Texture get(String name) {
		return textures.get(name);
	}

	/**
	 * Create a new 2D texture from loading a file, and set its properties to allow for 
	 * mip-mapping.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param filename The file name (or path) of the texture in the user's system.
	 * @return The texture that has been loaded.
	 */
	public static Texture loadTexture(GL3 gl3, String filename) {
		Texture t = null; 
		try {
			File f = new File(filename);
			t = (Texture)TextureIO.newTexture(f, true);
			t.bind(gl3);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT); 
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
			gl3.glGenerateMipmap(GL3.GL_TEXTURE_2D);
		}
		catch(Exception e) {
			System.out.println("Error loading texture " + filename); 
		}
		return t;
	}

	/**
	 * Create a cube map texture from loading a selection of 6 images that each represent a 
	 * different face of the cube.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param filenames The file names (or paths) of the individual face textures.
	 * @return The cube map texture that has been loaded.
	 */
	public static Texture loadCubemap(GL3 gl3, String[] filenames) {
		Texture t = null;
		try {
			t = (Texture)TextureIO.newTexture(GL3.GL_TEXTURE_CUBE_MAP);
			t.bind(gl3);

			// Assigning texture to each face of the cube map
			for (int i=0; i<filenames.length; i++) {
				File f = new File(filenames[i]);
				TextureData faceData = TextureIO.newTextureData(gl3.getGLProfile(), f, true, null);
				gl3.glTexImage2D(GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
								 0,
								 faceData.getInternalFormat(),
								 faceData.getWidth(),
								 faceData.getHeight(),
								 0,
								 faceData.getPixelFormat(),
								 faceData.getPixelType(),
								 faceData.getBuffer());
			}

			t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
			t.setTexParameteri(gl3, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
		}
		catch(Exception e) {
			System.out.println("Error loading textures for cubemap"); 
		}
		return t;
	}

	/**
	 * Dispose of all the textures in the hash map to free resources.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void destroy(GL3 gl3) {
		for (var entry : textures.entrySet()) {
			entry.getValue().destroy(gl3);
		}
	}
}