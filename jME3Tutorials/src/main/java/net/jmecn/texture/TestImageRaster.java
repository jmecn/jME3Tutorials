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
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.ImageRaster;

/**
 * 演示ImageRaster的使用。
 * 
 * @author yan
 *
 */
public class TestImageRaster extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		// 分辨率
		int width = 64;
		int height = 64;
		
		// 图形格式
		Format format = Format.ABGR8;
		// 字节数
		int byteSize = width * height * format.getBitsPerPixel() / 8;
		
		// 在内存中创建一个图像
		Image image = new Image(format, 
				width, height, 
				ByteBuffer.allocate(byteSize),
				null,// mipmap size
				ColorSpace.sRGB);
		
		// 创建一个像素修改器
		ImageRaster ir = ImageRaster.create(image);
		
		// 把整个图片涂成淡粉色
		ColorRGBA color = new ColorRGBA(0.7f, 0.2f, 0.3f, 1f);
		for(int y=0; y<height; y++) {
			for(int x=0; x<width; x++) {
				ir.setPixel(x, y, color);
			}
		}
		
		// 在图像中间画一条横线
		for(int x=0; x<width; x++) {
			ir.setPixel(x, height/2, ColorRGBA.Black);
		}
		
		// 使用该图像生成纹理，并应用到材质
		Texture2D texture = new Texture2D(image);
		
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", texture);
		
		Geometry geom = new Geometry("Box", new Box(1, 1, 1));
		geom.setMaterial(mat);
		
		rootNode.attachChild(geom);
	}

	public static void main(String[] args) {
		TestImageRaster app = new TestImageRaster();
		app.start();
	}
}
