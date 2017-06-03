package net.jmecn.logic;

import com.jme3.app.SimpleApplication;

/**
 * 演示AppState的作用
 * 
 * @author yanmaoyuan
 *
 */
public class HelloAppState extends SimpleApplication {

    public static void main(String[] args) {
        HelloAppState app = new HelloAppState();
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        stateManager.attach(new LightAppState());
        stateManager.attach(new VisualAppState());
        stateManager.attach(new InputAppState());
    }

}
