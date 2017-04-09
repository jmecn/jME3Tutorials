package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;

/**
 * 光与影
 * @author yanmaoyuan
 *
 */
public class HelloLight extends SimpleApplication {

    // 定向光
    private DirectionalLight sunLight;
    // 点光源
    private PointLight pointLight;
    // 环境光
    private AmbientLight ambientLight;
    
    public static void main(String[] args) {
        // 启动程序
        HelloLight app = new HelloLight();
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
        addLight();
        
        // 添加滤镜
        addShadow();
    }
    
    /**
     * 创建一个场景
     * @return
     */
    private void addObjects() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        // 创建一个平面，把它作为地板，用来承载光影
        Geometry geom = new Geometry("Floor",  new Quad(20, 20));
        geom.setMaterial(mat);
        geom.setShadowMode(ShadowMode.Receive);// 承载阴影
        
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geom);
        
        // 创造多个方块
        for(int y=0; y<10; y+=3) {
            for(int x=0; x<10; x+=3) {
                geom = new Geometry("Cube", new Box(0.5f, 0.5f, 0.5f));
                geom.setMaterial(mat);
                geom.setShadowMode(ShadowMode.Cast);// 产生阴影
                geom.move(x+4, 0.5f, -y-4);
                rootNode.attachChild(geom);
            }
        }
        
    }
    
    /**
     * 添加光源
     */
    private void addLight() {
        
        // 定向光
        sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-1, -2, -3));
        sunLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

        // 点光源
        pointLight = new PointLight();
        pointLight.setPosition(new Vector3f(5.5f, 4, -5.5f));
        pointLight.setRadius(30);
        pointLight.setColor(new ColorRGBA(0.8f, 0.8f, 0f, 1f));
        
        // 环境光
        ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
        
        // 将模型和光源添加到场景图中
        rootNode.addLight(sunLight);
        rootNode.addLight(pointLight);
        rootNode.addLight(ambientLight);
    }
    
    /**
     * 添加影子
     */
    private void addShadow() {

        int shadowMapSize = 512;
        EdgeFilteringMode mode = EdgeFilteringMode.PCFPOISSON;
        
        // 定向光影子
        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, shadowMapSize, 4);
        dlsr.setLight(sunLight);// 设置定向光源
        dlsr.setEdgeFilteringMode(mode);
        viewPort.addProcessor(dlsr);
        
        // 点光源影子
        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, shadowMapSize);
        plsr.setLight(pointLight);// 设置点光源
        plsr.setEdgeFilteringMode(mode);
        viewPort.addProcessor(plsr);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
    }
}
