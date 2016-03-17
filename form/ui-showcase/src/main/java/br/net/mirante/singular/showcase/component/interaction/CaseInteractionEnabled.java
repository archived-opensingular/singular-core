/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseInteractionEnabled extends CaseBase implements Serializable {

    public CaseInteractionEnabled() {
        super("Enabled, Visible, Required", "Enabled");
        setDescriptionHtml("Habilita os componentes dinamicamente.");
    }

}
