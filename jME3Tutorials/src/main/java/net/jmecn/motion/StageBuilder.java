package net.jmecn.motion;

import java.io.File;
import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.jme3.asset.plugins.ClasspathLocator;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.material.Material;
import com.jme3.material.plugins.J3MLoader;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

/**
 * 自定义网格，作为Jaime运动的舞台。
 * 
 * 这个程序执行后，将生成一个Stage.j3o文件。
 * 
 * @author yanmaoyuan
 *
 */
public class StageBuilder {
	/**
	 * 自定义网格数据
	 */
	// 顶点数据
	final static float[] Vertexes = {
		1.8660254f, -1f, 1f,// 0
		1.8660254f, -1f, 0f,// 1
		0.8660254f, -1f, 0f,// 2
		0.8660254f, -1f, 1f,// 3
		
		0.8660254f, -1f, 1f,// 4
		0.8660254f, -1f, 0f,// 5
		-0.8660254f, 0f, 0f,// 6
		-0.8660254f, 0f, 1f,// 7
		
		-0.8660254f, 0f, 1f,// 8
		-0.8660254f, 0f, -1f,// 9
		-1.8660254f, 0f, -1f,// 10
		-1.8660254f, 0f, 1f,// 11
		
		0.8660254f, 1f, 0,// 12
		0.8660254f, 1f, -1f,// 13
		-0.8660254f, 0f, -1f,// 14
		-0.8660254f, 0f, 0f,// 15
		
		1.8660254f, 1f, 0f,// 16
		1.8660254f, 1f, -1f,// 17
		0.8660254f, 1f, -1f,// 18
		0.8660254f, 1f, 0,// 19
	};
	
	// 纹理坐标
	final static float[] TexCoords = {
		1.0f, 0.0f,
		1.0f, 0.5f,
		0.732050807f, 0.5f,
		0.732050807f, 0.0f,
		
		0.732050807f, 0.0f,
		0.732050807f, 0.5f,
		0.267949193f, 0.5f,
		0.267949193f, 0.0f,
		
		0.267949193f, 0.0f,
		0.267949193f, 1.0f,
		0.0f, 1.0f,
		0.0f, 0.0f,
		
		0.732050807f, 0.5f,
		0.732050807f, 1.0f,
		0.267949193f, 1.0f,
		0.267949193f, 0.5f,
		
		1.0f, 0.5f,
		1.0f, 1.0f,
		0.732050807f, 1.0f,
		0.732050807f, 0.5f,
	};

	// 顶点法线
	final static float[] Normals = {
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		
		0.5f, 0.8660254f, 0f,
		0.5f, 0.8660254f, 0f,
		0.5f, 0.8660254f, 0f,
		0.5f, 0.8660254f, 0f,
		
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		
		-0.5f, 0.8660254f, 0f,
		-0.5f, 0.8660254f, 0f,
		-0.5f, 0.8660254f, 0f,
		-0.5f, 0.8660254f, 0f,
		
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
		0, 1, 0,
	};

	// 顶点索引
	final static int[] Indexes = {
		0, 1, 2,
		0, 2, 3,
		
		4, 5, 6,
		4, 6, 7,
		
		8, 9, 10,
		8, 10, 11,
		
		12, 13, 14,
		12, 14, 15,
		
		16, 17, 18,
		16, 18, 19,
	};
	
	public static void main(String[] args) {
		
		// 自定义网格
		Mesh mesh = new Mesh();
		mesh.setBuffer(Type.Position, 3, Vertexes);
		mesh.setBuffer(Type.Normal, 3, Normals);
		mesh.setBuffer(Type.TexCoord, 2, TexCoords);
		mesh.setBuffer(Type.Index, 3, Indexes);
		
		mesh.updateBound();
		mesh.setStatic();
		
		// 初始化资源管理器
		AssetManager assetManager = new DesktopAssetManager();
		assetManager.registerLocator("/", ClasspathLocator.class);
		assetManager.registerLoader(J3MLoader.class, "j3m", "j3md");
		
		// 加载材质
		Material mat = assetManager.loadMaterial("Models/Stage/Stage.j3m");
	
		// 创建几何体
		Geometry geom = new Geometry("stage", mesh);
		geom.setMaterial(mat);
		
		try {
			// 将Geometry保存为Stage.j3o文件
			BinaryExporter.getInstance().save(geom, new File("Stage.j3o"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
