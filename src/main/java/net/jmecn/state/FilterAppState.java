package net.jmecn.state;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;

/**
 * 滤镜功能测试
 * @author yanmaoyuan
 *
 */
public class FilterAppState extends BaseAppState {

    private AssetManager assetManager;
    private FilterPostProcessor fpp;
    private ViewPort viewPort;
    
    private List<Filter> filtersToAdd = new ArrayList<Filter>();
    private List<Filter> filtersToRemove = new ArrayList<Filter>();
    
    @Override
    protected void initialize(Application app) {
        this.assetManager = app.getAssetManager();
        this.viewPort = app.getViewPort();
        app.getCamera();
        
        /**
         * 检查用户是否已经设置过FilterPostProcessor
         */
        for( SceneProcessor processor : viewPort.getProcessors()) {
            if (processor instanceof FilterPostProcessor) {
                fpp = (FilterPostProcessor) processor;
                break;
            }
        }
        
        /**
         * 初始化 FilterPostProcessor
         */
        if (fpp == null) {
            // 创建滤镜处理器
            fpp = new FilterPostProcessor(assetManager);
            
            // 检查用户是否设置过抗拒齿
            int numSamples = app.getContext().getSettings().getSamples();
            if (numSamples > 0) {
                fpp.setNumSamples(numSamples);
            }
        }
    }
    
    @Override
    public void update(float tpf) {
        if (filtersToAdd.size() > 0) {
            // 添加滤镜
            addFilters();
            filtersToAdd.clear();
        }
        
        if (filtersToRemove.size() > 0) {
            // 移除滤镜
            removeFilters();
            filtersToRemove.clear();
        }
    }
    
    /**
     * 添加滤镜
     * @param filter
     */
    public void add(Filter filter) {
        if (filter == null)
            return;
        
        filtersToAdd.add(filter);
    }
    
    /**
     * 移除滤镜
     * @param filter
     */
    public void remove(Filter filter) {
        if (filter == null)
            return;
        
        filtersToRemove.add(filter);
    }
    
    /**
     * 添加所有滤镜
     */
    private void addFilters() {
        int length = filtersToAdd.size();
         // 按顺序添加到 FilterPostProcessor 中
        for(int i=0; i<length; i++) {
            Filter filter = filtersToAdd.get(i);
            // 相同滤镜只添加一次。
            if (null == fpp.getFilter(filter.getClass())) {
                fpp.addFilter(filter);
            }
        }
    }
    
    /**
     * 移除所有滤镜
     */
    private void removeFilters() {
        int length = filtersToRemove.size();
        
        for(int i=0; i<length; i++) {
            Filter filter = filtersToRemove.get(i);
            fpp.removeFilter(filter);
        }
    }
    
    @Override
    protected void cleanup(Application app) {
        filtersToAdd.clear();
        filtersToRemove.clear();
    }

    @Override
    protected void onEnable() {
        viewPort.addProcessor(fpp);
    }

    @Override
    protected void onDisable() {
        viewPort.removeProcessor(fpp);
    }

}
