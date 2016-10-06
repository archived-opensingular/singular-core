/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.mapper.search;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.view.SView;
import org.opensingular.singular.form.view.SViewSearchModal;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.mapper.AbstractControlsFieldComponentMapper;
import org.opensingular.singular.util.wicket.bootstrap.layout.BSControls;

public class SearchModalMapper extends AbstractControlsFieldComponentMapper {

    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final SView view = ctx.getView();

        if (view instanceof SViewSearchModal) {
            final SearchModalPanel selectModalBusca = new SearchModalPanel("SelectModalBusca", ctx);
            formGroup.appendDiv(selectModalBusca);
            return selectModalBusca;
        }
        throw new RuntimeException("SearchModalMapper only works with a MSelecaoPorModalBuscaView.");
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValue() != null) {
            if (mi.asAtr().getDisplayString() != null) {
                return mi.toStringDisplay();
            }
            if (!(mi instanceof SIComposite)) {
                return String.valueOf(mi.getValue());
            }
            return mi.toString();
        }
        return StringUtils.EMPTY;
    }

}
