package org.opensingular.singular.form.provider;

import org.opensingular.singular.form.SInstance;

import java.io.Serializable;

public class SIProvider<P extends Provider<T, SInstance>, T extends Serializable> extends SInstance {

    private P provider;

    @Override
    public Object getValue() {
        return provider;
    }

    @Override
    public void clearInstance() {
        provider = null;
    }

    @Override
    public boolean isEmptyOfData() {
        return provider != null;
    }

    @Override
    public void setValue(Object value) {
        this.provider = (P) value;
    }
}
