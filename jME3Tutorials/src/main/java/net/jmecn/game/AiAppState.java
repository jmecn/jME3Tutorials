package net.jmecn.game;

import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Ai模块
 * @author yanmaoyuan
 *
 */
public class AiAppState extends BaseAppState {

	private Node rootNode = new Node("AI Node");
	private Spatial player;
	
	private Material peaceMat;
	private Material angryMat;
	
	// 最大数量
	private final static int MAX_COUNT = 10;
	
	@Override
	protected void initialize(Application app) {
		// 平静状态下的材质
		peaceMat = new Material(getApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		peaceMat.setColor("Diffuse", ColorRGBA.Green);
		peaceMat.setColor("Ambient", ColorRGBA.Black);
		peaceMat.setBoolean("UseMaterialColors", true);
		
		// 愤怒状态下的材质
		angryMat = new Material(getApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
		angryMat.setColor("Diffuse", ColorRGBA.Red);
		angryMat.setColor("Ambient", ColorRGBA.Red.mult(0.2f));
		angryMat.setColor("GlowColor", ColorRGBA.Red);
		angryMat.setBoolean("UseMaterialColors", true);
		
		// 初始化所有AI
		for(int i=0; i<MAX_COUNT; i++) {
			Spatial spatial = createMob();
			rootNode.attachChild(spatial);
		}
	}

	@Override
	protected void cleanup(Application app) {
		// 移除所有AI
	}

	@Override
	protected void onEnable() {
		// 将所有AI添加到场景的根结点中
		SimpleApplication simpleApp = (SimpleApplication)getApplication();
		simpleApp.getRootNode().attachChild(rootNode);
	}

	@Override
	protected void onDisable() {
		// 从场景中移除
		rootNode.removeFromParent();
	}
	
	private Spatial createMob() {
		// 创建一个方块，作为怪物的外形。
		Geometry geom = new Geometry("Cube", new Box(0.5f, 0.5f, 0.5f));
		geom.setMaterial(peaceMat);
		geom.move(0f, 0.5f, 0f);
		
		Node mob = new Node("mob");
		mob.setShadowMode(ShadowMode.Cast);
		mob.attachChild(geom);
		
		// 随机坐标
		float x = FastMath.rand.nextFloat() * 50 - 25;
		float z = FastMath.rand.nextFloat() * 50 - 25;
		mob.move(x, 0, z);
		
		mob.addControl(new MotionControl(3.0f));
		return mob;
	}

	public void setPlayer(Spatial player) {
		this.player = player;
	}
	
	public void update(float tpf) {
		
		if (player != null) {
			
			List<Spatial> children = rootNode.getChildren();
			int size = children.size();
			for(int i=0; i<size; i++) {
				Node child = (Node)children.get(i);
				
				// 计算敌人和自己的距离
				Vector3f pLoc = player.getWorldTranslation();
				Vector3f thisLoc = child.getWorldTranslation();
				
				float dist = pLoc.distance(thisLoc);
				
				if (dist <= 5f && dist > 1f) {
					// 设置目标
					child.getControl(MotionControl.class).setTarget(new Vector3f(pLoc));
				} else {
					child.getControl(MotionControl.class).setTarget(null);
				}
				
				if (dist <= 5f) {
					child.setMaterial(angryMat);
				} else {
					child.setMaterial(peaceMat);
				}
			}
		}


	}
}
