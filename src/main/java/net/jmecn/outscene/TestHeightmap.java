package net.jmecn.outscene;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;

/**
 * 演示高度图是如何变成地形的
 * @author yanmaoyuan
 *
 */
public class TestHeightmap extends SimpleApplication implements ActionListener {

    // 图片分辨率
    private int width;
    private int height;
    // 高度数据
    private float[] heightData;

    // 网格
    private Mesh points;// 点云
    private Mesh triangles;// 三角形
    private boolean isPoints = false;
    
    // 地形
    private Geometry terrain;
    // 材质
    private Material material;
    
    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-47.20265f, 216.08107f, 186.42526f));
        cam.setRotation(new Quaternion(0.05941244f, 0.8785786f, -0.46010754f, 0.1134501f));
        flyCam.setMoveSpeed(100);
        
        /**
         * 加载地形的高度图
         */
        Texture heightMapImage = assetManager.loadTexture("Scenes/Maps/DefaultMap/default.png");
        Image image = heightMapImage.getImage();

        /**
         * 从图像中读取高度数组
         */
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.heightData = new float[width * height];

        ImageRaster imageRaster = ImageRaster.create(image);
        ColorRGBA color = new ColorRGBA();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                imageRaster.getPixel(x, y, color);
                // 根据灰度，计算高度。
                heightData[x + y * width] = grayScale(color);
            }
        }

        // 高斯平滑
        GaussianBlur gaussianBlur = new GaussianBlur();
        heightData = gaussianBlur.filter(heightData, width, height);
        
        // 加载材质
        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setBoolean("VertexColor", true);

        // 点云网格
        points = createPointCloud();
        // 三角网格
        triangles = createTriMesh();

        // 生成几何体
        terrain = new Geometry("heightmap", triangles);
        terrain.setMaterial(material);
        terrain.center();

        rootNode.attachChild(terrain);
        
        /**
         * 输出响应
         */
        inputManager.addMapping("changeMesh", new KeyTrigger(KeyInput.KEY_TAB));
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "changeMesh", "wireframe");
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if ("changeMesh".equals(name)) {
                if (isPoints) {
                    terrain.setMesh(triangles);
                } else {
                    terrain.setMesh(points);
                }
                terrain.updateModelBound();
                isPoints = !isPoints;
            } else if ("wireframe".equals(name)) {
                boolean isWireframe = material.getAdditionalRenderState().isWireframe();
                material.getAdditionalRenderState().setWireframe(!isWireframe);
            }
        }
        
    }
    
    /**
     * 生成点云网格
     * 
     * @return
     */
    private Mesh createPointCloud() {

        // 计算顶点坐标、顶点颜色、顶点索引
        Vector3f[] positions = new Vector3f[width * height];
        ColorRGBA[] colors = new ColorRGBA[width * height];
        int[] indexes = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = x + y * width;
                positions[index] = new Vector3f(x, heightData[index] * 255, -y);
                colors[index] = new ColorRGBA(heightData[index], heightData[index], heightData[index], 1f);
                indexes[index] = index;
            }
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        mesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));
        mesh.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(colors));
        mesh.setMode(Mode.Points);
        mesh.setStatic();
        mesh.updateBound();

        return mesh;
    }

    /**
     * 生成三角网格
     * @return
     */
    private Mesh createTriMesh() {
        // 计算顶点坐标、顶点颜色
        Vector3f[] positions = new Vector3f[width * height];
        ColorRGBA[] colors = new ColorRGBA[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = x + y * width;
                positions[index] = new Vector3f(x, heightData[index] * 255, -y);
                colors[index] = new ColorRGBA(heightData[index], heightData[index], heightData[index], 1f);
            }
        }

        // 顶点索引
        int[] indexes = new int[(width - 1) * (height - 1) * 2 * 3];

        int count = 0;
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width - 1; x++) {
                int n0 = x + y * width;
                int n1 = (x + 1) + y * width;
                int n2 = (x + 1) + (y + 1) * width;
                int n3 = x + (y + 1) * width;

                //n0, n1, n2,
                indexes[count] = n0;
                indexes[count+1] = n1;
                indexes[count+2] = n2;
                //n0, n2, n3,
                indexes[count+3] = n0;
                indexes[count+4] = n2;
                indexes[count+5] = n3;
                count += 6;
            }
        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(positions));
        mesh.setBuffer(Type.Index, 3, BufferUtils.createIntBuffer(indexes));
        mesh.setBuffer(Type.Color, 4, BufferUtils.createFloatBuffer(colors));
        mesh.setMode(Mode.Triangles);
        mesh.setStatic();
        mesh.updateBound();

        return mesh;
    }

    /**
     * 使用加权平均值，计算象素的灰度。
     * 
     * @param color
     * @return
     */
    private float grayScale(ColorRGBA color) {
        return (float) (0.299 * color.r + 0.587 * color.g + 0.114 * color.b);
    }

    public static void main(String[] args) {
        TestHeightmap app = new TestHeightmap();
        app.start();
    }


}
