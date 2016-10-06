/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.provider;

import org.opensingular.form.document.SDocument;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.form.SingularFormException;

import java.util.List;

/**
 * Class responsible for looking up for the desired providers from the current
 * instance in order to populate the options.
 *
 * @author Fabricio Buzeto
 */
@SuppressWarnings("serial")
public class LookupOptionsProvider implements Provider {

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
        return whichProvider(context.getInstance().getDocument()).load(context);
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