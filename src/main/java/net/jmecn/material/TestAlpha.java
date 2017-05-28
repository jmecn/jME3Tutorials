package net.jmecn.material;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

/**
 * 测试透明物体
 * 
 * @author yanmaoyuan
 *
 */
public class TestAlpha extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);

        createQpaqueSphere();
        createTranslucentSphere();
        createTranslucentQuad();
        
        addLight();
    }
    
    /**
     * 创造一个不透明的红色小球
     * @return
     */
    private Geometry createQpaqueSphere() {
        // 加载一个受光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Ambient", ColorRGBA.Red);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 16f);// 光泽度，取值范围1~128。
        mat.setBoolean("UseMaterialColors", true);
        
        // 应用材质
        Geometry geom = new Geometry("不透明的红色小球", new Sphere(40, 36, 1));
        geom.setMaterial(mat);
        
        rootNode.attachChild(geom);
        return geom;
    }

    /**
     * 创造一个半透明的青色小球
     * @return
     */
    private void createTranslucentSphere() {
        // 加载一个受光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", new ColorRGBA(0, 1, 1, 0.5f));
        mat.setColor("Ambient", ColorRGBA.Cyan);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 16f);
        mat.setBoolean("UseMaterialColors", true);
        
        // 创造1个球体，应用此材质。
        Geometry geom = new Geometry("半透明的青色小球", new Sphere(40, 36, 1));
        geom.setMaterial(mat);
        
        // 使小球看起来透明
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setQueueBucket(Bucket.Transparent);
        
        geom.move(0, 0, 3);
        rootNode.attachChild(geom);
    }
    
    /**
     * 创造一个半透明的白色正方形
     */
    private void createTranslucentQuad() {
        // 加载一个无光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1f, 1f, 1f, 0.5f));// 镜面反射时，高光的颜色。
        
        // 应用材质。
        Geometry geom = new Geometry("一个半透明的正方形", new Quad(1, 1));
        geom.setMaterial(mat);
        
        // 使物体看起来透明
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setQueueBucket(Bucket.Transparent);
        
        geom.move(-1, -1, 5);
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
        
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
    }
    
    public static void main(String[] args) {
        TestAlpha app = new TestAlpha();
        app.start();
    }

}
