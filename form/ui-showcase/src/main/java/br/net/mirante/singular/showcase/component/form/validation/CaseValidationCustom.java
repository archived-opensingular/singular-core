/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.validation;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseValidationCustom extends CaseBase {

    public CaseValidationCustom() {
        super("Custom");
        setDescriptionHtml("Validação customizada, no exemplo verifica se o campo é menor que 1000");
    }
}
