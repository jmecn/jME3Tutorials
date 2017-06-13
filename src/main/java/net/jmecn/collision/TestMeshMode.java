package net.jmecn.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Sphere;

/**
 * Test collision detection between different meshes and collidables.
 * 
 * @author yanmaoyuan
 *
 */
public class TestMeshMode extends SimpleApplication {

	float[] vertex = {
			-2, 0, 0,    // 0
			0, 0, 0.5f,  // 1
			2, 0, 0,     // 2
			0, 0, -0.5f  // 3
	};
	
	float[] normals = {
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0
	};
	
	int[] components = {1, 2, 2, 2, 3, 3, 3, 2};
	short[][] indexes = {
		{ 0, 1, 2, 3 },             // Points
		{ 0, 1, 1, 2, 2, 3, 3, 0 }, // Lines
		{ 0, 1, 2, 3, 0 },          // LineStrip
		{ 0, 1, 1, 2, 2, 3, 3, 0 }, // LineLoop
		{ 0, 1, 2, 0, 2, 3 },       // Triangles
		{ 0, 1, 3, 2 },             // TriangleStrip
		{ 0, 1, 2, 3 },             // TriangleFan
		{ 0, 1, 1, 2, 2, 3},        // LineLoop   NOTE: this one is supposed to throw IndexOutOfBoundsException
	};
	
	Mesh.Mode[] modes = {
		Mode.Points,
		Mode.Lines,
		Mode.LineStrip,
		Mode.LineLoop,
		Mode.Triangles,
		Mode.TriangleStrip,
		Mode.TriangleFan,
		Mode.LineLoop, // NOTE: this one is supposed to throw IndexOutOfBoundsException
		Mode.Hybrid,   // DONT USE, It is best to avoid using this mode as it may not be supported by all renderers.
		Mode.Patch,    // DONT USE, This mode is used for Tesselation only. 
	};
	
	// Geometries with different mesh mode
	Mesh[] meshes;

	// Ray, BoundingBox, BoundingSphere
	Collidable[] collidables;
	
	private Arrow arrow;// for ray
	private WireBox wirebox;// for bounding box
	private Sphere sphere;// for bounding sphere
	
	private Material matOrange;// for geometry
	private Material matGreen;// when collision happens
	private Material matRed;// when no collision happens
	
	@Override
	public void simpleInitApp() {
		viewPort.setBackgroundColor(ColorRGBA.DarkGray);
		// init camera
		cam.setLocation(new Vector3f(23.49161f, 28.29185f, -1.0197717f));
		cam.setRotation(new Quaternion(-0.22470495f, 0.77360606f, -0.3889088f, -0.44698012f));
		flyCam.setMoveSpeed(10);
		
		// materials
		matOrange = unshadedMaterial(ColorRGBA.Orange);
		matGreen = unshadedMaterial(ColorRGBA.Green);
		matRed = unshadedMaterial(ColorRGBA.Red);
		
		// Create collidables: Ray, BoundingBox, BoundingSphere
		Ray ray = new Ray();
		ray.setOrigin(new Vector3f(0, 1, 0));
		ray.setDirection(new Vector3f(0, -1, 0));
		ray.setLimit(2);
		
		BoundingBox bb = new BoundingBox(new Vector3f(0, 0, 0), 1f, 1f, 1f);
		
		BoundingSphere bs = new BoundingSphere(1f, new Vector3f(0, 0, 0));
		
		collidables = new Collidable[] { ray, bb, bs };
		
		// Create meshes for collidables
		arrow = new Arrow(ray.direction.mult(ray.limit));
		wirebox = new WireBox(1, 1, 1);
		sphere = new Sphere(9, 12, 1);
		
		// Create meshes for different mesh modes.
		meshes = new Mesh[indexes.length];
		for(int i=0; i<indexes.length; i++) {
			short[] index = indexes[i];
			Mesh.Mode mode = modes[i];
			int component = components[i];
			meshes[i] = makeMesh(index, mode, component);
		}
		
		// collide with each other
		for (int x=0; x<meshes.length; x++) {
			Mesh mesh = meshes[x];
			String name = mesh.getMode().name();
			System.out.printf("%s vs:\n", name);
			
			// Display the Mesh mode
			BitmapText text = guiFont.createLabel(name);
			text.addControl(new BillboardControl());
			text.scale(1/24f);
			text.move(-8, 0, -x * 4);
			rootNode.attachChild(text);
			
			for (int y=0; y<collidables.length; y++) {
				Collidable collidable = collidables[y];
				System.out.printf("> %s:\n", collidable.getClass().getSimpleName());

				// print collision results
				boolean collision = collideWith(mesh, collidable);
				
				// make visual part
				Node node = makeScene(mesh, collidable, collision);
				node.move(y * 6, 0, -x * 4);
				rootNode.attachChild(node);
			}
		}
		
		rootNode.addLight(new DirectionalLight(new Vector3f(0, -1, 0)));
	}

