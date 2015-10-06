package br.net.mirante.singular.form.wicket;

import java.io.Serializable;
import java.util.HashMap;

import br.net.mirante.singular.form.wicket.IWicketComponentMapper.HintKey;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;

public class WicketBuildContext implements Serializable {

    private final WicketBuildContext                parent;
    private final BSContainer<?>                    container;
    private final HashMap<HintKey<?>, Serializable> hints = new HashMap<>();
    private final boolean                           hintsInherited;

    public WicketBuildContext(BSCol container) {
        this(null, container, false);
    }
    public WicketBuildContext(WicketBuildContext parent, BSContainer<?> container, boolean hintsInherited) {
        this.parent = parent;
        this.container = container;
        this.hintsInherited = hintsInherited;
    }
    public WicketBuildContext getParent() {
        return parent;
    }
    public BSContainer<?> getContainer() {
        return container;
    }

    public <T extends Serializable> WicketBuildContext setHint(HintKey<T> key, T value) {
        hints.put(key, value);
        return this;
    }
    @SuppressWarnings("unchecked")
    public <T> T getHint(HintKey<T> key) {
        if (hints.containsKey(key)) {
            return (T) hints.get(key);
        } else if (hintsInherited && getParent() != null) {
            return getParent().getHint(key);
        } else {
            return key.getDefaultValue();
        }
    }

    public WicketBuildContext createChild(BSContainer<?> childContainer, boolean hintsInherited) {
        return new WicketBuildContext(this, childContainer, hintsInherited);
    }
}
