package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 * Test Fog Ligthing shader
 * 
 * @author yanmaoyuan
 *
 */
public class TestFogLightingShader extends SimpleApplication {

	@Override
    public void simpleInitApp() {
		
        createScene();

        addLights();
        
        flyCam.setMoveSpeed(10f);
        
        Material mat = new Material(assetManager, "Materials/Fog/Lighting.j3md");
        mat.setBoolean("UseFog", false);// disable the fog
        
        int size = rootNode.getChildren().size();
        Spatial divine = rootNode.getChild(size/2);
        divine.setMaterial(mat);
    }

    private void createScene() {
        Material mat = new Material(assetManager, "Materials/Fog/Lighting.j3md");
        mat.setBoolean("UseFog", true);// Let the fog work

        Geometry geom = new Geometry("Floor", new Quad(40, 40));
        geom.setMaterial(mat);
        geom.rotate(-FastMath.HALF_PI, 0, 0);
        rootNode.attachChild(geom);

        for (int y = 0; y < 33; y += 4) {
            for (int x = 0; x < 33; x += 4) {
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
		TestFogLightingShader app = new TestFogLightingShader();
		app.start();
	}

}
