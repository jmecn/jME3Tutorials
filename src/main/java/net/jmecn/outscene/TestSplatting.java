package net.jmecn.outscene;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

public class TestSplatting extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        Quad quad = new Quad(4f, 4f);
        Geometry geom = new Geometry("splatting", quad);
        Material mat = assetManager.loadMaterial("Scenes/Maps/DefaultMap/default.j3m");
        geom.setMaterial(mat);
        geom.center();
        
        rootNode.attachChild(geom);
        
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(0, 0, -1));
        rootNode.addLight(light);
        
    }

    public static void main(String[] args) {
        TestSplatting app = new TestSplatting();
        app.start();
    }

}
