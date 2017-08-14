package org.opensingular.form.flatview;

import org.opensingular.form.SFormUtil;
import org.opensingular.form.SInstance;

public class FlatViewContext {

    private final SInstance instance;

    public FlatViewContext(SInstance instance) {
        this.instance = instance;
    }

    public <T extends SInstance> T getInstanceAs(Class<T> tClass) {
        return (T) instance;
    }

    public SInstance getInstance() {
        return instance;
    }

    public String getLabelOrName() {
        String label = instance.asAtr().getLabel();
        if (label == null) {
            label = SFormUtil.getTypeLabel(instance.getType().getClass()).orElse(null);
        }
        if (label == null) {
            label = instance.getName();
        }
        return label;
    }

    public boolean shouldRender(){
        return instance.asAtr().isVisible() && instance.asAtr().isExists();
    }

}
