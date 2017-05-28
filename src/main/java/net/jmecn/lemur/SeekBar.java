package net.jmecn.lemur;

import com.jme3.input.MouseInput;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.core.AbstractGuiControlListener;
import com.simsilica.lemur.core.GuiControl;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.CursorButtonEvent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.CursorMotionEvent;
import com.simsilica.lemur.event.DefaultCursorListener;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Styles;

/**
 * SeekBar is A horizontal progress indicator with a draggable slider.
 * 
 * @author yan
 */
public class SeekBar extends Panel {

    public static final String ELEMENT_ID = "seek";
    public static final String RANGE_ID = "range";
    public static final String VALUE_ID = "value";
    public static final String THUMB_ID = "thumb.button";

    private BorderLayout layout;
    private Panel range;
    private Panel value;
    private Button thumb;

    private RangedValueModel model;
    private VersionedReference<Double> state;
    
    private boolean seekable = true;

    public SeekBar() {
        this(new DefaultRangedValueModel(), true, new ElementId(ELEMENT_ID), null);
    }

    public SeekBar(RangedValueModel model) {
        this(model, true, new ElementId(ELEMENT_ID), null);
    }

    public SeekBar(String style) {
        this(new DefaultRangedValueModel(), true, new ElementId(ELEMENT_ID), style);
    }

    public SeekBar(ElementId elementId, String style) {
        this(new DefaultRangedValueModel(), true, elementId, style);
    }

    public SeekBar(RangedValueModel model, String style) {
        this(model, true, new ElementId(ELEMENT_ID), style);
    }

    public SeekBar(RangedValueModel model, ElementId elementId, String style) {
        this(model, true, elementId, style);
    }

    protected SeekBar(RangedValueModel model, boolean applyStyles, ElementId elementId, String style) {

        super(false, elementId, style);

        // Because the slider accesses styles (for its children) before
        // it has applied its own, it is possible that its default styles
        // will not have been applied. So we'll make sure.
        Styles styles = GuiGlobals.getInstance().getStyles();
        styles.initializeStyles(getClass());

        this.model = model;

        this.layout = new BorderLayout();
        getControl(GuiControl.class).setLayout(layout);
        getControl(GuiControl.class).addListener(new ReshapeListener());

        // Add the label child.

        this.range = layout.addChild(new Panel(100, 5, elementId.child(RANGE_ID), style));

        this.value = new Panel(elementId.child(VALUE_ID), style);
        attachChild(value);

        this.thumb = new Button(null, elementId.child(THUMB_ID), style);
        ButtonDragger dragger = new ButtonDragger();
        CursorEventControl.addListenersToSpatial(thumb, dragger);
        attachChild(thumb);

        // A child that is not managed by the layout will not otherwise lay
        // itself out... so we will force it to be its own preferred size.
        thumb.getControl(GuiControl.class).setSize(thumb.getControl(GuiControl.class).getPreferredSize());

        if (applyStyles) {
            styles.applyStyles(this, getElementId(), style);
        }
    }

    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles(Styles styles, Attributes attrs) {
        ElementId parent = new ElementId(ELEMENT_ID);
        styles.getSelector(parent.child(RANGE_ID), null).set("background",
                new QuadBackgroundComponent(new ColorRGBA(0.3f, 0.3f, 0.3f, 0.5f), 2, 2));
        styles.getSelector(parent.child(VALUE_ID), null).set("background",
                new QuadBackgroundComponent(new ColorRGBA(0.1f, 0.78f, 0.05f, 1)));
        styles.getSelector(parent.child(THUMB_ID), null).set("text", "|", false);
    }

    /**
     * Sets the current progress value as a percentage (0-1.0) of the current
     * range.
     */
    public void setProgressPercent(double percent) {
        this.model.setPercent(percent);
    }

    /**
     * Returns the current progress value as a percentage (0-1.0) of the current
     * range.
     */
    public double getProgressPercent() {
        return this.model.getPercent();
    }

    /**
     * Sets the raw progress value.
     */
    public void setProgressValue(double val) {
        this.model.setValue(val);
    }

    /**
     * Returns the raw progress value.
     */
    public double getProgressValue() {
        return this.model.getValue();
    }

    /**
     * Sets the ranged value model that will be used to calculate progress
     * percentage. The default model is is a DefaultRangedValueModel() where the
     * range is 0 to 100. If setModel(null) is called then a new default range
     * is created.
     */
    public void setModel(RangedValueModel model) {
        if (this.model == model) {
            return;
        }
        if (model == null) {
            model = new DefaultRangedValueModel();
        }
        this.model = model;
        this.state = null;
    }

    /**
     * Returns the current range model for this progress bar.
     */
    public RangedValueModel getModel() {
        return model;
    }

    /**
     * Returns the GUI element that is used for the value indicator. This can be
     * used to apply special styling.
     */
    public Panel getValueIndicator() {
        return range;
    }

    public void setSeekable(boolean seekable) {
        this.seekable = seekable;
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

        if (state == null || state.update()) {
            resetStateView();
        }
    }

    protected void resetStateView() {
        if (state == null) {
            state = model.createReference();
        }

        Vector3f pos = range.getLocalTranslation();
        Vector3f rangeSize = range.getSize();
        Vector3f thumbSize = thumb.getSize();
        Vector3f size = getSize();

        // Calculate where the thumb center should be
        double x = pos.x + rangeSize.x * model.getPercent() - thumbSize.x * 0.5;
        double y = pos.y - rangeSize.y * 0.5;

        // We cheated and included the half-thumb spacing in x already which
        // is why this is axis-specific.
        thumb.setLocalTranslation((float) x, (float) (y + thumbSize.y * 0.5), pos.z + size.z);

        double width = model.getPercent() * rangeSize.x;
        value.setSize(new Vector3f((float) width, rangeSize.y, rangeSize.z));
    }

    private class ReshapeListener extends AbstractGuiControlListener {
        @Override
        public void reshape(GuiControl source, Vector3f pos, Vector3f size) {
            // Make sure the thumb is positioned appropriately
            // for the new size
            resetStateView();
        }
    }

    private class ButtonDragger extends DefaultCursorListener {

        private Vector2f drag = null;
        private double startPercent;

        @Override
        public void cursorButtonEvent(CursorButtonEvent event, Spatial target, Spatial capture) {
            if (!seekable) {
                return;
            }
            
            if (event.getButtonIndex() != MouseInput.BUTTON_LEFT)
                return;
            
            event.setConsumed();
            if (event.isPressed()) {
                drag = new Vector2f(event.getX(), event.getY());
                startPercent = model.getPercent();
            } else {
                // Dragging is done.
                drag = null;
            }
        }

        @Override
        public void cursorMoved(CursorMotionEvent event, Spatial target, Spatial capture) {
            if (drag == null)
                return;

            Vector3f v1 = new Vector3f(thumb.getSize().x * 0.5f, 0, 0);
            Vector3f v2 = v1.add(range.getSize().x - thumb.getSize().x * 0.5f, 0, 0);

            v1 = event.getRelativeViewCoordinates(range, v1);
            v2 = event.getRelativeViewCoordinates(range, v2);

            Vector3f dir = v2.subtract(v1);
            float length = dir.length();
            dir.multLocal(1 / length);
            Vector3f cursorDir = new Vector3f(event.getX() - drag.x, event.getY() - drag.y, 0);

            float dot = cursorDir.dot(dir);

            // Now, the actual amount is then dot/length
            float percent = dot / length;
            model.setPercent(startPercent + percent);

            event.setConsumed();
        }
    }
}
