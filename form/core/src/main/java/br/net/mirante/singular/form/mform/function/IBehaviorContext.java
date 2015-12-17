package br.net.mirante.singular.form.mform.function;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;

public interface IBehaviorContext {

    public IBehaviorContext update(MTipo<?>... fields);

    public default IBehaviorContext update(MInstancia... fields) {
        MTipo<?>[] tipos = new MTipo<?>[fields.length];
        for (int i = 0; i < fields.length; i++)
            tipos[i] = fields[i].getMTipo();
        update(tipos);
        return this;
    }
}
