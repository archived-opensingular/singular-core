package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.lang3.ObjectUtils;

import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;

public class WicketBuildContext implements Serializable {

    private final WicketBuildContext      parent;
    private final BSCol                   container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                 hintsInherited;

    public WicketBuildContext(BSCol container) {
        this(null, container, false);
    }
    public WicketBuildContext(WicketBuildContext parent, BSCol container, boolean hintsInherited) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
    }
    public WicketBuildContext getParent() {
        return parent;
    }
    public BSCol getContainer() {
        return container;
    }

    public <T extends Serializable> WicketBuildContext setHint(HintKey<T> key, T value) {
        hints.put(key, value);
        return this;
    }
    public <T> T getHint(HintKey<T> key, T defaultValue) {
        return ObjectUtils.defaultIfNull((T) getHint(key), defaultValue);
    }
    @SuppressWarnings("unchecked")
    public <T> T getHint(HintKey<T> key) {
        if (hintsInherited && !hints.containsKey(key) && getParent() != null) {
            return getParent().getHint(key);
        }
        return (T) hints.get(key);
    }

    public WicketBuildContext createChild(BSCol childContainer, boolean hintsInheritedh) {
        return new WicketBuildContext(this, childContainer, hintsInheritedh);
    }
}
