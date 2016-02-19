package br.net.mirante.singular.form.mform.basic.view;

import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.core.STypeBoolean;

/**
 * View para renderização do {@link STypeBoolean} em forma de input radio.
 */
public class MBooleanRadioView extends MSelecaoPorRadioView {

    private String labelTrue = "Sim";
    
    private String labelFalse = "Não";
    
    @Override
    public boolean aplicavelEm(SType<?> tipo) {
        return tipo instanceof STypeBoolean;
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
