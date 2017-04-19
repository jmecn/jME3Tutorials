package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * 场景图、节点
 * @author yanmaoyuan
 *
 */
public class HelloNode extends SimpleApplication {

	private Spatial spatial;
	
	@Override
	public void simpleUpdate(float tpf) {
		if (spatial != null) {
			// 绕Y轴旋转
			spatial.rotate(0, 3.1415926f * tpf, 0);
		}
	}
	
	@Override
	public void simpleInitApp() {
		// 球体网格
		Mesh mesh = new Sphere(16, 24, 1);
		
		// 创建2个球体
		Geometry geomA = new Geometry("红色气球", mesh);
		geomA.setMaterial(newLightingMaterial(ColorRGBA.Red));
		
		Geometry geomB = new Geometry("青色气球", mesh);
		geomB.setMaterial(newLightingMaterial(ColorRGBA.Cyan));
		
		// 将两个球体添加到一个Node节点中
		Node node = new Node("原点");
		node.attachChild(geomA);
		node.attachChild(geomB);
		node.scale(0.5f);
		
		// 设置两个球体的相对位置
		geomA.setLocalTranslation(-1, 3, 0);
		geomB.setLocalTranslation(1.5f, 2, 0);		
		// 将这个节点添加到场景图中
		rootNode.attachChild(node);
		
		// 添加光源
		addLight();
		
		this.spatial = node;
	}
	
	/**
	 * 创建一个感光材质
	 * @param color
	 * @return
	 */
	private Material newLightingMaterial(ColorRGBA color) {
		// 创建材质
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		
		mat.setColor("Diffuse", color);
		mat.setColor("Ambient", color);
		mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 24);
        mat.setBoolean("UseMaterialColors", true);
        
		return mat;
	}
	
    /**
     * 添加光源
     */
    private void addLight() {
        // 定向光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));

        // 环境光
        AmbientLight ambient = new AmbientLight();

        // 调整光照亮度
        ColorRGBA lightColor = new ColorRGBA();
        sun.setColor(lightColor.mult(0.8f));
        ambient.setColor(lightColor.mult(0.2f));
        
        // #3 将模型和光源添加到场景图中
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
    }
    
	public static void main(String[] args) {
		HelloNode app = new HelloNode();
		app.start();
	}

}