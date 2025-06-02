I declare that this code is my own work.
Author: Ellis Barker
Email: ebarker5@sheffield.ac.uk

Brief description of every class:
- Camera: camera-related functionality (editing the angle/position of the camera based on user input)
- Cube: vertices and associated information needed for a cube model
- Globe: information related to the models forming the globe scene graph and updating the globe's rotation over time
- Light: data to create a light within the scene (both global light and spotlight)
- Material: stores material properties for a mesh (e.g. ambient, specular, diffuse, shininess, etc.)
- Mesh: buffer management for models
- ModelMaker: helper for quickly creating new models (of any shape) and model nodes within a scene graph
- ModelMultipleLights: information related to models and their rendering (includes material, mesh, shader interaction, etc.)
- ModelNode: representing a model within a scene graph
- NameNode: representing a name within a scene graph
- Robot1: information related to the models forming robot 1's scene graph and updating it over time (as well handling user interaction regarding its dancing)
- Robot2: information related to the models forming robot 2's scene graph, updating it over time and rendering the spotlight to move alongside robot 2
- Room: preparing and rendering all models needed to setup the spacecraft's main room
- SGNode: representing a node within a scene graph (and handling the propagation of transforms to its children)
- Shader: linking/loading/compiling shader files and setting uniform variables
- Skybox: setting up and rendering the skybox
- Spacecraft_GLEventListener: handles rendering elements of the spacecraft and interaction between these elements and the user
- Spacecraft: prepares the GUI window and the interactive elements the user can use to alter the spacecraft's objects
- Sphere: vertices and associated information needed for a sphere model
- TextureLibrary: manage texture information through file interaction and manage texture binding/parameters
- TransformNode: represent a transform within a scene graph and update children nodes accordingly
- Triangle: vertices and associated information needed for a triangle
- TwoTriangles: vertices and associated information needed for a square (two triangles)



--------------------------------------------------
CHANGES FROM LAB CODE
--------------------------------------------------

Classes with mostly all-new functionality:
- Globe
- ModelMaker
- Robot1
- Robot2
- Room
- Skybox

Classes edited from lab code:
- Light: new 'shape' parameter for the constructor to set the light's shape, new getter method for the light's direction (used for the spotlight)
- ModelMultipleLights: updating the shaders now sets new uniform variables: e.g. cutoff, outer cutoff, direction, etc. (used for the spotlight)
- Spacecraft_GLEventListener: new functions to process inputs from the new UI elements
- Spacecraft: new UI elements (sliders and buttons) for interacting with various elements in the canvas
- Sphere: new function to calculate sphere vertices with only position data (used for the spotlight)
- TextureLibrary: new function to load a cubemap texture composed of 6 2D textures (used for the skybox)
- Triangle: class derived from TwoTriangles to allow for creation of single triangles (used in creating the room)
- TwoTriangles: new vertices with different texture coordinates to allow for texture repetition across the room's right wall

Other new files:
- fs_skybox: new fragment shader for rendering the skybox
- vs_skybox: new vertex shader for rendering the skybox
- fs_standard_m_0t: edited to account for the spotlight
- fs_standard_m_1t: edited to account for the spotlight
- fs_standard_m_2t: edited to account for the spotlight

All other classes not mentioned above (e.g. scene graph node-related classes) have been taken from lab code where the only changes made are inclusions of Javadocs for classes/methods (as well as general formatting for consistency across other classes). The 'gmaths' package has been unaltered.

More detailed breakdowns of what has been changed can be seen at the start of each class with such changes.



--------------------------------------------------
VIDEO
--------------------------------------------------

YouTube link to the code in action: https://youtu.be/sOkN7IminD4



--------------------------------------------------
FORMATTING NOTE
--------------------------------------------------

A maximum line length of around 100 characters is used (give or take 10 characters for lines that shouldn't be split up without decreasing overall readability).



--------------------------------------------------
IMAGE REFERENCES
--------------------------------------------------

Skybox textures taken from:
- https://www.flickriver.com/photos/webtreatsetc/5436446554/ [1]
- https://www.pngwing.com/en/free-png-zxzrk [2]
Every other texture has been created by me.

[1] WebTreatsETC, "Seamless starfield space tileable texture," Flickriver. [Online]. Available: https://www.flickriver.com/photos/webtreatsetc/5436446554/. [Accessed: Nov. 29 2024]
[2] PNGWing, "Space skybox texture with stars and full moon," PNGWing. [Online]. Available: https://www.pngwing.com/en/free-png-zxzrk. [Accessed: Nov. 29 2024]