/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.document.SDocument;

/**
 * Class responsible for looking up for the desired providers from the current
 * instance in order to populate the options.
 *
 * @author Fabricio Buzeto
 *
 */
@SuppressWarnings("serial")
public class LookupOptionsProvider implements SOptionsProvider {
    private String providerName;
    private Class<? extends SOptionsProvider> providerClass;

    public LookupOptionsProvider(String providerName) {
        this.providerName = providerName;
    }

    public LookupOptionsProvider(Class<? extends SOptionsProvider> providerClass) {
        this.providerClass = providerClass;
    }

    @Override
    public String toDebug() {
        return this.getClass().getName();
    }

    @Override
    public SIList<? extends SInstance> listOptions(SInstance instance) {
        SDocument document = instance.getDocument();
        SOptionsProvider provider = whichProvider(document);
        return provider.listAvailableOptions(instance);
    }

    private SOptionsProvider whichProvider(SDocument document) {
        SOptionsProvider p;
        if(providerName != null){
            p = document.lookupService(providerName, SOptionsProvider.class);
            if (p == null) {
                throw new SingularFormException("Não foi localizado o " + SOptionsProvider.class.getSimpleName() + " de nome '"
                        + providerName + "' nos serviços registrado para o documento");
            }
        }else if(providerClass != null){
            p = document.lookupService(providerClass);
            if (p == null) {
                throw new SingularFormException("Não foi localizado o " + SOptionsProvider.class.getSimpleName() + " da classe '"
                        + providerClass + "' nos serviços registrado para o documento");
            }
        } else {
            throw new SingularException("Não foi configurador a origem do " + SOptionsProvider.class.getSimpleName());
        }
        return p;
    }
}