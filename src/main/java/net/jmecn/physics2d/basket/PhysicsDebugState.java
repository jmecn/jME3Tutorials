package net.jmecn.physics2d.basket;

import org.dyn4j.collision.Fixture;
import org.dyn4j.dynamics.Body;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import net.jmecn.physics2d.BodyControl;

public class PhysicsDebugState extends BaseAppState {

	private Node rootNode = new Node("debug root");
	
	@Override
	protected void initialize(Application app) {}

	@Override
	protected void cleanup(Application app) {}
	
	@Override
	protected void onEnable() {
		SimpleApplication simpleApp = (SimpleApplication) getApplication();
		simpleApp.getRootNode().attachChild(rootNode);
	}

	@Override
	protected void onDisable() {
		rootNode.removeFromParent();
	}

	public void addDebugShape(Body body) {
		Node node = new Node("Body_" + System.currentTimeMillis());
		node.addControl(new BodyControl(body));
		rootNode.attachChild(node);
		
		node.attachChild(AssetFactory.createVisual(0.01f, 0.02f, null));
		
		for(Fixture fixture: body.getFixtures()) {
			Convex shape = fixture.getShape();
			if (shape instanceof Rectangle) {
				Rectangle rect = (Rectangle) shape;
				float width = (float)rect.getWidth();
				float height = (float)rect.getHeight();
				Spatial spatial = AssetFactory.makeDebugRect(width, height);
				
				spatial.move((float)rect.getCenter().x, (float)rect.getCenter().y, -1);
				node.attachChild(spatial);
			} else if (shape instanceof Circle) {
				Circle cir = (Circle) shape;
				float radius = (float) cir.getRadius();
				Spatial spatial = AssetFactory.makeDebugCircle(radius);
				
				spatial.move((float)cir.getCenter().x, (float)cir.getCenter().y, -1);
				node.attachChild(spatial);
			}
		}
	}

}
