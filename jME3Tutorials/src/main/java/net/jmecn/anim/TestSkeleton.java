package net.jmecn.anim;

import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;

/**
 * 显示Jaime的骨骼
 * @author yanmaoyuan
 *
 */
public class TestSkeleton extends SimpleApplication {

    private Node jaime;

    @Override
    public void simpleInitApp() {
        // 要有光
        rootNode.addLight(new AmbientLight(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f)));
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3), new ColorRGBA(0.8f, 0.8f, 0.8f, 1f)));

        // 加载Jaime的模型
        jaime = (Node)assetManager.loadModel("Models/Jaime/Jaime.j3o");
        rootNode.attachChild(jaime);

        AnimControl animControl = jaime.getControl(AnimControl.class);
        animControl.createChannel().setAnim("Walk");

        // 创建一个SkeletonDebugger，用于显示骨骼的形状
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setDepthTest(false);
        
        SkeletonDebugger skeletonDebugger = new SkeletonDebugger("debugger", animControl.getSkeleton());
        skeletonDebugger.setMaterial(mat);
        
        // 将SkeletonDebugger的姿态与Jaime同步。
        Spatial child = jaime.getChild(0);
        skeletonDebugger.setLocalTransform(child.getLocalTransform());

        jaime.attachChild(skeletonDebugger);

        jaime.scale(5f);
    }

    public static void main(String[] args) {
        TestSkeleton app = new TestSkeleton();
        app.start();
    }

}
