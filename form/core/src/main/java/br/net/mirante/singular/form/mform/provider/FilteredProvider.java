package br.net.mirante.singular.form.mform.provider;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.filter.Config;

import java.io.Serializable;

public interface FilteredProvider<R extends Serializable> extends Provider<R, SInstance> {

    void configureProvider(Config cfg);

}