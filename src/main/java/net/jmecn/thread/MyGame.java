package net.jmecn.thread;

import com.jme3.app.SimpleApplication;

/**
 * 多线程实验
 * @author yanmaoyuan
 *
 */
public class MyGame extends SimpleApplication {

    private World world = new World();
    private MyThread thread;
    
    @Override
    public void simpleInitApp() {
        thread = new MyThread(world);
        //thread.start();
        stateManager.attach(new CollisionAppState(world));
        stateManager.attach(new VisualAppState(world));
    }
    
    @Override
    public void stop() {
        thread.exit();
        super.stop();
    }

    public static void main(String[] args) {
        new MyGame().start();
    }

}
