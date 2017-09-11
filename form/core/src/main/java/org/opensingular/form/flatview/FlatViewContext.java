package org.opensingular.form.flatview;

import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;

public class FlatViewContext {

    private final SInstance instance;
    private final boolean withoutTitle;
    private final boolean renderIfEmpty;

    public FlatViewContext(SInstance instance) {
        this.instance = instance;
        this.withoutTitle = false;
        this.renderIfEmpty = false;
    }

    public FlatViewContext(SInstance instance, boolean withoutTitle) {
        this.instance = instance;
        this.withoutTitle = withoutTitle;
        this.renderIfEmpty = false;
    }

    public FlatViewContext(SInstance instance, boolean withoutTitle, boolean renderIfEmpty) {
        this.instance = instance;
        this.withoutTitle = withoutTitle;
        this.renderIfEmpty = renderIfEmpty;
    }

    public <T extends SInstance> T getInstanceAs(Class<T> tClass) {
        return (T) instance;
    }

    public SInstance getInstance() {
        return instance;
    }

    public String getLabel() {
        String label = instance.asAtr().getLabel();
        if (label == null) {
            label = SFormUtil.getTypeLabel(instance.getType().getClass()).orElse(null);
        }
        if (label == null) {
            label = instance.getName();
        }
        return label;
    }

    public boolean shouldRender() {
        return instance.asAtr().isVisible()
                && instance.asAtr().isExists()
                && (renderIfEmpty || !instance.isEmptyOfData());
    }

    public boolean isWithoutTitle() {
        return withoutTitle;
    }

}