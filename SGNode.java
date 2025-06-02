import gmaths.*;
import java.util.ArrayList;
import com.jogamp.opengl.*;

/**
 * Class for a node within the scene graph to store its children and associated transforms.
 */
public class SGNode {
	protected String name;
	protected ArrayList<SGNode> children;
	protected Mat4 worldTransform;

	/**
	 * Constructor. Initialise the node's children and set its name.
	 * 
	 * @param name The name of the node.
	 */
	public SGNode(String name) {
		children = new ArrayList<SGNode>();
		this.name = name;
		worldTransform = new Mat4(1);
	}

	/**
	 * Add a node to be one of the children of this scene graph node.
	 * 
	 * @param child The scene graph that acts a child to this node.
	 */
	public void addChild(SGNode child) {
		children.add(child);
	}
	
	/**
	 * Update the world transform for this node and its children.
	 */
	public void update() {
		update(worldTransform);
	}
	
	/**
	 * Update the world transform for this node and its children recursively.
	 * 
	 * @param t The transformation to apply to all child nodes.
	 */
	protected void update(Mat4 t) {
		worldTransform = t;
		for (int i=0; i<children.size(); i++) {
			children.get(i).update(t);
		}
	}

	/**
	 * Provide a indented string for formatting purposes when the node is printed.
	 * 
	 * @param indent The quantity of indents applied to the string.
	 * @return Whitespace used for indenting the node's information.
	 */
	protected String getIndentString(int indent) {
		String s = ""+indent+" ";
		for (int i=0; i<indent; ++i) {
			s+="  ";
		}
		return s;
	}
	
	/**
	 * Display information surrounding this scene graph node to the user.
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
		}
		for (int i=0; i<children.size(); i++) {
			children.get(i).print(indent+1, inFull);
		}
	}
	
	/**
	 * Draw the node and is children.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
	public void draw(GL3 gl) {
		for (int i=0; i<children.size(); i++) {
			children.get(i).draw(gl);
		}
	}
}