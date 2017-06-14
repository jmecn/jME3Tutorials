package net.jmecn.physics2d.basket;

import org.dyn4j.dynamics.Body;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.util.TempVars;

import net.jmecn.physics2d.BodyControl;

/**
 * 当玩家准备投篮时，屏幕上会出现一个箭头，用来显示投篮方向和力度大小。
 */
public class ViewState extends BaseAppState {

	private Node rootNode = new Node("Hud root");
	
	private Geometry arrow;
	private BitmapFont font;
	private BitmapText text;

	private AssetManager assetManager;

	@Override
	protected void initialize(Application app) {
		
		this.assetManager = app.getAssetManager();
		app.getViewPort().setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 0.85f, 1f));

		font = assetManager.loadFont("Interface/Fonts/Default.fnt");

		initIndicator();
	}
	
	private void initIndicator() {
		Mesh mesh = new Arrow(new Vector3f(1, 0, 0));

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Red);

		/**
		 * 这个箭头用来指示投篮方向和力度大小。
		 */
		arrow = new Geometry("Arrow", mesh);
		arrow.setMaterial(mat);

		text = font.createLabel("0, 0N");

		text.setLocalScale(1 / getStateManager().getState(CameraState.class).getFactor());
	}

	/**
	 * 把箭头添加到场景图中，并初始化箭头的姿势。
	 * 
	 * @param pos
	 */
	public void attachIndicator(Vector2f pos) {
		arrow.setLocalTranslation(pos.x, pos.y, 0);
		arrow.setLocalScale(0);
		arrow.setLocalRotation(Quaternion.IDENTITY);

		rootNode.attachChild(arrow);

		text.setText("Deg=0, F=0N");
		text.setLocalTranslation(pos.x, pos.y, 1);
		rootNode.attachChild(text);
	}

	public void updateIndicator(Vector2f force) {
		// 使用jme3的全局临时变量，避免分配新的对象，造成内存泄漏。
		TempVars temp = TempVars.get();
		// 计算方向
		Vector2f dir = temp.vect2d.set(force);
		dir.normalizeLocal();// 单位化
		float length = force.length();

		// 生成旋转矩阵
		Matrix3f rotation = temp.tempMat3;
		Vector3f uAxis = temp.vect1.set(dir.x, dir.y, 0);
		Vector3f vAxis = temp.vect2.set(-dir.y, dir.x, 0);
		Vector3f wAxis = temp.vect3.set(Vector3f.UNIT_Z);
		rotation.fromAxes(uAxis, vAxis, wAxis);

		arrow.setLocalRotation(rotation);// 旋转剪头指向

		/**
		 * 根据投篮的力量，改变箭头的颜色。
		 */
		ColorRGBA color = temp.color;
		color.interpolateLocal(ColorRGBA.Green, ColorRGBA.Red, length / Constants.MAX_ARROW_LENGTH);
		arrow.getMaterial().setColor("Color", color);

		/**
		 * 改变箭头的长度
		 */
		arrow.setLocalScale(length);

		/**
		 * 改变指示器文字
		 */
		Vector2f UNIT_X = temp.vect2d2.set(1, 0);
		float degree = -dir.angleBetween(UNIT_X) / FastMath.DEG_TO_RAD;
		float power = length * Constants.MAX_SHOOT_FORCE / Constants.MAX_ARROW_LENGTH;
		text.setText(String.format("Deg=%.1f, F=%.1fN", degree, power));

		// 释放全局临时变量
		temp.release();
	}

	/**
	 * 投篮
	 * @param start
	 * @param force
	 */
	public void shoot(Vector2f start, Vector2f force) {
		// physics body
        Body body = getStateManager().getState(PhysicsState.class).makeBall(start, force);
        
		Spatial spatial = AssetFactory.makeBall();
        spatial.addControl(new BodyControl(body));
        rootNode.attachChild(spatial);
	}
	
	/**
	 * 移除指示器
	 */
	public void detachIndicator() {
		arrow.removeFromParent();
		text.removeFromParent();
	}

	@Override
	protected void cleanup(Application app) {
	}

	@Override
	protected void onEnable() {
		SimpleApplication simpleApp = (SimpleApplication) getApplication();
		simpleApp.getRootNode().attachChild(rootNode);
	}

	@Override
	protected void onDisable() {
		rootNode.removeFromParent();
	}

}
