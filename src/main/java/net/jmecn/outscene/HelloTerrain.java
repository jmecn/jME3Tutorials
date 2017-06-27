package net.jmecn.outscene;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

public class HelloTerrain extends SimpleApplication {
    @Override
    public void simpleInitApp() {

        cam.setLocation(new Vector3f(-63.904045f, 298.0622f, 177.26915f));
        cam.setRotation(new Quaternion(0.08737865f, 0.8777567f, -0.43718588f, 0.17543258f));

        flyCam.setMoveSpeed(100f);

        initLight();

        initSky();

        initWater();

        initTerrain();
    }

    private void initLight() {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.298f, 0.2392f, 0.2745f, 1f));
        rootNode.addLight(ambient);

        DirectionalLight light = new DirectionalLight();
        light.setDirection((new Vector3f(0.097551f, -0.733139f, -0.673046f)).normalize());
        light.setColor(new ColorRGBA(1, 1, 1, 1));
        rootNode.addLight(light);
    }

    /**
     * 初始化天空
     */
    private void initSky() {
        // 天空盒
        Spatial sky = SkyFactory.createSky(assetManager, "Textures/Sky/SkySphereMap.jpg",
                SkyFactory.EnvMapType.SphereMap);
        rootNode.attachChild(sky);
    }
    
    /**
     * 初始化水面
     */
    private void initWater() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        // 水
        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setWaterHeight(59.75f);
        waterFilter.setWaveScale(10f);
        waterFilter.setWaterTransparency(0.5f);
        waterFilter.setSpeed(2f);
        waterFilter.setWaterColor(new ColorRGBA(0.4314f, 0.9373f, 0.8431f, 1f));
        waterFilter.setLightDirection(new Vector3f(0.097551f, -0.733139f, -0.673046f));

        fpp.addFilter(waterFilter);
    }

    /**
     * 初始化地形
     */
    private void initTerrain() {

        // 加载地形的高度图
        Texture heightMapImage = assetManager.loadTexture("Scenes/Maps/DefaultMap/default.png");

        // 根据图像内容，生成高度图
        ImageBasedHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
        heightmap.load();

        /**
         * Optimal terrain patch size is 65 (64x64). The total size is up to
         * you. At 1025 it ran fine for me (200+FPS), however at size=2049, it
         * got really slow. But that is a jump from 2 million to 8 million
         * triangles...
         */
        /*
         * 根据高度图生成实际的地形。图片的分辨率为257 (256*256)。
         */
        TerrainQuad terrain = new TerrainQuad("terrain", 65, 257, heightmap.getHeightMap());

        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        // patch size, and a multiplier
        control.setLodCalculator(new DistanceLodCalculator (65, 2f));
        terrain.addControl(control);

        terrain.setMaterial(assetManager.loadMaterial("Scenes/Maps/DefaultMap/default.j3m"));

        terrain.setLocalTranslation(0, -90, 0);
        terrain.setLocalScale(1, 1, 1);
        rootNode.attachChild(terrain);
    }

    public static void main(String[] args) {
        HelloTerrain app = new HelloTerrain();
        app.start();
    }

}
