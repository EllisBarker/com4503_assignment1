import com.jogamp.opengl.*;

/**
 * Class for representing a model object within a scene graph.
 */
public class ModelNode extends SGNode {
	protected ModelMultipleLights model;

	/**
	 * Constructor. Initialises and sets the model for the node.
	 * 
	 * @param name The name of the node.
	 * @param m The model object.
	 */
	public ModelNode(String name, ModelMultipleLights m) {
		super(name);
		model = m; 
	}

	/**
	 * Render the model as well as children nodes of the current node.
	 * 
	 * @param gl The OpenGL context used for rendering.
	 */
    public void draw(GL3 gl) {
		model.render(gl, worldTransform);
		for (int i=0; i<children.size(); i++) {
			children.get(i).draw(gl);
		}
    }
}