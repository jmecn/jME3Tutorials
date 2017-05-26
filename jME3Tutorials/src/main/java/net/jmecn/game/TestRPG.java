package net.jmecn.game;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;

import net.jmecn.logic.FloatControl;

/**
 * 测试RPG游戏中常见的运动方式。
 * 
 * @author yanmaoyuan
 *
 */
public class TestRPG extends SimpleApplication implements ActionListener, AnimEventListener, Observer {

    public static void main(String[] args) {
        TestRPG app = new TestRPG();
        app.start();
    }

    // 平台
    private Spatial floor;
    // 标志
    private Spatial flag;

    private MotionControl motionControl;

    private AnimChannel animChannel;
    private boolean isWalking = false;
    private boolean isRunning = false;

    public TestRPG() {
        super(new StatsAppState(), new ChaseCameraAppState(), new AiAppState(), new LightAppState());
    }
    
    @Override
    public void simpleInitApp() {
        initCamera();
        initKeys();
        initScene();
    }

    /**
     * 初始化跟踪摄像机
     */
    private void initCamera() {

        // 设置跟踪相机的参数
        ChaseCameraAppState chaseCam = stateManager.getState(ChaseCameraAppState.class);

        // 仰角最小10°，最大90°，默认30°
        chaseCam.setMinVerticalRotation(FastMath.DEG_TO_RAD * 10);
        chaseCam.setDefaultVerticalRotation(FastMath.DEG_TO_RAD * 30);

        // 摄像机到观察点的距离，最小5f，最大30f，默认5f
        chaseCam.setMinDistance(5f);
        chaseCam.setMaxDistance(30f);
        chaseCam.setDefaultDistance(10f);

        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setInvertHorizontalAxis(true);

        // 鼠标右键触发旋转
        chaseCam.setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        stateManager.attach(chaseCam);
    }

    /**
     * 初始化按键
     */
    private void initKeys() {
        inputManager.addMapping("LeftClick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "LeftClick", "Jump", "Run");
    }

    /**
     * 初始化场景
     */
    private void initScene() {
        // 加载Jaime模型
        loadJaime();

        // 创建一个平面作为舞台
        this.floor = createFloor();

        // 创建一个目标点的标记
        this.flag = createTargetFlag();
    }

    /**
     * 加载Jaime模型
     * 
     * @return
     */
    private Spatial loadJaime() {
        // 加载模型
        Node jaime = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        jaime.scale(2);
        rootNode.attachChild(jaime);
        jaime.setShadowMode(ShadowMode.Cast);

        // 创造一个空心节点，作为摄像机的交点
        Node camPiovt = new Node("CamPiovt");
        camPiovt.move(0, 1f, 0);
        jaime.attachChild(camPiovt);

        stateManager.getState(ChaseCameraAppState.class).setTarget(camPiovt);
        stateManager.getState(AiAppState.class).setPlayer(jaime);
        
        // 添加一个运动组件
        jaime.addControl(motionControl = new MotionControl(4.0f));
        motionControl.setObserver(this);

        AnimControl animControl = jaime.getControl(AnimControl.class);
        animControl.addListener(this);

        // 播放动画
        animChannel = animControl.createChannel();
        animChannel.setAnim("Idle");

        return jaime;
    }

    /**
     * 创建一个平面作为Jaime行走的舞台
     * 
     * @return
     */
    private Spatial createFloor() {

        Quad q = new Quad(50, 50);
        q.scaleTextureCoordinates(new Vector2f(10, 10));
        Geometry stage = new Geometry("Stage", q);
        stage.setMaterial(assetManager.loadMaterial("Textures/Terrain/Pond/Pond.j3m"));

        stage.rotate(-FastMath.HALF_PI, 0, 0);
        stage.center();

        stage.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(stage);

        return stage;
    }

    /**
     * 创造一个标记点
     */
    private Spatial createTargetFlag() {

        Geometry arrow = new Geometry("Arrow", new Arrow(new Vector3f(0, -1f, 0)));
        arrow.move(0, 1.5f, 0);
        arrow.addControl(new FloatControl(0.3f, 1.5f));

        Geometry sphere = new Geometry("Sphere", new Sphere(6, 9, 0.2f));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setLineWidth(2f);

        Node flagNode = new Node();
        flagNode.attachChild(arrow);
        flagNode.attachChild(sphere);

        flagNode.setMaterial(mat);
        flagNode.setShadowMode(ShadowMode.Off);

        return flagNode;
    }


    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        /**
         * 若Jaime已经处于JumpStart/Jumping/JumpEnd状态，就不要再做其他动作了。
         */
        // 查询当前正在播放的动画
        String curAnim = animChannel.getAnimationName();
        if (curAnim != null && curAnim.startsWith("Jump")) {
            return;
        }

        if (isPressed) {
            if ("LeftClick".equals(name)) {
                pickTarget();
            } else if ("Jump".equals(name)) {
            	// 跳跃时是原有速度的1.5倍
                if (isRunning) {
                	motionControl.setWalkSpeed(3.0f);
                } else {
                	motionControl.setWalkSpeed(1.5f);
                }
                
                // 播放“起跳”动画
                animChannel.setAnim("JumpStart");
                animChannel.setLoopMode(LoopMode.DontLoop);
                animChannel.setSpeed(1.5f);
            } else if ("Run".equals(name)) {
            	isRunning = !isRunning;
            	
            	if (isRunning) {
                	motionControl.setWalkSpeed(1.5f);
                	if (curAnim.equals("Walk")) {
                		animChannel.setAnim("Run");
                		animChannel.setSpeed(2f);
                	}
                } else {
                	motionControl.setWalkSpeed(1.0f);
                	if (curAnim.equals("Run")) {
                		animChannel.setAnim("Walk");
                		animChannel.setSpeed(2f);
                	}
                }
            	
            }
        }
    }

