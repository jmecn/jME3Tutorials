package net.jmecn;

import com.jme3.app.SimpleApplication;

import net.jmecn.logic.InputAppState;
import net.jmecn.logic.LightAppState;
import net.jmecn.logic.VisualAppState;

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
