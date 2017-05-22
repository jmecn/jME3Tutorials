package net.jmecn.game;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * 这是一个运动控件，其作用是让模型朝目标点直线运动。
 * 
 * @author yanmaoyuan
 *
 */
public class MotionControl extends AbstractControl {
    
    // 运动速度
    private float walkSpeed = 1.0f;
    private float speedFactor = 1.0f;
    
    // 运动的方向向量
    private Vector3f walkDir;
    // 运动一步的向量
    private Vector3f step;
    
    // 当前位置
    private Vector3f loc;
    // 目标位置
    private Vector3f target;

    // 观察者
    private Observer observer;

    public MotionControl() {
        this(1.0f);
    }
    
    public MotionControl(float walkSpeed) {
        this.walkSpeed = walkSpeed;
        walkDir = null;
        target = null;
        loc = new Vector3f();
        step = new Vector3f();
    }
    
    /**
     * 设置运动速度
     * @param walkSpeed
     */
    public void setWalkSpeed(float walkSpeed) {
        this.speedFactor = walkSpeed;
    }
    
    /**
     * 设置观察者
     * @param observer
     */
    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    /**
     * 设置目标点
     * 
     * @param target
     */
    public void setTarget(Vector3f target) {
        this.target = target;

        if (target == null) {
        	walkDir = null;
        	return;
        }
        
        // 当模型面朝目标点
        this.spatial.lookAt(target, Vector3f.UNIT_Y);

        // 计算运动方向
        walkDir = target.subtract(loc);
        walkDir.normalizeLocal();
        
    }
    
    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        // 初始化位置
        loc = new Vector3f(spatial.getLocalTranslation());
    }

    /**
     * 重写主循环，让这个模型向目标点移动。
     */
    @Override
    protected void controlUpdate(float tpf) {
        if (walkDir != null) {

            // 计算下一步的步长
            float stepDist = walkSpeed * tpf * speedFactor;
            
            if (stepDist == 0f) {
            	return;
            }

            // 计算离目标点的距离
            float dist = loc.distance(target);

            if (stepDist < dist) {
                // 计算位移
                walkDir.mult(stepDist, step);
                loc.addLocal(step);
                
                spatial.setLocalTranslation(loc);
                
            } else {
                // 可以到达目标点
                walkDir = null;
                
                spatial.setLocalTranslation(target);
                target = null;

                // 通知观察者，已经抵达目标点了。
                if (observer != null) {
                	observer.onReachTarget();
                }
            }

        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
