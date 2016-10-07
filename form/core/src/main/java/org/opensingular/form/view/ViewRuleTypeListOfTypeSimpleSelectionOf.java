/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.view;

import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.provider.Provider;
import org.opensingular.form.SType;
import org.opensingular.form.provider.ProviderContext;

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
            SType<?> elementType = listType.getElementsType();
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
