/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.validation;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import org.apache.wicket.ajax.markup.html.form.AjaxButton;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.CaseCustomizer;

public class CaseValidationPartialCustomizer implements CaseCustomizer {

    @Override
    public void customize(CaseBase caseBase) {
        caseBase.getBotoes().add((id, currentInstance) -> {
            final AjaxButton aj = new PartialValidationButton(id, currentInstance);

            aj.add($b.attr("value", "Validação Parcial"));

            return aj;
        });
    }

}
