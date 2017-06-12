package net.jmecn.physics2d;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

import net.jmecn.state.AxisAppState;

public class TestDyn4j extends SimpleApplication {
	/** The dynamics engine */
	protected World world;

	@Override
	public void simpleInitApp() {
		stateManager.attach(new AxisAppState());

		cam.setLocation(new Vector3f(5.693723f, 11.948935f, 20.422218f));
		cam.setRotation(new Quaternion(-0.030991483f, 0.96380556f, -0.23111638f, -0.12924244f));
		
		viewPort.setBackgroundColor(ColorRGBA.White);
		
		rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3)));

		// create the world
		this.world = new World();

		floor();
		
		createCircle(0.5f);
		createCircle(0.5f);
		createCircle(0.5f);
		
		rectangle();
	}

	private Material getMaterial() {
		ColorRGBA color = ColorRGBA.randomColor();
		
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setColor("Diffuse", color);
		mat.setColor("Ambient", color.mult(0.7f));
		mat.setColor("Specular", ColorRGBA.Black);
		mat.setFloat("Shininess", 1f);
		mat.setBoolean("UseMaterialColors", true);
		return mat;
	}
	
	private void floor() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Body floor = new Body();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(MassType.INFINITE);
		this.world.addBody(floor);

		// create spatial
		Box box = new Box(7.5f, 0.5f, 1f);
		Geometry geom = new Geometry("body", box);
		geom.setMaterial(getMaterial());
		geom.addControl(new BodyControl(floor));
		rootNode.attachChild(geom);
	}

	private void createCircle(float radius) {
		// create a circle
		Circle cirShape = new Circle(radius);
		Body circle = new Body();
		circle.addFixture(cirShape);
		circle.setMass(MassType.NORMAL);
		circle.translate(2.0, 10.0);
		// test adding some force
		circle.applyForce(new Vector2(-100.0, 0.0));
		// set some linear damping to simulate rolling friction
		circle.setLinearDamping(0.05);
		this.world.addBody(circle);

		// create a sphere
		Sphere sphere = new Sphere(12, 12, radius);
		Geometry geom = new Geometry("body", sphere);
		geom.setMaterial(getMaterial());
		geom.addControl(new BodyControl(circle));
		rootNode.attachChild(geom);
	}

	private void rectangle() {
		// try a rectangle
		Rectangle rectShape = new Rectangle(1.0, 1.0);
		Body rectangle = new Body();
		rectangle.addFixture(rectShape);
		rectangle.setMass(MassType.NORMAL);
		rectangle.translate(0.0, 6.0);// 上移6个单位
		rectangle.getLinearVelocity().set(-5.0, 0.0);// 线速度
		rectangle.setAngularVelocity(-FastMath.PI);// 角速度
		this.world.addBody(rectangle);

		// create a rectangle
		Box rect = new Box(0.5f, 0.5f, 0.5f);
		Geometry geom = new Geometry("body", rect);
		geom.setMaterial(getMaterial());
		geom.addControl(new BodyControl(rectangle));
		rootNode.attachChild(geom);
	}

	@Override
	public void simpleUpdate(float tpf) {
		this.world.update(tpf);
	}

	public static void main(String[] args) {
		TestDyn4j app = new TestDyn4j();
		app.start();
	}

}
