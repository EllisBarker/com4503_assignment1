import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;


/**
 * Class for making models and model nodes in scene graphs (avoiding repetition of each object
 * class).
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - A new class for preparing model matrices and models to be put into scene graphs
 */
public final class ModelMaker {
	/**
	 * Model creation function with many parameters to fit different shape types and texture 
	 * selections.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param name The name of the model.
	 * @param material The material of the model.
	 * @param modelMatrix The transformation matrix associated with the model.
	 * @param shader The shader to use (whether it accounts for specular/diffuse, etc.).
	 * @param textures The array of textures for the model (used to determine which model 
	 				    constructor to use)
	 * @param lights The lights in the scene.
	 * @param camera The camera in the scene.
	 * @param modelOption The shape type of the model (sphere, cube, triangle, etc.).
	 * @return Fully prepared model for rendering.
	 */
    public static ModelMultipleLights makePart(GL3 gl, String name, Material material, 
	                                           Mat4 modelMatrix, Shader shader, Texture[] textures, 
											   Light[] lights, Camera camera, String modelOption) {
		Mesh mesh = null;
        if (modelOption == "sphere")
            mesh = new Mesh(gl, Sphere.vertices.clone(), Sphere.indices.clone());
        else if (modelOption == "cube")
            mesh = new Mesh(gl, Cube.vertices.clone(), Cube.indices.clone());
        else if (modelOption == "triangle")
            mesh = new Mesh(gl, Triangle.vertices.clone(), Triangle.indices.clone());
        else if (modelOption == "two triangles")
            mesh = new Mesh(gl, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone());
		// Used for the right wall to loop one texture across the whole surface
        else if (modelOption == "two triangles (loop texture)")
			mesh = new Mesh(gl, TwoTriangles.vertices_mipmap.clone(), TwoTriangles.indices.clone());

		ModelMultipleLights model;
		switch (textures.length) {
			// Case for single textures
			case 1:
				model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, 
												camera, textures[0]);
				break;
			// Case for diffuse and specular textures
			case 2:
				model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, 
												camera, textures[0], textures[1]);
				break;
			// All other cases (no texture, etc.)
			default:
				model = new ModelMultipleLights(name, mesh, modelMatrix, shader, material, lights, 
												camera);
		}
		return model;
	}

	/**
	 * Create a branch for preparing the model for the scene graph and converting it to a SGNode.
	 * 
	 * @param model Model instance used as a basis for the scene graph node.
	 * @param name Name of the node (and associated transform nodes).
	 * @param sx Scaling value (in the x direction)
	 * @param sy Scaling value (in the y direction)
	 * @param sz Scaling value (in the z direction)
	 * @param rx Rotation value (about the x-axis)
	 * @param ry Rotation value (about the y-axis)
	 * @param rz Rotation value (about the z-axis)
	 * @return Node for the scene graph based on the model and options supplied.
	 */
	public static SGNode makeBranch(ModelMultipleLights model, String name, 
									float sx, float sy, float sz,
									float rx, float ry, float rz) {
		NameNode branchName = new NameNode(name + "branch");

		//Rotation transforms
		TransformNode branchRotateX = new TransformNode(
			name + " rotateX", 
			Mat4Transform.rotateAroundX(rx));
		TransformNode branchRotateY = new TransformNode(
			name + " rotateY", 
			Mat4Transform.rotateAroundY(ry));
		TransformNode branchRotateZ = new TransformNode(
			name + " rotateZ", 
			Mat4Transform.rotateAroundZ(rz));

		Mat4 m = Mat4Transform.scale(sx,sy,sx);
		m = Mat4.multiply(m, Mat4Transform.translate(0,0.5f,0));
		TransformNode branchScale = new TransformNode(name + " scale", m);
		ModelNode modelNode = new ModelNode(name + "model", model);
    	branchName.addChild(branchRotateX);
			branchRotateX.addChild(branchRotateY);
				branchRotateY.addChild(branchRotateZ);
					branchRotateZ.addChild(branchScale);
						branchScale.addChild(modelNode);
		return branchName;
	}
}