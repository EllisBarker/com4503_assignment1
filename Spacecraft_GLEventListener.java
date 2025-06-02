import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.*;
import com.jogamp.opengl.util.texture.spi.JPEGImage;

/**
 * Class for handling rendering the elements of the spacecraft, user interactions and general 
 * setup for the scene.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - changeGlobalLightIntensity(): process UI interaction for the global light's intensity
 * - changeSpotlightIntensity(): process UI interaction for the spotlight's intensity
 * - changeDistanceThreshold(): process UI interaction for robot 1's dancing proximity distance
 * - startStopRobot1Movement(): process UI interaction for manually setting robot 1's dancing state
 * - startStopRobot2Movement(): process UI interaction for manually setting robot 2's movement
 */
public class Spacecraft_GLEventListener implements GLEventListener {
	private static final boolean DISPLAY_SHADERS = false;
	private Camera camera;
	
	/**
	 * Constructor. Set the camera for the scene and its position/target.
	 * 
	 * @param camera The camera used in rendering.
	 */
	public Spacecraft_GLEventListener(Camera camera) {
		this.camera = camera;
		this.camera.setPosition(new Vec3(0f,8f,16f));
		this.camera.setTarget(new Vec3(0f,4f,0f));
	}
	
	// ***************************************************
	/* METHODS DEFINED BY GLEventListener */

