package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;

public class SIProvider<P extends FilteredPagedProvider<T>, T> extends SInstance {

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
