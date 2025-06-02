import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Class for storing model-related information for rendering a globe in the world and updating it
 * over time (in terms of rotation).
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - The whole class is new for globe construction/rotation updates
 */
public class Globe {
    private ModelMultipleLights[] globe;
    private Camera camera;
	private Light[] lights;
    private Texture standTextureDiffuse, standTextureSpecular, axisTexture, earthTexture;
	private int noObjects = 3;

    private SGNode globeGraph;
	private TransformNode rotateEarth;
	private float rotateEarthAngleStart = 45, rotateEarthAngle = rotateEarthAngleStart;
	private double startTime;

	/**
	 * Constructor. Initialise the globe object by creating its individual models, setting their 
	 * textures and creating the scene graph.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param c The camera object in the world.
	 * @param l The light sources in the world (as an array).
	 * @param t The array of all textures to be used for this specific object.
	 */
    public Globe(GL3 gl, Camera c, Light[] l, Texture[] t) {
		camera = c;
		lights = l;
		this.standTextureDiffuse = t[0];
		this.standTextureSpecular = t[1];
		this.axisTexture = t[2];
		this.earthTexture = t[3];
		globe = new ModelMultipleLights[noObjects];
		Shader shaderTexture = new Shader(
			gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_m_1t.txt");
		Shader shaderTextures = new Shader(
			gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_m_2t.txt");
		Material material = new Material(
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.3f, 0.3f, 0.3f), 
			4.0f);
        Mat4 modelMatrix = new Mat4(1);
		
        globe[0] = ModelMaker.makePart(gl,
									   "stand", 
									   material,
									   modelMatrix,
									   shaderTextures,
									   new Texture[] {standTextureDiffuse, standTextureSpecular},
									   lights,
									   camera,
									   "cube");
        
        globe[1] = ModelMaker.makePart(gl,
									   "axis", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {axisTexture},
									   lights,
									   camera,
									   "sphere");

        globe[2] = ModelMaker.makePart(gl,
									  "earth", 
									  material,
									  modelMatrix,
									  shaderTexture,
									  new Texture[] {earthTexture},
									  lights,
									  camera,
									  "sphere");

		startTime = getSeconds();
        createGlobeSceneGraph();
    }

	/**
	 * Render the globe object in the world by updating its rotation and redrawing the scene graph.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
    public void render(GL3 gl) {
		updateRotation();
		globeGraph.draw(gl);
	}

	/**
	 * Discard the globe object by disposing of each individual model and the resources they use.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		for (int i=0; i<noObjects; i++) {
		    globe[i].dispose(gl);
		}
	}

    // ***************************************************
	/* SCENE GRAPH */

	/**
	 * Initialise the scene graph of the object by connecting each component hierarchically.
	 */
	private void createGlobeSceneGraph () {
		globeGraph = new NameNode("globe");

		float standHeight = 1.0f;
		float axisHeight = 3.0f;
		float earthHeight = 2.0f;

		SGNode standBranch = ModelMaker.makeBranch(
			globe[0], "globe stand", standHeight, standHeight, standHeight, 0, 0, 0);
		SGNode axisBranch = ModelMaker.makeBranch(
			globe[1], "globe axis", axisHeight/30, axisHeight, axisHeight/30, 0, 0, 0);
		SGNode earthBranch = ModelMaker.makeBranch(
			globe[2], "globe earth", earthHeight, earthHeight, earthHeight, 0, 0, 0);

		TransformNode translateToPosition = new TransformNode(
			"translate globe to position in room", 
			Mat4Transform.translate(3.5f, 0, 3.5f));
		TransformNode translateToTop1 = new TransformNode(
			"translate to top of globe stand", 
			Mat4Transform.translate(0, 3*standHeight/4, 0));
    	TransformNode translateToTop2 = new TransformNode(
			"translate to middle of globe axis", 
			Mat4Transform.translate(0, (axisHeight/2 - earthHeight/2), 0));
		rotateEarth = new TransformNode("rotate globe earth around y axis", 
										Mat4Transform.rotateAroundY(rotateEarthAngle));

		globeGraph.addChild(translateToPosition);
			translateToPosition.addChild(standBranch);
				standBranch.addChild(translateToTop1);
					translateToTop1.addChild(axisBranch);
						axisBranch.addChild(translateToTop2);
							translateToTop2.addChild(rotateEarth);
								rotateEarth.addChild(earthBranch);

		globeGraph.update();
	}

	/**
	 * Update the angle of the Earth based on the current time.
	 */
	private void updateRotation() {
		double elapsedTime = getSeconds()-startTime;
		rotateEarthAngle = rotateEarthAngleStart*(float)elapsedTime;
		
		rotateEarth.setTransform(Mat4Transform.rotateAroundY(rotateEarthAngle));
		globeGraph.update();
	}
  
	/**
	 * Get the current time in seconds.
	 * 
	 * @return The current time in seconds in a double format.
	 */
  	private double getSeconds() {
    	return System.currentTimeMillis()/1000.0;
  	}
}