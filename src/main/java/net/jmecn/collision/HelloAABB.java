package net.jmecn.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Cylinder;

import net.jmecn.logic.FloatControl;
import net.jmecn.logic.RotateControl;
import net.jmecn.state.AxisAppState;

/**
 * 演示轴对齐包围盒（Axis Align Bounding Box）
 * @author yanmaoyuan
 *
 */
public class HelloAABB extends SimpleApplication {

	private Geometry debug;
	private Geometry cylinder;
	
	@Override
	public void simpleInitApp() {
		// 初始化摄像机
		cam.setLocation(new Vector3f(4.5114727f, 6.176994f, 13.277485f));
		cam.setRotation(new Quaternion(-0.038325474f, 0.96150225f, -0.20146479f, -0.18291113f));
		flyCam.setMoveSpeed(10);
		
		viewPort.setBackgroundColor(ColorRGBA.LightGray);
		
		// 参考坐标系
		stateManager.attach(new AxisAppState());
		
		// 圆柱体
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setColor("Diffuse", ColorRGBA.Yellow);
		mat.setColor("Ambient", ColorRGBA.Yellow);
		mat.setColor("Specular", ColorRGBA.White);
		mat.setFloat("Shininess", 24);
		mat.setBoolean("UseMaterialColors", true);
		
		cylinder = new Geometry("cylinder", new Cylinder(2, 36, 1f, 8, true));
		cylinder.setMaterial(mat);
		// 让圆柱体运动，这样才能看到包围盒的变化。
		cylinder.addControl(new RotateControl());
		cylinder.addControl(new FloatControl(2, 2));
		
		// 用于显示包围盒
		mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Magenta);
		debug = new Geometry("debug", new WireBox(1, 1, 1));
		debug.setMaterial(mat);
		
		rootNode.attachChild(cylinder);
		rootNode.attachChild(debug);
		
		// 光源
		rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3)));
		rootNode.addLight(new AmbientLight(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f)));
	}

	@Override
	public void simpleUpdate(float tpf) {
		// 根据圆柱体当前的包围盒，更新线框的位置和大小。
		BoundingBox bbox = (BoundingBox) cylinder.getWorldBound();
		debug.setLocalScale(bbox.getExtent(null));
		debug.setLocalTranslation(bbox.getCenter());
	}
	
	public static void main(String[] args) {
		HelloAABB app = new HelloAABB();
		app.start();
	}

}
