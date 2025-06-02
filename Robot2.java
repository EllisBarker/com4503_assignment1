import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.*;
import com.jogamp.opengl.util.glsl.*;
import com.jogamp.opengl.util.texture.*;

/**
 * Class for storing relevant information for the rendering and creation of robot 2 (the one that
 * moves along the lines on the floor) as well as its maintenance in terms of scene graph updates.
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - A new class for creating/updating robot 2
 */
public class Robot2 {
    private ModelMultipleLights[] robot;
    private Camera camera;
	private Light[] lights;
    private Texture bodyTexture, eyeTexture, antennaTexture, casingTexture;
    private int noObjects = 5;

    // Attributes relating to robot 2's movement
    private float trackLength = 12.8f;
    private float preTurnAngle = 360;
    private float moveSpeed = 0.05f;
    private float rotateSpeed = 2.0f;
    private boolean turning = false;
    private boolean movingX = false;
    private boolean movingZ = true;
    private boolean negativeMove = false;

    // Attributes relating to updating values in the scene graph of robot 2
    private SGNode robotGraph;
    private TransformNode translateAll, rotateAll, rotateCasing;
    private float translateAllDistanceXStart = trackLength/2, translateAllDistanceX = translateAllDistanceXStart;
    private float translateAllDistanceYStart = 0, translateAllDistanceY = translateAllDistanceYStart;
    private float translateAllDistanceZStart = 0, translateAllDistanceZ = translateAllDistanceZStart;
    private float rotateAllAngleStart = 360, rotateAllAngle = rotateAllAngleStart;
    private float rotateCasingAngleStart = 0, rotateCasingAngle = rotateCasingAngleStart;
    private double startTime;

    // Attributes denoting the size of certain models in robot 2
    private float bodySize = 1.0f;
    private float antennaSize = 2.5f;
    private float casingSize = 0.75f;

	/**
	 * Constructor. Create the models for the robot, set individual textures for each one, and
	 * generate the scene graph.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 * @param c The camera object in the world.
	 * @param l The light sources in the world (as an array).
	 * @param t The array of all textures to be used for this specific object.
	 */
    public Robot2(GL3 gl, Camera c, Light[] l, Texture[] t) {
        camera = c;
		lights = l;
		this.bodyTexture = t[0];
		this.eyeTexture = t[1];
		this.antennaTexture = t[2];
        this.casingTexture = t[3];
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
						               "body", 
						               material,
						               modelMatrix,
						               shaderTexture,
						               new Texture[] {bodyTexture},
                                       lights,
                                       camera,
                                       "cube");
        
        robot[1] = ModelMaker.makePart(gl,
						               "eye1", 
						               material,
						               modelMatrix,
						               shaderTexture,
						               new Texture[] {eyeTexture},
                                       lights,
                                       camera,
                                       "sphere");

        robot[2] = ModelMaker.makePart(gl,
						               "eye2", 
						               material,
						               modelMatrix,
						               shaderTexture,
						               new Texture[] {eyeTexture},
                                       lights,
                                       camera,
                                       "sphere");
        
        robot[3] = ModelMaker.makePart(gl,
						               "antenna", 
						               material,
						               modelMatrix,
						               shaderTexture,
						               new Texture[] {antennaTexture},
                                       lights,
                                       camera,
                                       "sphere");

        robot[4] = ModelMaker.makePart(gl,
						               "casing", 
						               material,
						               modelMatrix,
						               shaderTexture,
						               new Texture[] {casingTexture},
                                       lights,
                                       camera,
                                       "sphere");

