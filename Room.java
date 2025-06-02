import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Class for setting up the objects needed to draw the room containing all elements of the 
 * spacecraft.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - A new class for creating the room and its individual components
 */
public class Room {
	private ModelMultipleLights[] wall;
	private Camera camera;
	private Light[] lights;
	private Texture floorTexture, ceilingTexture, bWallDiffuseTexture, bWallSpecularTexture, 
		lWallTexture1, lWallTexture2, lWallTexture3, windowPiece, rWallTexture;
	private float size = 16f;
	private int noObjects = 12;

	/**
	 * Constructor. Initialise the models comprising the room.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param c The camera object in the world.
	 * @param l The light sources in the world (as an array).
	 * @param t The array of all textures to be used for the room.
	 */
	public Room(GL3 gl, Camera c, Light[] l, Texture[] t) {
		camera = c;
		lights = l;
		this.floorTexture = t[0];
		this.ceilingTexture = t[1];
		this.bWallDiffuseTexture = t[2];
		this.bWallSpecularTexture = t[3];
		this.lWallTexture1 = t[4];
		this.lWallTexture2 = t[5];
		this.lWallTexture3 = t[6];
		this.windowPiece = t[7];
		this.rWallTexture = t[8];
		wall = new ModelMultipleLights[noObjects];
		Shader shaderTexture = new Shader(
			gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_m_1t.txt");
		Shader shaderTextures = new Shader(
			gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_m_2t.txt");
		Material material = new Material(
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.3f, 0.3f, 0.3f), 
			4.0f);

		// Floor
		Mat4 modelMatrix = prepareModelMatrix(size, 1f, size, 0, 0, 0, 0, 0, 0);
		wall[0] = ModelMaker.makePart(gl,
						   			  "floor", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {floorTexture},
						   			  lights,
						   			  camera,
						   			  "two triangles");

		// Back wall
		modelMatrix = prepareModelMatrix(size, 1f, size, 90, 0, 0, 0, size*0.5f, -size*0.5f);
		wall[1] = ModelMaker.makePart(gl,
						   			  "back wall", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTextures,
						   			  new Texture[] {bWallDiffuseTexture, bWallSpecularTexture},
						   			  lights,
						   			  camera,
						   			  "two triangles");

		// Left wall pieces
		modelMatrix = prepareModelMatrix(
			size*0.25f, 1f, 
			size, 0, 90, -90, 
			-size*0.5f, size*0.5f, size*0.375f);
		wall[2] = ModelMaker.makePart(gl,
						   			  "left wall (vertical piece 1)", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {lWallTexture1},
						   			  lights,
						   			  camera,
						   			  "two triangles");
		
		modelMatrix = prepareModelMatrix(
			size*0.25f, 1f, size, 
			0, 90, -90, 
			-size*0.5f, size*0.5f, -size*0.375f);
		wall[3] = ModelMaker.makePart(gl,
						   			  "left wall (vertical piece 2)", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {lWallTexture1},
						   			  lights,
						   			  camera,
						   			  "two triangles");

		modelMatrix = prepareModelMatrix(
			size*0.5f, 1f, size*0.25f, 
			0, 90, -90, 
			-size*0.5f, size*0.125f, 0);
		wall[4] = ModelMaker.makePart(gl,
						   			  "left wall (horizontal piece 1)", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {lWallTexture3},
						   			  lights,
						   			  camera,
						   			  "two triangles");

		modelMatrix = prepareModelMatrix(
			size*0.5f, 1f, size*0.25f, 
			0, 90, -90, 
			-size*0.5f, size*0.875f, 0);
		wall[5] = ModelMaker.makePart(gl,
						   			  "left wall (horizontal piece 2)", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {lWallTexture2},
						   			  lights,
						   			  camera,
						   			  "two triangles");

		// Window parts
		modelMatrix = prepareModelMatrix(
			size*0.125f, 1f, size*0.125f, 
			0, 90, -90, 
			-size*0.5f, size*0.3125f, size*0.1875f);
		wall[6] = ModelMaker.makePart(gl,
						   			  "window piece 1", 
						   			  material,
						   			  modelMatrix,
						   			  shaderTexture,
						   			  new Texture[] {windowPiece},
						   			  lights,
						   			  camera,
						   			  "triangle");

		modelMatrix = prepareModelMatrix(
			size*0.125f, 1f, size*0.125f, 
			0, 180, -90, 
			-size*0.5f, size*0.3125f, -size*0.1875f);
		wall[7] = ModelMaker.makePart(gl,
									  "window piece 2", 
									  material,
									  modelMatrix,
									  shaderTexture,
									  new Texture[] {windowPiece},
									  lights,
									  camera,
									  "triangle");

		modelMatrix = prepareModelMatrix(
			size*0.125f, 1f, size*0.125f, 
			0, 270, -90, 
			-size*0.5f, size*0.6875f, -size*0.1875f);
		wall[8] = ModelMaker.makePart(gl,
									  "window piece 3", 
									  material,
									  modelMatrix,
									  shaderTexture,
									  new Texture[] {windowPiece},
									  lights,
									  camera,
									  "triangle");

		modelMatrix = prepareModelMatrix(
			size*0.125f, 1f, size*0.125f, 
			0, 0, -90, -
			size*0.5f, size*0.6875f, size*0.1875f);
		wall[9] = ModelMaker.makePart(gl,
									  "window piece 4", 
									  material,
									  modelMatrix,
									  shaderTexture,
									  new Texture[] {windowPiece},
									  lights,
									  camera,
									  "triangle");

		// Right wall
		modelMatrix = prepareModelMatrix(size, 1f, size, 0, -90, 90, size*0.5f, size*0.5f, 0);
		wall[10] = ModelMaker.makePart(gl,
									   "right wall", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {rWallTexture},
									   lights,
									   camera,
									   "two triangles (loop texture)");

		// Ceiling
		modelMatrix = prepareModelMatrix(size, 1f, size, 180, 0, 0, 0, size, 0);
		wall[11] = ModelMaker.makePart(gl,
									   "ceiling", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {ceilingTexture},
									   lights,
									   camera,
									   "two triangles");
	}

	/**
	 * Specialised model matrix preparation function to scale, rotate and translate specific
	 * parts of the room.
	 * 
	 * @param scaleX The scale factor for the model (x-direction)
	 * @param scaleY The scale factor for the model (y-direction)
	 * @param scaleZ The scale factor for the model (z-direction)
	 * @param rotateX The angle at which to rotate the model (around the x-axis)
	 * @param rotateY The angle at which to rotate the model (around the y-axis)
	 * @param rotateZ The angle at which to rotate the model (around the z-axis)
	 * @param translateX The distance with which to translate the model (x-direction)
	 * @param translateY The distance with which to translate the model (y-direction)
	 * @param translateZ The distance with which to translate the model (z-direction)
	 * @return The transformation matrix of one model.
	 */
	private Mat4 prepareModelMatrix(float scaleX, float scaleY, float scaleZ,
									int rotateX, int rotateY, int rotateZ,
									float translateX, float translateY, float translateZ) {
		Mat4 modelMatrix = new Mat4(1);
		modelMatrix = Mat4.multiply(Mat4Transform.scale(scaleX, scaleY, scaleZ), modelMatrix);
		modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(rotateX), modelMatrix);
		modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(rotateY), modelMatrix);
		modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(rotateZ), modelMatrix);
		modelMatrix = Mat4.multiply(
			Mat4Transform.translate(translateX, translateY, translateZ), 
			modelMatrix);
		return modelMatrix;
	}

	/**
	 * Draw all of the parts of the room.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void render(GL3 gl) {
		for (int i=0; i<noObjects; i++) {
			wall[i].render(gl);
		}
	}

	/**
	 * Dispose of each individual part of the room and the resources they use.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		for (int i=0; i<noObjects; i++) {
			wall[i].dispose(gl);
		}
	}
}