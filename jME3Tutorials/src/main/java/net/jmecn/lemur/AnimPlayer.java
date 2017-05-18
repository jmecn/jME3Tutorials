package net.jmecn.lemur;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.simsilica.lemur.Action;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.ProgressBar;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;

/**
 * 使用Lemur GUI控制动画
 * 
 * @author yanmaoyuan
 *
 */
public class AnimPlayer extends SimpleApplication {

	private Spatial spatial;
	private AnimControl animControl;
	private AnimChannel animChannel;
	
	// 动画进度
	private RangedValueModel progressModel;
	// 旋转滑动条
	private VersionedReference<Double> rotation;
	
	public static void main(String[] args) {
		AnimPlayer app = new AnimPlayer();
		app.start();
	}

	@Override
	public void simpleInitApp() {
		
		// 初始化摄像机
		initCamera();
		
		// 初始化灯光
		initLight();
		
		// 初始化场景
		initScene();
		
		// 动画控制器
		animControl = spatial.getControl(AnimControl.class);
		animChannel = animControl.createChannel();
		
		// 初始化Lemur GUI
		initLemur();
	}
	
	@Override
	public void simpleUpdate(float tpf) {
		// 更新动画播放进度条
		if (animChannel != null && animChannel.getSpeed() != 0f) {
			float time = animChannel.getTime();
			float maxTime = animChannel.getAnimMaxTime();
			progressModel.setPercent(time/maxTime);
		}
		
		// 旋转Jaime
		if (rotation.update()) {
			// 角度
			double deg = rotation.get();
			// 弧度
			float rad = (float)(Math.PI * deg / 180.0);
			
			Quaternion rotation = new Quaternion().fromAngleNormalAxis(rad, Vector3f.UNIT_Y);
			spatial.setLocalRotation(rotation);
		}
	}

	/**
	 * 初始化摄像机
	 */
	private void initCamera() {
		// 禁用第一人称摄像机
		flyCam.setEnabled(false);
		
		cam.setLocation(new Vector3f(0, 2, 3));
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
	 * 初始化场景
	 */
	private void initScene() {
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
		
		// 加载Jaime模型
		spatial = assetManager.loadModel("Models/Jaime/Jaime.j3o");
		rootNode.attachChild(spatial);
	}
	
	/**
	 * 初始化Lemur GUI
	 */
	private void initLemur() {
		GuiGlobals.initialize(this);
		BaseStyles.loadGlassStyle();
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
		
		getAnimWnd();
		getRotateWnd();
	}
	
	/**
	 * 动画窗口
	 */
	private void getAnimWnd() {
		Container wnd = new Container();
		guiNode.attachChild(wnd);
		
		Label label = new Label("Animation", new ElementId("title"));
		wnd.addChild(label);
		
		// 动画列表
		final ListBox<String> box = new ListBox<String>();
		box.getModel().addAll(animControl.getAnimationNames());
		box.getSelectionModel().setSelection(0);// 默认选中第1个
		wnd.addChild(box);
		
		// 动画进度条
		ProgressBar progress = new ProgressBar();
		progressModel = progress.getModel();
		wnd.addChild(progress);
		
		// 控制面板
		Container container = new Container(new BoxLayout(Axis.X, FillMode.Even));
		wnd.addChild(container);
		
		final Checkbox checkBox = new Checkbox("Loop");
		
		// 播放动画
		Button playBtn = new ActionButton(new Action("Play") {
			@Override
			public void execute(Button source) {
				Integer index = box.getSelectionModel().getSelection();
				if (index != null) {
					String animName = box.getModel().get(index);
					
					// 播放动画
					animChannel.setAnim(animName);
					
					// 设置循环模式
					if (checkBox.isChecked()) {
						animChannel.setLoopMode(LoopMode.Loop);
					} else {
						animChannel.setLoopMode(LoopMode.DontLoop);
					}
				}
			}
		});
		container.addChild(playBtn);
		container.addChild(checkBox);
		
		// 将窗口置于屏幕右上角
		wnd.setLocalTranslation(cam.getWidth() - wnd.getPreferredSize().x - 10, cam.getHeight() - 10, 0);
	}
	
	/**
	 * 旋转
	 */
	private void getRotateWnd() {
		
		Slider slider = new Slider(new DefaultRangedValueModel(-180, 180, 0));
		rotation = slider.getModel().createReference();
		guiNode.attachChild(slider);
		
		Vector3f size = new Vector3f(200, 24, 1);
		slider.setPreferredSize(size);
		
		float width = cam.getWidth();
		slider.setLocalTranslation((width-size.x)*0.5f, size.y + 10, 0);
		
		
		SeekBar seekBar = new SeekBar();
		guiNode.attachChild(seekBar);
		
		size = seekBar.getPreferredSize();
		seekBar.setLocalTranslation((width-size.x)*0.5f, size.y + 10 + 50, 0);
	}
}
