package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Sphere;

/**
 * 材质
 * @author yanmaoyuan
 *
 */
public class HelloMaterial extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        // 把窗口背景改成淡蓝色
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.7f, 0.9f, 1));
        
        // #1 创建一个球形的网格
        Mesh mesh = new Sphere(12, 18, 2);
        
        // #2 创建一个材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", ColorRGBA.Red);// 把球体变成红色
        
        // #3 创造1个地板和1个球体
        Geometry sphere = new Geometry("球体");
        sphere.setMesh(mesh);
        sphere.setMaterial(mat);

        // #4 将地板和球体都添加到场景中
        rootNode.attachChild(sphere);
        
    }

    public static void main(String[] args) {
        // 启动程序
        HelloMaterial app = new HelloMaterial();
        app.start();
    }
}
