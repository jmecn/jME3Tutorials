package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * 光与影
 * @author yanmaoyuan
 *
 */
public class HelloLight extends SimpleApplication {

    public static void main(String[] args) {
        // 启动程序
        HelloLight app = new HelloLight();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        // 初始化摄像机位置
        cam.setLocation(new Vector3f(-3.06295f, 3.1202009f, 6.756448f));
        cam.setRotation(new Quaternion(0.036418974f, 0.94834185f, -0.11822353f, 0.29213792f));
        
        flyCam.setMoveSpeed(10);
        
        // 添加物体
        addLightingBox();
        
        // 添加光源
        addLight();
        
        // 把窗口背景改成淡蓝色
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.7f, 0.9f, 1));
    }
    
    /**
     * 创建一个场景
     * @return
     */
    private void addLightingBox() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        // #3 创造1个方块，应用此材质。
        Geometry geom = new Geometry("文艺方块", new Box(1, 1, 1));
        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
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
}
