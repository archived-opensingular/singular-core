package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;
import br.net.mirante.singular.form.mform.function.IBehavior;

@MInfoTipo(nome = "MTipoBehavior", pacote = SPackageBasic.class)
public class STypeBehavior extends STypeCode<SIBehavior, IBehavior<SInstance2>> {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeBehavior() {
        super((Class) SIBehavior.class, (Class) IBehavior.class);
    }
}
