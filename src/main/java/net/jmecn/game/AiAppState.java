package net.jmecn.game;

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

import net.jmecn.logic.FloatControl;

/**
 * AI模块
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
		peaceMat.setColor("Ambient", ColorRGBA.Green.mult(0.2f));
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
	
	/**
	 * 创造一个怪物
	 * @return
	 */
	private Node createMob() {
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
	
	/**
	 * 设置玩家
	 * @param player
	 */
	public void setPlayer(Spatial player) {
		this.player = player;
	}
	
	/**
	 * 主循环
	 */
	@Override
	public void update(float tpf) {
		
		if (player != null) {
			
			Spatial[] children = rootNode.getChildren().toArray(new Spatial[0]);
			
			for(int i=0; i<children.length; i++) {
				Node child = (Node)children[i];
				
				if (reachBounds(child)) {
					// 消灭原有方块
					child.removeFromParent();
					
					// 重新刷一个方块
					child = createMob();
					rootNode.attachChild(child);
				}
				
				// 计算玩家和方块之间距离
				Vector3f mobLoc = child.getLocalTranslation();
				Vector3f playerLoc = player.getLocalTranslation();
				float dist = playerLoc.distance(mobLoc);
				
				/**
				 * 根据玩家和方块之间的距离，决定方块的外观。
				 */
				if (dist <= 6f) {
					child.setMaterial(angryMat);
				} else {
					child.setMaterial(peaceMat);
				}
				
				/**
				 * 根据玩家和方块之间的距离，决定方块的行为。
				 */
				MotionControl motionControl = child.getControl(MotionControl.class);
				if (dist <= 6f && dist > 2f) {
					// 玩家靠近了！躲开玩家！
					Vector3f dir = mobLoc.subtract(playerLoc);
					dir.addLocal(mobLoc);
					dir.y = 0;
					
					motionControl.setTarget(dir);
				} else if (dist > 6){
					// 玩家离得比较远，不用管他了。
					motionControl.setTarget(null);
				} else {
					// 被玩家追上了！
					if (motionControl.isEnabled()) {
						motionControl.setEnabled(false);
						
						child.move(0, 0.5f, 0);
						child.addControl(new FloatControl(0.2f, 1f));
					}
				}
				
			}
		}

	}
	
	/**
	 * 边界检测
	 */
	private boolean reachBounds(Spatial child) {
		boolean flag = false;
		
		Vector3f mobLoc = child.getLocalTranslation();
		if (mobLoc.x > 24.5f) {
			flag = true;
		} else if (mobLoc.x < -24.5) {
			flag = true;
		}
		
		if (mobLoc.z > 24.5) {
			flag = true;
		} else if (mobLoc.z < -24.5) {
			flag = true;
		}
		
		return flag;
		
	}
}
