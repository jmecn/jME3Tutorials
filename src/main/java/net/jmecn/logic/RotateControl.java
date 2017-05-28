package net.jmecn.logic;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * 让模型绕Y轴以固定速率旋转
 * 
 * @author yanmaoyuan
 *
 */
public class RotateControl extends AbstractControl {

	// 旋转速度：每秒180°
	private float rotateSpeed = 1f;
	
	public RotateControl(float rotateSpeed) {
		this.rotateSpeed = rotateSpeed;
	}
	
	@Override
	protected void controlUpdate(float tpf) {
		spatial.rotate(0, tpf * rotateSpeed, 0);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}

}
