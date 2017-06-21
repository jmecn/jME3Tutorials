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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 * 演示Dyn4j物理引擎
 * 
 * @author yanmaoyuan
 *
 */
public class TestDyn4j extends SimpleApplication {

    // Dyn4j的物理世界
    private World world = new World();

    @Override
    public void simpleInitApp() {
        // 调整摄像机的位置，便于观察场景。
        cam.setLocation(new Vector3f(-5.326285f, 13.811526f, 22.174831f));
        cam.setRotation(new Quaternion(0.024544759f, 0.9620277f, -0.2556783f, 0.09235176f));

        // 背景色改成白色
        viewPort.setBackgroundColor(ColorRGBA.White);

        // 添加光源
        rootNode.addLight(new DirectionalLight(new Vector3f(-1, -2, -3).normalizeLocal()));

        makeFloor();

        makeCircle();

        makeRectangle();
    }

    /**
     * 创建随机颜色的感光材质
     * 
     * @return
     */
    private Material getMaterial() {
        ColorRGBA color = ColorRGBA.randomColor();

        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Diffuse", color);
        mat.setColor("Ambient", color.mult(0.7f));
        mat.setColor("Specular", ColorRGBA.Black);
        mat.setFloat("Shininess", 1f);
        mat.setBoolean("UseMaterialColors", true);
        return mat;
    }

    /**
     * 建造一个固定的“地板”
     */
    private void makeFloor() {
        // 矩形的高和宽
        float width = 28f;
        float height = 0.1f;

        // 刚体
        Body body = new Body();
        body.addFixture(new Rectangle(width, height));// 碰撞形状
        body.setMass(MassType.INFINITE);// 质量无穷大

        world.addBody(body);

        // 几何体
        // 2D的物体并没有厚度，但jME3毕竟是一个3D引擎。所以也可以用3D方块来表示2D的矩形。 :)
        Geometry geom = new Geometry("body", new Box(width / 2, height / 2, 0.1f));
        geom.setMaterial(getMaterial());
        
        // 绑定刚体和模型
        geom.addControl(new BodyControl(body));

        rootNode.attachChild(geom);
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
        body.translate(-10, 1);// 位置坐标
        body.setLinearVelocity(8, 5);// 线速度
        body.setLinearDamping(0.05);// 阻尼

        world.addBody(body);
        
        // 几何体
        Geometry geom = new Geometry("body", new Sphere(32, 32, radius));
        geom.setMaterial(getMaterial());

        // 绑定刚体和模型
        geom.addControl(new BodyControl(body));

        rootNode.attachChild(geom);
    }

    /**
     * 制造矩形木板
     */
    private void makeRectangle() {
        float width = 0.03f;
        float height = 1.05f;

        // 生成10个木板，从半场开始，间隔一米摆放。
        for (int i = 0; i < 10; i++) {
            // 刚体
            Body body = new Body();
            // 碰撞形状
            body.addFixture(new Rectangle(width, height));
            body.setMass(MassType.NORMAL);
            body.translate(i * 1, height / 2);// 设置位置

            world.addBody(body);

            // 几何体
            Geometry geom = new Geometry("body", new Box(width / 2, height / 2, 0.1f));
            geom.setMaterial(getMaterial());

            // 绑定刚体和模型
            geom.addControl(new BodyControl(body));

            rootNode.attachChild(geom);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        // 更新Dyn4j物理世界
        world.update(tpf);
    }

    public static void main(String[] args) {
        TestDyn4j app = new TestDyn4j();
        app.start();
    }

}
