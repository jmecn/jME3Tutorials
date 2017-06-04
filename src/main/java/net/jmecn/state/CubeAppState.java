package net.jmecn.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 * 这是一个测试用场景
 * 
 * @author yanmaoyuan
 *
 */
public class CubeAppState extends BaseAppState {

    private Node rootNode = new Node("Scene root");
    private AmbientLight ambient;
    private DirectionalLight sun;
    
    private AssetManager assetManager;
    
    @Override
    protected void initialize(Application app) {
        
        this.assetManager = app.getAssetManager();
        
        // 创造地板
        Geometry geom = new Geometry("Floor", new Quad(40, 40));
        geom.setMaterial(getMaterial(ColorRGBA.Orange));
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geom);

        for (int y = 0; y <= 32; y += 4) {
            for (int x = 0; x <= 32; x += 4) {
                geom = new Geometry("Cube", new Box(0.5f, 0.5f, 0.5f));
                geom.setMaterial(getMaterial(new ColorRGBA(1-x/32f, y/32f, 1f, 1f)));
                geom.move(x + 4, 0.5f, -y - 4);
                rootNode.attachChild(geom);
            }
        }

        // 创造光源
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        sun.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));

        ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));
    }
    
    private Material getMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color);
        mat.setColor("Specular", ColorRGBA.Black);
        mat.setFloat("Shininess", 1f);
        mat.setBoolean("UseMaterialColors", true);
        
        return mat;
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        SimpleApplication app = (SimpleApplication) getApplication();
        
        app.getRootNode().attachChild(rootNode);
        app.getRootNode().addLight(ambient);
        app.getRootNode().addLight(sun);
    }

    @Override
    protected void onDisable() {
        SimpleApplication app = (SimpleApplication) getApplication();
        
        app.getRootNode().detachChild(rootNode);
        app.getRootNode().removeLight(ambient);
        app.getRootNode().removeLight(sun);

    }

}
