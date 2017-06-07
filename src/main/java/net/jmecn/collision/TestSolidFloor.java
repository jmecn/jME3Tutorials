package net.jmecn.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

import net.jmecn.state.AxisAppState;

/**
 * 使用碰撞检测，让地板变“坚固”。
 * 
 * @author yanmaoyuan
 *
 */
public class TestSolidFloor extends SimpleApplication {

	private Geometry cube;
	private Geometry floor;
	private CollisionResults results = new CollisionResults();
	
	public TestSolidFloor() {
		super(new AxisAppState());
		this.setPauseOnLostFocus(false);
	}
	
	@Override
	public void simpleInitApp() {
		cam.setLocation(new Vector3f(0f, 16f, 20f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

		viewPort.setBackgroundColor(ColorRGBA.White);
		
		// 方块
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		cube = new Geometry("Green", new Box(1, 1, 1));
		cube.setMaterial(mat);
		cube.move(-10, 0, 0);
		
		// 添加物理组件
		Physical physical = new Physical();
		physical.setVelocity(new Vector3f(3, 10, 0));// 速度
		physical.setGravity(new Vector3f(0, -9.8f, 0));// 重力加速度
		cube.addControl(physical);
		
		// 地板
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Pink);
		
		floor = new Geometry("Floor", new Quad(16, 4));
		floor.rotate(-FastMath.HALF_PI, 0f, 0f);
		floor.move(-10, 0, 2);
		floor.setMaterial(mat);
		
		rootNode.attachChild(cube);
		rootNode.attachChild(floor);
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		// 清空上次的检测结果
		if (results.size() > 0) {
			results.clear();
		}
		
		// 碰撞检测
		floor.collideWith(cube.getWorldBound(), results);
		if (results.size() > 0) {
			
			Physical physical = cube.getControl(Physical.class);
			Vector3f v = physical.getVelocity();
			
			// 改变速度方向
			if (v.y <= 0) {
				v.y = -v.y;
			}
			
			if (v.y > 0.0000001f) {
				v.y *= 0.9f;// 碰撞后地板吸收一定的能量。
			} else {
				v.y = 0f;
			}
		
		}
	}

	public static void main(String[] args) {
		TestSolidFloor app = new TestSolidFloor();
		app.start();
	}

}
