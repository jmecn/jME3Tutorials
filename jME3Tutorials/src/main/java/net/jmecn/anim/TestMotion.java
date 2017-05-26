package net.jmecn.anim;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.cinematic.events.MotionEvent.Direction;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline.SplineType;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 演示运动路径
 * @author yanmaoyuan
 *
 */
public class TestMotion extends SimpleApplication {

	public static void main(String[] args) {
		// 启动程序
		TestMotion app = new TestMotion();
		app.start();
	}
	
	/**
	 * 缩放系数
	 */
	final static float SCALE_FACTOR = 5f;
	
    private Spatial player;// 玩家
    private Spatial stage;// 舞台
    
    private boolean active = true;
    private boolean playing = false;
    
    private MotionPath motionPath;
    private MotionEvent motionControl;
    
	@Override
	public void simpleInitApp() {

		flyCam.setMoveSpeed(10);

		initLights();
		
		initInputs();
		
		initMotionPath();
		
		stage = assetManager.loadModel("Models/Stage/Stage.j3o");
		stage.scale(SCALE_FACTOR);
		rootNode.attachChild(stage);
		
		player = assetManager.loadModel("Models/Jaime/Jaime.j3o");
		rootNode.attachChild(player);
		
		AnimControl ac = player.getControl(AnimControl.class);
		AnimChannel channel = ac.createChannel();
		channel.setAnim("Run");
		channel.setSpeed(2f);
		
		motionControl = new MotionEvent(player, motionPath);
        
		// 在行进中，注视某个位置
//		Vector3f position = new Vector3f(0, 0, 0);
//		motionControl.setLookAt(position, Vector3f.UNIT_Y);
//		motionControl.setDirectionType(Direction.LookAt);
		
		// 在行进中，面朝固定方向
//		Quaternion rotation = new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y);
//		motionControl.setRotation(rotation);
//		motionControl.setDirectionType(Direction.Rotation);

		// 在行进中，面朝前进方向
		motionControl.setDirectionType(Direction.Path);
		
		// 设置走完全程所需的时间（单位：秒）。
		motionControl.setInitialDuration(10f);
	}

	/**
	 * 初始化光源
	 */
	private void initLights() {
		AmbientLight ambient = new AmbientLight(new ColorRGBA(0.4f, 0.4f, 0.4f, 1f));
		
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(0.6486864f, -0.72061276f, 0.24479222f));
		sun.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));
		
		rootNode.addLight(ambient);
		rootNode.addLight(sun);
	}
	
	/**
	 * 路径点数据
	 */
	final static float[] WayPoints = {
		1.3660254f, -1f, 0.5f,// 0
		0.8660254f, -1f, 0.5f,// 1
		-0.8660254f, 0f, 0.5f,// 2
		-1.3660254f, 0f, 0.5f,// 3
		-1.3660254f, 0f, -0.5f,// 4
		-0.8660254f, 0f, -0.5f,// 5
		0.8660254f, 1f, -0.5f,// 6
		1.3660254f, 1f, -0.5f// 7
	};
	
	/**
	 * 建造路径点
	 */
	private void initMotionPath() {
		
		motionPath = new MotionPath();

		// 路径点个数
		int count = WayPoints.length / 3;
		
		for(int i = 0; i < count; i++) {
			// 按比例放大顶点坐标
			int n = i * 3;
			float x = SCALE_FACTOR * WayPoints[n];
			float y = SCALE_FACTOR * WayPoints[n + 1];
			float z = SCALE_FACTOR * WayPoints[n + 2];
			
			motionPath.addWayPoint(new Vector3f(x, y, z));
		}
		
		motionPath.setPathSplineType(SplineType.Linear);
		
		motionPath.enableDebugShape(assetManager, rootNode);
	}
	
	/**
	 * 初始化输入
	 */
	private void initInputs() {
		inputManager.addMapping("display_hidePath", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("SwitchPathInterpolation", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("tensionUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("tensionDown", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("play_stop", new KeyTrigger(KeyInput.KEY_SPACE));
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("display_hidePath") && keyPressed) {
                    if (active) {
                        active = false;
                        motionPath.disableDebugShape();
                    } else {
                        active = true;
                        motionPath.enableDebugShape(assetManager, rootNode);
                    }
                }
                
                if (name.equals("play_stop") && keyPressed) {
                    if (playing) {
                        playing = false;
                        motionControl.stop();
                    } else {
                        playing = true;
                        motionControl.play();
                    }
                }

                if (name.equals("SwitchPathInterpolation") && keyPressed) {
                    if (motionPath.getPathSplineType() == SplineType.CatmullRom){
                        motionPath.setPathSplineType(SplineType.Linear);
                    } else {
                        motionPath.setPathSplineType(SplineType.CatmullRom);
                    }
                }

                if (name.equals("tensionUp") && keyPressed) {
                    motionPath.setCurveTension(motionPath.getCurveTension() + 0.1f);
                    System.err.println("Tension : " + motionPath.getCurveTension());
                }
                if (name.equals("tensionDown") && keyPressed) {
                    motionPath.setCurveTension(motionPath.getCurveTension() - 0.1f);
                    System.err.println("Tension : " + motionPath.getCurveTension());
                }


            }
        };

        inputManager.addListener(acl, "display_hidePath", "play_stop", "SwitchPathInterpolation", "tensionUp", "tensionDown");
	}

}
