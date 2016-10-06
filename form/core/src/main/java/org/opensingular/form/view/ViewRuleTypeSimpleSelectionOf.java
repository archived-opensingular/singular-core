/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.SInstance;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.provider.ProviderContext;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 *
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public SView apply(SInstance instance) {
        if (instance != null && instance.asAtrProvider().getProvider() != null) {
            return decideView(instance, instance, instance.asAtrProvider().getProvider());
        }
        return null;
    }

    //TODO: [Fabs] this decision is strange to apply when the value is dynamic
    private SView decideView(SInstance instance, SInstance simple, Provider provider) {
        int size = provider.load(ProviderContext.of(instance)).size();
        /*
         * Tamanho zero indica uma possivel carga condicional e/ou dinamica.
         * Nesse caso é mais produtente escolher combo: MSelecaoPorSelectView
         */
        if (size <= 3 && size != 0 && simple.getType().isRequired()) {
            return newInstance(SViewSelectionByRadio.class);
        }
        return newInstance(SViewSelectionBySelect.class);
    }

}
