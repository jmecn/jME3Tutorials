package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * 材质
 * @author yanmaoyuan
 *
 */
public class HelloMaterial extends SimpleApplication {

    public static void main(String[] args) {
        // 启动程序
        HelloMaterial app = new HelloMaterial();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        
        // 初始化摄像机位置
        cam.setLocation(new Vector3f(-3.06295f, 3.1202009f, 6.756448f));
        cam.setRotation(new Quaternion(0.036418974f, 0.94834185f, -0.11822353f, 0.29213792f));
        
        flyCam.setMoveSpeed(10);
        
        // 添加物体
        addUnshadedBox();
        addLightingBox();
        
        addUnshadedSphere();
        addLightingSphere();
        
        // 添加光源
        addLight();
        
        // 把窗口背景改成淡蓝色
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.7f, 0.9f, 1));
    }
    
    /**
     * 创造一个红色的小球，应用无光材质。
     * @return
     */
    private void addUnshadedSphere() {
        // #1 加载一个反光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        // #2 设置参数
        mat.setColor("Color", ColorRGBA.Red);// 小球的颜色。
        
        // #3 创造1个球体，应用此材质。
        Geometry geom = new Geometry("普通球体", new Sphere(20, 40, 1));
        geom.setMaterial(mat);
        
        geom.move(4, 3, 0);
        rootNode.attachChild(geom);
    }
    
    /**
     * 创造一个红色的小球，应用受光材质。
     * @return
     */
    private void addLightingSphere() {
        // #1 加载一个受光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
        // #2 设置参数
        mat.setColor("Diffuse", ColorRGBA.Red);// 在漫射光照射下反射的颜色。
        mat.setColor("Ambient", ColorRGBA.Red);// 在环境光照射下，反射的颜色。
        mat.setColor("Specular", ColorRGBA.White);// 镜面反射时，高光的颜色。
        
        // 反光度越低，光斑越大，亮度越低。
        mat.setFloat("Shininess", 32);// 反光度
        
        // 使用上面设置的Diffuse、Ambient、Specular等颜色
        mat.setBoolean("UseMaterialColors", true);
        
        // #3 创造1个球体，应用此材质。
        Geometry geom = new Geometry("文艺小球", new Sphere(20, 40, 1));
        geom.setMaterial(mat);
        
        geom.move(0, 3, 0);
        rootNode.attachChild(geom);
    }
    
    /**
     * 创造一个方块，应用无光材质。
     * @return
     */
    private void addUnshadedBox() {
        // #1 创建一个无光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");

        // #2 加载一个纹理贴图，设置给这个材质。
        Texture tex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat.setTexture("ColorMap", tex);// 设置贴图
        
        // #3 创造1个方块，应用此材质。
        Geometry geom = new Geometry("普通方块", new Box(1, 1, 1));
        geom.setMaterial(mat);
        
        geom.move(4, 0, 0);
        rootNode.attachChild(geom);
    }
    
    
    /**
     * 创造一个方块，应用受光材质。
     * @return
     */
    private void addLightingBox() {
        // #1 创建一个无光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        // #2 设置纹理贴图
        // 漫反射贴图
        Texture tex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        mat.setTexture("DiffuseMap", tex);
        
        // 法线贴图
        tex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_normal.jpg");
        mat.setTexture("NormalMap", tex);
        
        // 视差贴图
        tex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall_height.jpg");
        mat.setTexture("ParallaxMap", tex);

        // 设置反光度
        mat.setFloat("Shininess", 2.0f);
        
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