    /**
     * 拣选目标点
     */
    private void pickTarget() {

        Vector2f pos = inputManager.getCursorPosition();
        Vector3f orgin = cam.getLocation();
        Vector3f to = cam.getWorldCoordinates(pos, 0.3f);

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(orgin, to.subtractLocal(orgin));
        floor.collideWith(ray, results);

        if (results.size() > 0) {
            // 设置目标点
            setTarget(results.getClosestCollision().getContactPoint());
        }
    }

    /**
     * 设置目标点
     * 
     * @param target
     */
    private void setTarget(Vector3f target) {
        flag.setLocalTranslation(target);
        rootNode.attachChild(flag);

        // 设置目标点
        motionControl.setTarget(target);

        if (!isWalking) {
            isWalking = true;
            if (isRunning) {
            	animChannel.setAnim("Run");
            } else {
            	animChannel.setAnim("Walk");
            }
            animChannel.setSpeed(2.0f);
        }
    }

    @Override
    public void onReachTarget() {
        // 恢复行走速度
        String curAnim = animChannel.getAnimationName();
        if (curAnim != null && curAnim.startsWith("Jump")) {
            if (isRunning) {
            	motionControl.setWalkSpeed(1.5f);
            } else {
            	motionControl.setWalkSpeed(1.0f);
            }
        }

        // 到达目标点，把动画改为Idle
        animChannel.setAnim("Idle");
        flag.removeFromParent();
        isWalking = false;

    }

    /**
     * 动画事件监听器
     */
    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if ("JumpStart".equals(animName)) {
            // “起跳”动作结束后，紧接着播放“着地”动画。
            channel.setAnim("JumpEnd");
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1.5f);

        } else if ("JumpEnd".equals(animName)) {
            if (isRunning) {
            	motionControl.setWalkSpeed(1.5f);
            } else {
            	motionControl.setWalkSpeed(1.0f);
            }
            
            // “着地”后，根据按键状态来播放“行走”或“闲置”动画。
            if (isWalking) {
            	if (isRunning) {
            		channel.setAnim("Run");
            		channel.setSpeed(2f);
            	} else {
            		channel.setAnim("Walk");
            		channel.setSpeed(2f);
            	}
            } else {
                channel.setAnim("Idle");
            }
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
