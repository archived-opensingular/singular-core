package br.net.mirante.singular.form.mform.function;

import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SType;

public interface IBehaviorContext {

    public IBehaviorContext update(SType<?>... fields);

    public default IBehaviorContext update(SInstance2... fields) {
        SType<?>[] tipos = new SType<?>[fields.length];
        for (int i = 0; i < fields.length; i++)
            tipos[i] = fields[i].getMTipo();
        update(tipos);
        return this;
    }
}
