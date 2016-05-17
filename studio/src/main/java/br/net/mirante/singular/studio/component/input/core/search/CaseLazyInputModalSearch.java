/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.search;

import java.io.Serializable;

import br.net.mirante.singular.studio.component.CaseBase;
import br.net.mirante.singular.studio.component.ResourceRef;


public class CaseLazyInputModalSearch extends CaseBase implements Serializable {

    public CaseLazyInputModalSearch() {
        super("Search Select", "Lazy Pagination");
        setDescriptionHtml("Permite a seleção a partir de uma busca filtrada, sendo necessario fazer o controle de paginação manualmente");
        getAditionalSources().add(ResourceRef.forSource(Funcionario.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(LazyFuncionarioProvider.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(FuncionarioRepository.class).orElse(null));
    }

}
