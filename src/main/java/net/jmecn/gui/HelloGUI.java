package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * GuiNode
 * @author yanmaoyuan
 *
 */
public class HelloGUI extends SimpleApplication {

    public static void main(String[] args) {
        // 启动程序
        HelloGUI app = new HelloGUI();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        // 创建X、Y、Z方向的箭头，作为参考坐标系。
        createArrow(new Vector3f(500, 0, 0), ColorRGBA.Green);
        createArrow(new Vector3f(0, 500, 0), ColorRGBA.Red);
        createArrow(new Vector3f(0, 0, 500), ColorRGBA.Blue);

        // 加载“图片”
        Spatial pic = getPicture();
        
        // 将“图片”添加到GUI场景中
        guiNode.attachChild(pic);
    }

    /**
     * 创建一个“图片”
     * @return
     */
    private Spatial getPicture() {
        
        // 获得屏幕分辨率
        float width = cam.getWidth();
        float height = cam.getHeight();
        
        // 创建一个四边形
        Quad quad = new Quad(width, height);
        Geometry geom = new Geometry("Picture", quad);
        
        // 将Z坐标设为-1
        geom.setLocalTranslation(0, 0, -1);

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
     * @param vec3  箭头向量
     * @param color 箭头颜色
     */
    private void createArrow(Vector3f vec3, ColorRGBA color) {
        // 创建材质，设定箭头的颜色
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);

        // 创建几何物体，应用箭头网格。
        Geometry geom = new Geometry("arrow", new Arrow(vec3));
        geom.setMaterial(mat);

        // 添加到GUI场景中
        guiNode.attachChild(geom);
    }
}
