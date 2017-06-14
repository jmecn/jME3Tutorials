package net.jmecn.physics2d.basket;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

public class AssetFactory {

	public final static String BASKET_BALL = "Textures/Dyn4j/Samples/Basketball.png";
	public final static String CRATE = "Textures/Dyn4j/Samples/Crate.png";
	public final static String CIRCLE = "Textures/Dyn4j/Samples/Circle.png";
	
	private static AssetManager assetManager;

	public static void setAssetManager(AssetManager assetManager) {
		AssetFactory.assetManager = assetManager;
	}
	
	public static Spatial makeBall() {
		return createVisual(0.246f, 0.246f, BASKET_BALL);
	}
	
	public static Spatial makeDebugRect(float width, float height) {
		return createVisual(width, height, CRATE);
	}
	
	public static Spatial makeDebugCircle(float radius) {
		return createVisual(radius * 2, radius * 2, CIRCLE);
	}
	
    public static Spatial createVisual(float width, float height, String tex) {

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        if (tex == null) {
            mat.setColor("Color", new ColorRGBA(0.1f, 0.2f, 0.3f, 1f));
        } else {
            try {
                Texture texture = assetManager.loadTexture(tex);
                mat.setTexture("ColorMap", texture);
                mat.setColor("Color", ColorRGBA.White);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // create spatial
        MyQuad quad = new MyQuad(width, height);
        Geometry geom = new Geometry("quad", quad);
        geom.setMaterial(mat);

        return geom;
    }
}
