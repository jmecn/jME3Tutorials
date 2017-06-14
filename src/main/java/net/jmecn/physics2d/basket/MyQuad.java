package net.jmecn.physics2d.basket;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

public class MyQuad extends Mesh {
	private float width;
    private float height;
    
    /**
     * Serialization only. Do not use.
     */
    public MyQuad() {
    }
    
    /**
     * Create a quad with the given width and height. The quad
     * is always created in the XY plane.
     * 
     * @param width The X extent or width
     * @param height The Y extent or width
     */
    public MyQuad(float width, float height) {
    	updateGeometry(width, height);
    }
    /**
     * Create a quad with the given width and height. The quad
     * is always created in the XY plane.
     * 
     * @param width The X extent or width
     * @param height The Y extent or width
     * @param flipCoords If true, the texture coordinates will be flipped
     * along the Y axis.
     */
    public MyQuad(float width, float height, boolean flipCoords) {
    	updateGeometry(width, height, flipCoords);
    }
    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void updateGeometry(float width, float height){
        updateGeometry(width, height, false);
    }

    public void updateGeometry(float width, float height, boolean flipCoords) {
        this.width = width;
        this.height = height;
        setBuffer(Type.Position, 3, 
        	new float[]{width * -.5f, height * -.5f, 0,
                        width * 0.5f, height * -.5f, 0,
                        width * 0.5f, height * 0.5f, 0,
                        width * -.5f, height * 0.5f, 0});
        

        if (flipCoords){
            setBuffer(Type.TexCoord, 2, 
            	new float[]{0, 1,
                            1, 1,
                            1, 0,
                            0, 0});
        }else{
            setBuffer(Type.TexCoord, 2, 
            	new float[]{0, 0,
                            1, 0,
                            1, 1,
                            0, 1});
        }
        setBuffer(Type.Normal, 3, 
        	new float[]{0, 0, 1,
                                              0, 0, 1,
                                              0, 0, 1,
                                              0, 0, 1});
        if (height < 0){
            setBuffer(Type.Index, 3, 
            	new short[]{0, 2, 1,
                            0, 3, 2});
        }else{
            setBuffer(Type.Index, 3, 
            	new short[]{0, 1, 2,
                            0, 2, 3});
        }
        
        updateBound();
        setStatic();
    }
    
    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        width = capsule.readFloat("width", 0);
        height = capsule.readFloat("height", 0);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
    }
}
