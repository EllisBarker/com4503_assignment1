import gmaths.*;
import com.jogamp.opengl.*;

/**
 * Class for representing a transformation (scaling, rotating, translating, etc.) within a scene
 * graph and updates children nodes appropriately.
 */
public class TransformNode extends SGNode {
	private Mat4 transform;

	/**
	 * Constructor. Set the name and transformation (as a matrix) for the node.
	 * 
	 * @param name The name of the node.
	 * @param t The transformation (scale/rotation/translation) as a matrix.
	 */
	public TransformNode(String name, Mat4 t) {
		super(name);
		transform = new Mat4(t);
	}

	/**
	 * Set a new value for the transformation matrix.
	 * 
	 * @param m The transformation matrix.
	 */
	public void setTransform(Mat4 m) {
		transform = new Mat4(m);
	}
	
	/**
	 * Merge the world transformation matrix with the node's transform matrix and apply the result
	 * to all children nodes.
	 * 
	 * @param t The world transformation matrix of the parent node.
	 */
	protected void update(Mat4 t) {
		worldTransform = t;
		t = Mat4.multiply(worldTransform, transform);
		for (int i=0; i<children.size(); i++) {
			children.get(i).update(t);
		}   
	}

	/**
	 * Display information surrounding this transformation node to the user.
	 * 
	 * @param indent The quantity of indents applied to show the hierarchy of the scene graph.
	 * @param inFull Variable that determines whether or not the world transformation is shown in
	                 the output.
	 */
	public void print(int indent, boolean inFull) {
		System.out.println(getIndentString(indent)+"Name: "+name);
		if (inFull) {
			System.out.println("worldTransform");
			System.out.println(worldTransform);
			System.out.println("transform node:");
			System.out.println(transform);
		}
		for (int i=0; i<children.size(); i++) {
			children.get(i).print(indent+1, inFull);
		}
	}
}