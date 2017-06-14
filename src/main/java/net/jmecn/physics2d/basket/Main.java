package net.jmecn.physics2d.basket;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;

/**
 * 投篮小游戏
 * 
 * @author yanmaoyuan
 *
 */
public class Main extends SimpleApplication {

	public Main() {
		this.setPauseOnLostFocus(false);
		this.setDisplayStatView(false);
		this.setDisplayFps(false);
	}
	
    @Override
    public void simpleInitApp() {
    	AssetFactory.setAssetManager(assetManager);
    	
    	stateManager.getState(FlyCamAppState.class).setEnabled(false);
    	stateManager.attachAll(
    			new CameraState(), new InputState(), new ViewState(),
    			new PhysicsState(), new PhysicsDebugState());
    }
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

}
