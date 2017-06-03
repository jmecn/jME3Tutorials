package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import net.jmecn.logic.RotateControl;

/**
 * 演示Control的用法
 * 
 * @author yanmaoyuan
 *
 */
public class HelloControl extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(3.3435764f, 3.7595856f, 6.611723f));
        cam.setRotation(new Quaternion(-0.05573249f, 0.9440857f, -0.23910178f, -0.22006002f));

        // 创建一个方块
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Geometry spatial = new Geometry("Box", new Box(1, 1, 1));
        spatial.setMaterial(mat);

        // 添加控件
        spatial.addControl(new RotateControl(FastMath.PI));

        rootNode.attachChild(spatial);

        // 添加光源
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3)));
    }

    public static void main(String[] args) {
        // 启动
        HelloControl app = new HelloControl();
        app.start();
    }

}
