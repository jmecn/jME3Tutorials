package net.jmecn.physics2d;

import org.dyn4j.dynamics.Body;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.util.TempVars;

/**
 * 物体控制器
 * 
 * @author yanmaoyuan
 *
 */
public class BodyControl extends AbstractControl {

    private Body body;

    public BodyControl(final Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    @Override
    protected void controlUpdate(float tpf) {
        TempVars temp = TempVars.get();

        float x = (float) body.getTransform().getTranslationX();
        float y = (float) body.getTransform().getTranslationY();
        float z = spatial.getLocalTranslation().z;
        spatial.setLocalTranslation(x, y, z);

        Quaternion rotation = temp.quat1;
        float angle = (float) body.getTransform().getRotation();
        rotation.fromAngleNormalAxis(angle, Vector3f.UNIT_Z);
        spatial.setLocalRotation(rotation);

        temp.release();
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
