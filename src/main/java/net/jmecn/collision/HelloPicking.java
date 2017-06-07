package net.jmecn.collision;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.TechniqueDef.LightMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import net.jmecn.state.CubeAppState;

/**
 * 利用射线检测，实现拾取。
 * @author yanmaoyuan
 *
 */
public class HelloPicking extends SimpleApplication implements ActionListener {

	// 空格键：切换摄像机模式
	public final static String CHANGE_CAM_MODE = "change_camera_mode";
	// 鼠标左键：拾取
	public final static String PICKING = "pick";
	
	// 准星
	private Spatial cross;
	// 拾取标记
	private Spatial flag;
	
	// 射线
	private Ray ray;
	
	public static void main(String[] args) {
		// 启动程序
		HelloPicking app = new HelloPicking();
		app.start();
	}
	
	public HelloPicking() {
		super(new FlyCamAppState(), new DebugKeysAppState(), new CubeAppState());
		
		// 初始化射线
		ray = new Ray();
		// 设置检测最远距离，可将射线变为线段。
		// ray.setLimit(500);
	}

	@Override
	public void simpleInitApp() {
		// 初始化摄像机
		flyCam.setMoveSpeed(20f);
		cam.setLocation(new Vector3f(89.0993f, 10.044929f, -86.18647f));
		cam.setRotation(new Quaternion(0.063343525f, 0.18075047f, -0.01166729f, 0.9814177f));
		
		// 设置灯光渲染模式为单通道，这样更加明亮。
		renderManager.setPreferredLightMode(LightMode.SinglePass);
		renderManager.setSinglePassLightBatchSize(2);
	
		// 做个准星
		cross = makeCross();
		
		// 做个拾取标记
		flag = makeFlag();
		
		// 用户输入
		inputManager.addMapping(PICKING, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping(CHANGE_CAM_MODE, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, PICKING, CHANGE_CAM_MODE);
	}

	/**
	 * 在摄像机镜头正中央贴一张纸，充当准星。
	 */
	private Spatial makeCross() {
		// 采用Gui的默认字体，做个加号当准星。
		BitmapText text = guiFont.createLabel("+");
		text.setColor(ColorRGBA.Red);// 红色
		
		// 居中
		float x = (cam.getWidth() - text.getLineWidth()) * 0.5f;
		float y = (cam.getHeight() + text.getLineHeight()) * 0.5f;
		text.setLocalTranslation(x, y, 0);
		
		guiNode.attachChild(text);
		
		return text;
	}
	
	/**
	 * 制作一个小球，用于标记拾取的地点。
	 * @return
	 */
	private Spatial makeFlag() {
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Green);
		mat.getAdditionalRenderState().setWireframe(true);
		
		Geometry geom = new Geometry("flag", new Sphere(8, 8, 1));
		geom.setMaterial(mat);
		
		return geom;
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (isPressed) {
			if (PICKING.equals(name)) {
				// 拾取
				pick();
			} else if (CHANGE_CAM_MODE.equals(name)) {
				
				if (flyCam.isDragToRotate()) {
					// 自由模式
					flyCam.setDragToRotate(false);
					guiNode.attachChild(cross);
				} else {
					// 拖拽模式
					flyCam.setDragToRotate(true);
					cross.removeFromParent();
				}
			}
		}
		
	}
	
	/**
	 * 使用射线检测，判断离摄像机最近的点。
	 */
	private void pick() {
		
        Ray ray = updateRay();
        CollisionResults results = new CollisionResults();
        
        // rootNode.collideWith(ray, results);// 碰撞检测
        
        Node cubeSceneNode = stateManager.getState(CubeAppState.class).getRootNode();
        cubeSceneNode.collideWith(ray, results);// 碰撞检测
        
        // 打印检测结果
        print(results);
        
        /**
         * 判断检测结果
         */
        if (results.size() > 0) {
        	
        	// 放置拾取标记
        	Vector3f position = results.getClosestCollision().getContactPoint();
        	flag.setLocalTranslation(position);
        	
        	if (flag.getParent() == null) {
        		rootNode.attachChild(flag);
        	}
        } else {
        	// 移除标记
        	if (flag.getParent() != null) {
        		flag.removeFromParent();
        	}
        }
	}
	
	/**
	 * 更新射线参数
	 * @return
	 */
	private Ray updateRay() {
		// 使用摄像机的位置作为射线的原点
		ray.setOrigin(cam.getLocation());
		
		if (!flyCam.isDragToRotate()) {
			/**
			 * 自由模式下，直接使用摄像机方向即可。
			 */
			ray.setDirection(cam.getDirection());
		} else {
			/**
			 * 拖拽模式下，通过鼠标的位置计算射线方向
			 */
			Vector2f screenCoord = inputManager.getCursorPosition();
			Vector3f worldCoord = cam.getWorldCoordinates(screenCoord, 1f);
			
			// 计算方向
			Vector3f dir = worldCoord.subtract(cam.getLocation());
			dir.normalizeLocal();
			
			ray.setDirection(dir);
		}
        
        return ray;
	}

	/**
	 * 打印检测结果
	 * @param results
	 */
	private void print(CollisionResults results) {
		System.out.println("碰撞结果：" + results.size());
        System.out.println("射线：" + ray);
        
        /**
         * 判断检测结果
         */
        if (results.size() > 0) {
        	
        	// 从进到远，打印出射线途径的所有交点。
        	for(int i=0; i<results.size(); i++) {
        		CollisionResult result = results.getCollision(i);
        		
        		float dist = result.getDistance();
        		Vector3f point = result.getContactPoint();
        		Vector3f normal = result.getContactNormal();
        		Geometry geom = result.getGeometry();
        		
        		System.out.printf("序号：%d, 距离：%.2f, 物体名称：%s, 交点：%s, 交点法线：%s\n",
        				i, dist, geom.getName(), point, normal);
        	}
        	
        	// 离射线原点最近的交点
        	Vector3f closest = results.getClosestCollision().getContactPoint();
        	// 离射线原点最远的交点
        	Vector3f farthest =  results.getFarthestCollision().getContactPoint();
        	
        	System.out.printf("最近点：%s, 最远点：%s\n", closest, farthest);
        }
	}
}
