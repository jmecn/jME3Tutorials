package net.jmecn;

import com.jme3.animation.AnimControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Cylinder;

/**
 * 演示Jaime的骨骼
 * @author yanmaoyuan
 *
 */
public class HelloSkeleton extends SimpleApplication {

    /**
     * 按F1显示/隐藏SkeletonDebugger。
     */
    public final static String TOGGLE_SKELETON_DEBUGGER = "toggle_SkeletonDebugger";
    
    /**
     * 按F2启用/禁用SkeletonContorl。
     */
    public final static String TOGGLE_SKELETON_CONTROL = "toggle_SkeletonControl";
    
    /**
     * 按F3移除/添加Jaime的附件。
     */
    public final static String TOGGLE_ATTACHMENT = "toggle_attachment";
    
    // 我们的模特：Jaime
    private Node jaime;
    
    // 骨骼调试器
    private SkeletonDebugger sd;
    
    // 骨骼控制器
    private SkeletonControl sc;
    
    // Jaime的右手
    private Node rightHand;
    // Jaime的棍子
    private Spatial stick;
    

    @Override
    public void simpleInitApp() {
        /**
         * 摄像机
         */
        cam.setLocation(new Vector3f(8.896082f, 12.328749f, 13.69658f));
        cam.setRotation(new Quaternion(-0.09457599f, 0.9038204f, -0.26543108f, -0.32204098f));
        flyCam.setMoveSpeed(10f);
        
        /**
         * 要有光
         */
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f)));
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3), new ColorRGBA(0.8f, 0.8f, 0.8f, 1f)));

        /**
         * 加载Jaime的模型
         */
        jaime = (Node)assetManager.loadModel("Models/Jaime/Jaime.j3o");
        // 将Jaime放大一点点，这样我们能观察得更清楚。
        jaime.scale(5f);
        rootNode.attachChild(jaime);
        
        // 获得SkeletonControl
        sc = jaime.getControl(SkeletonControl.class);
        
        // 打印骨骼的名称和层次关系
        System.out.println(sc.getSkeleton());
        
        /**
         * 创建一个SkeletonDebugger，用于显示骨骼的形状。
         */
        sd = new SkeletonDebugger("debugger", sc.getSkeleton());
        jaime.attachChild(sd);
        
        // 将SkeletonDebugger的姿态与Jaime的几何体同步。
        Spatial child = jaime.getChild(0);
        sd.setLocalTransform(child.getLocalTransform());
        
        // 创建材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);// 禁用深度测试，实现透视效果。
        sd.setMaterial(mat);

        /**
         * 绑定附件
         */
        stick = createAttachment();
        
        rightHand = sc.getAttachmentsNode("hand.L");
        rightHand.attachChild(stick);
        
        /**
         * 播放骨骼动画
         */
        AnimControl animControl = jaime.getControl(AnimControl.class);
        animControl.createChannel().setAnim("Walk");
        
        /**
         * 用户输入
         */
        inputManager.addMapping(TOGGLE_SKELETON_DEBUGGER, new KeyTrigger(KeyInput.KEY_F1));
        inputManager.addMapping(TOGGLE_SKELETON_CONTROL, new KeyTrigger(KeyInput.KEY_F2));
        inputManager.addMapping(TOGGLE_ATTACHMENT, new KeyTrigger(KeyInput.KEY_F3));
        inputManager.addListener(actionListener,
                TOGGLE_SKELETON_DEBUGGER, TOGGLE_SKELETON_CONTROL, TOGGLE_ATTACHMENT);
    }
    
    /**
     * 给Jaime做一根棍子当做武器
     * @return
     */
    private Spatial createAttachment() {
        Node node = new Node("Golden Stick");
        
        // 棍子的中间用黄色的材质
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", ColorRGBA.Yellow);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setColor("Ambient", ColorRGBA.Yellow);
        mat.setFloat("Shininess", 2f);
        mat.setBoolean("UseMaterialColors", true);
        
        Geometry body = new Geometry("body", new Cylinder(2, 6, 0.02f, 1.2f, true));
        body.setMaterial(mat);

        // 棍子两端用红色材质。
        mat = mat.clone();// 为了省事，直接克隆材质。
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Ambient", ColorRGBA.Red);
        
        Geometry head1 = new Geometry("head1", new Cylinder(2, 6, 0.02f, 0.4f, true));
        Geometry head2 = new Geometry("head2", new Cylinder(2, 6, 0.02f, 0.4f, true));
        head1.setMaterial(mat);
        head2.setMaterial(mat);
        
        node.attachChild(head1);
        node.attachChild(body);
        node.attachChild(head2);
        head1.move(0, 0, 0.8f);
        head2.move(0, 0, -0.8f);
        
        // 稍微调整一下这根棍子的姿态，使它和Jaime的右手掌心契合。
        node.rotate(0, FastMath.HALF_PI, 0);
        node.move(0.8f, 0.1f, 0.05f);
        return node;
    }
    
    /**
     * 动作监听器
     */
    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if (TOGGLE_SKELETON_DEBUGGER.equals(name)) {
                    toggleSkeletonDebugger();
                } else if (TOGGLE_SKELETON_CONTROL.equals(name)) {
                    toggleSkeletonControl();
                } else if (TOGGLE_ATTACHMENT.equals(name)) {
                    toggleAttachment();
                }
            }
            
        }
    };
    
    /**
     * 显示或隐藏SkeletonDebugger
     */
    private void toggleSkeletonDebugger() {
        if (sd.getParent() != null) {
            sd.removeFromParent();
        } else {
            jaime.attachChild(sd);
        }
    }
    
    /**
     * 启用/禁用SkeletonContorl
     */
    private void toggleSkeletonControl() {
        // sc.setEnabled(!sc.isEnable());
        
        if (sc.isEnabled()) {
            sc.setEnabled(false);
        } else {
            sc.setEnabled(true);
        }
    }
    
    /**
     * 移除或添加Jaime右手的附件
     */
    private void toggleAttachment() {
        if (stick.getParent() != null) {
            stick.removeFromParent();
        } else {
            rightHand.attachChild(stick);
        }
            
    }

    public static void main(String[] args) {
        HelloSkeleton app = new HelloSkeleton();
        app.start();
    }

}
