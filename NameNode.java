import gmaths.*;
import com.jogamp.opengl.*;

/**
 * Class for representing a name as a node in a scene graph.
 */
public class NameNode extends SGNode {
	/**
	 * Constructor. Sets the name for the the node.
	 * 
	 * @param name Name for the node.
	 */
	public NameNode(String name) {
		super(name);
	}
}