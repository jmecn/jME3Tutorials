package net.jmecn.physics2d;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * 演示2D物理
 * 
 * @author yanmaoyuan
 *
 */
public class TestDyn4j2D extends SimpleApplication {

    // Dyn4j的物理世界
    private World world = new World();

    @Override
    public void simpleInitApp() {
        // 重置摄像机参数
        resetCamera();

        // 背景色改成白色
        viewPort.setBackgroundColor(ColorRGBA.White);

        // 添加光源
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3).normalizeLocal()));

        makeFloor();

        makeCircle();

        makeRectangle();
    }

    /**
     * 将摄像机改变为平行模式，并调整分辨率。
     */
    private void resetCamera() {
        /**
         * 改变摄像机的分辨率。
         */
        // 摄像机原分辨率
        float width = cam.getWidth();
        float height = cam.getHeight();

        // 调整后的宽度
        float w = 10;
        // 调整后的高度
        float h = w * height / width;

        // 画面边框离到中心的距离
        float right = w / 2;// 右
        float left = -right;// 左
        float top = h / 2; // 上
        float bottom = -top;// 下

        cam.setFrustum(-1000, 1000, left, right, top, bottom);

        cam.setParallelProjection(true);// 开启平行投影模式

        cam.setLocation(new Vector3f(0, 2, 0));
        cam.lookAtDirection(new Vector3f(0, 0, -1), Vector3f.UNIT_Y);
    }

    /**
     * 建造一个固定的“地板”
     */
    private void makeFloor() {
        // 矩形的高和宽
        float width = 8f;
        float height = 0.3f;

        // 刚体
        Body body = new Body();
        body.addFixture(new Rectangle(width, height));// 碰撞形状
        body.setMass(MassType.INFINITE);// 质量无穷大

        world.addBody(body);

        // 纹理贴图
        Texture tex = assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg");
        tex.setWrap(WrapMode.Repeat);
        // 材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        // 几何体
        Quad quad = new Quad(width, height);
        quad.scaleTextureCoordinates(new Vector2f(width, height));
        Geometry geom = new Geometry("floor", quad);
        geom.setMaterial(mat);

        // 使用辅助节点，用于调整模型的相对位置
        Node node = new Node("Floor");
        node.addControl(new BodyControl(body));

        node.attachChild(geom);
        geom.setLocalTranslation(-width / 2, -height / 2, 0);

        rootNode.attachChild(node);
    }

    /**
     * 制造一个圆形
     */
    private void makeCircle() {
        // 半径
        float radius = 0.123f;

        // 刚体
        Body body = new Body();

        // 碰撞形状
        BodyFixture fixture = body.addFixture(new Circle(radius));
        fixture.setRestitution(0.7);// 弹性

        body.setMass(MassType.NORMAL);// 普通质量
        body.translate(-5, 1.5);// 位置坐标
        body.setLinearVelocity(8, 5);// 线速度
        body.setLinearDamping(0.05);// 阻尼

        world.addBody(body);

        // 纹理贴图
        Texture tex = assetManager.loadTexture("Textures/Dyn4j/Samples/Basketball.png");
        tex.setWrap(WrapMode.Repeat);
        // 材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);

        // 几何体
        Quad quad = new Quad(radius * 2, radius * 2);
        Geometry geom = new Geometry("body", quad);
        geom.setMaterial(mat);

        // 辅助节点
        Node node = new Node("Ball");
        node.addControl(new BodyControl(body));

        // 调整相对位置。
        node.attachChild(geom);
        geom.setLocalTranslation(-radius, -radius, 0);

        rootNode.attachChild(node);
    }

    /**
     * 制造矩形木板
     */
    private void makeRectangle() {
        float width = 0.3f;
        float height = 0.3f;

        // 纹理贴图
        Texture tex = assetManager.loadTexture("Textures/Dyn4j/Samples/Crate.png");
        tex.setWrap(WrapMode.Repeat);
        // 材质
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        // 网格
        Quad quad = new Quad(width, height);

        // 生成10个木板，从半场开始，间隔一米摆放。
        for (int i = 0; i < 10; i++) {
            // 刚体
            Body body = new Body();
            // 碰撞形状
            body.addFixture(new Rectangle(width, height));
            body.setMass(MassType.NORMAL);
            body.translate(3, height / 2 + 0.2f + i * height);// 设置位置

            world.addBody(body);

            // 几何体
            Geometry geom = new Geometry("body", quad);
            geom.setMaterial(mat);

            Node node = new Node("Box");
            node.addControl(new BodyControl(body));

            node.attachChild(geom);
            geom.setLocalTranslation(-width / 2, -height / 2, 0);

            rootNode.attachChild(node);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // 更新Dyn4j物理世界
        world.update(tpf);
    }

    public static void main(String[] args) {
        TestDyn4j2D app = new TestDyn4j2D();
        app.start();
    }

}
