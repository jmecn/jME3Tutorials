package net.jmecn.physics2d.basket;

import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.Camera;

/**
 * 篮球场中的物理模块
 * @author yanmaoyuan
 *
 */
public class PhysicsState extends BaseAppState {

	/**
	 * Dyn4j的物理世界
	 */
	protected World world;
	
	// 场景整体向右移动一点点距离
	private float offsetX;

	public PhysicsState() {
		world = new World();
	}

	@Override
	protected void initialize(Application app) {
		Camera cam = app.getCamera();
		
		AxisAlignedBounds aabb = new AxisAlignedBounds(cam.getWidth(), cam.getHeight());
		aabb.translate(cam.getLocation().x, cam.getLocation().y);
		
		world.setBounds(aabb);

		buildGround();
		buildBoard();
		buildBasketLeft();
		buildBasketRight();
	}

	@Override
	public void update(float tpf) {
		world.update(tpf);
	}

	@Override
	protected void cleanup(Application app) {
		world.removeAllListeners();
		world.removeAllBodiesAndJoints();
	}

	public void addBody(Body body) {
		world.addBody(body);
		
		getStateManager().getState(PhysicsDebugState.class).addDebugShape(body);
	}

	public void addJoint(Joint joint) {
		world.addJoint(joint);
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

		addBody(body);
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

		addBody(body);
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

		addBody(body);
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

		addBody(body);

	}

    /**
     * 生成一个篮球
     * @param pos
     * @param force
     * @return
     */
    public Body makeBall(Vector2f pos, Vector2f force) {

        Body body = new Body();

        BodyFixture fixture = new BodyFixture(new Circle(Constants.BALL_RADIUS));
        fixture.setDensity(Constants.BALL_DENSITY);
        fixture.setFriction(Constants.BALL_FRICTION);
        fixture.setRestitution(Constants.BALL_RESTITUTION);
        body.addFixture(fixture);
        
        body.setMass(MassType.NORMAL);
        body.translate(pos.x, pos.y);
        body.applyForce(new Vector2(force.x, force.y));
        body.setAngularVelocity(1.24f);
        body.setLinearDamping(0.05);

        addBody(body);
        
        return body;
    }

	@Override
	protected void onEnable() {
	}

	@Override
	protected void onDisable() {
	}
}
