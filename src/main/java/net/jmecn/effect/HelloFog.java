package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;

import net.jmecn.state.CubeAppState;

/**
 * 演示雾化滤镜(FogFilter)的作用
 * 
 * @author yanmaoyuan
 *
 */
public class HelloFog extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        stateManager.attach(new CubeAppState());
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.setNumSamples(4);// 4倍抗拒齿
        viewPort.addProcessor(fpp);

        // 雾化滤镜
        FogFilter fogFilter = new FogFilter(ColorRGBA.White, 1.5f, 100f);
        fpp.addFilter(fogFilter);

        flyCam.setMoveSpeed(10f);
    }

    public static void main(String[] args) {
        HelloFog app = new HelloFog();
        app.start();
    }

}