        startTime = getSeconds();
        createRobotSceneGraph();
    }

	/**
	 * Render robot 2 in the world and update its transforms over time (as well as the spotlight
     * seen on top of its antenna).
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
    public void render(GL3 gl) {
        updateTransforms();
        updateSpotlight(gl);
        robotGraph.draw(gl);
    }

	/**
	 * Discard robot 2 by disposing of each individual model and the resources they use.
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
    	robotGraph = new NameNode("robot2");
        float eyeSize = 0.25f;

        SGNode bodyBranch = ModelMaker.makeBranch(
            robot[0], "robot 2 body", bodySize, bodySize, bodySize, 0, 0, 0);
        SGNode eye1Branch = ModelMaker.makeBranch(
            robot[1], "robot 2 eye 1", eyeSize, eyeSize, eyeSize, 0, 180, 0);
        SGNode eye2Branch = ModelMaker.makeBranch(
            robot[2], "robot 2 eye 2", eyeSize, eyeSize, eyeSize, 0, 180, 0);
        SGNode antennaBranch = ModelMaker.makeBranch(
            robot[3], "robot 2 antenna", antennaSize/8, antennaSize, antennaSize/8, 0, 0, 0);
        SGNode casingBranch = ModelMaker.makeBranch(
            robot[4], "robot 2 casing", casingSize, casingSize, casingSize, 0, 0, 0);

        TransformNode translateToEyePosition1 = new TransformNode(
            "translate to front of robot 2's body (eye position 1)", 
            Mat4Transform.translate(-bodySize/4, bodySize/2, bodySize/2));
        TransformNode translateToEyePosition2 = new TransformNode(
            "translate to front of robot 2's body (eye position 2)", 
            Mat4Transform.translate(bodySize/4, bodySize/2, bodySize/2));
        TransformNode translateToTopBody = new TransformNode(
            "translate to top of robot 2's body", 
            Mat4Transform.translate(0,bodySize,0));
        TransformNode translateToTopAntenna = new TransformNode(
            "translate to top of robot 2's antenna", 
            Mat4Transform.translate(0,antennaSize+bodySize,0));
        translateAll = new TransformNode(
            "translate robot 2 to position in room", 
            Mat4Transform.translate(translateAllDistanceXStart,
                                    translateAllDistanceYStart,
                                    translateAllDistanceZStart));
        rotateAll = new TransformNode(
            "rotate robot 2 around y axis", 
            Mat4Transform.rotateAroundY(rotateAllAngleStart));
        rotateCasing = new TransformNode(
            "rotate robot 2's spotlight casing around y axis", 
            Mat4Transform.rotateAroundY(rotateAllAngleStart));

        robotGraph.addChild(translateAll);
            translateAll.addChild(rotateAll);
                rotateAll.addChild(bodyBranch);
                    bodyBranch.addChild(translateToEyePosition1);
                        translateToEyePosition1.addChild(eye1Branch);
                    bodyBranch.addChild(translateToEyePosition2);
                        translateToEyePosition2.addChild(eye2Branch);
                    bodyBranch.addChild(translateToTopBody);
                        translateToTopBody.addChild(antennaBranch);
            translateAll.addChild(rotateCasing);
                rotateCasing.addChild(translateToTopAntenna);
                    translateToTopAntenna.addChild(casingBranch);

        robotGraph.update();
    }

    /**
     * Update the position of the robot over time (and account for user interaction causing the 
     * robot to stop moving or turning).
     */
    private void updateTransforms() {
		double elapsedTime = getSeconds()-startTime;

        // Determine the new position of the robot if it is moving and not rotating
        if (!turning) {
            if (movingZ) {
                if (negativeMove)
                    translateAllDistanceZ -= moveSpeed;
                else
                    translateAllDistanceZ += moveSpeed;
            }
            else if (movingX) {
                if (negativeMove)
                    translateAllDistanceX -= moveSpeed;
                else
                    translateAllDistanceX += moveSpeed;
            }
        }

        // Robot exceeds path to follow in Z direction and needs to change direction
        if (((translateAllDistanceZ > trackLength/2) || 
            (translateAllDistanceZ < -trackLength/2)) &&
            (movingZ)) {
            movingZ = !movingZ;
            movingX = !movingX;
            turning = !turning;
            if (translateAllDistanceZ > trackLength/2) {
                negativeMove = true;
                // Slightly pull the robot back into position (course correction)
                translateAllDistanceZ = (trackLength/2) - moveSpeed;
            }
            else if (translateAllDistanceZ < -trackLength/2) {
                negativeMove = false;
                // Slightly pull the robot back into position (course correction)
                translateAllDistanceZ = (-trackLength/2) + moveSpeed;
            }
        }

        // Robot exceeds path to follow in X direction and needs to change direction
        else if (((translateAllDistanceX > trackLength/2) || 
            (translateAllDistanceX < -trackLength/2)) &&
            (movingX)) {
            movingX = !movingX;
            movingZ = !movingZ;
            turning = !turning;
            if (translateAllDistanceX > trackLength/2) {
                negativeMove = false;
                // Slightly pull the robot back into position (course correction)
                translateAllDistanceX = (trackLength/2) - moveSpeed;
            }
            else if (translateAllDistanceX < -trackLength/2){
                negativeMove = true;
                // Slightly pull the robot back into position (course correction)
                translateAllDistanceX = (-trackLength/2) + moveSpeed;
            }
        }

        /* State where robot is not being translated in x or z direction.
           It is only being rotated 90 degrees and making one complete jump 
           before it starts following the path again */
        if (turning) {
            translateAllDistanceY = 2*Math.abs((float)Math.sin(Math.toRadians(rotateAllAngle)*2));
            rotateAllAngle -= rotateSpeed;
            rotateAll.setTransform(Mat4Transform.rotateAroundY(rotateAllAngle));
            if (Math.abs(rotateAllAngle - ((preTurnAngle-90)%360)) <= 0) {
                rotateAllAngle = (preTurnAngle-90)%360;
                preTurnAngle = (preTurnAngle-90)%360;
                turning = !turning;
                translateAllDistanceY = 0;
            }
            if (rotateAllAngle <= 0) {
                rotateAllAngle = 360;
                preTurnAngle = 360;
                translateAllDistanceY = 0;
            }
        }

        if (moveSpeed != 0.0) {
            // Angle matches the direction of the light as it rotates
            rotateCasingAngle = (float)Math.toDegrees(Math.atan2(
                Math.sin(elapsedTime),
                Math.cos(elapsedTime)
            ));
            rotateCasing.setTransform(Mat4Transform.rotateAroundY(rotateCasingAngle));
        }

        translateAll.setTransform(Mat4Transform.translate(
            translateAllDistanceX, 
            translateAllDistanceY, 
            translateAllDistanceZ));
        robotGraph.update();
    }

    /**
     * Set whether or not robot 2 is currently moving via user interaction.
     */
    public void setRobotMoving() {
        // Movement speed is reduced to zero/restored to normal for robot movement and turning
		if (moveSpeed == 0.05f)
            moveSpeed = 0;
        else
            moveSpeed = 0.05f;

        if (rotateSpeed == 2.0f)
            rotateSpeed = 0;
        else
            rotateSpeed = 2.0f;
        // Start time is reset for the rotating spotlight
        startTime = getSeconds()-startTime;
	}

    /**
     * Update the spotlight following the robot to new positions/directions when robot 2 is moving 
     * and subsequently render the spotlight.
     * 
     * @param gl The OpenGL context used for rendering.
     */
    private void updateSpotlight(GL3 gl) {
        double elapsedTime = getSeconds()-startTime;
        // Only update light position and direction if robot 2 is moving
        if (moveSpeed != 0) {
            lights[1].setPosition(
                translateAllDistanceX + 0.25f*(float)Math.sin(elapsedTime), 
                translateAllDistanceY + bodySize + antennaSize + casingSize/3, 
                translateAllDistanceZ + 0.25f*(float)Math.cos(elapsedTime));
            lights[1].setDirection(
                new Vec3(1.5f*(float)Math.sin(elapsedTime), 
                -1.0f, 
                1.5f*(float)Math.cos(elapsedTime)));
        }
        lights[1].render(gl);
    }

    /**
     * Get the current position of robot 2.
     * 
     * @return The position of robot 2 as an array of x,y,z.
     */
    public float[] getPosition() {
        return new float[] {translateAllDistanceX, translateAllDistanceY, translateAllDistanceZ};
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