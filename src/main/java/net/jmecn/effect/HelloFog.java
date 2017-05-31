package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 * 演示雾化滤镜(FogFilter)的作用
 * 
 * @author yanmaoyuan
 *
 */
public class HelloFog extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.setNumSamples(4);
        viewPort.addProcessor(fpp);

        FogFilter fog = new FogFilter(ColorRGBA.White, 1.5f, 20f);
        fpp.addFilter(fog);

        createScene();

        addLights();
        
        flyCam.setMoveSpeed(10f);
    }

    private void createScene() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");

        Geometry geom = new Geometry("Floor", new Quad(40, 40));
        geom.setMaterial(mat);
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geom);

        for (int y = 0; y < 30; y += 4) {
            for (int x = 0; x < 30; x += 4) {
                geom = new Geometry("Cube", new Box(0.5f, 0.5f, 0.5f));
                geom.setMaterial(mat);
                geom.move(x + 4, 0.5f, -y - 4);
                rootNode.attachChild(geom);
            }
        }
    }

    private void addLights() {
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());
        sunLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

        rootNode.addLight(sunLight);
        rootNode.addLight(ambientLight);
    }

    public static void main(String[] args) {
        HelloFog app = new HelloFog();
        app.start();
    }

}
