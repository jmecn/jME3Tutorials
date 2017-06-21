package net.jmecn.physics3d.jaime;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

/**
 * 输入管理模块
 * 
 * @author yanmaoyuan
 *
 */
public class InputAppState extends BaseAppState implements ActionListener {
    /**
     * 显示或隐藏BulletAppState的debug形状。按F1键触发。
     */
    public final static String DEBUG = "debug";
    public final static Trigger DEBUG_TRIGGER = new KeyTrigger(KeyInput.KEY_F1);

    // 前、后、左、右、跳跃
    public final static String FORWARD = "forward";
    public final static Trigger FORWARD_TRIGGER = new KeyTrigger(KeyInput.KEY_W);

    public final static String BACKWARD = "backward";
    public final static Trigger BACKWARD_TRIGGER = new KeyTrigger(KeyInput.KEY_S);

    public final static String LEFT = "left";
    public final static Trigger LEFT_TRIGGER = new KeyTrigger(KeyInput.KEY_A);

    public final static String RIGHT = "right";
    public final static Trigger RIGHT_TRIGGER = new KeyTrigger(KeyInput.KEY_D);

    public final static String JUMP = "jump";
    public final static Trigger JUMP_TRIGGER = new KeyTrigger(KeyInput.KEY_SPACE);

    private boolean left = false;
    private boolean right = false;
    private boolean forward = false;
    private boolean backward = false;

    private InputManager inputManager;
    private AppStateManager stateManager;

    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        inputManager.addMapping(DEBUG, DEBUG_TRIGGER);
        inputManager.addMapping(LEFT, LEFT_TRIGGER);
        inputManager.addMapping(RIGHT, RIGHT_TRIGGER);
        inputManager.addMapping(FORWARD, FORWARD_TRIGGER);
        inputManager.addMapping(BACKWARD, BACKWARD_TRIGGER);
        inputManager.addMapping(JUMP, JUMP_TRIGGER);

        inputManager.addListener(this, DEBUG, LEFT, RIGHT, FORWARD, BACKWARD, JUMP);
    }

    @Override
    protected void onDisable() {
        inputManager.removeListener(this);

        inputManager.deleteTrigger(DEBUG, DEBUG_TRIGGER);
        inputManager.deleteTrigger(LEFT, LEFT_TRIGGER);
        inputManager.deleteTrigger(RIGHT, RIGHT_TRIGGER);
        inputManager.deleteTrigger(FORWARD, FORWARD_TRIGGER);
        inputManager.deleteTrigger(BACKWARD, BACKWARD_TRIGGER);
        inputManager.deleteTrigger(JUMP, JUMP_TRIGGER);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (DEBUG.equals(name) && isPressed) {
            toggleBulletDebug();
        } else if (JUMP.equals(name) && isPressed) {
            jump();
        } else if (LEFT.equals(name)) {
            left = isPressed;
        } else if (RIGHT.equals(name)) {
            right = isPressed;
        } else if (FORWARD.equals(name)) {
            forward = isPressed;
        } else if (BACKWARD.equals(name)) {
            backward = isPressed;
        }

        if (left || right || forward || backward) {
            walk();
        } else {
            idle();
        }
    }

    private void toggleBulletDebug() {
        BulletAppState bulletAppState = stateManager.getState(BulletAppState.class);
        if (bulletAppState != null) {
            boolean debugEnabled = bulletAppState.isDebugEnabled();
            bulletAppState.setDebugEnabled(!debugEnabled);
        }
    }

    private void walk() {
        TempVars tmpVar = TempVars.get();

        Vector3f frontDir = tmpVar.vect1.set(0, 0, 0.6f);
        Vector3f leftDir = tmpVar.vect2.set(0.4f, 0, 0);
        Vector3f walkDir = tmpVar.vect3.set(0, 0, 0);

        if (forward) {
            walkDir.addLocal(frontDir);
        }
        if (backward) {
            walkDir.addLocal(frontDir.negateLocal());
        }
        if (left) {
            walkDir.addLocal(leftDir);
        }
        if (right) {
            walkDir.addLocal(leftDir.negateLocal());
        }

        walkDir.normalizeLocal();

        stateManager.getState(CharacterAppState.class).walk(walkDir);

        tmpVar.release();
    }

    private void idle() {
        stateManager.getState(CharacterAppState.class).idle();
    }

    private void jump() {
        stateManager.getState(CharacterAppState.class).jump();
    }
}
