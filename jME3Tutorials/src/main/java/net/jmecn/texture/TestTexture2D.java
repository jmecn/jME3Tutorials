package net.jmecn.texture;

import java.nio.ByteBuffer;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ImageRaster;

/**
 * 演示程序生成Texture2D
 * @author yanmaoyuan
 *
 */
public class TestTexture2D extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		int width = 64;
		int height = 64;
		Format format = Format.BGRA8;
		int byteSize = width * height * format.getBitsPerPixel() / 8;
		
		Texture2D tex = new Texture2D(width, height, format);

		Image image = tex.getImage();
		ImageRaster ir = ImageRaster.create(image);
		
		// 直接创建的Texture中是没有Image数据的，因此需要手动分配一块内存。
		image.addData(ByteBuffer.allocateDirect(byteSize));

		// 把整个图片涂成淡粉色
		ColorRGBA color = new ColorRGBA(0.7f, 0.2f, 0.3f, 1f);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ir.setPixel(x, y, color);
			}
		}

		// 在图像中间画一条横线
		for (int x = 0; x < width; x++) {
			ir.setPixel(x, height / 2, ColorRGBA.Black);
		}

		// 使用该图像生成纹理，并应用到材质
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", tex);

		Geometry geom = new Geometry("Box", new Box(1, 1, 1));
		geom.setMaterial(mat);

		rootNode.attachChild(geom);
	}

	public static void main(String[] args) {
		TestTexture2D app = new TestTexture2D();
		app.start();
	}

}
