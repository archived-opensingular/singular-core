package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseInteractionEnabled extends CaseBase implements Serializable {

    public CaseInteractionEnabled() {
        super("Enabled, Visible, Required", "Enabled");
        setDescriptionHtml("Habilita os componentes dinamicamente.");
    }

}
