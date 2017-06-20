package net.jmecn.physics3d;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 使用Bullet物理引擎
 * 
 * @author yanmaoyuan
 */
public class TestBullet extends SimpleApplication {

    @Override
    public void simpleInitApp() {
        cam.setLocation(new Vector3f(-18.317675f, 16.480816f, 13.418682f));
        cam.setRotation(new Quaternion(0.13746259f, 0.86010045f, -0.3107305f, 0.38049686f));

        // 初始化物理引擎
        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        // 获得Bullet的物理空间，它代表了运转物理规则的世界。
        PhysicsSpace physicsSpace = bulletAppState.getPhysicsSpace();

        // 创建地板的刚体对象，尺寸为长28m，宽15m，厚0.1m。
        // 刚体的质量设为0，这样地板就不会受到任何力的作用。
        RigidBodyControl rigidBodyFloor = new RigidBodyControl(0);
        Vector3f halfExtents = new Vector3f(14f, 0.05f, 7.5f);
        rigidBodyFloor.setCollisionShape(new BoxCollisionShape(halfExtents));
        rigidBodyFloor.setRestitution(0.8f);// 弹性系数
        // 将刚体添加到物理空间中
        physicsSpace.add(rigidBodyFloor);

        // 创建球形刚体，质量为0.65kg，半径为0.123m。
        RigidBodyControl rigidBodyBall = new RigidBodyControl(0.65f);
        float radius = 0.123f;
        rigidBodyBall.setCollisionShape(new SphereCollisionShape(radius));
        rigidBodyBall.setPhysicsLocation(new Vector3f(-10, 1, 0));// 在物理世界中的坐标
        rigidBodyBall.setLinearVelocity(new Vector3f(8, 5, 0));// 线速度
        rigidBodyBall.setFriction(0.2f);// 摩擦系数
        rigidBodyBall.setRestitution(0.8f);// 弹性系数
        physicsSpace.add(rigidBodyBall);

        // 创建挡板的刚体对象，质量为0.2kg，尺寸为横宽1.8m * 竖高1.05m * 厚0.03m。
        RigidBodyControl rigidBodyBoard = new RigidBodyControl(0.2f);
        halfExtents = new Vector3f(0.015f, 0.525f, 0.9f);
        rigidBodyBoard.setCollisionShape(new BoxCollisionShape(halfExtents));

        // 克隆10个挡板，从半场开始，间隔一米摆放。
        for (int i = 0; i < 10; i++) {
            RigidBodyControl board = (RigidBodyControl) rigidBodyBoard.jmeClone();
            board.setPhysicsLocation(new Vector3f(i * 1, 0.52f, 0f));
            // 将刚体添加到Bullet的物理空间中。
            physicsSpace.add(board);
        }

        // 开启调试模式，这样能够可视化观察物体之间的运动。
        bulletAppState.setDebugEnabled(true);
    }

    public static void main(String[] args) {
        TestBullet app = new TestBullet();
        app.start();
    }

}
