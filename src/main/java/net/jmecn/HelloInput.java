package net.jmecn;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * 用户交互
 * 
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
    /**
     * 加载模型
     */
    public final static String LOAD = "Load";

    @Override
    public void simpleInitApp() {
        // 初始化光源
        initLight();

        // 初始化输入
        initInput();
    }

    /**
     * 初始化光源
     */
    private void initLight() {
        // 定向光
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -2, -3));

        // 环境光
        AmbientLight ambient = new AmbientLight();

        // 调整光照亮度
        ColorRGBA lightColor = new ColorRGBA();
        sun.setColor(lightColor.mult(0.6f));
        ambient.setColor(lightColor.mult(0.4f));

        // #3 添加到场景图中
        rootNode.addLight(sun);
        rootNode.addLight(ambient);
    }

    /**
     * 初始化输入
     */
    private void initInput() {
        // 检测输入设备
        System.out.printf("Mouse: %b\nKeyboard: %b\nJoystick: %b\nTouch: %b\n", mouseInput != null, keyInput != null,
                joyInput != null, touchInput != null);

        // 绑定消息和触发器
        inputManager.addMapping(FIRE, new KeyTrigger(KeyInput.KEY_SPACE),
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addMapping(LOAD, new KeyTrigger(KeyInput.KEY_L));

        // 绑定消息和监听器
        inputManager.addListener(new MyActionListener(), FIRE, LOAD);

        // 原始输入监听器
        inputManager.addRawInputListener(new MyRawInputListener());
    }

    /**
     * 加载模型
     */
    private void loadModel() {
        new Thread () {
            public void run() {
                // 导入模型
                final Spatial model = assetManager.loadModel("Models/Ashe/b_ashe_b.obj");
                model.scale(0.03f);// 按比例缩小
                model.center();// 将模型的中心移到原点
                
                // 通知主线程，将模型添加到场景图中。
                enqueue(new Runnable() {
                    public void run() {
                        rootNode.attachChild(model);
                    }
                });
            }
        }.start();
    }

    // 事件监听器
    class MyActionListener implements ActionListener {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if (FIRE.equals(name)) {
                    System.out.println("bang!");
                } else if (LOAD.equals(name)) {
                    loadModel();
                }
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

            // 当玩家按下Y键时，输出"Yes!"
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
            System.out.println("x=" + x + " y=" + y);
        }

        @Override
        public void onMouseButtonEvent(MouseButtonEvent evt) {
        }

        @Override
        public void beginInput() {
        }

        @Override
        public void endInput() {
        }

        @Override
        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }

        @Override
        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }

        @Override
        public void onTouchEvent(TouchEvent evt) {
        }
    }

}
