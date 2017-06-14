package net.jmecn.physics2d.basket;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector2f;

public class InputState extends BaseAppState implements ActionListener {
	public final static String CLICK = "click";
    public final static Trigger ClickTrigger = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);

	private boolean drag = false;
	private Vector2f start = new Vector2f();
	private Vector2f stop = new Vector2f();
	private Vector2f force = new Vector2f();
	// 记录光标的位置。当光标坐标没有改变时，就不要再次计算力量向量。
	private Vector2f lastCursorPos = new Vector2f();

	private InputManager inputManager;
	private AppStateManager stateManager;
	
	@Override
	protected void initialize(Application app) {
		this.inputManager = app.getInputManager();
		this.stateManager = app.getStateManager();
	}

	@Override
	public void update(float tpf) {
		if (drag) {
			updateForce();
		}
	}

	@Override
	protected void cleanup(Application app) {}

	@Override
	protected void onEnable() {
		inputManager.addMapping(CLICK, ClickTrigger);
        inputManager.addListener(this, CLICK);
	}

	@Override
	protected void onDisable() {
		inputManager.removeListener(this);
		inputManager.deleteTrigger(CLICK, ClickTrigger);
	}

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (CLICK.equals(name)) {
            drag = isPressed;
            if (isPressed) {
            	// 初始化向量
            	initForce();
            } else {
            	// 投篮
            	shootBall();
            }
        }
    }

    /**
     * 根据光标位置，初始化投篮力量向量。
     */
    private void initForce() {
    	
        Vector2f cursor = stateManager.getState(CameraState.class).getCursorPosition();
        
    	start.set(cursor);
        stop.set(cursor);
        force.set(0, 0);
        
        // 把箭头添加到场景图中，并初始化箭头的姿势。
        stateManager.getState(ViewState.class).attachIndicator(cursor);
    }
    
    /**
     * 投篮
     */
    private void shootBall() {
        
        force.multLocal(Constants.MAX_SHOOT_FORCE / Constants.MAX_ARROW_LENGTH);
        
        stateManager.getState(ViewState.class).shoot(start, force);
        stateManager.getState(ViewState.class).detachIndicator();
    }
    
    /**
     * 更新投篮的力量
     */
    private void updateForce() {
        Vector2f pos = inputManager.getCursorPosition();
        
        // 不需要计算
        if (lastCursorPos.equals(pos)) {
        	return;
        } else {
        	lastCursorPos.set(pos);
        }

        // 计算终点坐标，并计算投篮的方向向量。
        Vector2f cursor = stateManager.getState(CameraState.class).getCursorPosition();
        stop.set(cursor);
        
        // Force = Stop - Start
        stop.subtract(start, force);
        
        /**
         * 限制投篮的力量，不让力度过大。
         */
        float length = force.length();
        if (length > Constants.MAX_ARROW_LENGTH) {
            // 修正终点坐标
            force.multLocal(Constants.MAX_ARROW_LENGTH / length);
            start.add(force, stop);
        }
        
        /**
         * 计算箭头的旋转
         */
        stateManager.getState(ViewState.class).updateIndicator(force);
    }

}
