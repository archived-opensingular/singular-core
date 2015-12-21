package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectCombo extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectCombo() {
        super("Multi Select", "Combo");
        setDescriptionHtml("Permite a seleção múltipla no formato de um combo. É funcional para listas curtas.");
    }

}
