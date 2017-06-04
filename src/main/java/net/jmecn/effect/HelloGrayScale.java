package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.post.FilterPostProcessor;

import net.jmecn.state.CubeAppState;

/**
 * 演示“灰度”滤镜的作用
 * 
 * @author yanmaoyuan
 *
 */
public class HelloGrayScale extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        stateManager.attach(new CubeAppState());
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.setNumSamples(4);
        viewPort.addProcessor(fpp);

        GrayScaleFilter grayScale = new GrayScaleFilter();
        fpp.addFilter(grayScale);

        flyCam.setMoveSpeed(10f);
    }

    public static void main(String[] args) {
        HelloGrayScale app = new HelloGrayScale();
        app.start();
    }

}
