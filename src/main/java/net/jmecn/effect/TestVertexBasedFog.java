package net.jmecn.effect;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.simsilica.lemur.Action;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultCheckboxModel;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.list.SelectionModel;
import com.simsilica.lemur.list.SelectionModel.SelectionMode;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;

/**
 * Test Vertex-based Fog
 * 
 * @author yanmaoyuan
 *
 */
public class TestVertexBasedFog extends SimpleApplication {

	public final static int FOG_MODE_LINEAR = 0;
	public final static int FOG_MODE_EXP = 1;
	public final static int FOG_MODE_EXP2 = 2;

	public final static float MAX_RANGE = 200f;
	/**
	 * Materials
	 */
	private Material fogMaterial = null;
	private Material noneFogMaterial = null;
	
	/**
	 * Fog parameters
	 */
	private boolean useFrag = false;
	private ColorRGBA fogColor = new ColorRGBA(0.75f, 0.8f, 0.9f, 1f);
	private int fogMode = FOG_MODE_LINEAR;
	private float fogDensity = 0.03f;
	private Vector2f fogRange = new Vector2f(2, 100);

	/**
	 * Lemur GUI components
	 */
	private Container fogSettings;
	private Container colorChooser;
	private Container modeChooser;
	
	private VersionedReference<Double> redRef = null;
	private VersionedReference<Double> greenRef = null;
	private VersionedReference<Double> blueRef = null;
	private Label lblFogColor = null;
	
	private Label lblFogMode = null;
	
	private VersionedReference<Boolean> useFragRef = null;
	private VersionedReference<Double> densityRef = null;
	private Label lblDensity = null;
	
	private VersionedReference<Double> fogStartRef = null;
	private RangedValueModel startRangedModel = new DefaultRangedValueModel(0, MAX_RANGE, fogRange.x);
	private Label lblFogStart = null;
	
	private VersionedReference<Double> fogEndRef = null;
	private RangedValueModel endRangedModel = new DefaultRangedValueModel(0, MAX_RANGE, fogRange.y);
	private Label lblFogEnd = null;

	@Override
	public void simpleInitApp() {

		GuiGlobals.initialize(this);
		BaseStyles.loadGlassStyle();
		GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

		cam.setLocation(new Vector3f(3f, 4f, 0f));
		flyCam.setMoveSpeed(0);
		flyCam.setDragToRotate(true);

		initLights();

		initScene();

		initFogSettingGUI();
		initFogColorGUI();
		initFogModeGUI();
	}
	
	private void initScene() {
		viewPort.setBackgroundColor(fogColor);

		/**
		 * FogMaterial
		 */
		fogMaterial = new Material(assetManager, "Materials/Fog/VertexBasedFog.j3md");
		fogMaterial.setColor("Diffuse", ColorRGBA.Red);

		fogMaterial.setColor("FogColor", fogColor);
		fogMaterial.setInt("FogMode", fogMode);
		fogMaterial.setVector2("FogRange", fogRange);
		fogMaterial.setFloat("FogDensity", fogDensity);
		fogMaterial.setBoolean("UseFrag", useFrag);

		/**
		 * Material with out fog
		 */
		noneFogMaterial = new Material(assetManager, "Materials/Fog/VertexBasedFog.j3md");
		noneFogMaterial.setColor("Diffuse", ColorRGBA.Green);

		/**
		 * Scene
		 */
		Mesh mesh = new Box(0.5f, 0.5f, 0.5f);
		for (int i = 0; i < 80; i++) {
			Geometry geom = new Geometry("Cube", mesh);
			if (i % 5 == 4) {
				geom.setMaterial(noneFogMaterial);
			} else {
				geom.setMaterial(fogMaterial);
			}
			geom.move(0, 0, -i * 2);
			rootNode.attachChild(geom);
		}
	}

