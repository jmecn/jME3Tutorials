package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 * 用j3m文件来保存/加载材质
 * @author yanmaoyuan
 *
 */
public class HelloJ3M extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        // 加载j3m材质，应用于一个方块表面。
        Material mat = assetManager.loadMaterial("Materials/BrickWall.j3m");
        
        Geometry geom = new Geometry("BrickWall", new Quad(8, 8));
        geom.setMaterial(mat);
        
        geom.center();
        rootNode.attachChild(geom);
       
        // 添加一个定向光
        DirectionalLight sun = new DirectionalLight(new Vector3f(0, 0, -1));
        rootNode.addLight(sun);
    }
    
    public static void main(String[] args) {
        HelloJ3M app = new HelloJ3M();
        app.start();
    }

}
