import gmaths.*;
import java.awt.event.*;
import com.jogamp.opengl.awt.GLCanvas;

/**
 * Class for camera-related functionality (editing angles, target, position, parsing user inputs,
 * etc.).
 */
public class Camera {
	public enum CameraType {X, Z};
	public enum Movement {NO_MOVEMENT, LEFT, RIGHT, UP, DOWN, FORWARD, BACK};
	
	private static final float DEFAULT_RADIUS = 25;
	public static final Vec3 DEFAULT_POSITION = new Vec3(0,0,25);
	public static final Vec3 DEFAULT_POSITION_2 = new Vec3(25,0,0);
	public static final Vec3 DEFAULT_TARGET = new Vec3(0,0,0);
	public static final Vec3 DEFAULT_UP = new Vec3(0,1,0);

	public final float YAW = -90f;
	public final float PITCH = 0f;
	public final float KEYBOARD_SPEED = 0.2f;
	public final float MOUSE_SPEED = 1.0f;
	
	private Vec3 position;
	private Vec3 target;
	private Vec3 up;
	private Vec3 worldUp;
	private Vec3 front;
	private Vec3 right;
	
	private float yaw;
	private float pitch;
	
	private Mat4 perspective;

	/**
	 * Constructor. Creates a camera object with a position vector, target vector and an up vector.
	 * 
	 * @param position The initial position of the camera in the world.
	 * @param target The point at which the camera is initially looking at in the world.
	 * @param up The direction vector for up for the camera.
	 */
	public Camera(Vec3 position, Vec3 target, Vec3 up) {
		setupCamera(position, target, up);
	}
	
	/**
	 * Prepares the camera's position and target by normalising vectors and initialising position 
	 * internally.
	 * 
	 * @param position The position of the camera in the world.
	 * @param target The point at which the camera is looking at in the world.
	 * @param up The direction vector for up for the camera.
	 */
	private void setupCamera(Vec3 position, Vec3 target, Vec3 up) {
		this.position = new Vec3(position);
		this.target = new Vec3(target);
		this.up = new Vec3(up);
		front = Vec3.subtract(target, position);
		front.normalize();
		up.normalize();
		calculateYawPitch(front);
		worldUp = new Vec3(up);
		updateCameraVectors();
	}
	
	/**
	 * Get the current position of the camera object in the world.
	 * 
	 * @return The current position of the camera in vector format.
	 */
	public Vec3 getPosition() {
		return new Vec3(position);
	}
	
	/**
	 * Set the current position of the camera object in the world to a new position.
	 * 
	 * @param p The new position in the world to set the camera object to.
	 */
	public void setPosition(Vec3 p) {
		setupCamera(p, target, up);
	}
	
	/**
	 * Set the current target of the camera object in the world to a new target.
	 * 
	 * @param t The new target in the world that the camera is pointed at.
	 */
	public void setTarget(Vec3 t) {
		setupCamera(position, t, up);
	}
	
	/**
	 * Edit the position, target and up vector of the camera based on predefined positions 
	 * alongside the X or Z axes.
	 * 
	 * @param c The type of camera (X or Z) that determines the camera's orientation.
	 */
	public void setCamera(CameraType c) {
		switch (c) {
			case X : setupCamera(DEFAULT_POSITION, DEFAULT_TARGET, DEFAULT_UP) ; break;
			case Z : setupCamera(DEFAULT_POSITION_2, DEFAULT_TARGET, DEFAULT_UP); break;
		}
	}

	/**
 	 * Calculate and store the yaw and pitch angles of the camera.
	 *
	 * @param v The direction of the camera as a vector.
 	 */
	private void calculateYawPitch(Vec3 v) {
		yaw = (float)Math.atan2(v.z,v.x);
		pitch = (float)Math.asin(v.y);
	}

	/**
	 * Get the current view of the camera in a matrix format.
	 * 
	 * @return The view of the camera.
	 */
	public Mat4 getViewMatrix() {
		target = Vec3.add(position, front);
		return Mat4Transform.lookAt(position, target, up);
	}
	
	/**
	 * Set the perspective of the camera.
	 * 
	 * @param m The new perspective of the camera as a matrix.
	 */
	public void setPerspectiveMatrix(Mat4 m) {
		perspective = m;
	}
	
	/**
	 * Get the current perspective of the camera.
	 * 
	 * @return The perspective of the camera.
	 */
	public Mat4 getPerspectiveMatrix() {
		return perspective;
	}

	/**
	 * Change the position of the camera based on the user's inputs via the arrow keys, A key and
	 * Z key.
	 * 
	 * @param movement The type of movement associated with the user's key press.
	 */
	public void keyboardInput(Movement movement) {
		switch (movement) {
			case NO_MOVEMENT: break;
			case LEFT: position.add(Vec3.multiply(right, -KEYBOARD_SPEED)); break;
			case RIGHT: position.add(Vec3.multiply(right, KEYBOARD_SPEED)); break;
			case UP: position.add(Vec3.multiply(up, KEYBOARD_SPEED)); break;
			case DOWN: position.add(Vec3.multiply(up, -KEYBOARD_SPEED)); break;
			case FORWARD: position.add(Vec3.multiply(front, KEYBOARD_SPEED)); break;
			case BACK: position.add(Vec3.multiply(front, -KEYBOARD_SPEED)); break;
		}
	}
	
	/**
	 * Edit the camera's yaw and pitch, whilst ensuring pitch does not reach 90 or -90 degrees.
	 * 
	 * @param y The new yaw angle (in degrees).
	 * @param p The new pitch angle (in degrees). 
	 */
	public void updateYawPitch(float y, float p) {
		yaw += y;
		pitch += p;
		if (pitch > 89) pitch = 89;
		else if (pitch < -89) pitch = -89;
		updateFront();
		updateCameraVectors();
	}
	
	/**
	 * Edit the front vector of the camera based on the yaw and pitch angles of the camera.
	 */
	private void updateFront() {
		double cy, cp, sy, sp;
		cy = Math.cos(yaw);
		sy = Math.sin(yaw);
		cp = Math.cos(pitch);
		sp = Math.sin(pitch);
		front.x = (float)(cy*cp);
		front.y = (float)(sp);
		front.z = (float)(sy*cp);
		front.normalize();
		target = Vec3.add(position,front);
	}
	
	/**
	 * Maintain the camera's orientation by maintaining the right and up vectors in accordance
	 * with the world's up vector.
	 */
	private void updateCameraVectors() {  
		right = Vec3.crossProduct(front, worldUp);
		right.normalize();
		up = Vec3.crossProduct(right, front);
		up.normalize();
	}
}