import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

/**
 * Class for model objects within the world (with the 'MultipleLights' denoting its compatibility
 * with worlds containing many light sources). Stores information related to mesh, material,
 * texture, shaders, etc.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - Shaders are now updated with uniform variables for spotlight functionality (cutoff, direction)
 */
public class ModelMultipleLights {
	private String name;
	private Mesh mesh;
	private Mat4 modelMatrix;
	private Shader shader;
	private Material material;
	private Camera camera;
	private Light[] lights;
	private Texture diffuse;
	private Texture specular;

	/**
	 * Constructor with no parameters. Sets every attribute as null.
	 */
	public ModelMultipleLights() {
		name = null;
		mesh = null;
		modelMatrix = null;
		material = null;
		camera = null;
		lights = null;
		shader = null;
	}

	/**
	 * Constructor. Accounts for both diffuse and specular textures and sets attributes to values
	 * offered by the parameters.
	 * 
	 * @param name The name of the model.
	 * @param mesh The shape of the model.
	 * @param modelMatrix The transformation matrix associated with the model.
	 * @param shader The shader to use (whether it accounts for specular/diffuse, etc.).
	 * @param material The material of the model.
	 * @param lights The lights in the scene.
	 * @param camera The camera in the scene.
	 * @param diffuse Diffuse texture of the object.
	 * @param specular Specular texture of the object.
	 */
	public ModelMultipleLights(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
		                       Camera camera, Texture diffuse, Texture specular) {
		this.name = name;
		this.mesh = mesh;
		this.modelMatrix = modelMatrix;
		this.shader = shader;
		this.material = material;
		this.lights = lights;
		this.camera = camera;
		this.diffuse = diffuse;
		this.specular = specular;
	}

	/**
	 * Constructor. Similar to other constructor but with the absence of the specular texture.
	 * 
	 * @param name The name of the model.
	 * @param mesh The shape of the model.
	 * @param modelMatrix The transformation matrix associated with the model.
	 * @param shader The shader to use (whether it accounts for specular/diffuse, etc.).
	 * @param material The material of the model.
	 * @param lights The lights in the scene.
	 * @param camera The camera in the scene.
	 * @param diffuse Diffuse texture of the object.
	 */
	public ModelMultipleLights(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
	                           Camera camera, Texture diffuse) {
		this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
	}

	/**
	 * Constructor with no textures associated with the model.
	 * 
	 * @param name The name of the model.
	 * @param mesh The shape of the model.
	 * @param modelMatrix The transformation matrix associated with the model.
	 * @param shader The shader to use (whether it accounts for specular/diffuse, etc.).
	 * @param material The material of the model.
	 * @param lights The lights in the scene.
	 * @param camera The camera in the scene.
	 */
	public ModelMultipleLights(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
		                       Camera camera) {
		this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
	}

	/**
	 * Set the name of the model.
	 * 
	 * @param s The name to be assigned to the model.
	 */
	public void setName(String s) {
		this.name = s;
	}

	/**
	 * Set the mesh of the model.
	 * 
	 * @param m The mesh to be assigned to the model.
	 */
	public void setMesh(Mesh m) {
		this.mesh = m;
	}

	/**
	 * Set the model matrix of the model.
	 * 
	 * @param m The model matrix to be assigned to the model.
	 */
	public void setModelMatrix(Mat4 m) {
		modelMatrix = m;
	}

	/**
	 * Set the material of the model.
	 * 
	 * @param material The material to be assigned to the model.
	 */
	public void setMaterial(Material material) {
		this.material = material;
	}

	/**
	 * Set the shader of the model.
	 * 
	 * @param shader The shader to be assigned to the model.
	 */
	public void setShader(Shader shader) {
		this.shader = shader;
	}

	/** 
	 * Set the camera of the model.
	 * 
	 * @param camera The camera to be assigned to the model.
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 * Set the lights applied to the model.
	 * 
	 * @param lights The lights to be assigned to the model.
	 */
	public void setLights(Light[] lights) {
		this.lights = lights;
	}

	/**
	 * Set the diffuse texture to the model.
	 * 
	 * @param t The diffuse texture to be assigned to the model.
	 */
	public void setDiffuse(Texture t) {
		this.diffuse = t;
	}

	/**
	 * Set the specular texture to the model.
	 * 
	 * @param t The specular texture to be assigned to the model.
	 */
	public void setSpecular(Texture t) {
		this.specular = t;
	}

	/**
	 * Display the name of the model.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void renderName(GL3 gl) {
		System.out.println("Name = " + name);
	}

	/**
	 * Version of render that is used to display the model if modelMatrix is not overriden with
	 * a new parameter.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void render(GL3 gl) {
		render(gl, modelMatrix);
	}

	/**
	 * Version of render so that modelMatrix can be overriden with a new parameter.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param modelMatrix The transformation matrix associated with the model.
	 */
	public void render(GL3 gl, Mat4 modelMatrix) {
		if (mesh_null()) {
			System.out.println("Error: null in model render");
			return;
		}

		Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
		shader.use(gl);
		shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
		shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

		shader.setVec3(gl, "viewPos", camera.getPosition());

		shader.setInt(gl,"numLights", lights.length);

		// Update global light/spotlight shaders
		for (int i=0; i<lights.length; i++) {
			shader.setVec3(gl, "lights["+i+"].position", lights[i].getPosition());
			shader.setVec3(gl, "lights["+i+"].ambient", lights[i].getMaterial().getAmbient());
			shader.setVec3(gl, "lights["+i+"].diffuse", lights[i].getMaterial().getDiffuse());
			shader.setVec3(gl, "lights["+i+"].specular", lights[i].getMaterial().getSpecular());
			shader.setVec3(gl, "lights["+i+"].direction", lights[i].getDirection());
			shader.setFloat(gl, "lights["+i+"].cutOff", (float)Math.cos(Math.toRadians(12.5f)));
			shader.setFloat(gl, "lights["+i+"].outerCutOff", (float)Math.cos(Math.toRadians(17.5f)));
			shader.setFloat(gl, "lights["+i+"].constant", 1.0f);
			shader.setFloat(gl, "lights["+i+"].linear", 0.09f);
			shader.setFloat(gl, "lights["+i+"].quadratic", 0.032f);
		}

		shader.setVec3(gl, "material.ambient", material.getAmbient());
		shader.setVec3(gl, "material.diffuse", material.getDiffuse());
		shader.setVec3(gl, "material.specular", material.getSpecular());
		shader.setFloat(gl, "material.shininess", material.getShininess());

		// Extra uniforms for textures (diffuse/specular textures are provided for the model).
		if (diffuse!=null) {
			shader.setInt(gl, "first_texture", 0);
			gl.glActiveTexture(GL.GL_TEXTURE0);
			diffuse.bind(gl);
		}
		if (specular!=null) {
			shader.setInt(gl, "second_texture", 1);
			gl.glActiveTexture(GL.GL_TEXTURE1);
			specular.bind(gl);
		}

		// Finally, the mesh is rendered.
		mesh.render(gl);
	}

	/**
	 * Return whether or not the mesh is null.
	 */
	private boolean mesh_null() {
		return (mesh==null);
	}

	/**
	 * Discard the model by only needing to dispose of the mesh to free up resources.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		mesh.dispose(gl);
	}
}