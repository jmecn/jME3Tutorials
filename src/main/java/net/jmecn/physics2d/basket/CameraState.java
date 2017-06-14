package net.jmecn.physics2d.basket;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class CameraState extends BaseAppState {

	private final static float PreferencedWidth = 800;
	private float factor = 90.0f;// 画面放大倍率

	// 屏幕分辨率
	private float screenWidth;
	private float screenHeight;

	// 世界分辨率
	private float worldWidth;
	private float worldHeight;

	private Vector2f cursor = new Vector2f();
	
	private Camera cam;
	private InputManager inputManager;

	@Override
	protected void initialize(Application app) {

		this.cam = app.getCamera();
		this.inputManager = app.getInputManager();

		// screen dimension
		screenWidth = cam.getWidth();
		screenHeight = cam.getHeight();

		factor = factor * screenWidth / PreferencedWidth;

		worldWidth = screenWidth / factor;
		worldHeight = screenHeight / factor;

		float right = worldWidth * 0.5f;
		float left = -right;
		float top = worldHeight * 0.5f;
		float bottom = -top;
		cam.setFrustum(-100, 100, left, right, top, bottom);
		cam.setParallelProjection(true);

		// 摄像机居中
		cam.setLocation(new Vector3f(right, top, 0));
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
	}

	@Override
	protected void onDisable() {
	}

	public Vector2f getCursorPosition() {
		if (inputManager != null) {
			Vector2f pos = inputManager.getCursorPosition();
			float x = (pos.x) / factor + cam.getLocation().x + cam.getFrustumLeft();
			float y = (pos.y) / factor + cam.getLocation().y + cam.getFrustumBottom();
			cursor.set(x, y);
		}
		
		return cursor;
	}

	public float getFactor() {
		return factor;
	}

	public float getScreenWidth() {
		return screenWidth;
	}

	public float getScreenHeight() {
		return screenHeight;
	}

	public float getWorldWidth() {
		return worldWidth;
	}

	public float getWorldHeight() {
		return worldHeight;
	}
}
