package net.jmecn.outscene;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

/**
 * 演示3种天空盒的用法
 * @author yanmaoyuan
 *
 */
public class HelloSky extends SimpleApplication implements ActionListener {

    Spatial sky1;
    Spatial sky2;
    Spatial sky3;
    Spatial sky4;
    Spatial sky5;
    Spatial sky6;
    Spatial sky7;

    int current = -1;
    Spatial[] skies;
    
    @Override
    public void simpleInitApp() {

        // 天空盒
        Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
        Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
        Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
        Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
        Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
        Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
        sky1 = SkyFactory.createSky(assetManager, west, east, north, south, up, down);

        // 天空盒
        sky2 = SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);

        // 天球，圣彼得大教堂
        sky3 = SkyFactory.createSky(assetManager, assetManager.loadTexture("Textures/Sky/St Peters/StPeters.hdr"),
                new Vector3f(1, -1, 1), // 图片上下颠倒，故改变Y方向的法线
                SkyFactory.EnvMapType.SphereMap);

        // 天球
        sky4 = SkyFactory.createSky(assetManager, "Textures/Sky/SkySphereMap.jpg", SkyFactory.EnvMapType.SphereMap);

        // 天球
        sky5 = SkyFactory.createSky(assetManager, "Textures/Sky/SkyEquirectMap.jpg", SkyFactory.EnvMapType.EquirectMap);

        // 地球
        sky6 = SkyFactory.createSky(assetManager, "Textures/Sky/earth.jpg", SkyFactory.EnvMapType.EquirectMap);

        // 天球，林间小径
        sky7 = SkyFactory.createSky(assetManager, "Textures/Sky/Path.hdr", SkyFactory.EnvMapType.EquirectMap);

        skies = new Spatial[]{sky1, sky2, sky3, sky4, sky5, sky6, sky7};
        
        setCurrentSky(0);
        
        inputManager.addMapping("NEXT", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("BACK", new KeyTrigger(KeyInput.KEY_BACK));
        inputManager.addListener(this, "BACK", "NEXT");
    }
    
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if ("NEXT".equals(name)) {
                if (current < skies.length-1)
                    setCurrentSky(current+1);
                else
                    setCurrentSky(0);
            } else if ("BACK".equals(name)){
                if (current > 0)
                    setCurrentSky(current-1);
                else
                    setCurrentSky(skies.length-1);
            }
        }
        
    }

    /**
     * 设置当前天空
     * @param index
     */
    private void setCurrentSky(int index) {
        if (current >= 0) {
            skies[current].removeFromParent();
        }
        
        current = index;
        rootNode.attachChild(skies[current]);
    }

    public static void main(String[] args) {
        HelloSky app = new HelloSky();
        app.start();
    }


}