	/**
	 * Make meshes for different mesh modes.
	 * @param index
	 * @param mode
	 * @param component 
	 * @return
	 */
	private Mesh makeMesh(short[] index, Mesh.Mode mode, int component) {
		
		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, vertex);
		mesh.setBuffer(Type.Normal, 3, normals);
		
		mesh.setBuffer(Type.Index, component, index);
		mesh.setMode(mode);
		
		mesh.setStatic();
		mesh.updateBound();
		
		return mesh;
	}
	
	/**
	 * Print collision results
	 * @param mesh
	 * @param collidable
	 * @return
	 */
	private boolean collideWith(Mesh mesh, Collidable collidable) {
		CollisionResults results = new CollisionResults();
		
		try {
			Geometry geom = new Geometry();
			geom.setMesh(mesh);
			geom.collideWith(collidable, results);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}

		// print collision results
		if (results.size() > 0) {
			for (CollisionResult result : results) {
				System.out.printf("  - triangle=%d, distance=%.1f, contactPoint=%s contactNormal=%s\n",
						result.getTriangleIndex(),
						result.getDistance(),
						result.getContactPoint(),
						result.getContactNormal());
			}
		} else {
			System.out.println("  - No collision.");
		}
		
		return results.size() > 0;
	}
	
	/**
	 * Make a unshaded material with given color.
	 * @param color
	 * @return
	 */
	private Material unshadedMaterial(ColorRGBA color) {
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		mat.getAdditionalRenderState().setLineWidth(2f);
		mat.getAdditionalRenderState().setWireframe(true);
		return mat;
	}
	
	/**
	 * Make the visual part for a Mesh and a Collidable.
	 * @param mesh
	 * @param collidable
	 * @param collision
	 * @return
	 */
	private Node makeScene(Mesh mesh, Collidable collidable, boolean collision) {
		Node node = new Node();
		
		// for Mesh
		Geometry g = new Geometry();
		g.setName(mesh.getMode().name());
		g.setMesh(mesh);
		g.setMaterial(matOrange);
		node.attachChild(g);

		// for Collidable
		Geometry c = new Geometry();
		if (collidable instanceof Ray) {
			c.setMesh(arrow);
			c.setName("ray");
			c.setLocalTranslation(0, 1, 0);
		} else if (collidable instanceof BoundingBox) {
			c.setMesh(wirebox);
			c.setName("bounding box");
		} else if (collidable instanceof BoundingSphere) {
			c.setMesh(sphere);
			c.setName("bounding sphere");
		}

		// set different material, so we know which one has no collision.
		if (collision) {
			c.setMaterial(matGreen);
		} else {
			// no collision
			c.setMaterial(matRed);
		}
		node.attachChild(c);
		
		return node;
	}
	
	public static void main(String[] args) {
		TestMeshMode app = new TestMeshMode();
		app.start();
	}

}
