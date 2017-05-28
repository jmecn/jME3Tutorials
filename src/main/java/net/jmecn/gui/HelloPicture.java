package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.ui.Picture;

/**
 * Picture的用法。
 * 
 * @author yanmaoyuan
 *
 */
public class HelloPicture extends SimpleApplication {

    public static void main(String[] args) {
        // 启动程序
        HelloPicture app = new HelloPicture();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // 初始化摄像机位置
        cam.setLocation(new Vector3f(9.443982f, 13.542627f, 8.93058f));
        cam.setRotation(new Quaternion(-0.015316938f, 0.9377411f, -0.34448296f, -0.041695934f));

        flyCam.setMoveSpeed(10);

        // 添加物体
        addObjects();

        // 添加光源
        addLights();

        // 添加图片
        addPicture();

        viewPort.setBackgroundColor(ColorRGBA.LightGray);
    }

    /**
     * 创建一个场景
     * 
     * @return
     */
    private void addObjects() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        // 创建一个平面，把它作为地板，用来承载光影
        Geometry geom = new Geometry("Floor", new Quad(17, 17));
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Receive);// 承载阴影

        geom.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geom);

        // 创造多个方块
        for (int y = 0; y < 10; y += 3) {
            for (int x = 0; x < 10; x += 3) {
                geom = new Geometry("Cube", new Box(0.5f, 0.5f, 0.5f));
                geom.setMaterial(mat);
                geom.setShadowMode(ShadowMode.Cast);// 产生阴影
                geom.move(x + 4, 0.5f, -y - 4);
                rootNode.attachChild(geom);
            }
        }

    }

    /**
     * 添加光源
     */
    private void addLights() {

        // 定向光
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-1, -2, -3));
        sunLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

        // 环境光
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

        // 将模型和光源添加到场景图中
        rootNode.addLight(sunLight);
        rootNode.addLight(ambientLight);
    }

    /**
     * 加载“图片”
     * 
     * @return
     */
    private void addPicture() {
        Picture pic = new Picture("picture");

        // 设置图片
        pic.setImage(assetManager, "Interface/Gui/pic_with_alpha.png", true);

        // 设置图片全屏显示
        pic.setWidth(cam.getWidth());
        pic.setHeight(cam.getHeight());

        // 将图片后移一个单位，避免遮住状态界面。
        pic.setLocalTranslation(0, 0, -1);

        guiNode.attachChild(pic);
    }

}
