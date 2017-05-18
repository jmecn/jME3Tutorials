package net.jmecn;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;

/**
 * 动画
 * 
 * @author yanmaoyuan
 *
 */
public class HelloAnimation extends SimpleApplication {

	/**
	 * 按W键行走
	 */
	private final static String WALK = "walk";
	
	/**
	 * 按空格键跳跃
	 */
	private final static String JUMP = "jump";
	
	/**
	 * 记录Jaime的行走状态。
	 */
	private boolean isWalking = false;
	
	/**
	 * 动画模型
	 */
	private Spatial spatial;
	
	private AnimControl animControl;
	private AnimChannel animChannel;
	
	public static void main(String[] args) {
		// 启动程序
		HelloAnimation app = new HelloAnimation();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		// 初始化摄像机
		initCamera();
		
		// 初始化灯光
		initLight();
		
		// 初始化按键输入
		initKeys();
		
		// 初始化场景
		initScene();
		
		// 动画控制器
		animControl = spatial.getControl(AnimControl.class);
		animControl.addListener(animEventListener);
		
		// 显示这个模型中有多少个动画，每个动画的名字是什么。
		System.out.println(animControl.getAnimationNames());

		animChannel = animControl.createChannel();
		// 播放“闲置”动画
		animChannel.setAnim("Idle");
	}
	
	/**
	 * 初始化摄像机
	 */
	private void initCamera() {
		// 禁用第一人称摄像机
		flyCam.setEnabled(false);
		
		cam.setLocation(new Vector3f(1, 2, 3));
		cam.lookAt(new Vector3f(0, 0.5f, 0), new Vector3f(0, 1, 0));
	}
	
	/**
	 * 初始化光影
	 */
	private void initLight() {
		// 定向光
		DirectionalLight sunLight = new DirectionalLight();
		sunLight.setDirection(new Vector3f(-1, -2, -3));
		sunLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));

		// 环境光
		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

		// 将光源添加到场景图中
		rootNode.addLight(sunLight);
		rootNode.addLight(ambientLight);
	}
	
	/**
	 * 初始化按键
	 */
	private void initKeys() {
		// 按W键行走
		inputManager.addMapping(WALK, new KeyTrigger(KeyInput.KEY_W));
		// 按空格键跳跃
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		
		inputManager.addListener(actionListener, WALK, JUMP);
	}
	
	/**
	 * 初始化场景
	 */
	private void initScene() {
		// 加载Jaime模型
		spatial = assetManager.loadModel("Models/Jaime/Jaime.j3o");
		rootNode.attachChild(spatial);
		
		// 创建一个平面作为舞台
		Geometry stage = new Geometry("Stage", new Quad(2, 2));
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setColor("Diffuse", ColorRGBA.White);
		mat.setColor("Specular", ColorRGBA.White);
		mat.setColor("Ambient", ColorRGBA.Black);
		mat.setFloat("Shininess", 0);
		mat.setBoolean("UseMaterialColors", true);
		stage.setMaterial(mat);
		
		stage.rotate(-FastMath.HALF_PI, 0, 0);
		stage.center();
		rootNode.attachChild(stage);
	}
	
	/**
	 * 按键动作监听器
	 */
	private ActionListener actionListener = new ActionListener() {
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (WALK.equals(name)) {// 走
				
				// 记录行走状态
				isWalking = isPressed;
				
				if (isPressed) {
					// 播放“行走”动画
					animChannel.setAnim("Walk");
					animChannel.setLoopMode(LoopMode.Loop);// 循环播放
				} else {
					// 播放“闲置”动画
					animChannel.setAnim("Idle");
					animChannel.setLoopMode(LoopMode.Loop);
				}
				
			} else if (JUMP.equals(name)) {// 跳
				
				if (isPressed) {
					
					/**
					 * 若Jaime已经处于JumpStart/Jumping/JumpEnd状态，就不要再做起跳动作了。
					 */
					
					// 查询当前正在播放的动画
					String curAnim = animChannel.getAnimationName();
					if (curAnim != null && curAnim.startsWith("Jump")) {
						
						return;
					}
					
					// 播放“起跳”动画
					animChannel.setAnim("JumpStart");
					animChannel.setLoopMode(LoopMode.DontLoop);
					animChannel.setSpeed(1.5f);
				}
			}
		}
	};
	
	/**
	 * 动画事件监听器
	 */
	private AnimEventListener animEventListener = new AnimEventListener() {
		@Override
		public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
			if ("JumpStart".equals(animName)) {
				// “起跳”动作结束后，紧接着播放“着地”动画。
				channel.setAnim("JumpEnd");
				channel.setLoopMode(LoopMode.DontLoop);
				channel.setSpeed(1.5f);
				
			} else if ("JumpEnd".equals(animName)) {
				// “着地”后，根据按键状态来播放“行走”或“闲置”动画。
				if (isWalking) {
					channel.setAnim("Walk");
					channel.setLoopMode(LoopMode.Loop);
				} else {
					channel.setAnim("Idle");
					channel.setLoopMode(LoopMode.Loop);
				}
			}
		}

		@Override
		public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
		}
	};
}
