package net.jmecn.outscene;

import java.io.InputStream;
import java.util.Scanner;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

/**
 * 解析EarthSculptor的map文件，生成地形。
 * @author yanmaoyuan
 *
 */
public class TestEarthSculptorMap extends SimpleApplication {

    public static void main(String[] args) {
        TestEarthSculptorMap app = new TestEarthSculptorMap();
        app.start();
    }

    private AssetInfo assetInfo;

    private String heightMap = null;
    private String[] detailMaps = new String[3];
    private double[] detailScales = new double[12];
    private String[] detailTextures = new String[12];
    private int textureCount = 0;
    private String mapname = "";
    private Vector3f lightDirection = null;
    private ColorRGBA light = null;
    private ColorRGBA ambient = null;

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(100);

        this.assetInfo = assetManager.locateAsset(new AssetKey<Spatial>("Scenes/Maps/DefaultMap/default.map"));

        if (assetInfo != null) {
            parse(assetInfo.openStream());

            validate();

            initTerrain();
            
            initLight();
        }
    }

    private void parse(InputStream in) {
        Scanner scanner = new Scanner(in);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (line.length() == 0) {
                continue;
            }

            String[] args = line.split(" ");
            if (args[0].equals("detailScales")) {
                if (args.length < 2) {
                    continue;
                }
                for (int i = 1; i < args.length; i++) {
                    detailScales[i - 1] = Double.parseDouble(args[i]);
                }
            } else if (args[0].startsWith("detailtexture")) {
                if (args.length < 2) {
                    continue;
                }

                String num = args[0].substring(13);
                int index = Integer.parseInt(num);

                detailTextures[index - 1] = args[1].replaceAll("\"", "");
                if (detailTextures[index - 1].length() > 0)
                    textureCount++;
            } else if (args[0].equals("mapname")) {
                if (args.length != 2) {
                    continue;
                }
                mapname = args[1];
            } else if (args[0].equals("lightDirection")) {
                if (args.length != 4) {
                    continue;
                }
                double[] dir = new double[3];
                for (int i = 1; i < args.length; i++) {
                    dir[i - 1] = Double.parseDouble(args[i]);
                }

                lightDirection = new Vector3f((float) -dir[0], (float) -dir[1], (float) -dir[2]);
            } else if (args[0].equals("light")) {
                if (args.length != 2) {
                    continue;
                }
                light = getColor(args[1]);
            } else if (args[0].equals("ambient")) {
                if (args.length != 2) {
                    continue;
                }
                ambient = getColor(args[1]);
            }
        }

        scanner.close();
    }

    private ColorRGBA getColor(String arg) {
        long color = Long.parseLong(arg);
        int alpha = (int) ((color >> 24) & 0xFF);
        int red = (int) ((color >> 16) & 0xFF);
        int green = (int) ((color >> 8) & 0xFF);
        int blue = (int) (color & 0xFF);

        float a = alpha / 255f;
        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;

        return new ColorRGBA(r, g, b, a);
    }

    private void validate() {
        String folder = assetInfo.getKey().getFolder();

        /**
         * 修正纹理路径
         */
        for (int i = 0; i < textureCount; i++) {
            String texture = detailTextures[i];
            texture = texture.replaceAll("\\\\", "/");
            detailTextures[i] = texture.replaceAll("\\[HOME\\]", folder);
        }

        /**
         * 修正alphaMap和heightMap的路径
         */
        String name = assetInfo.getKey().getName();

        // 去掉后缀
        int dotIndex = name.lastIndexOf(".");
        name = name.substring(0, dotIndex);

        this.heightMap = name + ".png";

        if (textureCount <= 4) {
            this.detailMaps[0] = name + "_d.png";
        } else {
            int cnt = textureCount / 4;
            for(int i=0; i<cnt; i++) {
                this.detailMaps[i] = name + "_d" + i + ".png";
            }
        }
    }
    
    /**
     * 生成地形
     */
    private void initTerrain() {
        /**
         * 加载材质
         */
        Material material = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        material.setBoolean("useTriPlanarMapping", false);

        Texture alphaMap = assetManager.loadTexture(detailMaps[0]);
        material.setTexture("AlphaMap", alphaMap);

        for (int i = 0; i < textureCount; i++) {
            Texture diffuseMap = assetManager.loadTexture(detailTextures[i]);
            diffuseMap.setWrap(WrapMode.Repeat);
            float scale = (float) detailScales[i];

            String key1 = i == 0 ? "DiffuseMap" : "DiffuseMap_" + i;
            String key2 = String.format("DiffuseMap_%d_scale", i);

            material.setTexture(key1, diffuseMap);
            material.setFloat(key2, scale);
        }

        /**
         * 加载高度图
         */
        Texture heightTexture = assetManager.loadTexture(heightMap);
        ImageBasedHeightMap heightMap = new ImageBasedHeightMap(heightTexture.getImage(), 1f);
        heightMap.load();
        heightMap.getSize();

        TerrainQuad terrain = new TerrainQuad(mapname, 65, heightMap.getSize(), heightMap.getHeightMap());
        terrain.setMaterial(material);

        TerrainLodControl lod = new TerrainLodControl();
        lod.setLodCalculator(new DistanceLodCalculator(64, 2f));
        terrain.addControl(lod);

        rootNode.attachChild(terrain);
    }

    private void initLight() {
        AmbientLight ambLight = new AmbientLight();
        ambLight.setColor(ambient);

        DirectionalLight dirLight = new DirectionalLight();
        dirLight.setDirection(lightDirection);
        dirLight.setColor(light);

        rootNode.addLight(ambLight);
        rootNode.addLight(dirLight);
    }
}
