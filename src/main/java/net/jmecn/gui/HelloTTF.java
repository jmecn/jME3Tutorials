package net.jmecn.gui;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;

import truetypefont.TrueTypeFont;
import truetypefont.TrueTypeKey;
import truetypefont.TrueTypeLoader;

/**
 * 测试TTF字体
 * 
 * @author yanmaoyuan
 *
 */
public class HelloTTF extends SimpleApplication {

	public static void main(String[] args) {
		// 启动程序
		HelloTTF app = new HelloTTF();
		app.start();
	}

	// 字形
	public final static int PLAIN = 0;// 普通
	public final static int BOLD = 1;// 粗体
	public final static int ITALIC = 2;// 斜体
	
	// 字号
	public final static int FONT_SIZE = 64;
	
	@Override
	public void simpleInitApp() {
		// 注册ttf字体资源加载器
		assetManager.registerLoader(TrueTypeLoader.class, "ttf");

		// 创建字体 (例如：楷书)
		TrueTypeKey ttk = new TrueTypeKey("Interface/Fonts/SIMKAI.TTF", // 字体
				PLAIN, // 字形：0 普通、1 粗体、2 斜体
				FONT_SIZE);// 字号

		TrueTypeFont font = (TrueTypeFont) assetManager.loadAsset(ttk);

		
		// 在屏幕中央显示一首五言绝句。
		String[] poem = { "空山新雨后", "天气晚来秋", "明月松间照", "清泉石上流" };

		// 计算坐标
		float x = 0.5f * (cam.getWidth() - FONT_SIZE * 5);
		float y = 0.5f * (cam.getHeight() + FONT_SIZE * 2);
		
		for (int i = 0; i < poem.length; i++) {
			// 创建文字
			Geometry text = font.getBitmapGeom(poem[i], 0, ColorRGBA.White);
			text.setLocalTranslation(x, y - i * FONT_SIZE, 0);
			guiNode.attachChild(text);
		}

	}

}
