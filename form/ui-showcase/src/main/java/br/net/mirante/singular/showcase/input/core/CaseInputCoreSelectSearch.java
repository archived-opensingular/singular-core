package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreSelectSearch extends CaseBase implements Serializable {

    public CaseInputCoreSelectSearch() {
        super("Single Select", "Search");
        setDescriptionHtml("Permite a seleção simples a partir de uma busca filtrada.");
    }

}
