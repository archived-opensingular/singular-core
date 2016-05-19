/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.search;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;

public class CaseInputModalSearch extends CaseBase implements Serializable {

    public CaseInputModalSearch() {
        super("Search Select", "In Memory Pagination");
        setDescriptionHtml("Permite a seleção a partir de uma busca filtrada, fazendo o controle de paginação de forma automatica.");
        getAditionalSources().add(ResourceRef.forSource(Funcionario.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(FuncionarioProvider.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(FuncionarioRepository.class).orElse(null));
    }

}
