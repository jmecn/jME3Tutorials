package net.jmecn.physics3d.jaime;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 * 演示使用第三人称控制Jaime在地图中自由行走。
 * 
 * @author yanmaoyuan
 *
 */
public class Main extends SimpleApplication {

    public Main() {
        super(new StatsAppState());
    }

    @Override
    public void simpleInitApp() {
        stateManager.attachAll(new BulletAppState(), 
                new SceneAppState(), 
                new CharacterAppState(),
                new InputAppState());
        
        // 环境光
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.7f, 0.7f, 0.7f, 1f));

        // 阳光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());

        rootNode.addLight(ambient);
        rootNode.addLight(sun);
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }
}
