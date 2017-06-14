package net.jmecn.physics2d;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.TempVars;

/**
 * 投篮小游戏
 * 
 * <pre>
 * 相关参数：
 * 篮球场：长28m，宽15m
 * 篮球：重600~650g，直径24.6cm，周长75~76cm。
 * 篮板：厚3厘米，竖高1.05m，横宽1.80m，下沿离地高：2.90m
 * 篮框：内缘直径0.45m，外缘直径0.48m，离地3.05m，网长40~45cm，离篮板15cm。
 * 三分线：6.75m
 * </pre>
 * 
 * @author yanmaoyuan
 *
 */
public class TestBasketball extends SimpleApplication implements ActionListener {

    public final static String CLICK = "click";

    private final static float PreferencedWidth = 720;
    private float factor = 90.0f;// 画面放大倍率
    
    // 屏幕分辨率
    private float screenWidth;
    private float screenHeight;

    // 世界分辨率
    private float worldWidth;
    private float worldHeight;
    
    // 篮球场地宽度
    private float groundWidth = 7;
    // 场景整体向右移动一点点距离
    private float offsetX;
    
    /**
     * Dyn4j的物理世界
     */
    protected World world;
    private Node debugNode;

    /**
     * 当玩家准备投篮时，屏幕上会出现一个箭头，用来显示投篮方向和力度大小。
     */
    private final static float MAX_ARROW_LENGTH = 2;
    private Geometry arrow;
    private BitmapText text;
    
    /**
     * 投篮的力量参数。
     * 根据玩家在屏幕上按下和释放的位置，计算出投篮的方向和力量大小。
     * 
     */
    private final static float MAX_SHOOT_FORCE = 500;
    private boolean drag = false;
    private Vector2f start = new Vector2f();
    private Vector2f stop = new Vector2f();
    private Vector2f force = new Vector2f();
    // 记录光标的位置。当光标坐标没有改变时，就不要再次计算力量向量。
    private Vector2f lastCursorPos = new Vector2f();
    
    @Override
    public void simpleInitApp() {
        world = new World();
        
        viewPort.setBackgroundColor(new ColorRGBA(0.6f, 0.7f, 0.8f, 1f));

        resetCamera();

        buildArrow();
        
        buildPlayGround();

        inputManager.addMapping(CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, CLICK);
    }

