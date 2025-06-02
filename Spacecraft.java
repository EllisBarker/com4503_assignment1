import gmaths.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Class for creating a GUI window and allowing interaction between the user and elements of the 
 * spacecraft scene (lighting, animation, camera view, etc.).
 * 
 * I declare that this code is my own work.
 * Author: Ellis Barker
 * Email address: ebarker5@sheffield.ac.uk
 * 
 * Changes made:
 * - setUpInteraction(): new function for all UI elements under the canvas
 * - stateChanged(): new function for handling slider elements
 */
public class Spacecraft extends JFrame implements ActionListener, ChangeListener {	
	private static final int WIDTH = 1024;
	private static final int HEIGHT = 768;
	private static final Dimension dimension = new Dimension(WIDTH, HEIGHT);
	private GLCanvas canvas;
	private JPanel interaction;
	private Spacecraft_GLEventListener glEventListener;
	private final FPSAnimator animator; 

	/**
	 * Creates the main window for seeing the newly-made Spacecraft object.
	 * 
	 * @param args Command line arguments (unused; solely here for compiling purposes).
	 */
	public static void main(String[] args) {
		Spacecraft b1 = new Spacecraft("Spacecraft");
		b1.getContentPane().setPreferredSize(dimension);
		b1.pack();
		b1.setVisible(true);
		b1.canvas.requestFocusInWindow();
	}

	/**
	 * Constructor. Sets up individual elements of the GUI (i.e. sliders, buttons, the OpenGL 
	 * canvas, etc.).
	 * 
	 * @param textForTitleBar The name used for the title of the main window.
	 */
	public Spacecraft(String textForTitleBar) {
		super(textForTitleBar);

		// Create the display area of the window
		setUpCanvas();
		getContentPane().add(canvas, BorderLayout.CENTER);

		// Create the menu bar at the top of the window (only has the quit functionality)
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		JMenuItem quitItem = new JMenuItem("Quit");
		quitItem.addActionListener(this);
		fileMenu.add(quitItem);
		menuBar.add(fileMenu);

		// Create the interactive elements of the window (buttons and sliders)
		setUpInteraction();
		getContentPane().add(interaction, BorderLayout.SOUTH);

		addWindowListener(new windowHandler());
		animator = new FPSAnimator(canvas, 60);
		animator.start();
	}

	/**
	 * Prepare the OpenGL canvas and event listeners (i.e. for mouse input, keyboard input, etc.).
	 */
	private void setUpCanvas() {
		GLCapabilities glcapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
		canvas = new GLCanvas(glcapabilities);
		Camera camera = new Camera(Camera.DEFAULT_POSITION,
			Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);
		glEventListener = new Spacecraft_GLEventListener(camera);
		canvas.addGLEventListener(glEventListener);
		canvas.addMouseMotionListener(new MyMouseInput(camera));
		canvas.addKeyListener(new MyKeyboardInput(camera));
	}

