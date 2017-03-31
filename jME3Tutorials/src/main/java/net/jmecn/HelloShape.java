package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 * 基本形状，显示一个球体网格。
 * @author yanmaoyuan
 *
 */
public class HelloShape extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);

        // 创建球体
        Geometry geom = new Geometry("球体", new Sphere(10, 16, 2));

        // 创建材质，并显示网格线
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        geom.setMaterial(mat);

        // 将物体添加到场景图中
        rootNode.attachChild(geom);

    }

    public static void main(String[] args) {
        HelloShape app = new HelloShape();
        app.start();
    }

}