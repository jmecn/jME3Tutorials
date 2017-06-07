package net.jmecn.collision;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * 经典物理定理运动。
 * 
 * @author yanmaoyuan
 *
 */
public class Physical extends AbstractControl {
    private Vector3f position = new Vector3f(0, 0, 0);// 位置
    private Vector3f velocity = new Vector3f(1, 0, 0);// 运动速度
    private Vector3f gravity = new Vector3f(0, 0, 0);// 重力加速度

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public Vector3f getGravity() {
        return gravity;
    }

    public void setGravity(Vector3f gravity) {
        this.gravity = gravity;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        position.set(spatial.getLocalTranslation());
    }

    @Override
    protected void controlUpdate(float tpf) {
        velocity.addLocal(gravity.mult(tpf));
        position.addLocal(velocity.mult(tpf));
        spatial.setLocalTranslation(position);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}