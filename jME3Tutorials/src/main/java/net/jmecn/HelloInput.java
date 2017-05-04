package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.*;

/**
 * 用户交互
 * @author yanmaoyuan
 *
 */
public class HelloInput extends SimpleApplication {

	public static void main(String[] args) {
		HelloInput app = new HelloInput();
		app.start();
	}
	
	/**
	 * 开火消息
	 */
	public final static String FIRE = "Fire";
	
	@Override
	public void simpleInitApp() {
		// 绑定消息和触发器
		inputManager.addMapping(FIRE, 
				new KeyTrigger(KeyInput.KEY_SPACE), 
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		
		// 绑定消息和监听器
		inputManager.addListener(new MyActionListener(), FIRE);
		
		
		// 原始输入监听器
		inputManager.addRawInputListener(new MyRawInputListener());
	}
	
	// 事件监听器
	class MyActionListener implements ActionListener {
		@Override
		public void onAction(String name, boolean isPressed, float tpf) {
			if (FIRE.equals(name) && isPressed) {
				System.out.println("bang!");
			}
		}
	}
	
	// 原始输入监听器
	class MyRawInputListener implements RawInputListener {

		/**
		 * 键盘输入事件
		 */
		@Override
		public void onKeyEvent(KeyInputEvent evt) {
			int keyCode = evt.getKeyCode();
			boolean isPressed = evt.isPressed();
			
			//当玩家按下Y键时，输出"Yes!"
			if (isPressed) {
				switch (keyCode) {
				case KeyInput.KEY_Y: {
					System.out.println("Yes!");
					break;
				}
				}
			}
		}
		
		/**
		 * 鼠标输入事件
		 */
		@Override
		public void onMouseMotionEvent(MouseMotionEvent evt) {
			int x = evt.getX();
			int y = evt.getY();
			// 打印鼠标的坐标
			System.out.println("x=" + x + " y="+y);
		}

		@Override public void onMouseButtonEvent(MouseButtonEvent evt) {}
		
		@Override public void beginInput() {}

		@Override public void endInput() {}

		@Override public void onJoyAxisEvent(JoyAxisEvent evt) {}

		@Override public void onJoyButtonEvent(JoyButtonEvent evt) {}

		@Override public void onTouchEvent(TouchEvent evt) {}
	}

}
