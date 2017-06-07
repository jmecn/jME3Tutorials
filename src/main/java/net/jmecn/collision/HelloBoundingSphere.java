package net.jmecn.collision;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingSphere;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Cylinder;

import net.jmecn.logic.FloatControl;
import net.jmecn.logic.RotateControl;
import net.jmecn.state.AxisAppState;

/**
 * 演示包围球（Bounding Sphere）
 * 
 * @author yanmaoyuan
 *
 */
public class HelloBoundingSphere extends SimpleApplication {

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

        // 设置网格包围球
        Mesh mesh = new Cylinder(2, 36, 1f, 8, true);
        mesh.setBound(new BoundingSphere());
        mesh.updateBound();

        cylinder = new Geometry("cylinder", mesh);
        cylinder.setMaterial(mat);
        // 让圆柱体运动，这样才能看到包围盒的变化。
        cylinder.addControl(new RotateControl());
        cylinder.addControl(new FloatControl(2, 2));

        // 用于显示包围球
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Magenta);
        mat.getAdditionalRenderState().setWireframe(true);
        debug = new Geometry("debug", new WireSphere(1));
        debug.setMaterial(mat);

        rootNode.attachChild(cylinder);
        rootNode.attachChild(debug);

        // 光源
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3)));
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f)));
    }

    @Override
    public void simpleUpdate(float tpf) {
        // 根据圆柱体当前的包围球，更新线框的位置和大小。
        BoundingSphere bs = (BoundingSphere) cylinder.getWorldBound();
        debug.setLocalScale(bs.getRadius());
        debug.setLocalTranslation(bs.getCenter());
    }

    public static void main(String[] args) {
        HelloBoundingSphere app = new HelloBoundingSphere();
        app.start();
    }

}
