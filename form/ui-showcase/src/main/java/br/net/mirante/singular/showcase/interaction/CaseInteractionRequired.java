package br.net.mirante.singular.showcase.interaction;

import br.net.mirante.singular.showcase.CaseBase;

public class CaseInteractionRequired extends CaseBase {

    public CaseInteractionRequired() {
        super("Enabled, Visible, Required", "Required");
        setDescriptionHtml("Torna os campos obrigatórios dinamicamente.");
    }

    @Override
    public boolean showValidateButton() {
        return true;
    }
}
