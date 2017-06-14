package net.jmecn.physics2d.basket;

/**
 * 游戏中的常量
 * 
 * @author yanmaoyuan
 *
 */
public interface Constants {
	/**
	 * 投篮的力量参数。 根据玩家在屏幕上按下和释放的位置，计算出投篮的方向和力量大小。
	 * 
	 */
	float MAX_SHOOT_FORCE = 500;

	/**
	 * 箭头的最大长度
	 */
	float MAX_ARROW_LENGTH = 2;

	/**
	 * 篮球场：长28m，宽15m，三分线6.75m。中圈：半径1.8米。
	 * 罚球线：从端线内沿到它的最外沿5.80米，长3.60米。
	 */
	float GROUND_LENGTH = 28;
	float GROUND_HALF_LENGTH = 14;
	float GROUND_THREE_SCORE = 6.75f;
	
	/**
	 * 篮球：重600~650g，直径24.6cm，周长75~76cm。
	 */
	float BALL_MASS = 0.65f;// kg
	float BALL_RADIUS = 0.123f;// m
	float BALL_DENSITY = 13.6758f;// kg/m²，密度可以根据篮球的质量和面积算出来。
	float BALL_FRICTION = 0.3f;// 摩擦系数
	float BALL_RESTITUTION = 0.75f;// 弹性系数
	
	/**
	 * 篮板：竖高1.05m，横宽1.80m，厚3厘米，下沿离地高：2.90m
	 */
	float BOARD_WIDTH = 1.80f;
	float BOARD_HEIGHT = 1.05f;
	float BOARD_THICKNESS = 0.03f;
	float BOARD_BOTTOM = 2.90f;
	
	/**
	 * 篮筐：内缘直径0.45m，外缘直径0.48m，离地3.05m，网长40~45cm，离篮板15cm。
	 */
	float BASKET_INNER = 0.45f;
	float BASKET_OUTTER = 0.48f;
	float BASKET_BOTTOM = 3.05f;
	float BASKET_RIGHT = 0.15f;
	
	float BASKET_EDGE_RADIUS = (BASKET_OUTTER - BASKET_INNER) * 0.5f;// 0.015f;
	
}