package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 第三章
 * 加载模型
 * @author yanmaoyuan
 *
 */
public class HelloModel extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(0.41600543f, 3.2057908f, 6.6927643f));
        cam.setRotation(new Quaternion(-0.00414816f, 0.9817784f, -0.18875499f, -0.021575727f));

        flyCam.setMoveSpeed(10);
        viewPort.setBackgroundColor(ColorRGBA.LightGray);

        // #1 导入模型
        Spatial model = assetManager.loadModel("Models/Ashe/b_ashe_b.obj");
        model.scale(0.03f);// 按比例缩小
        model.center();// 将模型的中心移到原点

        // #2 创造光源

        // 定向光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));

        // 环境光
        AmbientLight ambient = new AmbientLight();

        // 调整光照亮度
        ColorRGBA lightColor = new ColorRGBA();
        sun.setColor(lightColor.mult(0.6f));
        ambient.setColor(lightColor.mult(0.4f));

        // #3 将模型和光源添加到场景图中
        rootNode.attachChild(model);
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
    }

    public static void main(String[] args) {
        // 启动程序
        HelloModel app = new HelloModel();
        app.start();
    }

}