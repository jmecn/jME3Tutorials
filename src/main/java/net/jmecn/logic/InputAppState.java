package net.jmecn.logic;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.renderer.RenderManager;

/**
 * 输入模块
 * @author yanmoayuan
 *
 */
public class InputAppState implements AppState, ActionListener {

    // 电灯开关
    public final static String SWITCH_LIGHT = "switch_light";
    public final static Trigger TRIGGER_KEY_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    
    // 显示/隐藏子场景
    public final static String TOGGLE_SUBSCENE = "toggle_subscene";
    public final static Trigger TRIGGER_KEY_TAB = new KeyTrigger(KeyInput.KEY_TAB);
    
    private boolean initialized = false;
    private boolean enabled = true;
    
    /**
     * 保存我们所需要的系统对象
     */
    private InputManager inputManager;
    private AppStateManager stateManager;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.inputManager = app.getInputManager();
        
        initialized = true;
        
        if (enabled)
            addInputs();
    }

    /**
     * 添加输入
     */
    public void addInputs() {
        inputManager.addMapping(SWITCH_LIGHT, TRIGGER_KEY_SPACE);
        inputManager.addMapping(TOGGLE_SUBSCENE, TRIGGER_KEY_TAB);
        
        inputManager.addListener(this, SWITCH_LIGHT, TOGGLE_SUBSCENE);
    }
    
    /**
     * 移除输入
     */
    public void removeInputs() {
        inputManager.deleteTrigger(SWITCH_LIGHT, TRIGGER_KEY_SPACE);
        inputManager.deleteTrigger(TOGGLE_SUBSCENE, TRIGGER_KEY_TAB);
        
        inputManager.removeListener(this); 
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (SWITCH_LIGHT.equals(name)) {
                
                // 开关灯
                LightAppState light = stateManager.getState(LightAppState.class);
                if (light != null)
                    light.setEnabled(!light.isEnabled());
                
            } else if (TOGGLE_SUBSCENE.equals(name)) {
                // 显示/隐藏场景
                VisualAppState visual = stateManager.getState(VisualAppState.class);
                if (visual != null)
                    visual.setEnabled(!visual.isEnabled());
            }
        }
    }
    
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void setEnabled(boolean active) {
        if ( this.enabled == active )
            return;
        this.enabled = active;
        
        if (!initialized)
            return;
        
        if (enabled) {
            addInputs();
        } else {
            removeInputs();
        }
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {}

    @Override
    public void stateDetached(AppStateManager stateManager) {}

    @Override
    public void update(float tpf) {}

    @Override
    public void render(RenderManager rm) {}

    @Override
    public void postRender() {}

    @Override
    public void cleanup() {
        if (enabled)
            removeInputs();

        initialized = false;
    }

}
