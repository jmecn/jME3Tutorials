package net.jmecn.effect;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.filters.ColorOverlayFilter;
import com.jme3.post.filters.CrossHatchFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.style.BaseStyles;
import com.simsilica.lemur.style.ElementId;

import net.jmecn.state.CubeAppState;
import net.jmecn.state.FilterAppState;

/**
 * 演示滤镜的作用
 * 
 * @author yanmaoyuan
 *
 */
public class HelloFilters extends SimpleApplication {

    /**
     * 全部滤镜
     */
    private List<Filter> filters = new ArrayList<Filter>();
    
    // 全部滤镜
    private BloomFilter bloomFilter;
    private CartoonEdgeFilter cartoonEdge;
    private ColorOverlayFilter colorOverlay;
    private CrossHatchFilter crossHatch;
    private DepthOfFieldFilter depthOfField;
    private FogFilter fogFilter;
    private LightScatteringFilter lightScattering;
    private SSAOFilter ssaoFilter;
    // 我们自定义的Filter
    private GrayScaleFilter grayScale;

    public HelloFilters() {
        super(new FlyCamAppState(), new StatsAppState(), new DebugKeysAppState(), new CubeAppState(),
                new FilterAppState(), new ScreenshotAppState("screenshots/", System.currentTimeMillis() + "_"));
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10f);
        flyCam.setDragToRotate(true);

        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");

        initFilters();

        initGui();
    }

    /**
     * 初始化所有滤镜
     */
    private void initFilters() {
        // 发光特效
        bloomFilter = new BloomFilter(BloomFilter.GlowMode.Scene);

        // 卡通边缘
        cartoonEdge = new CartoonEdgeFilter();
        cartoonEdge.setEdgeColor(ColorRGBA.LightGray);

        // 纯色叠加
        colorOverlay = new ColorOverlayFilter(new ColorRGBA(1f, 0.8f, 0.8f, 1f));

        // 交叉阴影
        crossHatch = new CrossHatchFilter();

        // 景深
        depthOfField = new DepthOfFieldFilter();
        depthOfField.setFocusDistance(0);
        depthOfField.setFocusRange(20);
        depthOfField.setBlurScale(1.4f);

        // 雾化
        fogFilter = new FogFilter(ColorRGBA.White, 1.5f, 200f);

        // 灰度化
        grayScale = new GrayScaleFilter();

        // 光线散射
        Vector3f sunDir = stateManager.getState(CubeAppState.class).getSunDirection();
        lightScattering = new LightScatteringFilter(sunDir.mult(-3000));

        // 屏幕空间环境光遮蔽
        ssaoFilter = new SSAOFilter();

        filters.add(bloomFilter);
        filters.add(cartoonEdge);
        filters.add(colorOverlay);
        filters.add(crossHatch);
        filters.add(depthOfField);
        filters.add(fogFilter);
        filters.add(grayScale);
        filters.add(lightScattering);
        filters.add(ssaoFilter);
    }

    private void initGui() {
        Container window = new Container();
        guiNode.attachChild(window);
        
        window.addChild(new Label("Filters", new ElementId("title")));
        
        for(int i=0; i<filters.size(); i++) {
            Filter filter = filters.get(i);
            window.addChild(createCheckbox(filter));
        }
        
        window.setLocalTranslation(10, cam.getHeight() - 10, 0);
    }
    
    /**
     * 实例化一个Checkbox，作为滤镜的开关。
     * @param filter
     * @return
     */
    @SuppressWarnings("unchecked")
    private Checkbox createCheckbox(final Filter filter) {
        
        String name = filter.getClass().getSimpleName();
        final Checkbox cb = new Checkbox(name);
        
        cb.addClickCommands(new Command<Button>() {
            @Override
            public void execute(Button source) {
                FilterAppState state = stateManager.getState(FilterAppState.class);
                if (cb.isChecked()) {
                    state.add(filter);
                } else {
                    state.remove(filter);
                }
            }
        });
        
        return cb;
    }

    public static void main(String[] args) {
        HelloFilters app = new HelloFilters();
        app.start();
    }

}
