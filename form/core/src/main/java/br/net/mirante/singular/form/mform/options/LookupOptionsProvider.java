/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.provider.Provider;
import br.net.mirante.singular.form.mform.provider.ProviderContext;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;

import java.util.List;

/**
 * Class responsible for looking up for the desired providers from the current
 * instance in order to populate the options.
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class LookupOptionsProvider implements SimpleProvider, FilteredProvider {

    private String                    providerName;
    private Class<? extends Provider> providerClass;

    public LookupOptionsProvider(String providerName) {
        this.providerName = providerName;
    }

    public LookupOptionsProvider(Class<? extends Provider> providerClass) {
        this.providerClass = providerClass;
    }

    @Override
    public List load(ProviderContext context) {
        return load(context.getInstance());
    }

    @Override
    public List load(SInstance ins) {
        final SDocument      document = ins.getDocument();
        final SimpleProvider provider = (SimpleProvider) whichProvider(document);
        return provider.load(ins);
    }

    @Override
    public List load(SInstance ins, String query) {
        final SDocument        document = ins.getDocument();
        final FilteredProvider provider = (FilteredProvider) whichProvider(document);
        return provider.load(ins, query);
    }

    private Provider whichProvider(SDocument document) {
        Provider p;
        if (providerName != null) {
            p = document.lookupService(providerName, Provider.class);
            if (p == null) {
                throw new SingularFormException("Não foi localizado o " + Provider.class.getSimpleName() + " de nome '"
                        + providerName + "' nos serviços registrado para o documento");
            }
        } else if (providerClass != null) {
            p = document.lookupService(providerClass);
            if (p == null) {
                throw new SingularFormException("Não foi localizado o " + Provider.class.getSimpleName() + " da classe '"
                        + providerClass + "' nos serviços registrado para o documento");
            }
        } else {
            throw new SingularException("Não foi configurador a origem do " + Provider.class.getSimpleName());
        }
        return p;
    }

}