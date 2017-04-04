package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.debug.Arrow;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

/**
 * 自定义网格，制作一个六边形。
 * 
 * @author yanmaoyuan
 *
 */
public class HelloMesh extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(4.893791f, 4.5420675f, 9.626116f));
        cam.setRotation(new Quaternion(-0.031222044f, 0.9664778f, -0.14307737f, -0.21089031f));

        flyCam.setMoveSpeed(10);

        // 创建六边形
        createHex();

        // 创建X、Y、Z方向的箭头，作为参考坐标系。
        createArrow(new Vector3f(5, 0, 0), ColorRGBA.Green);
        createArrow(new Vector3f(0, 5, 0), ColorRGBA.Red);
        createArrow(new Vector3f(0, 0, 5), ColorRGBA.Blue);
        
        viewPort.setBackgroundColor(ColorRGBA.LightGray);
    }

    /**
     * 创建一个六边形
     */
    private void createHex() {
        // 六个顶点
        float[] vertex = {
                2.5f, 4f, 0f, // 零
                1f, 3.26f, 0f,// 壹
                1f, 1.74f, 0f,// 贰
                2.5f, 1f, 0f, // 叁
                4f, 1.74f, 0f,// 肆
                4f, 3.26f, 0f // 伍
        };

        // 纹理坐标
        float[] texCoords = new float[] {
               0.5f, 0.75f,  // 零
               0.25f, 0.625f,// 壹
               0.25f, 0.375f,// 贰
               0.5f, 0.25f,  // 叁
               0.75f, 0.375f,// 肆
               0.75f, 0.625f // 伍
        };
        
        // 四个三角形
        int[] indices = new int[] {
                0, 1, 2, // 三角形0
                2, 3, 4, // 三角形1
                4, 5, 0, // 三角形2
                0, 2, 4 // 三角形3
        };

        // 创建网格
        Mesh mesh = new Mesh();
        // 保存顶点位置和顶点索引
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertex));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords));
        mesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indices));

        mesh.updateBound();
        mesh.setStatic();

        // 创建材质，使我们可以看见这个六边形
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        // mat.getAdditionalRenderState().setWireframe(true);

        // 设置纹理贴图
        Texture tex = assetManager.loadTexture("Models/Hexagon/hex.png");
        mat.setTexture("ColorMap", tex);
        
        // 使用网格和材质创建一个物体
        Geometry geom = new Geometry("六边形");
        geom.setMesh(mesh);
        geom.setMaterial(mat);
        geom.center();

        // 将物体添加到场景图中
        rootNode.attachChild(geom);
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

        // 添加到场景中
        rootNode.attachChild(geom);
    }

    public static void main(String[] args) {
        // 启动程序
        HelloMesh app = new HelloMesh();
        app.start();
    }
}