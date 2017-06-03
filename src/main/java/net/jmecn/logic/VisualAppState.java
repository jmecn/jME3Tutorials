package net.jmecn.logic;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * 管理自场景的AppState
 * 
 * @author yanmaoyuan
 *
 */
public class VisualAppState implements AppState {
    
    private boolean initialized = false;
    private boolean enabled = true;

    /**
     * 创建一个独立的根节点，便于管理子场景。
     */
    private Node sceneNode = new Node("MyScene");
    
    private Geometry cube = null;
    
    /**
     * 对于那些我们用得上的系统对象，保存一份对象的引用。
     */
    private SimpleApplication simpleApp;
    private AssetManager assetManager;
    
    @Override
    public void stateAttached(AppStateManager stateManager) {}
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.simpleApp = (SimpleApplication) app;
        this.assetManager = app.getAssetManager();
        
        // 创建一个方块
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Ambient", ColorRGBA.Red);
        mat.setColor("Specular", ColorRGBA.Black);
        mat.setFloat("Shininess", 1);
        mat.setBoolean("UseMaterialColors", true);
        
        Mesh mesh = new Box(1, 1, 1);
        
        cube = new Geometry("Cube", mesh);
        cube.setMaterial(mat);
        
        // 将方块添加到我们这个场景中。
        sceneNode.attachChild(cube);
        
        // 初始化完毕
        initialized = true;
        
        if (enabled)
            simpleApp.getRootNode().attachChild(sceneNode);
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setEnabled(boolean active) {
        if ( this.enabled == active )
            return;
        this.enabled = active;
        
        if (!initialized)
            return;
        
        if (enabled) {
            simpleApp.getRootNode().attachChild(sceneNode);
        } else {
            sceneNode.removeFromParent();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void update(float tpf) {
        cube.rotate(0, tpf * FastMath.PI, 0);
    }
    
    @Override
    public void render(RenderManager rm) {}
    
    @Override
    public void postRender() {}

    @Override
    public void stateDetached(AppStateManager stateManager) {}

    @Override
    public void cleanup() {
        if (enabled)
            sceneNode.removeFromParent();
        
        initialized = false;
    }

}
