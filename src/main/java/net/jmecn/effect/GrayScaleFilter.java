package net.jmecn.effect;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 * Convert render image to grayscale image.
 * 
 * @author H
 */
public class GrayScaleFilter extends Filter {

	/**
	 * Constructor.
	 */
	public GrayScaleFilter() {
		super("GrayScaleFilter");
	}

	/**
	 * @see com.jme3.post.Filter#getMaterial()
	 */
	@Override
	protected Material getMaterial() {
		return this.material;
	}

	/**
	 * @see com.jme3.post.Filter#initFilter(com.jme3.asset.AssetManager, com.jme3.renderer.RenderManager,
	 *      com.jme3.renderer.ViewPort, int, int)
	 */
	@Override
	protected void initFilter(final AssetManager manager, final RenderManager renderManager, final ViewPort vp,
			final int w, final int h) {
		this.material = new Material(manager, "Materials/GrayScale/GrayScale.j3md");
	}

}