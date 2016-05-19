package br.net.mirante.singular.showcase.component.form.core.search;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;
import br.net.mirante.singular.showcase.component.ResourceRef;


public class CaseLazyInputModalSearch extends CaseBase implements Serializable {

    public CaseLazyInputModalSearch() {
        super("Search Select", "Lazy Pagination");
        setDescriptionHtml("Permite a seleção a partir de uma busca filtrada, sendo necessario fazer o controle de paginação manualmente");
        getAditionalSources().add(ResourceRef.forSource(Funcionario.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(LazyFuncionarioProvider.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(FuncionarioRepository.class).orElse(null));
    }

}
