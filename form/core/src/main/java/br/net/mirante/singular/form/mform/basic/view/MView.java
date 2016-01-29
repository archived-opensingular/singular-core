package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;

import br.net.mirante.singular.form.mform.SType;

@SuppressWarnings("serial")
public class MView implements Serializable {
    public static final MView DEFAULT = new MView();

    public boolean aplicavelEm(SType<?> tipo) {
        return true;
    }
}
