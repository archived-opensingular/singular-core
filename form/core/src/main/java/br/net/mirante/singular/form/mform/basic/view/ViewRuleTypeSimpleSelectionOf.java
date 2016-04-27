/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.SimpleProvider;

/**
 * Decide a melhor view para um tipo simples que seja um selection of.
 *
 * @author Daniel C. Bordin
 */
public class ViewRuleTypeSimpleSelectionOf extends ViewRule {

    @Override
    public SView apply(SInstance instance) {
        if (instance != null && instance.asAtrProvider().getSimpleProvider() != null) {
            return decideView(instance, instance, instance.asAtrProvider().getSimpleProvider());
        }
        return null;
    }

    //TODO: [Fabs] this decision is strange to apply when the value is dynamic
    private SView decideView(SInstance instance, SInstance simple, SimpleProvider provider) {
        int size = provider.load(instance).size();
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
