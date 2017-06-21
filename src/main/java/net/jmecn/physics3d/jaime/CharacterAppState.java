package net.jmecn.physics3d.jaime;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * 角色控制模块
 * 
 * @author yanmaoyuan
 *
 */
public class CharacterAppState extends BaseAppState implements AnimEventListener {

    /**
     * 角色的尺寸
     */
    private float radius = 0.3f;// 胶囊半径0.3米
    private float height = 1.8f;// 胶囊身高1.8米
    private float stepHeight = 0.5f;// 角色步高0.5米

    /**
     * 角色相关模型
     */
    private Node character;
    private Spatial model;// 角色模型
    private Node camNode;// 辅助摄像机节点
    private CharacterControl player;// 角色控制器

    /**
     * 用于计算角色行走方向的变量
     */
    private Vector3f walkDir = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Quaternion camRot = new Quaternion();

    /**
     * 动画控制器
     */
    private AnimControl animControl;
    private AnimChannel animChannel;

    /**
     * 全局对象
     */
    private Camera cam;
    private AssetManager assetManager;
    private InputManager inputManager;
    private AppStateManager stateManager;

    @Override
    protected void initialize(Application app) {
        this.cam = app.getCamera();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.stateManager = app.getStateManager();

        initCharacter();// 角色模型
        initPhysics();// 物理控制
        initAnimation();// 骨骼动画
        initChaseCamera();//第三人称摄像机

    }

    @Override
    public void update(float tpf) {

        if (walkDir.lengthSquared() != 0) {
            // 计算摄像机在水平面的方向
            camDir.set(cam.getDirection());
            camDir.y = 0;
            camDir.normalizeLocal();

            // 根据摄像机方向，计算旋转角度
            camRot.lookAt(camDir, Vector3f.UNIT_Y);

            // 使用该旋转，改变行走方向。
            camRot.mult(walkDir, camDir);

            // 改变玩家的朝向
            player.setViewDirection(camDir);

            // 调整速度大小
            camDir.multLocal(0.1f);
        } else {
            camDir.set(0, 0, 0);
        }

        player.setWalkDirection(camDir);
        cam.setLocation(camNode.getWorldTranslation());
    }

    /**
     * 初始化角色节点
     */
    private void initCharacter() {
        this.character = new Node("Character");
        character.setLocalTranslation(0, height / 2 + radius, 0);

        // 加载模型
        this.model = assetManager.loadModel("Models/Jaime/Jaime.j3o");
        character.attachChild(model);// 挂到角色根节点下

        model.setLocalTranslation(0, -(height / 2 + radius), 0);
        model.scale(1.8f);

        // 创造一个辅助节点，用于修正摄像机的位置。
        this.camNode = new Node("Camera");
        character.attachChild(camNode);
        camNode.setLocalTranslation(0, height / 2, radius);// 将此节点上移一段距离，使摄像机位于角色的头部。
    }

    /**
     * 为角色增加物理属性
     */
    private void initPhysics() {
        // 使用胶囊体作为玩家的碰撞形状
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(radius, height, 1);

        // 使用CharacterControl来控制玩家物体
        this.player = new CharacterControl(capsuleShape, stepHeight);
        character.addControl(player);// 绑定角色控制器

        player.setJumpSpeed(10);// 起跳速度
        player.setFallSpeed(55);// 坠落速度
        player.setGravity(9.8f * 3);// 重力加速度
        player.setPhysicsLocation(new Vector3f(0, height / 2 + radius, 0));// 位置

        stateManager.getState(BulletAppState.class).getPhysicsSpace().add(player);
    }

    /**
     * 初始化角色动画
     */
    private void initAnimation() {
        animControl = model.getControl(AnimControl.class);
        animChannel = animControl.createChannel();

        animControl.addListener(this);
        animChannel.setAnim("Idle");
    }
    
    /**
     * 第三人称摄像机
     */
    private void initChaseCamera() {
        ChaseCamera chaseCam = new ChaseCamera(cam, camNode, inputManager);
        chaseCam.setInvertVerticalAxis(true);// 垂直反转
        chaseCam.setMinDistance(0.1f);// 相机离焦点的最近距离
        chaseCam.setDefaultDistance(10f);// 默认距离
    }

    @Override
    protected void onEnable() {
        SimpleApplication simpleApp = (SimpleApplication) getApplication();
        simpleApp.getRootNode().attachChild(character);
    }

    @Override
    protected void onDisable() {
        character.removeFromParent();
    }

    /**
     * 让角色跳起来
     */
    public void jump() {
        if (player.onGround()) {
            player.jump();
            
            animChannel.setAnim("JumpStart");
            animChannel.setLoopMode(LoopMode.DontLoop);
            animChannel.setSpeed(1.8f);
        }
    }

    /**
     * 让角色走路
     * 
     * @param dir
     */
    public void walk(Vector3f dir) {
        if (dir != null) {
            
            if (walkDir.lengthSquared() == 0) {
                animChannel.setAnim("Walk");
                animChannel.setSpeed(3f);
            }
            
            dir.normalizeLocal();

            walkDir.set(dir);
        }
    }

    /**
     * 让角色停下来
     */
    public void idle() {
        walkDir.set(0, 0, 0);
        if (player.onGround()) {
            animChannel.setAnim("Idle");
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if ("JumpStart".equals(animName)) {
            // “起跳”动作结束后，紧接着播放“着地”动画。
            channel.setAnim("JumpEnd");
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setSpeed(1.8f);

        } else if ("JumpEnd".equals(animName)) {
            // “着地”后，根据按键状态来播放“行走”或“闲置”动画。
            if (walkDir.lengthSquared() != 0) {
                channel.setAnim("Walk");
                channel.setLoopMode(LoopMode.Loop);
                channel.setSpeed(3f);
            } else {
                channel.setAnim("Idle");
                channel.setLoopMode(LoopMode.Loop);
            }
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    protected void cleanup(Application app) {
    }
}