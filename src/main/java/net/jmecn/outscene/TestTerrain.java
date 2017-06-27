package net.jmecn.outscene;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 * 演示加载高度图
 * 
 * @author yanmaoyuan
 *
 */
public class TestTerrain extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(100);

        // 加载材质
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);

        // 加载高度图
        Texture heightMapImage = assetManager.loadTexture("Scenes/Maps/DefaultMap/default.png");

        // 根据图像内容，生成高度数据
        ImageBasedHeightMap heightMap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
        heightMap.load();
        float[] heightData = heightMap.getHeightMap();

        // 根据高度图生成3D地形。
        // 该地形被分解成边长65(64*64)的矩形区块，用于优化网格。
        // 高度图的边长为 257，分辨率 256*256。
        TerrainQuad terrain = new TerrainQuad("heightmap", 65, 257, heightData);
        terrain.setMaterial(material);
        terrain.center();

        rootNode.attachChild(terrain);

    }

    public static void main(String[] args) {
        TestTerrain app = new TestTerrain();
        app.start();
    }

}
