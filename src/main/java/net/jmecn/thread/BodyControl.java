package net.jmecn.thread;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class BodyControl extends AbstractControl {

    // 半径
    float radius;
    float speed;
    // 位置
    Vector3f position;
    // 速度
    Vector3f velocity;
    
    public BodyControl() {
        radius = 5;
        speed = 30;
        position = new Vector3f();
        velocity = new Vector3f();
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.setLocalTranslation(position);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
