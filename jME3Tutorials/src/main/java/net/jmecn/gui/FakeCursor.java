package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.cursors.plugins.JmeCursor;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.ui.Picture;

/**
 * 障眼法：伪装鼠标
 * 
 * @author yanmaoyuan
 *
 */
public class FakeCursor extends SimpleApplication {

    public static void main(String[] args) {
        // 启动程序
        FakeCursor app = new FakeCursor();
        app.start();
    }

    private Picture cursor;// 伪装鼠标
    private Vector3f position = new Vector3f();// 鼠标位置
    private boolean isPressed = false;// 鼠标按键状态

    // 屏幕分辨率
    private float width;
    private float height;

    @Override
    public void simpleInitApp() {
        width = cam.getWidth();
        height = cam.getHeight();

        // 改变摄像机摆动方式，显示鼠标。
        flyCam.setDragToRotate(true);

        // 隐藏鼠标图标
        hideCursor();

        // 创造伪装鼠标
        cursor = fakeCursor();
        guiNode.attachChild(cursor);

        //
        inputManager.addRawInputListener(inputListener);
    }

    /**
     * 隐藏鼠标图标
     */
    private void hideCursor() {
        // 初始化鼠标
        // 把鼠标的图片搞成透明的，这样玩家就看不见鼠标了！
        JmeCursor jmeCursor = (JmeCursor) assetManager.loadAsset("Interface/Gui/Cursor/invisible.cur");
        inputManager.setMouseCursor(jmeCursor);
    }

    /**
     * 创建一个纸片，伪装成鼠标
     * 
     * @return
     */
    private Picture fakeCursor() {
        Picture cursor = new Picture("cur");
        cursor.setWidth(32);
        cursor.setHeight(32);
        cursor.setImage(assetManager, "Interface/Gui/Cursor/MyCursor.tga", true);

        // 设置鼠标居中
        position = new Vector3f(width / 2, height / 2 - 32, Float.MAX_VALUE);
        cursor.setLocalTranslation(position);
        return cursor;
    }

    /**
     * 光标移动监听器
     */
    private RawInputListener inputListener = new RawInputListener() {
        private float x;
        private float y;

        public void onMouseMotionEvent(MouseMotionEvent evt) {
            if (isPressed)
                return;
            // 获得当前鼠标的坐标
            x = evt.getX();
            y = evt.getY();

            // 处理屏幕边缘
            x = FastMath.clamp(x, 0, cam.getWidth());
            y = FastMath.clamp(y, 0, cam.getHeight());

            position.x = x;
            position.y = y - 32;

            // 设置光标位置
            cursor.setLocalTranslation(position);
        }

        public void beginInput() {
        }

        public void endInput() {
        }

        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }

        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }

        public void onMouseButtonEvent(MouseButtonEvent evt) {
            isPressed = evt.isPressed();

            if (isPressed) {
                // 显示鼠标位置
                System.out.printf("MousePosition:(%.0f, %.0f)\n", position.x, position.y + 32f);
            }
        }

        public void onKeyEvent(KeyInputEvent evt) {
        }

        public void onTouchEvent(TouchEvent evt) {
        }
    };

}
