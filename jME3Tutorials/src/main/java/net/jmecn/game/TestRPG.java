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
import com.jme3.light.AmbientLight;
import com.jme3.light.PointLight;
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
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PointLightShadowRenderer;

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

    public TestRPG() {
        super(new StatsAppState(), new ChaseCameraAppState());
    }

    @Override
    public void simpleInitApp() {
        initCamera();
        initKeys();
        initScene();
        initLights();
    }

    /**
     * 初始化跟踪摄像机
     */
    private void initCamera() {

        // 设置跟踪相机的参数
        ChaseCameraAppState chaseCam = stateManager.getState(ChaseCameraAppState.class);

        chaseCam.setMinVerticalRotation(FastMath.DEG_TO_RAD * 10);
        chaseCam.setDefaultVerticalRotation(FastMath.DEG_TO_RAD * 30);

        chaseCam.setMinDistance(5f);
        chaseCam.setMaxDistance(20f);
        chaseCam.setDefaultDistance(5f);

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
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "LeftClick", "Jump");
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

        // 添加一个运动组件
        jaime.addControl(motionControl = new MotionControl());
        motionControl.setObserver(this);
        motionControl.setWalkSpeed(4.0f);

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
        q.scaleTextureCoordinates(new Vector2f(20, 20));
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

    /**
     * 初始化光源
     */
    private void initLights() {
        // 环境光
        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setColor(new ColorRGBA(0.6f, 0.6f, 0.6f, 1f));

        // 将光源添加到场景图中
        rootNode.addLight(ambientLight);

        // 点光源
        addPointLight(new Vector3f(25, 10, 0), new ColorRGBA(0.5f, 0.5f, 0f, 1f));
        addPointLight(new Vector3f(-25, 10, 0), new ColorRGBA(0f, 0.5f, 0.5f, 1f));

        rootNode.setShadowMode(ShadowMode.CastAndReceive);
    }

    private void addPointLight(Vector3f position, ColorRGBA color) {
        // 点光源
        PointLight pointLight = new PointLight();
        pointLight.setPosition(position);
        pointLight.setRadius(40);
        pointLight.setColor(color);
        rootNode.addLight(pointLight);

        // 点光源影子
        PointLightShadowRenderer plsr = new PointLightShadowRenderer(assetManager, 1024);
        plsr.setLight(pointLight);// 设置点光源
        plsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        viewPort.addProcessor(plsr);
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
                motionControl.setWalkSpeed(6.0f);
                // 播放“起跳”动画
                animChannel.setAnim("JumpStart");
                animChannel.setLoopMode(LoopMode.DontLoop);
                animChannel.setSpeed(1.5f);
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
            animChannel.setAnim("Walk");
            animChannel.setSpeed(2.0f);
            isWalking = true;
        }
    }

    @Override
    public void onReachTarget() {
        // 恢复行走速度
        String curAnim = animChannel.getAnimationName();
        if (curAnim != null && curAnim.startsWith("Jump")) {
            motionControl.setWalkSpeed(4.0f);
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
            motionControl.setWalkSpeed(4.0f);
            // “着地”后，根据按键状态来播放“行走”或“闲置”动画。
            if (isWalking) {
                channel.setAnim("Walk");
                channel.setSpeed(1.5f);
            } else {
                channel.setAnim("Idle");
            }
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
