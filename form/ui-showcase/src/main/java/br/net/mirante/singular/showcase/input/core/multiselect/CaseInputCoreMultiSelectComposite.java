package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputCoreMultiSelectComposite extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectComposite() {
        super("Multi Select", "Tipo Composto");
        setDescriptionHtml(
                "Para usar um tipo composto na seleção, este deve ser do tipo MTipoSelectItem.\n" +
                        "É permitido se mudar quais campos serão utilizados como chave e valor.");
    }
}