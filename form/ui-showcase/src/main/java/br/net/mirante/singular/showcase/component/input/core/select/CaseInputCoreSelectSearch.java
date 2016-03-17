/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.showcase.component.CaseBase;
import java.io.Serializable;

public class CaseInputCoreSelectSearch extends CaseBase implements Serializable {

    public CaseInputCoreSelectSearch() {
        super("Search Select", "Search");
        setDescriptionHtml("Permite a seleção simples a partir de uma busca filtrada.");
    }

}
