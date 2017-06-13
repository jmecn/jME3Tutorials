package net.jmecn.physics2d;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

import net.jmecn.state.AxisAppState;

public class TestBasketball extends SimpleApplication implements ActionListener {

	public final static String CLICK = "click";
	
	float screenWidth;
	float screenHeight;
	
	float factor = 45.0f;// 屏幕放大倍速

	/** The dynamics engine */
	protected World world;

	@Override
	public void simpleInitApp() {
		stateManager.attach(new AxisAppState());

		viewPort.setBackgroundColor(ColorRGBA.White);

		resetCamera();

		// create the world
		this.world = new World();

		floor();

		createCircle(9, 7);

		rectangle();
		
		inputManager.addMapping(CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, CLICK);
	}

	private void resetCamera() {
		// disable flyCam
		if (flyCam != null) {
			flyCam.setEnabled(false);
		}

		// screen dimension
		screenWidth = cam.getWidth();
		screenHeight = cam.getHeight();

		// change to 2d
		cam.setParallelProjection(true);

		float right = screenWidth / 2 / factor;
		float left = -right;
		float top = screenHeight / 2 / factor;
		float bottom = -top;
		cam.setFrustum(-1000, 1000, left, right, top, bottom);

		cam.setLocation(new Vector3f(right, top, 0));
	}

	private void floor() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Body floor = new Body();
		floor.addFixture(new BodyFixture(floorRect));
		floor.translate(9, 2);
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);

		// create spatial
		Node node = createVisual(15f, 1f, null, floor);
		rootNode.attachChild(node);
	}

	private void createCircle(float x, float y) {
		// create a circle
		float radius = 0.5f;
		
		Body circle = new Body();
		circle.addFixture(org.dyn4j.geometry.Geometry.createCircle(0.5), 1, 0.2, 0.5);
		circle.setMass(MassType.NORMAL);
		circle.translate(x, y);
		
		// test adding some force
		float rad = FastMath.rand.nextFloat() * FastMath.TWO_PI;
		float dx = FastMath.sin(rad) * 100;
		float dy = FastMath.cos(rad) * 100;
		circle.applyForce(new Vector2(dx, dy));
		
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);

		// create a sphere
		Node node = createVisual(radius * 2, radius * 2, "Textures/Dyn4j/Samples/Basketball.png", circle);
		rootNode.attachChild(node);
	}

	private void rectangle() {
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Body rectangle = new Body();
		rectangle.addFixture(rectShape);
		rectangle.setMass(MassType.NORMAL);
		rectangle.getLinearVelocity().set(-5.0, 0.0);// 线速度
		rectangle.setAngularVelocity(-FastMath.PI);// 角速度
		this.world.addBody(rectangle);

		// create a rectangle
		Node node = createVisual(1, 1, "Textures/Dyn4j/Samples/Crate.png", rectangle);
		rootNode.attachChild(node);
	}
	

	private Node createVisual(float width, float height, String tex, Body body) {

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		
		if (tex == null) {
			mat.setColor("Color", ColorRGBA.randomColor());
		} else {
			try {
				Texture texture = assetManager.loadTexture(tex);
				mat.setTexture("ColorMap", texture);
				mat.setColor("Color", ColorRGBA.White);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// create spatial
		Quad quad = new Quad(width, height);
		Geometry geom = new Geometry("quad", quad);
		geom.setMaterial(mat);
		geom.center();

		Node node = new Node();
		node.attachChild(geom);
		node.addControl(new BodyControl(body));

		return node;
	}

	@Override
	public void simpleUpdate(float tpf) {
		this.world.update(tpf);
	}

	public static void main(String[] args) {
		TestBasketball app = new TestBasketball();
		app.start();
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		
		if (isPressed) {
			if (CLICK.equals(name)) {
				Vector2f pos = inputManager.getCursorPosition();
				
				float x = (pos.x) / factor + cam.getLocation().x - cam.getFrustumRight();
				float y = (pos.y) / factor + cam.getLocation().y - cam.getFrustumTop();
				
				createCircle(x, y);
			}
		}
		
	}

}
