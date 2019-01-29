package org.opensingular.lib.wicket.util.model;

import org.apache.wicket.model.LoadableDetachableModel;
import org.opensingular.lib.commons.base.SingularProperties;

public class SingularPropertyModel extends LoadableDetachableModel<String> {
    private final String key;
    private final String defaulValue;

    public SingularPropertyModel(String key, String defaulValue) {
        this.key = key;
        this.defaulValue = defaulValue;
    }

    @Override
    protected String load() {
        return SingularProperties.get(key, defaulValue);
    }
}