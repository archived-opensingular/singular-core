package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.showcase.CaseBase;
import java.io.Serializable;

public class CaseInputCoreSelectSearch extends CaseBase implements Serializable {

    public CaseInputCoreSelectSearch() {
        super("Search Select", "Search");
        setDescriptionHtml("Permite a seleção simples a partir de uma busca filtrada.");
    }

}
