package net.jmecn.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;
import com.jme3.shadow.SpotLightShadowRenderer;

/**
 * 灯光模块
 * @author yanmaoyuan
 *
 */
public class LightAppState extends BaseAppState {

	private Node rootNode;
	
	// 光源点
	private Node node = new Node("LightSources");
	
	private ViewPort viewPort;
	private AssetManager assetManager;
	
	// 灯光渲染模式
	private LightMode lm = TechniqueDef.LightMode.SinglePass;
	
	// 光源
	private AmbientLight al;
	private SpotLight sl;
	private PointLight[] pls;
	
	// 阴影渲染器
	private SpotLightShadowRenderer slsr;
	private PointLightShadowRenderer[] plsrs;
	
	// 后置滤镜处理器
	private FilterPostProcessor fpp;
	
	@Override
	protected void initialize(Application app) {
		
		viewPort = app.getViewPort();
		assetManager = app.getAssetManager();
		
		SimpleApplication simpleApp = (SimpleApplication) app;
		rootNode = simpleApp.getRootNode();
		rootNode.setShadowMode(ShadowMode.CastAndReceive);
		
		// 设置灯光渲染模式为单通道
		RenderManager renderManager = app.getRenderManager();
		renderManager.setPreferredLightMode(lm);
        renderManager.setSinglePassLightBatchSize(7);
        
        /**
         * 光源
         */
        // 环境光
        al = new AmbientLight();
        
        // 聚光灯
        sl = new SpotLight(new Vector3f(0f, 25f, 0f), new Vector3f(0, -1, 0), ColorRGBA.White);
        sl.setSpotOuterAngle(0.3f);
        sl.setSpotInnerAngle(0.05f);
        
        lightSource(new Vector3f(0f, 25f, 0f), ColorRGBA.White);
        
        // 点光源
        pls = new PointLight[4];
        pls[0] = createPointLight(new Vector3f(13, 8, 13), ColorRGBA.Yellow);
        pls[1] = createPointLight(new Vector3f(13, 8, -13), ColorRGBA.Yellow);
        pls[2] =  createPointLight(new Vector3f(-13, 8, 13), ColorRGBA.Yellow);
        pls[3] = createPointLight(new Vector3f(-13, 8, -13), ColorRGBA.Yellow);

        /**
         * 影子
         */
        // 聚光灯
        slsr = new SpotLightShadowRenderer(assetManager, 1024);
        slsr.setLight(sl);// 设置光源
        slsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        
        // 点光源影子
        plsrs = new PointLightShadowRenderer[4];
        for(int i=0; i<4; i++) {
	        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, 1024);
	        plsr.setLight(pls[i]);// 设置光源
	        plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
	        plsrs[i] = plsr;
        }
        
        /**
         * 发光滤镜
         */
        fpp = new FilterPostProcessor(assetManager);
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        
        // 4倍抗锯齿
        fpp.setNumSamples(4);
	}

	/**
	 * 创建一个小球，表示光源的位置。
	 */
	private void lightSource(Vector3f position, ColorRGBA color) {
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	mat.setColor("Color", color);
    	mat.setColor("GlowColor", color);
    	
    	Geometry geom = new Geometry("LightSource", new Sphere(6, 12, 0.2f));
    	geom.setMaterial(mat);
    	geom.setLocalTranslation(position);
    	geom.setShadowMode(ShadowMode.Off);
    	
    	node.attachChild(geom);
	}
    /**
     * 创造一个点光源
     * @param position
     * @param color
     */
    private PointLight createPointLight(Vector3f position, ColorRGBA color) {
        PointLight pointLight = new PointLight(position, color);
        pointLight.setRadius(18);
        
        lightSource(position, color);
        
        return pointLight;
    }
    
	@Override
	protected void cleanup(Application app) {}

	@Override
	protected void onEnable() {
		// 添加光源
		rootNode.addLight(al);
		rootNode.addLight(sl);
		for(PointLight p : pls) {
			rootNode.addLight(p);
		}

		// 添加影子
		viewPort.addProcessor(slsr);
		for(PointLightShadowRenderer plsr : plsrs) {
			viewPort.addProcessor(plsr);
		}
		
		// 添加滤镜
        viewPort.addProcessor(fpp);
        
		// 添加光源节点
		rootNode.attachChild(node);
	}

	@Override
	protected void onDisable() {
		// 移除光源节点
		node.removeFromParent();
		
		viewPort.removeProcessor(fpp);
		
		viewPort.removeProcessor(slsr);
		for(PointLightShadowRenderer plsr : plsrs) {
			viewPort.removeProcessor(plsr);
		}
		
		// 移除光源
		rootNode.removeLight(al);
		rootNode.removeLight(sl);
		for(PointLight p : pls) {
			rootNode.removeLight(p);
		}
	}

}
