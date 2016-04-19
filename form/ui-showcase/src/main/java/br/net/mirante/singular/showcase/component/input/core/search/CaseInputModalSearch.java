/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.search;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

import java.io.Serializable;

public class CaseInputModalSearch extends CaseBase implements Serializable {

    public CaseInputModalSearch() {
        super("Search Select", "Search");
        setDescriptionHtml("Permite a seleção simples a partir de uma busca filtrada.");
        getAditionalSources().add(ResourceRef.forSource(Funcionario.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(FuncionarioProvider.class).orElse(null));
    }

}
