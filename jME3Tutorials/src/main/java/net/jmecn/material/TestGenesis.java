package net.jmecn.material;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.jme3.water.WaterFilter;

/**
 * 创世纪
 * 
 * @author yanmaoyuan
 *
 */
public class TestGenesis extends SimpleApplication {

    // 光源
    private DirectionalLight sunLight;
    private AmbientLight ambientLight;
    private SpotLight spotLight;

    private Vector3f sunLightDir = new Vector3f(-0.13969503f, -0.38276914f, 0.91322124f);

    // 滤镜处理器
    private FilterPostProcessor fpp;

    public TestGenesis() {
        super(new ScreenshotAppState(""), new FlyCamAppState(), new DebugKeysAppState());
    }

    /**
     * 初始化摄像机
     */
    private void initCamera() {
        // 改变摄像位置，好让我们看清整个场景。
        cam.setLocation(new Vector3f(-23.537182f, 13.784662f, -13.3079605f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

        // 改变第一人称摄像机移动速度
        flyCam.setMoveSpeed(10);
    }

    /**
     * 创造天空
     */
    private void createSky() {
        Spatial skyCube = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds",
                EnvMapType.CubeMap);

        rootNode.attachChild(skyCube);
    }

    /**
     * 创造一个小球
     */
    private void createSphere() {
        Material mat = assetManager.loadMaterial("Textures/Terrain/Rock/Rock.j3m");

        Geometry geom = new Geometry("球体", new Sphere(20, 40, 2));
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Cast);

        geom.move(0, 5, 0);
        rootNode.attachChild(geom);
    }

    /**
     * 创造一块地板
     */
    private void createFloor() {
        Material mat = assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m");

        Geometry geom = new Geometry("地板", new Box(20, 0.2f, 20));
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Receive);

        rootNode.attachChild(geom);

    }

    /**
     * 创造光源
     */
    private void createLight() {
        // 直射光
        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White.mult(0.8f));
        sunLight.setDirection(sunLightDir);

        // 环境光
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White.mult(0.2f));

        // 聚光灯
        spotLight = new SpotLight();
        spotLight.setPosition(new Vector3f(12.649755f, 21.236967f, 0.36793f));
        spotLight.setDirection(new Vector3f(-0.5475501f, -0.8365753f, -0.018176913f));
        spotLight.setColor(ColorRGBA.White.mult(2));
        spotLight.setSpotRange(60);
        spotLight.setSpotInnerAngle(10 * FastMath.DEG_TO_RAD);
        spotLight.setSpotOuterAngle(20 * FastMath.DEG_TO_RAD);

        rootNode.addLight(sunLight);
        rootNode.addLight(ambientLight);
        rootNode.addLight(spotLight);
    }

    /**
     * 绘制光线
     */
    private void createLightScattering() {
        Vector3f lightPos = sunLightDir.mult(-3000);
        LightScatteringFilter filter = new LightScatteringFilter(lightPos);
        fpp.addFilter(filter);
    }

    /**
     * 绘制影子
     */
    private void createShadow() {
        int shadowMapSize = 512;

        // 阳光的影子
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, shadowMapSize, 3);
        dlsf.setLight(sunLight);

        // 聚光灯的影子
        SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, shadowMapSize);
        slsf.setLight(spotLight);

        fpp.addFilter(dlsf);
        fpp.addFilter(slsf);
    }
    
    /**
     * 绘制水面
     */
    private void createWater() {
        final WaterFilter water = new WaterFilter(rootNode, sunLightDir);
        water.setWaterHeight(-3);

        fpp.addFilter(water);
    }

    /**
     * 初始化场景
     */
    @Override
    public void simpleInitApp() {
        fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        initCamera();

        createSky();
        createSphere();
        createFloor();

        createLight();
        createLightScattering();
        createShadow();
        createWater();
    }

    public static void main(String[] args) {
        TestGenesis app = new TestGenesis();
        app.start();
    }

}