	/**
	 * Initialises the OpenGL context/rendering options and the scene in general.
	 * 
	 * @param drawable The OpenGL drawable object.
	 */
	public void init(GLAutoDrawable drawable) {   
		GL3 gl = drawable.getGL().getGL3();
		System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); 
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glFrontFace(GL.GL_CCW);
		initialise(gl);
		startTime = getSeconds();
	}
	
	/**
	 * Update the camera if the window (drawing area) is resized.
	 * 
	 * @param drawable The OpenGL drawable object.
	 * @param x The x-coordinate of the viewport's origin.
	 * @param y The y-coordinate of the viewport's origin.
	 * @param width The width of the viewport to be set.
	 * @param height The height of the viewport to be set.
	 */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL3 gl = drawable.getGL().getGL3();
		gl.glViewport(x, y, width, height);
		float aspect = (float)width/(float)height;
		camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
	}

	/**
	 * Render the whole scene (draw every object).
	 * 
	 * @param drawable The OpenGL drawable object.
	 */
	public void display(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		render(gl);
	}

	/**
	 * Clean up resources by disposing of each object (e.g. when closing the window).
	 * 
	 * @param drawable The OpenGL drawable object.
	 */
	public void dispose(GLAutoDrawable drawable) {
		GL3 gl = drawable.getGL().getGL3();
		room.dispose(gl);
		globe.dispose(gl);
		robot1.dispose(gl);
		robot2.dispose(gl);
		lights[0].dispose(gl);
		lights[1].dispose(gl);
		textures.destroy(gl);
	}

	// ***************************************************
	/* ACTIONS PERFORMED ACHIEVED BY USER INTERACTION */

	/**
	 * Edit the light intensity of the general light by altering its material.
	 * 
	 * @param intensity The level of light intensity desired by user interaction.
	 */
	public void changeGlobalLightIntensity(float intensity) {
		Material material = new Material();
		Vec3 ambient = defaultGlobalLightIntensity.getAmbient();
		Vec3 diffuse = defaultGlobalLightIntensity.getDiffuse();
		Vec3 specular = defaultGlobalLightIntensity.getSpecular();
		material.setAmbient(ambient.x*intensity, ambient.y*intensity, ambient.z*intensity);
		material.setDiffuse(diffuse.x*intensity, diffuse.y*intensity, diffuse.z*intensity);
		material.setSpecular(specular.x*intensity, specular.y*intensity, specular.z*intensity);
		lights[0].setMaterial(material);
	}
	
	/**
	 * Edit the light intensity of the spotlight by altering its material.
	 * 
	 * @param intensity The level of light intensity desired by user interaction.
	 */
	public void changeSpotlightIntensity(float intensity) {
		Material material = new Material();
		Vec3 ambient = defaultSpotLightIntensity.getAmbient();
		Vec3 diffuse = defaultSpotLightIntensity.getDiffuse();
		Vec3 specular = defaultSpotLightIntensity.getSpecular();
		material.setAmbient(ambient.x*intensity, ambient.y*intensity, ambient.z*intensity);
		material.setDiffuse(diffuse.x*intensity, diffuse.y*intensity, diffuse.z*intensity);
		material.setSpecular(specular.x*intensity, specular.y*intensity, specular.z*intensity);
		lights[1].setMaterial(material);
	}

	/**
	 * Edit the distance threshold for robot 2 causing robot 1 to dance.
	 * 
	 * @param distance The distance desired by user interaction.
	 */
	public void changeDistanceThreshold(float distance) {
		robot1.setDistanceThreshold(distance);
	}

	/**
	 * Start or stop robot 1's dancing (manual override from the user).
	 */
	public void startStopRobot1Movement() {
		robot1.setRobotDancing();	
	}

	/**
	 * Start or stop robot 2's movement (manual override from the user).
	 */
	public void startStopRobot2Movement() {
		robot2.setRobotMoving();
	}

	// ***************************************************
	/* THE SCENE */

	private TextureLibrary textures;

	// The environment
	private Room room;
	private Skybox skybox;

	// Light-related variables
	private Light[] lights = new Light[2];
	private Material defaultGlobalLightIntensity;
	private Material defaultSpotLightIntensity;

	// Objects within the room
	private Globe globe;
	private Robot1 robot1;
	private Robot2 robot2;

	/**
	 * Load textures given their file paths and associate them with certain identifying names.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	private void loadTextures(GL3 gl) {
		textures = new TextureLibrary();
		textures.add(gl, "floor", "assets/textures/floor.jpg");
		textures.add(gl, "ceiling", "assets/textures/ceiling.jpg");
		textures.add(gl, "name_diffuse", "assets/textures/diffuse_ellis.jpg");
		textures.add(gl, "name_specular", "assets/textures/specular_ellis.jpg");
		textures.add(gl, "left_wall1", "assets/textures/left_wall1.jpg");
		textures.add(gl, "left_wall2", "assets/textures/left_wall2.jpg");
		textures.add(gl, "left_wall3", "assets/textures/left_wall3.jpg");
		textures.add(gl, "window_piece", "assets/textures/window_piece.jpg");
		textures.add(gl, "right_wall", "assets/textures/right_wall.jpg");

		textures.add(gl, "stand_diffuse", "assets/textures/diffuse_stand.jpg");
		textures.add(gl, "stand_specular", "assets/textures/specular_stand.jpg");
		textures.add(gl, "axis", "assets/textures/axis.jpg");
		textures.add(gl, "earth", "assets/textures/earth_landmarks.jpg");

		textures.add(gl, "robot1_base", "assets/textures/robot1_base.jpg");
		textures.add(gl, "robot1_body1", "assets/textures/robot1_body1.jpg");
		textures.add(gl, "robot1_body2", "assets/textures/robot1_body2.jpg");
		textures.add(gl, "robot1_body3", "assets/textures/robot1_body3.jpg");
		textures.add(gl, "robot1_arm", "assets/textures/robot1_arm.jpg");
		textures.add(gl, "robot1_head", "assets/textures/robot1_head.jpg");
		textures.add(gl, "robot1_eye", "assets/textures/robot1_eye.jpg");
		textures.add(gl, "robot1_appendage", "assets/textures/robot1_appendage.jpg");
		
		textures.add(gl, "robot2_body", "assets/textures/robot2_body.jpg");
		textures.add(gl, "robot2_eye", "assets/textures/robot2_eye.jpg");
		textures.add(gl, "robot2_antenna", "assets/textures/robot2_antenna.jpg");
		textures.add(gl, "robot2_casing", "assets/textures/robot2_casing.jpg");

		String[] skyboxFilenames = {"assets/textures/skybox_right.jpg",
									"assets/textures/skybox_left.jpg",
									"assets/textures/skybox_top.jpg",
									"assets/textures/skybox_bottom.jpg",
									"assets/textures/skybox_front.jpg",
									"assets/textures/skybox_back.jpg"};
		textures.addCubemap(gl, "skybox", skyboxFilenames);
		textures.add(gl, "skybox_moving", "assets/textures/skybox_moving.jpg");
	}

	/**
	 * Set up each element of the scene in terms of model making, setting of positions/directions,
	 * etc.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void initialise(GL3 gl) {
		loadTextures(gl);

		lights[0] = new Light(gl, "cube");
		lights[0].setCamera(camera);
		lights[0].setPosition(new Vec3(0,8,-4));
		defaultGlobalLightIntensity = lights[0].getMaterial();
		lights[1] = new Light(gl, "sphere");
		lights[1].setCamera(camera);
		defaultSpotLightIntensity = lights[1].getMaterial();

		Texture[] roomTextures = {textures.get("floor"),
								  textures.get("ceiling"),
								  textures.get("name_diffuse"),
								  textures.get("name_specular"),
								  textures.get("left_wall1"),
								  textures.get("left_wall2"),
								  textures.get("left_wall3"),
								  textures.get("window_piece"),
								  textures.get("right_wall")};
		room = new Room(gl, camera, lights, roomTextures);

		Texture[] globeTextures = {textures.get("stand_diffuse"),
								   textures.get("stand_specular"),
								   textures.get("axis"),
								   textures.get("earth")};
		globe = new Globe(gl, camera, lights, globeTextures);

		Texture[] robot1Textures = {textures.get("robot1_base"),
									textures.get("robot1_body1"),
									textures.get("robot1_body2"),
									textures.get("robot1_body3"),
									textures.get("robot1_arm"),
									textures.get("robot1_head"),
								    textures.get("robot1_eye"),
									textures.get("robot1_appendage")};
		robot1 = new Robot1(gl, camera, lights, robot1Textures);

		Texture[] robot2Textures = {textures.get("robot2_body"),
									textures.get("robot2_eye"),
									textures.get("robot2_antenna"),
									textures.get("robot2_casing")};
		robot2 = new Robot2(gl, camera, lights, robot2Textures);

		skybox = new Skybox(gl, camera, textures.get("skybox"), textures.get("skybox_moving"));
	}
	
	/**
	 * Draw each object in the scene.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void render(GL3 gl) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		lights[0].render(gl);
		room.render(gl);
		globe.render(gl);
		robot1.render(gl, robot2.getPosition());
		robot2.render(gl);
		skybox.render(gl);
	}

	// ***************************************************
	/* TIME */ 
	
	private double startTime;
	
	/**
	 * Get the current time in seconds.
	 * 
	 * @return The current time in seconds in a double format.
	 */
	private double getSeconds() {
		return System.currentTimeMillis()/1000.0;
	}
}