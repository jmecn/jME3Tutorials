package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * 障眼法：伪装图片
 * 
 * @author yanmaoyuan
 *
 */
public class FakePicture extends SimpleApplication {

    public static void main(String[] args) {
        // 启动游戏
        FakePicture app = new FakePicture();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        cam.setLocation(new Vector3f(5.076195f, 5.100953f, 10.473327f));
        cam.setRotation(new Quaternion(-0.03069693f, 0.96919596f, -0.16531673f, -0.17996438f));
        
        // 改变视锥大小
        cam.setFrustum(-999, 999, 4, -4, 3, -3);
        // 开启平行投影
        cam.setParallelProjection(true);

        // 创建X、Y、Z方向的箭头，作为参考坐标系。
        createArrow(new Vector3f(5, 0, 0), ColorRGBA.Green);
        createArrow(new Vector3f(0, 5, 0), ColorRGBA.Red);
        createArrow(new Vector3f(0, 0, 5), ColorRGBA.Blue);

        // 加载“图片”
        Spatial pic = getPicture();

        // 将“图片”添加到场景图中
        rootNode.attachChild(pic);
    }

    /**
     * 创建一个“图片”
     * 
     * @return
     */
    private Spatial getPicture() {
        // 创建一个四边形
        Quad quad = new Quad(4, 3);
        Geometry geom = new Geometry("Picture", quad);

        // 加载图片
        Texture tex = assetManager.loadTexture("Interface/Gui/pic.png");

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        // 应用这个材质
        geom.setMaterial(mat);

        return geom;
    }

    /**
     * 创建一个箭头
     * 
     * @param vec3
     *            箭头向量
     * @param color
     *            箭头颜色
     */
    private void createArrow(Vector3f vec3, ColorRGBA color) {
        // 创建材质，设定箭头的颜色
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);

        // 创建几何物体，应用箭头网格。
        Geometry geom = new Geometry("arrow", new Arrow(vec3));
        geom.setMaterial(mat);

        // 添加到场景中
        rootNode.attachChild(geom);
    }

}
