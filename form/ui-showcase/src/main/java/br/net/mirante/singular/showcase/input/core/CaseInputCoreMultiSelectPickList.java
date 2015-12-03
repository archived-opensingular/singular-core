package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectPickList extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectPickList() {
        super("Multi Select", "Pick List");
        setDescriptionHtml("Permite a seleção múltipla no formato de uma pick list.");
    }

}
