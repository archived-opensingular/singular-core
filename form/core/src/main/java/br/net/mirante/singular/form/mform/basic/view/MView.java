package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.MTipo;

@SuppressWarnings("serial")
public class MView implements Serializable {
    public static final MView DEFAULT = new MView();

    public boolean aplicavelEm(MTipo<?> tipo) {
        return true;
    }
}
