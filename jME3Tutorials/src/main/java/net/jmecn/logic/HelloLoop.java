package net.jmecn.logic;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * 主循环
 * 
 * @author yanmaoyuan
 *
 */
public class HelloLoop extends SimpleApplication {

	// 旋转的物体
	private Spatial spatial;
	// 旋转速度：每秒180°
	private float rotateSpeed = FastMath.PI;
	
	@Override
	public void simpleInitApp() {
		cam.setLocation(new Vector3f(3.3435764f, 3.7595856f, 6.611723f));
		cam.setRotation(new Quaternion(-0.05573249f, 0.9440857f, -0.23910178f, -0.22006002f));
		
		// 创建一个方块
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		spatial = new Geometry("Box", new Box(1, 1, 1));
		spatial.setMaterial(mat);
		
		rootNode.attachChild(spatial);
		
		// 添加光源
		rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3)));
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		// 绕Y轴以固定速率旋转
		spatial.rotate(0, tpf * rotateSpeed, 0);
	}

	public static void main(String[] args) {
		// 启动
		HelloLoop app = new HelloLoop();
		app.start();
	}

}
