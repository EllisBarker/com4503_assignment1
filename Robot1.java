import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Class for storing relevant information for the rendering and creation of robot 1 (the one that
 * dances) as well as its maintenance in terms of scene graph updates.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - A new class for the creation and updating of robot 1
 */
public class Robot1 {
    private ModelMultipleLights[] robot;
    private Camera camera;
	private Light[] lights;
    private Texture baseTexture, bodyTexture1, bodyTexture2, bodyTexture3, armTexture, headTexture, 
					eyeTexture, appendageTexture;
	private int noObjects = 12;

	// Attributes surrounding nodes in the scene graph and the values that get updated over time.
    private SGNode robotGraph;
	private TransformNode translateBase, rotateBase, rotateBodyPiece1, rotateBodyPiece2, 
						  rotateBodyPiece3, rotateArm1, rotateArm2, scaleHead;
	private float translateToPositionDistance = -2.0f;
	private float translateBaseDistanceStart = 0, translateBaseDistance = translateBaseDistanceStart;
	private float rotateBaseAngleStart = 90, rotateBaseAngle = rotateBaseAngleStart;
	private float rotateBodyPiece1AngleStart = 30, rotateBodyPiece1Angle = rotateBodyPiece1AngleStart;
	private float rotateBodyPiece2AngleStart = -30, rotateBodyPiece2Angle = rotateBodyPiece2AngleStart;
	private float rotateBodyPiece3AngleStart = 45, rotateBodyPiece3Angle = rotateBodyPiece3AngleStart;
    private float rotateArm1AngleStart = 60, rotateArm1Angle = rotateArm1AngleStart;
    private float rotateArm2AngleStart = -60, rotateArm2Angle = rotateArm2AngleStart;
    private float scaleHeadSizeStart = 1, scaleHeadSize = scaleHeadSizeStart;
	private double startTime;

	// Attributes for controlling the robot's dancing (distance threshold, user input, etc.)
	private boolean robotDancingManual = false;
	private boolean robotDancingProximity = true;
	private boolean timeStorageNeeded = true;
	private double timePaused;
	private float distanceThreshold = 8.0f;

	/**
	 * Constructor. Create the models for the robot, set individual textures for each one, and
	 * generate the scene graph.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param c The camera object in the world.
	 * @param l The light sources in the world (as an array).
	 * @param t The array of all textures to be used for this specific object.
	 */
    public Robot1(GL3 gl, Camera c, Light[] l, Texture[] t) {
		camera = c;
		lights = l;
		this.baseTexture = t[0];
		this.bodyTexture1 = t[1];
		this.bodyTexture2 = t[2];
		this.bodyTexture3 = t[3];
		this.armTexture = t[4];
		this.headTexture = t[5];
		this.eyeTexture = t[6];
		this.appendageTexture = t[7];
		robot = new ModelMultipleLights[noObjects];
		Shader shaderTexture = new Shader(
			gl, "assets/shaders/vs_standard.txt", "assets/shaders/fs_standard_m_1t.txt");
		Material material = new Material(
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.5f, 0.5f, 0.5f), 
			new Vec3(0.3f, 0.3f, 0.3f), 
			4.0f);
		Mat4 modelMatrix = new Mat4(1);

		robot[0] = ModelMaker.makePart(gl,
									   "base", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {baseTexture},
									   lights,
									   camera,
									   "sphere");

		robot[1] = ModelMaker.makePart(gl,
									   "body1", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {bodyTexture1},
									   lights,
									   camera,
									   "sphere");

		robot[2] = ModelMaker.makePart(gl,
									   "body2", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {bodyTexture2},
									   lights,
									   camera,
									   "sphere");

		robot[3] = ModelMaker.makePart(gl,
									   "body3", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {bodyTexture3},
									   lights,
									   camera,
									   "sphere");

		robot[4] = ModelMaker.makePart(gl,
									   "arm1", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {armTexture},
									   lights,
									   camera,
									   "sphere");

		robot[5] = ModelMaker.makePart(gl,
									   "arm2", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {armTexture},
									   lights,
									   camera,
									   "sphere");

		robot[6] = ModelMaker.makePart(gl,
									   "head", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {headTexture},
									   lights,
									   camera,
									   "sphere");

		robot[7] = ModelMaker.makePart(gl,
									   "eye1", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {eyeTexture},
									   lights,
									   camera,
									   "sphere");

		robot[8] = ModelMaker.makePart(gl,
									   "appendage1", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {appendageTexture},
									   lights,
									   camera,
									   "sphere");

