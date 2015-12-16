package br.net.mirante.singular.showcase.interaction;

import br.net.mirante.singular.showcase.CaseBase;

import java.io.Serializable;

public class CaseInputInteractionDependsOnOptions  extends CaseBase implements Serializable {

    public CaseInputInteractionDependsOnOptions() {
        super("Duas combos");
        setDescriptionHtml("Opções interdependentes");
    }

}