	/**
	 * Prepare the section of the window for the user to control robot movement, light intensity,
	 * etc.
	 */
	private void setUpInteraction() {
		interaction = new JPanel();
		interaction.setLayout(new BoxLayout(interaction, BoxLayout.X_AXIS));

		// Slider for the global light's intensity
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider.addChangeListener(this);
		slider.setName("Set Global Light Intensity");
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(25);
		slider.setPaintTicks(true);
        slider.setLabelTable(slider.createStandardLabels(25));
		slider.setPaintLabels(true);
		JLabel label = new JLabel("Set Global Light Intensity");
		sliderPanel.add(label);
		sliderPanel.add(slider);
		interaction.add(sliderPanel);

		// Slider for the spotlight's intensity
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
		slider.addChangeListener(this);
		slider.setName("Set Spotlight Intensity");
		slider.setMinorTickSpacing(5);
		slider.setMajorTickSpacing(25);
		slider.setPaintTicks(true);
        slider.setLabelTable(slider.createStandardLabels(25));
		slider.setPaintLabels(true);
		label = new JLabel("Set Spotlight Intensity");
		sliderPanel.add(label);
		sliderPanel.add(slider);
		interaction.add(sliderPanel);

		// Slider for the distance determining how close robot 2 must be for robot 1 to dance.
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		slider = new JSlider(JSlider.HORIZONTAL, 0, 20, 8);
		slider.addChangeListener(this);
		slider.setName("Change Distance Threshold for Robot 1's Dance");
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(4);
		slider.setPaintTicks(true);
        slider.setLabelTable(slider.createStandardLabels(4));
		slider.setPaintLabels(true);
		label = new JLabel("Change Distance Threshold for Robot 1's Dance");
		sliderPanel.add(label);
		sliderPanel.add(slider);
		interaction.add(sliderPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		// Button to decide how robot 1 should move (manually overriding the proximity checks).
		JButton button = new JButton("Start/stop manual robot 1 movement");
		button.addActionListener(this);
		buttonPanel.add(button);
		// Button to decide whether robot 2 (and its attached spotlight) should be moving or not.
		button = new JButton("Start/stop robot 2 movement");
		button.addActionListener(this);
		buttonPanel.add(button);
		interaction.add(buttonPanel);
	}

	/**
	 * Handles action events relating to buttons being pressed (i.e. robot 1 movement, robot 2 
	 * movement, 'Quit' button).
	 * 
	 * @param e The action event triggered by a button press.
	 */
	public void actionPerformed(ActionEvent e) {
		// Change the contents of the canvas based on which button has been pressed
		if (e.getActionCommand().equalsIgnoreCase("Start/stop manual robot 1 movement")) {
			glEventListener.startStopRobot1Movement();
		}
		else if (e.getActionCommand().equalsIgnoreCase("Start/stop robot 2 movement")) {
			glEventListener.startStopRobot2Movement();
		}
		else if(e.getActionCommand().equalsIgnoreCase("Quit"))
			System.exit(0);
	}

	/**
	 * Handles change events relating to slider values being changed (i.e. light intensities, 
	 * distance thresholds).
	 * 
	 * @param e The change event triggered by a slider by used.
	 */
	public void stateChanged(ChangeEvent e) {
		// Change the contents of the canvas based on changes to the sliders
		JSlider source = (JSlider)e.getSource();
		if (source.getName() == "Set Global Light Intensity") { 
			float lightIntensity = ((float)source.getValue())/100;
			glEventListener.changeGlobalLightIntensity(lightIntensity);
		}
		else if (source.getName() == "Set Spotlight Intensity") {
			float lightIntensity = ((float)source.getValue())/100;
			glEventListener.changeSpotlightIntensity(lightIntensity);
		}
		else if (source.getName() == "Change Distance Threshold for Robot 1's Dance") {
			float distance = ((float)source.getValue());
			glEventListener.changeDistanceThreshold(distance);
		}
	}

	/**
	 * Class that handles events that take place when the window is closed (resource management).
	 */
	private class windowHandler extends WindowAdapter {
		/**
		 * Stops the program and frees resources upon closing the main window.
		 * 
		 * @param e The window event itself (unused).
		 */
		public void windowClosing(WindowEvent e) {
			animator.stop();
			remove(canvas);
			dispose();
			System.exit(0);
		}
	}
}

/**
 * Class to process keyboard inputs and have the associated outputs be shown in the camera's 
 * movement.
 */
class MyKeyboardInput extends KeyAdapter  {
	private Camera camera;
	
	/**
	 * Constructor. Set the camera object that will affected upon keyboard input.
	 * 
	 * @param camera The camera to control.
	 */
	public MyKeyboardInput(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Associate the specific keyboard inputs with the direction of movement for the camera.
	 * 
	 * @param e The keyboard button pressed.
	 */
	public void keyPressed(KeyEvent e) {
		Camera.Movement m = Camera.Movement.NO_MOVEMENT;
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
			case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
			case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
			case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
			case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
			case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
		}
		camera.keyboardInput(m);
	}
}

/**
 * Class to control the camera's yaw and pitch via mouse movement and input.
 */
class MyMouseInput extends MouseMotionAdapter {
	private Point lastpoint;
	private Camera camera;
	
	/**
	 * Constructor. Set the camera object that will be affected when using the mouse.
	 * 
	 * @param camera The camera to control.
	 */
	public MyMouseInput(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * Mouse is used to control camera position when clicked in and dragged.
	 *
	 * @param e  Instance of MouseEvent
	 */    
	public void mouseDragged(MouseEvent e) {
		Point ms = e.getPoint();
		float sensitivity = 0.001f;
		float dx=(float) (ms.x-lastpoint.x)*sensitivity;
		float dy=(float) (ms.y-lastpoint.y)*sensitivity;
		//System.out.println("dy,dy: "+dx+","+dy);
		if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
			camera.updateYawPitch(dx, -dy);
		lastpoint = ms;
	}

	/**
	 * Mouse is used to control camera position.
	 *
	 * @param e  Instance of MouseEvent
	 */  
	public void mouseMoved(MouseEvent e) {   
		lastpoint = e.getPoint(); 
	}
}