		robot[9] = ModelMaker.makePart(gl,
									   "appendage2", 
									   material,
									   modelMatrix,
									   shaderTexture,
									   new Texture[] {appendageTexture},
									   lights,
									   camera,
									   "sphere");

		robot[10] = ModelMaker.makePart(gl,
										"appendage3", 
										material,
										modelMatrix,
										shaderTexture,
										new Texture[] {appendageTexture},
										lights,
										camera,
										"sphere");

		robot[11] = ModelMaker.makePart(gl,
										"eye2", 
										material,
										modelMatrix,
										shaderTexture,
										new Texture[] {eyeTexture},
										lights,
										camera,
										"sphere");

        startTime = getSeconds();
		createRobotSceneGraph();
    }

	/**
	 * Render robot 1 in the world and update its transforms depending on its proximity/user 
	 * input.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param robot2Position The current position of robot 2 in a x,y,z format to decide whether
	                         robot 1 should dance or not.
	 */
	public void render(GL3 gl, float[] robot2Position) {
		if (robotDancingProximity) {
			if (timePaused != 0.0) {
				/* Determine new start time for once the robot starts dancing again 
				   (based on how long the robot has been paused). */
				startTime += getSeconds() - timePaused;
				timePaused = 0.0;
			}
			timeStorageNeeded = true;
			updateTransforms();
		}
		else if (!robotDancingProximity) {
			// Store the time at which the robot first stopping moving
			if (timeStorageNeeded)
				timePaused = getSeconds();
				timeStorageNeeded = false;
		}
		
		// Only need to check for proximity if manual dance option is not being used
		if (!robotDancingManual) {
			if (calculateDistanceBetweenRobots(robot2Position) < distanceThreshold)
				robotDancingProximity = true;
			else
				robotDancingProximity = false;
		}

		robotGraph.draw(gl);
	}

	/**
	 * Discard robot 1 by disposing of each individual model and the resources they use.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void dispose(GL3 gl) {
		for (int i=0; i<noObjects; i++) {
		    robot[i].dispose(gl);
		}
	}

    // ***************************************************
	/* SCENE GRAPH */

	/**
	 * Initialise the scene graph of the object by connecting each component hierarchically.
	 */
	private void createRobotSceneGraph () {
		robotGraph = new NameNode("robot1");

		float baseY = 0.1f;
		float baseXZ = 1.5f;
		float bodyY = 1.25f;
		float bodyXZ = 0.5f;
		float headSize = 0.5f;
		float eyeSize = 0.25f;
		float appendageSize = 0.5f;

		SGNode baseBranch = ModelMaker.makeBranch(
			robot[0], "robot 1 base", baseXZ, baseY, baseXZ, 0, 0, 0);
        SGNode body1Branch = ModelMaker.makeBranch(
			robot[1], "robot 1 body piece 1", bodyXZ, bodyY, bodyXZ, rotateBodyPiece1Angle, 0, 0);
        SGNode body2Branch = ModelMaker.makeBranch(
			robot[2], "robot 1 body piece 2", bodyXZ, bodyY, bodyXZ, rotateBodyPiece2Angle, 0, 0);
		SGNode body3Branch = ModelMaker.makeBranch(
			robot[3], "robot 1 body piece 3", bodyXZ, bodyY, bodyXZ, 0, 0, 0);
		SGNode arm1Branch = ModelMaker.makeBranch(
			robot[4], "robot 1 arm 1", bodyXZ*0.75f, bodyY, bodyXZ*0.75f, 0, 0, rotateArm1Angle);
		SGNode arm2Branch = ModelMaker.makeBranch(
			robot[5], "robot 1 arm 2", bodyXZ*0.75f, bodyY, bodyXZ*0.75f, 0, 0, rotateArm2Angle);
		SGNode headBranch = ModelMaker.makeBranch(
			robot[6], "robot 1 head", headSize, headSize, headSize, 0, 0, 0);
		SGNode eye1Branch = ModelMaker.makeBranch(
			robot[7], "robot 1 eye 1", eyeSize, eyeSize, eyeSize, 0, 180, 0);
		SGNode appendage1Branch = ModelMaker.makeBranch(
			robot[8], "robot 1 appendage 1", appendageSize/2, appendageSize, appendageSize, 0, 0, 0);
		SGNode appendage2Branch = ModelMaker.makeBranch(
			robot[9], "robot 1 appendage 2", appendageSize/2, appendageSize, appendageSize, 0, 0, 0);
		SGNode appendage3Branch = ModelMaker.makeBranch(
			robot[10], "robot 1 appendage 3", appendageSize, appendageSize, appendageSize, 0, 0, 0);
		SGNode eye2Branch = ModelMaker.makeBranch(
			robot[11], "robot 1 eye 2", eyeSize, eyeSize, eyeSize, 0, 180, 0);
	
		TransformNode translateToPosition = new TransformNode(
			"translate robot 1 to position in room", 
			Mat4Transform.translate(translateToPositionDistance,0,translateToPositionDistance));
		TransformNode translateToTopBase = new TransformNode(
			"translate to top of robot 1's base", 
			Mat4Transform.translate(0,baseY,0));
		TransformNode translateToTopBody1 = new TransformNode(
			"translate to top of robot 1's body piece 1", 
			Mat4Transform.translate(0,
									(float)Math.cos(Math.toRadians(rotateBodyPiece1Angle))*bodyY,
									(float)Math.sin(Math.toRadians(rotateBodyPiece1Angle))*bodyY));
		TransformNode translateToTopBody2 = new TransformNode(
			"translate to top of robot 1's body piece 2", 
			Mat4Transform.translate(0,
									(float)Math.cos(Math.toRadians(rotateBodyPiece1Angle))*bodyY,
									-(float)Math.sin(Math.toRadians(rotateBodyPiece1Angle))*bodyY));
		TransformNode translateToMiddleBody1 = new TransformNode(
			"translate to middle of robot 1's body (1)", 
			Mat4Transform.translate(-bodyXZ/2.0f, bodyY/2.0f, 0));
		TransformNode translateToMiddleBody2 = new TransformNode(
			"translate to middle of robot 1's body (2)", 
			Mat4Transform.translate(bodyXZ/2.0f, bodyY/2.0f, 0));
		TransformNode translateToTopBody3 = new TransformNode(
			"translate to top of robot 1's body piece 3", 
			Mat4Transform.translate(0,bodyY,0));
		TransformNode translateToFrontHead = new TransformNode(
			"translate to front of robot 1's head", 
			Mat4Transform.translate(0,headSize/4.0f,headSize/3.0f));
		TransformNode translateToHeadSide1 = new TransformNode(
			"translate to side of robot 1's head (1)", 
			Mat4Transform.translate(headSize/2.0f,headSize/2.0f,0));
		TransformNode translateToHeadSide2 = new TransformNode(
			"translate to side of robot 1's head (2)", 
			Mat4Transform.translate(-headSize/2.0f,headSize/2.0f,0));
		TransformNode translateToHeadTop = new TransformNode(
			"translate to top of robot 1's head", 
			Mat4Transform.translate(0,headSize,0));
		TransformNode translateToFrontAppendage = new TransformNode(
			"translate to front of robot 1's appendage 3", 
			Mat4Transform.translate(0, headSize/4.0f,headSize/3.0f));
		translateBase = new TransformNode(
			"translate robot 1 up and down", 
			Mat4Transform.translate(0,translateBaseDistanceStart,0));
		rotateBase = new TransformNode(
			"rotate robot 1's base around y axis", 
			Mat4Transform.rotateAroundY(rotateBaseAngleStart));
		rotateBodyPiece1 = new TransformNode(
			"rotate robot 1's body piece 1 around x axis", 
			Mat4Transform.rotateAroundX(rotateBodyPiece1AngleStart));
		rotateBodyPiece2 = new TransformNode(
			"rotate robot 1's body piece 2 around z axis", 
			Mat4Transform.rotateAroundZ(rotateBodyPiece2AngleStart));
		rotateBodyPiece3 = new TransformNode(
			"rotate robot 1's body piece 3 around y axis", 
			Mat4Transform.rotateAroundY(rotateBodyPiece3AngleStart));
		rotateArm1 = new TransformNode(
			"rotate robot 1's arm 1 around x axis", 
			Mat4Transform.rotateAroundX(rotateArm1AngleStart));
		rotateArm2 = new TransformNode(
			"rotate robot 1's arm 2 around x axis", 
			Mat4Transform.rotateAroundX(rotateArm2AngleStart));
		scaleHead = new TransformNode(
			"scale robot 1's head", 
			Mat4Transform.scale(scaleHeadSizeStart, scaleHeadSizeStart, scaleHeadSizeStart));
		
		// Scene graph (base to body)
		robotGraph.addChild(translateToPosition);
			translateToPosition.addChild(translateBase);
				translateBase.addChild(rotateBase);
					rotateBase.addChild(baseBranch);
						baseBranch.addChild(translateToTopBase);
							translateToTopBase.addChild(rotateBodyPiece1);
								rotateBodyPiece1.addChild(body1Branch);
									body1Branch.addChild(translateToTopBody1);
										translateToTopBody1.addChild(rotateBodyPiece2);
											rotateBodyPiece2.addChild(body2Branch);
												body2Branch.addChild(translateToTopBody2);
													translateToTopBody2.addChild(rotateBodyPiece3);
														rotateBodyPiece3.addChild(body3Branch);

		// Scene graph (body to arms/head)
		body3Branch.addChild(translateToMiddleBody1);
			translateToMiddleBody1.addChild(rotateArm1);
				rotateArm1.addChild(arm1Branch);
		body3Branch.addChild(translateToMiddleBody2);
			translateToMiddleBody2.addChild(rotateArm2);
				rotateArm2.addChild(arm2Branch);
		body3Branch.addChild(translateToTopBody3);
			translateToTopBody3.addChild(scaleHead);
				scaleHead.addChild(headBranch);
					headBranch.addChild(translateToFrontHead);
						translateToFrontHead.addChild(eye1Branch);
					headBranch.addChild(translateToHeadSide1);
						translateToHeadSide1.addChild(appendage1Branch);
					headBranch.addChild(translateToHeadSide2);
						translateToHeadSide2.addChild(appendage2Branch);
					headBranch.addChild(translateToHeadTop);
						translateToHeadTop.addChild(appendage3Branch);
							appendage3Branch.addChild(translateToFrontAppendage);
								translateToFrontAppendage.addChild(eye2Branch);

		robotGraph.update();
	}

	/**
	 * Update the angles of each model, scale of the head, and position of the base based on the 
	 * current time.
	 */
	private void updateTransforms() {
		double elapsedTime = getSeconds()-startTime;
		translateBaseDistance = translateBaseDistanceStart + (float)(Math.abs(Math.sin(elapsedTime*3.0f)));
		rotateBaseAngle = rotateBaseAngleStart*(float)elapsedTime;
		rotateBodyPiece1Angle = rotateBodyPiece1AngleStart*(float)Math.sin(elapsedTime)/2.0f;
		rotateBodyPiece2Angle = rotateBodyPiece2AngleStart*(float)Math.cos(elapsedTime)/2.0f;
		rotateBodyPiece3Angle = rotateBodyPiece3AngleStart*(float)elapsedTime;
		rotateArm1Angle = rotateArm1AngleStart*(float)elapsedTime*3.0f;
		rotateArm2Angle = rotateArm2AngleStart*(float)elapsedTime*3.0f;
		scaleHeadSize = 2.0f+scaleHeadSizeStart*(float)Math.sin(elapsedTime);

		translateBase.setTransform(Mat4Transform.translate(0,translateBaseDistance,0));
		rotateBase.setTransform(Mat4Transform.rotateAroundY(rotateBaseAngle));
		rotateBodyPiece1.setTransform(Mat4Transform.rotateAroundX(rotateBodyPiece1Angle));
		rotateBodyPiece2.setTransform(Mat4Transform.rotateAroundZ(rotateBodyPiece2Angle));
		rotateBodyPiece3.setTransform(Mat4Transform.rotateAroundY(rotateBodyPiece3Angle));
		rotateArm1.setTransform(Mat4Transform.rotateAroundX(rotateArm1Angle));
		rotateArm2.setTransform(Mat4Transform.rotateAroundX(rotateArm2Angle));
		scaleHead.setTransform(Mat4Transform.scale(scaleHeadSize,scaleHeadSize,scaleHeadSize));
		robotGraph.update();
	}

	/**
	 * Set whether or not the robot's dancing is caused by robot 2's proximity or the user's input.
	 */
	public void setRobotDancing() {
		// Manually flip the dancing state of the robot
		robotDancingManual = !robotDancingManual;
		if (robotDancingManual)
			robotDancingProximity = !robotDancingProximity;
	}

	/**
	 * Set the distance threshold for robot 2 to cause robot 1 to dance to a new value.
	 * 
	 * @param distance The new value for the distance threshold.
	 */
	public void setDistanceThreshold(float distance) {
		distanceThreshold = distance;
	}

	/**
	 * Calculate the Euclidean distance between the two robots based on their positions in the
	 * world.
	 * 
	 * @param robot2Position The current position of robot 2 in a x,y,z format to decide whether
	                         robot 1 should dance or not.
	 * @return The Euclidean distance between the two robots.
	 */
	private float calculateDistanceBetweenRobots(float[] robot2Position) {
		return ((float)Math.sqrt(Math.pow(translateToPositionDistance - robot2Position[0], 2) + 
								 Math.pow(translateToPositionDistance - robot2Position[1], 2) +
		                         Math.pow(translateToPositionDistance - robot2Position[2], 2)));
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