    @Override
    public void simpleUpdate(float tpf) {
        world.update(tpf);
        
        if (drag) {
            updateForce();
        }
    }
    

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (CLICK.equals(name)) {
            drag = isPressed;
            if (isPressed) {
            	// 初始化向量
            	initForce();
            } else {
            	// 投篮
            	shootBall();
            }
        }
    }
    
    /**
     * 重设摄像机
     */
    private void resetCamera() {
        // disable flyCam
        if (flyCam != null) {
            flyCam.setEnabled(false);
        }

        // screen dimension
        screenWidth = cam.getWidth();
        screenHeight = cam.getHeight();
        
        factor = factor * screenWidth / PreferencedWidth;

        worldWidth = screenWidth / factor;
        worldHeight = screenHeight / factor;
        
        offsetX = (worldWidth - groundWidth) / 2;
        
        // reset 2d camera
        float right = worldWidth * 0.5f;
        float left = -right;
        float top = worldHeight * 0.5f;
        float bottom = -top;
        cam.setFrustum(-100, 100, left, right, top, bottom);
        cam.setParallelProjection(true);
        
        // 摄像机居中
        cam.setLocation(new Vector3f(right, top, 0));
    }

    /**
     * 这个箭头用来指示投篮方向和力度大小。
     */
    private void buildArrow() {
        
        Mesh mesh = new Arrow(new Vector3f(1, 0, 0));
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        
        arrow = new Geometry("Arrow", mesh);
        arrow.setMaterial(mat);
        
        text = guiFont.createLabel("0, 0N");
        text.setLocalScale(1/factor);
    }
    
    /**
     * 篮球场
     */
    private void buildPlayGround() {
    	debugNode = new Node("debugNode");
    	rootNode.attachChild(debugNode);
    	
        buildGround();
        buildBoard();
        buildBasketLeft();
        buildBasketRight();
    }

    /**
     * 地板
     */
    private void buildGround() {
        // 篮球场，三分线距离6.75m，厚0.1m
        float width = 7.0f;
        float height = 0.1f;
        Rectangle rect = new Rectangle(width, height);
        Body body = new Body();
        body.addFixture(new BodyFixture(rect));
        body.translate(offsetX + width / 2, height / 2);
        body.setMass(MassType.INFINITE);

        this.world.addBody(body);

        // create spatial
        Node node = createVisual(width, height, null);
        node.addControl(new BodyControl(body));
        debugNode.attachChild(node);
    }

    /**
     * 篮板
     */
    private void buildBoard() {
        // 篮板厚3厘米，竖高1.05m，横宽1.80m，下沿离地高：2.90m
        float width = 0.03f;
        float height = 1.05f;

        Rectangle floorRect = new Rectangle(width, height);
        Body body = new Body();
        body.addFixture(new BodyFixture(floorRect));
        body.translate(offsetX + width / 2 + 7, height / 2 + 2.9);
        body.setMass(MassType.INFINITE);

        this.world.addBody(body);

        // create spatial
        Node node = createVisual(width, height, null);
        node.addControl(new BodyControl(body));
        debugNode.attachChild(node);
    }

    /**
     * 篮框左边
     */
    private void buildBasketLeft() {
        float radius = 0.015f;

        Body body = new Body();
        Circle circle = new Circle(radius);
        body.addFixture(new BodyFixture(circle));
        body.translate(offsetX + radius + 6.4, radius + 3.05);
        body.setMass(MassType.INFINITE);

        this.world.addBody(body);

        // create spatial
        Node node = createVisual(radius * 2, radius * 2, null);
        node.addControl(new BodyControl(body));
        debugNode.attachChild(node);
    }

    /**
     * 篮框右边边
     */
    private void buildBasketRight() {
        float radius = 0.015f;

        Body body = new Body();
        Circle circle = new Circle(radius);
        body.addFixture(new BodyFixture(circle));
        body.translate(offsetX + radius + 6.85, radius + 3.05);
        body.setMass(MassType.INFINITE);

        this.world.addBody(body);

        // create spatial
        Node node = createVisual(radius * 2, radius * 2, null);
        node.addControl(new BodyControl(body));
        debugNode.attachChild(node);
    }

    /**
     * 生成一个篮球
     * @param x
     * @param y
     * @return
     */
    private Body makeBall(float x, float y) {
        // create a circle
        float radius = 0.123f;// 半径

        Body body = new Body();

        BodyFixture fixture = new BodyFixture(new Circle(radius));
        fixture.setDensity(13.6758);// 密度，可以根据篮球的质量和面积算出来。
        fixture.setFriction(0.3);// 摩察系数
        fixture.setRestitution(0.75);// 弹性系数

        body.addFixture(fixture);
        body.setMass(MassType.NORMAL);
        body.translate(x, y);

        // set some linear damping to simulate rolling friction
        body.setLinearDamping(0.05);

        return body;
    }

    private Node createVisual(float width, float height, String tex) {

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        if (tex == null) {
            mat.setColor("Color", new ColorRGBA(0.1f, 0.2f, 0.3f, 1f));
        } else {
            try {
                Texture texture = assetManager.loadTexture(tex);
                mat.setTexture("ColorMap", texture);
                mat.setColor("Color", ColorRGBA.White);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // create spatial
        Quad quad = new Quad(width, height);
        Geometry geom = new Geometry("quad", quad);
        geom.setMaterial(mat);
        geom.move(-width * 0.5f, -height * 0.5f, 0);

        Node node = new Node();
        node.attachChild(geom);
        return node;
    }

    /**
     * 根据光标位置，初始化投篮力量向量。
     */
    private void initForce() {
    	
    	Vector2f pos = inputManager.getCursorPosition();
    	lastCursorPos.set(pos);
    	
        float x = (pos.x) / factor + cam.getLocation().x + cam.getFrustumLeft();
        float y = (pos.y) / factor + cam.getLocation().y + cam.getFrustumBottom();
        
    	start.set(x, y);
        stop.set(x, y);
        force.set(0, 0);
        
        // 把箭头添加到场景图中，并初始化箭头的姿势。
        arrow.setLocalTranslation(x, y, 0);
        arrow.setLocalScale(0);
        arrow.setLocalRotation(Quaternion.IDENTITY);
        
        rootNode.attachChild(arrow);
        
        text.setText("Deg=0, F=0N");
        text.setLocalTranslation(x, y, 1);
        rootNode.attachChild(text);
    }
    
    /**
     * 投篮
     */
    private void shootBall() {
        
        force.multLocal(MAX_SHOOT_FORCE / MAX_ARROW_LENGTH);
        
        // physics body
        Body body = makeBall(start.x, start.y);
        body.applyForce(new Vector2(force.x, force.y));
        world.addBody(body);

        // create a ball
        Node node = createVisual(0.246f, 0.246f, "Textures/Dyn4j/Samples/Basketball.png");
        node.addControl(new BodyControl(body));
        rootNode.attachChild(node);
        
        arrow.removeFromParent();
        text.removeFromParent();
    }
    
    /**
     * 更新投篮的力量
     */
    private void updateForce() {
        Vector2f pos = inputManager.getCursorPosition();
        
        // 不需要计算
        if (lastCursorPos.equals(pos)) {
        	return;
        } else {
        	lastCursorPos.set(pos);
        }

        // 计算终点坐标，并计算投篮的方向向量。
        float x = (pos.x) / factor + cam.getLocation().x + cam.getFrustumLeft();
        float y = (pos.y) / factor + cam.getLocation().y + cam.getFrustumBottom();
        stop.set(x, y);
        
        // Force = Stop - Start
        stop.subtract(start, force);
        
        // 使用jme3的全局临时变量，避免分配新的对象，造成内存泄漏。
        TempVars temp = TempVars.get();
        // 计算方向
        Vector2f dir = temp.vect2d.set(force);
        dir.normalizeLocal();// 单位化
        
        /**
         * 限制投篮的力量，不让力度过大。
         */
        float length = force.length();
        if (length > MAX_ARROW_LENGTH) {
            length = MAX_ARROW_LENGTH;
            
            // 修正终点坐标
            dir.multLocal(length);
            force.set(dir);
            start.add(force, stop);
        }
        
        /**
         * 计算箭头的旋转
         */
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
        color.interpolateLocal(ColorRGBA.Green, ColorRGBA.Red, length/MAX_ARROW_LENGTH);
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
        float power = length * MAX_SHOOT_FORCE / MAX_ARROW_LENGTH;
        text.setText(String.format("Deg=%.1f, F=%.1fN", degree, power));
        
        // 释放全局临时变量
        temp.release();
    }
    
    public static void main(String[] args) {
        TestBasketball app = new TestBasketball();
        app.setPauseOnLostFocus(false);
        app.start();
    }

}
