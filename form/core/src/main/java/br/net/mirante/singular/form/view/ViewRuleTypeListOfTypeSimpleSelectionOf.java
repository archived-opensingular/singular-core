/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.view;

import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.provider.Provider;
import br.net.mirante.singular.form.provider.ProviderContext;

/**
 * Decide qual a view mas adequada para uma seleção múltipla de tipos simples
 * (Lista de Tipo Simple, sendo o tipo simples um multiselectionOf).
 *
 * @author Daniel C. Bordin
 */
class ViewRuleTypeListOfTypeSimpleSelectionOf extends ViewRule {

    @Override
    @SuppressWarnings("rawtypes")
    public SView apply(SInstance listInstance) {
        if (listInstance instanceof SIList) {
            SIList<?> listType    = (SIList<?>) listInstance;
            SType<?>  elementType = listType.getElementsType();
            if (elementType != null && elementType.asAtrProvider().getProvider() != null) {
                return decideView(listInstance, elementType.asAtrProvider().getProvider());
            }
        }
        return null;
    }

    private SView decideView(SInstance instance, Provider provider) {
        int size = provider.load(ProviderContext.of(instance)).size();
        if (size <= 3) {
            return newInstance(SMultiSelectionByCheckboxView.class);
        } else if (size < 20) {
            return newInstance(SMultiSelectionBySelectView.class);
        }
        return newInstance(SMultiSelectionByPicklistView.class);
    }

}
