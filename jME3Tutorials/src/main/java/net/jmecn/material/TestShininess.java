package net.jmecn.material;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * 测试Lighting.j3md的Shininess参数
 * 
 * @author yanmaoyuan
 *
 */
public class TestShininess extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        
        addLight();

        for(float shininess = 1, x= 0; shininess <= 128; shininess += 32f, x += 2.5f) {
            Geometry geom = createSphere(shininess);
            rootNode.attachChild(geom);
            geom.move(x, 0, 0);
        }
    }
    
    private Geometry createSphere(float shininess) {
        // 加载一个受光材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Red);// 在漫射光照射下反射的颜色。
        mat.setColor("Ambient", ColorRGBA.Red);// 在环境光照射下，反射的颜色。
        
        mat.setColor("Specular", ColorRGBA.White);// 镜面反射时，高光的颜色。
        mat.setFloat("Shininess", shininess);// 光泽度，取值范围1~128。
        
        // 使用上面设置的Diffuse、Ambient、Specular等颜色
        mat.setBoolean("UseMaterialColors", true);
        
        // 创造1个球体，应用此材质。
        Geometry geom = new Geometry("小球", new Sphere(40, 36, 1));
        geom.setMaterial(mat);
        
        return geom;
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
        TestShininess app = new TestShininess();
        app.start();
    }

}