	/**
	 * Let there be light
	 */
	private void initLights() {
		DirectionalLight sunLight = new DirectionalLight();
		sunLight.setDirection(new Vector3f(-1, -2, -3f).normalizeLocal());
		sunLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 1f));

		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setColor(new ColorRGBA(0.2f, 0.2f, 0.2f, 1f));

		rootNode.addLight(sunLight);
		rootNode.addLight(ambientLight);
	}

	/**
	 * Create a control panel to change the parameters of fog shader
	 */
	private void initFogSettingGUI() {
		fogSettings = new Container();
		fogSettings.setLocalTranslation(0, cam.getHeight(), 0);
		guiNode.attachChild(fogSettings);

		fogSettings.addChild(new Label("Fog Settings", new ElementId("title")));

		// Parameters
		Container container = new Container(new SpringGridLayout());
		fogSettings.addChild(container);

		// fog shader
		DefaultCheckboxModel useFragCheckModel = new DefaultCheckboxModel(useFrag);
		this.useFragRef = useFragCheckModel.createReference();
		container.addChild(new Label("FragShader"), 0, 0);
		Checkbox cb = container.addChild(new Checkbox("Enabled"), 0, 1);
		cb.setModel(useFragCheckModel);

		// fog color
		container.addChild(new Label("Fog Color"), 1, 0);
		this.lblFogColor = container.addChild(new Label("#FFFFFF"), 1, 1);
		container.addChild(new ActionButton(new CallMethodAction("Toggle", this, "toggleFogColor")), 1, 2);
		updateFogColor();

		// fog mode
		container.addChild(new Label("Fog Mode"), 2, 0);
		this.lblFogMode = container.addChild(new Label(getFogMode()), 2, 1);
		container.addChild(new ActionButton(new CallMethodAction("Toggle", this, "toggleFogMode")), 2, 2);

		// fog density
		RangedValueModel densityRangedModel = new DefaultRangedValueModel(0, 100, 100 * fogDensity);
		this.densityRef = densityRangedModel.createReference();
		container.addChild(new Label("Fog Density"), 3, 0);
		this.lblDensity = container.addChild(new Label("" + fogDensity), 3, 1);
		container.addChild(new Slider(densityRangedModel), 3, 2);

		// fog range start
		this.fogStartRef = startRangedModel.createReference();
		container.addChild(new Label("Fog Start"), 4, 0);
		this.lblFogStart = container.addChild(new Label("" + fogRange.x), 4, 1);
		container.addChild(new Slider(startRangedModel), 4, 2);

		// fog range end
		this.fogEndRef = endRangedModel.createReference();
		container.addChild(new Label("Fog End"), 5, 0);
		this.lblFogEnd = container.addChild(new Label("" + fogRange.y), 5, 1);
		container.addChild(new Slider(endRangedModel), 5, 2);
	}
	
	/**
	 * This is to changing the fogColor.
	 */
	private void initFogColorGUI() {
		colorChooser = new Container();
		colorChooser.addChild(new Label("Fog Color", new ElementId("title")));
		
		colorChooser.addChild(new Label("Red"));
		redRef = colorChooser.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100*fogColor.r))).getModel().createReference();
		colorChooser.addChild(new Label("Green"));
		greenRef = colorChooser.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100*fogColor.g))).getModel().createReference();
		colorChooser.addChild(new Label("Blue"));
		blueRef = colorChooser.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100*fogColor.b))).getModel().createReference();
		
		colorChooser.setLocalTranslation(fogSettings.getPreferredSize().x + 2, cam.getHeight(), 0);
	}
	
	/**
	 * Create a list box, so I can change the way of calculation.
	 */
	private void initFogModeGUI() {
		modeChooser = new Container();
		modeChooser.addChild(new Label("Fog Mode", new ElementId("title")));
		
		ListBox<String> listBox = new ListBox<String>();
		modeChooser.addChild(listBox);
		listBox.getModel().add("Linear");
		listBox.getModel().add("Exponential");
		listBox.getModel().add("Exp2");
		
		final SelectionModel selection = listBox.getSelectionModel();
		selection.setSelectionMode(SelectionMode.Single);
		
		modeChooser.addChild(new ActionButton(new Action("Apply") {
			@Override
			public void execute(Button source) {
				Integer index = selection.getSelection();

				fogMode = index;
				fogMaterial.setInt("FogMode", fogMode);
				lblFogMode.setText(getFogMode());
			}
		}));
		
		modeChooser.setLocalTranslation(fogSettings.getPreferredSize().x + 2, cam.getHeight(), 0);
	}
	
	public void toggleFogColor() {
		if (modeChooser.getParent() != null) {
			modeChooser.removeFromParent();
		}
		
		if (colorChooser.getParent() == null) {
			guiNode.attachChild(colorChooser);
		} else {
			colorChooser.removeFromParent();
		}
	}

	public void toggleFogMode() {
		if (colorChooser.getParent() != null) {
			colorChooser.removeFromParent();
		}
		
		if (modeChooser.getParent() == null) {
			guiNode.attachChild(modeChooser);
		} else {
			modeChooser.removeFromParent();
		}
	}


	@Override
	public void simpleUpdate(float tpf) {
		if (useFragRef.update()) {
			useFrag = useFragRef.get();
			fogMaterial.setBoolean("UseFrag", useFrag);
		}
		
		if (densityRef.update()) {
			fogDensity = (float)(densityRef.get() / 100.0);
			
			fogMaterial.setFloat("FogDensity", fogDensity);
			
			lblDensity.setText(String.format("%.4f", fogDensity));
		}
		
		// update fog range
		if (fogStartRef.update() || fogEndRef.update()) {
			double minVar = fogStartRef.get();
			double maxVar = fogEndRef.get();
			
			if (minVar > maxVar) {
				minVar = maxVar;
				startRangedModel.setValue(minVar);
			}
			if (maxVar < minVar) {
				maxVar = minVar;
				endRangedModel.setValue(maxVar);
			}
			
			fogRange.set((float) minVar, (float) maxVar);
			
			updateFogRange();
		}
		
		// update color
        if( redRef.update() || greenRef.update() || blueRef.update()) {
        	float r = (float)(redRef.get() / 100.0);
        	float g = (float)(greenRef.get() / 100.0);
        	float b = (float)(blueRef.get() / 100.0);
        	fogColor.set(r, g, b, 1f);
        	
        	updateFogColor();
        }
	}

	/**
	 * Update linear fog range
	 */
	private void updateFogRange() {
		fogMaterial.setVector2("FogRange", fogRange);
		
		lblFogStart.setText(String.format("%.1f", fogRange.x));
		lblFogEnd.setText(String.format("%.1f", fogRange.y));
	}
	
	/**
	 * Update fog color
	 */
	private void updateFogColor() {
		viewPort.setBackgroundColor(fogColor);
		fogMaterial.setColor("FogColor", fogColor);
		
		int r = (int) (fogColor.r * 255);
		int g = (int) (fogColor.g * 255);
		int b = (int) (fogColor.b * 255);
		lblFogColor.setText(String.format("#%02X%02X%02X", r, g, b));
	}
	
	/**
	 * get fog mode
	 * @return
	 */
	private String getFogMode() {
		switch (fogMode) {
		case FOG_MODE_LINEAR:
			return "Linear";
		case FOG_MODE_EXP:
			return "Exponential";
		case FOG_MODE_EXP2:
			return "Exp2";
		default:
			return "Exp2";
		}
	}
	
	public static void main(String[] args) {
		TestVertexBasedFog app = new TestVertexBasedFog();
		app.start();
	}

}
