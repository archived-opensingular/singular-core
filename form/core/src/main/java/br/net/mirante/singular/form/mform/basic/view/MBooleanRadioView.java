package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;

@SuppressWarnings("serial")
public class MBooleanRadioView extends MView {

    private String labelTrue = "Sim";
    
    private String labelFalse = "Não";
    
    @Override
    public boolean aplicavelEm(MTipo<?> tipo) {
        return tipo instanceof MTipoBoolean;
    }

    public String labelTrue() {
        return labelTrue;
    }

    public void labelTrue(String labelTrue) {
        this.labelTrue = labelTrue;
    }

    public String labelFalse() {
        return labelFalse;
    }

    public void labelFalse(String labelFalse) {
        this.labelFalse = labelFalse;
    }
    
    
}
