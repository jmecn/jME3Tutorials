package net.jmecn.physics3d;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 演示角色在地图中自由行走。
 * 
 * @author yanmaoyuan
 *
 */
public class HelloCollision extends SimpleApplication implements ActionListener {
    /**
     * 显示或隐藏BulletAppState的debug形状。按F1键触发。
     */
    public final static String DEBUG = "debug";

    // 前、后、左、右、跳跃
    public final static String FORWARD = "forward";
    public final static String BACKWARD = "backward";
    public final static String LEFT = "left";
    public final static String RIGHT = "right";
    public final static String JUMP = "jump";

    private BulletAppState bulletAppState;

    // 角色控制器
    private CharacterControl player;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;

    // 临时变量，用于保存摄像机的方向。避免在simpleUpdate中重复创建对象。
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();

    @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // 初始化按键
        initKeys();

        // 初始化光照
        initLight();

        // 初始化场景
        initScene();

        // 初始化玩家
        initPlayer();
    }

    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);

        // 计算运动方向
        boolean changed = false;
        if (left) {
            walkDirection.addLocal(camLeft);
            changed = true;
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
            changed = true;
        }
        if (up) {
            walkDirection.addLocal(camDir);
            changed = true;
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
            changed = true;
        }
        if (changed) {
            walkDirection.y = 0;// 将行走速度的方向限制在水平面上。
            walkDirection.normalizeLocal();// 单位化
            walkDirection.multLocal(0.5f);// 改变速率
        }

        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
    }

    /**
     * 初始化按键输入
     */
    private void initKeys() {
        inputManager.addMapping(DEBUG, new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(FORWARD, new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));

        inputManager.addListener(this, DEBUG, LEFT, RIGHT, FORWARD, BACKWARD, JUMP);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (DEBUG.equals(name) && isPressed) {
            boolean debugEnabled = bulletAppState.isDebugEnabled();
            bulletAppState.setDebugEnabled(!debugEnabled);
        } else if (LEFT.equals(name)) {
            left = isPressed;
        } else if (RIGHT.equals(name)) {
            right = isPressed;
        } else if (FORWARD.equals(name)) {
            up = isPressed;
        } else if (BACKWARD.equals(name)) {
            down = isPressed;
        } else if (JUMP.equals(name) && isPressed) {
            player.jump();
        }
    }

    /**
     * 初始化光照
     */
    private void initLight() {
        // 环境光
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));

        // 阳光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3).normalizeLocal());

        rootNode.addLight(ambient);
        rootNode.addLight(sun);
    }

    /**
     * 初始化场景
     */
    private void initScene() {
        // 从zip文件中加载地图场景
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial sceneModel = assetManager.loadModel("main.scene");

        // 为地图创建精确网格形状
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);

        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
    }

    /**
     * 初始化玩家
     */
    private void initPlayer() {
        // 使用胶囊体作为玩家的碰撞形状
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.3f, 1.8f, 1);
        float stepHeight = 0.5f;// 玩家能直接登上多高的台阶？

        // 使用CharacterControl来控制玩家物体
        player = new CharacterControl(capsuleShape, stepHeight);
        player.setJumpSpeed(10);// 起跳速度
        player.setFallSpeed(55);// 坠落速度
        player.setGravity(9.8f * 3);// 重力加速度
        player.setPhysicsLocation(new Vector3f(0, 10, 0));// 位置

        bulletAppState.getPhysicsSpace().add(player);
    }

    public static void main(String[] args) {
        HelloCollision app = new HelloCollision();
        app.start();
    }
}
