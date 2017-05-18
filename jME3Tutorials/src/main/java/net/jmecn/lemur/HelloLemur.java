package net.jmecn.lemur;

import com.jme3.app.SimpleApplication;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;

/**
 * Lemur GUI
 * @author yanmaoyuan
 *
 */
public class HelloLemur extends SimpleApplication {

	public static void main(String[] args) {
		// 启动程序
		HelloLemur app = new HelloLemur();
		app.start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void simpleInitApp() {

		// 初始化Lemur GUI
		GuiGlobals.initialize(this);

		// 加载 'glass' 样式
		BaseStyles.loadGlassStyle();

		// 将'glass'设置为GUI默认样式
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

		// 创建一个Container作为窗口中其他GUI元素的容器
		Container myWindow = new Container();
		guiNode.attachChild(myWindow);

		// 设置窗口在屏幕上的坐标
		// 注意：Lemur的GUI元素是以控件左上角为原点，向右、向下生成的。
		// 然而，作为一个Spatial，它在GuiNode中的坐标原点依然是屏幕的左下角。
		myWindow.setLocalTranslation(300, 300, 0);

		// 添加一个Label控件
		myWindow.addChild(new Label("Hello, World."));
		
		// 添加一个Button控件
		Button clickMe = myWindow.addChild(new Button("Click Me"));
		clickMe.addClickCommands(new Command<Button>() {
			@Override
			public void execute(Button source) {
				System.out.println("The world is yours.");
			}
		});
	}

}
