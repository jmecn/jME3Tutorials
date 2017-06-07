package net.jmecn.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResults;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import net.jmecn.state.AxisAppState;

/**
 * 基于包围体的碰撞检测。
 * 
 * @author yanmaoyuan
 *
 */
public class TestCollisionWith extends SimpleApplication implements RawInputListener {

	private Geometry greenCube;
	private Geometry pinkCube;
	
	public static void main(String[] args) {
		TestCollisionWith app = new TestCollisionWith();
		app.start();
	}
	
	public TestCollisionWith() {
		super(new AxisAppState());
	}
	
	@Override
	public void simpleInitApp() {
		cam.setLocation(new Vector3f(0f, 18f, 22f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		viewPort.setBackgroundColor(ColorRGBA.White);
		
		// 绿色方块
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		greenCube = new Geometry("Green", new Box(1, 1, 1));
		greenCube.setMaterial(mat);
		greenCube.move(-8, 0, 8);
		
		// 粉色方块
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Pink);
		pinkCube = new Geometry("Pink", new Box(1, 1, 1));
		pinkCube.setMaterial(mat);
		
		rootNode.attachChild(greenCube);
		rootNode.attachChild(pinkCube);
		
		inputManager.addRawInputListener(this);
	}

	/**
	 * 碰撞检测
	 * @return
	 */
	private boolean collisionDetection() {
		// 包围体
		BoundingVolume bound = greenCube.getWorldBound();
		
		// 碰撞检测
		CollisionResults results = new CollisionResults();
		pinkCube.collideWith(bound, results);
		
		return results.size() > 0;
	}
	
	@Override
	public void onMouseMotionEvent(MouseMotionEvent evt) {
		// 根据鼠标的位置来改变绿色方块的坐标，并将其限制在 (-10, -10) 到 (10, 10)的范围内。
		float x = evt.getX();
		float y = evt.getY();
		
		// 坐标系大小为 20 * 20
		x = x * 20 / cam.getWidth() - 10;
		y = y * 20 / cam.getHeight() - 10;
		
		greenCube.setLocalTranslation(x, 0, -y);
		
		if (collisionDetection()) {
			greenCube.getMaterial().setColor("Color", ColorRGBA.Red);
		} else {
			greenCube.getMaterial().setColor("Color", ColorRGBA.Green);
		}
	}
	
	@Override public void beginInput() {}
	@Override public void endInput() {}
	@Override public void onJoyAxisEvent(JoyAxisEvent evt) {}
	@Override public void onJoyButtonEvent(JoyButtonEvent evt) {}
	@Override public void onMouseButtonEvent(MouseButtonEvent evt) {}
	@Override public void onKeyEvent(KeyInputEvent evt) {}
	@Override public void onTouchEvent(TouchEvent evt) {}

}
