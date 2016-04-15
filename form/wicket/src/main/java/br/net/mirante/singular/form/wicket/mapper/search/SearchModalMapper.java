/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.search;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.SViewSearchModal;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentAbstractMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

public class SearchModalMapper extends ControlsFieldComponentAbstractMapper {


    public Component appendInput()
    {
        if (view instanceof SViewSearchModal) {
            if(ctx.getModel().getObject().asAtrBasic().getDisplayString() == null){
                throw new SingularFormException("Para uso do SViewSearchModal é necessario informar o string display.");
            }
            final SearchModalContainer selectModalBusca = new SearchModalContainer("SelectModalBusca", ctx);
            formGroup.appendDiv(selectModalBusca);
            return selectModalBusca;
        }
        throw new RuntimeException("SearchModalMapper only works with a MSelecaoPorModalBuscaView.");
    }

    @Override
    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if (mi != null && mi.getValue() != null) {
            return mi.toStringDisplay();
        }
        return StringUtils.EMPTY;
    }

}

