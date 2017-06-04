package net.jmecn.thread;

import java.util.Random;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

public class VisualAppState extends BaseAppState {

    private World world;
    
    private Node rootNode = new Node();
    private AssetManager assetManager;
    private Camera camera;
    
    public VisualAppState(World world) {
        rootNode.setLocalTranslation(0, 0, -1);
        this.world = world;
    }
    
    @Override
    protected void initialize(Application app) {
        this.assetManager = app.getAssetManager();
        this.camera = app.getCamera();
        
        float width = camera.getWidth();
        float height = camera.getHeight();
        float radius = 6;
        float speed = 30;
        
        Random rand = FastMath.rand;
        
        for(int i=0; i<2000; i++) {
            BodyControl body = new BodyControl();
            world.addBody(body);
            
            // 计算半径
            body.radius = radius + rand.nextFloat() * radius;
            
            // 计算初始位置
            float x = rand.nextFloat() * (width - body.radius * 2) + body.radius;
            float y = rand.nextFloat() * (height - body.radius * 2) + body.radius;
            body.position.set(x, y, 0);
            
            // 计算初速度
            body.speed = speed + rand.nextFloat() * speed;
            x = rand.nextFloat();
            y = rand.nextFloat();
            body.velocity.set(x, y, 0);
            body.velocity.normalizeLocal();
            body.velocity.multLocal(body.speed);
            
            
            Spatial spatial = createSpatial(body);
            rootNode.attachChild(spatial);
        }
    }

    private Spatial createSpatial(BodyControl body) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
        
        Sphere sphere = new Sphere(3, 12, body.radius);
        Geometry geom = new Geometry("sphere", sphere);
        geom.setMaterial(mat);
        
        geom.addControl(body);
        
        return geom;
    }
    
    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        SimpleApplication app = (SimpleApplication) getApplication();
        app.getGuiNode().attachChild(rootNode);
    }

    @Override
    protected void onDisable() {
        rootNode.removeFromParent();
    }

}
