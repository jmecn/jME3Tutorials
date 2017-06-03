package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

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
        
        // 初始化摄像机
        cam.setLocation(new Vector3f(2.4611378f, 2.8119917f, 9.150583f));
        cam.setRotation(new Quaternion(-0.020502187f, 0.97873497f, -0.16252096f, -0.1234684f));
    }

}
