package net.jmecn.texture;

import java.nio.ByteBuffer;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.image.ImageRaster;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import net.jmecn.texture.ImagePainter.BlendMode;

/**
 * 演示ImagePainter工具的用法
 * @author yanmaoyuan
 *
 */
public class TestImagePainter extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		int width = 128;
		int height = 128;
		Format format = Format.BGRA8;
		
		int byteSize = width * height * format.getBitsPerPixel() / 8;
		
		Texture2D tex = new Texture2D(width, height, format);

		Image image = tex.getImage();
		// 直接创建的Texture中是没有Image数据的，因此需要手动分配一块内存。
		image.addData(ByteBuffer.allocateDirect(byteSize));

		ImagePainter painter = new ImagePainter(image);
		
		// 涂成淡粉色
		painter.paintRect(0, 0, width, height, new ColorRGBA(0.8f, 0.3f, 0.3f, 1f), BlendMode.SET);
		
		// 画一条绿色的斜线
		painter.paintLine(0, 0, width, height, 0.5f, ColorRGBA.Green, BlendMode.SET);
		
		// 在中部画一个渐变色方块
		painter.paintGradient(10, 10, width-20, height-20, ColorRGBA.Red, ColorRGBA.Magenta, ColorRGBA.Orange, ColorRGBA.Cyan, BlendMode.ADD);
		
		// 再画一个图片
		Texture monkey = assetManager.loadTexture("com/jme3/app/Monkey.png");
		Image monkeyImage = monkey.getImage();
		// 使用缩放模式，绘制一个图片
		painter.paintStretchedImage(20, 20, 60, 60, ImageRaster.create(monkeyImage), BlendMode.SET, 1.0f);
		
		// 使用该图像生成纹理，并应用到材质
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", tex);

		Geometry geom = new Geometry("Box", new Box(1, 1, 1));
		geom.setMaterial(mat);

		rootNode.attachChild(geom);
	}

	public static void main(String[] args) {
		TestImagePainter app = new TestImagePainter();
		app.start();
	}

}
