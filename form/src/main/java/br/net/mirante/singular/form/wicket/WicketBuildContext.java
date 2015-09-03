package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSCol;

public class WicketBuildContext {

    private final WicketBuildContext parent;
    private final BSCol              container;

    public WicketBuildContext(BSCol container) {
        this(null, container);
    }
    public WicketBuildContext(WicketBuildContext parent, BSCol container) {
        this.parent = parent;
        this.container = container;
    }
    public WicketBuildContext getParent() {
        return parent;
    }
    public BSCol getContainer() {
        return container;
    }
    public WicketBuildContext createChild(BSCol childContainer) {
        return new WicketBuildContext(this, childContainer);
    }
}
