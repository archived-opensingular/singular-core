/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.multiselect;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreMultiSelectPickList extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectPickList() {
        super("Multi Select", "Pick List");
        setDescriptionHtml("Permite a seleção múltipla no formato de uma pick list.");
    }

}
