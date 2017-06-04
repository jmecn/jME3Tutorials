package net.jmecn.thread;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.renderer.Camera;

public class CollisionAppState extends AbstractAppState {

    private World world;

    public CollisionAppState(World world) {
        this.world = world;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        
        Camera cam = app.getCamera();
        world.setBounds(0, 0, cam.getWidth(), cam.getHeight());
    }

    @Override
    public void update(float tpf) {
        world.update(tpf);
    }
    
    public World getWorld() {
        return world;
    }
}
