/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.interaction;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInteractionRequired extends CaseBase {

    public CaseInteractionRequired() {
        super("Enabled, Visible, Required", "Required");
        setDescriptionHtml("Torna os campos obrigatórios dinamicamente.");
    }

    @Override
    public boolean showValidateButton() {
        return true;
    }
}
