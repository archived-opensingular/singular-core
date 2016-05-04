package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.provider.filter.FilterConfigBuilder;

import java.io.Serializable;

public interface FilteredPagedProvider<R extends Serializable> extends Provider<R, SInstance> {

    void configureFilter(FilterConfigBuilder fcb);

    default long getSize(ProviderContext<SInstance> context) {
        throw new SingularFormException("O FilteredPageProvider foi marcado como lazy, porem não foi implementado o metodo getSize");
    }

}