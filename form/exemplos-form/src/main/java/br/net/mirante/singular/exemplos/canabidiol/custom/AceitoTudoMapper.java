/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol.custom;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.form.wicket.mapper.BooleanMapper;
import br.net.mirante.singular.form.wicket.model.AtributoModel;

/**
 * Mapper customizado para substituir os "\n"
 * do texto da label por "<br />"
 */
public class AceitoTudoMapper extends BooleanMapper {

    @Override
    protected Label buildLabel(String id, AtributoModel<String> labelModel) {
        String s = labelModel.getObject();
        s = s.replace("\n", "<br />");
        Label label = new Label(id, Model.of(s));
        label.setEscapeModelStrings(false);
        return label;
    }

}
