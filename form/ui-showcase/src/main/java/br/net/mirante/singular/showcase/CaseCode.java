package br.net.mirante.singular.showcase;

import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.PacoteBuilder;

public abstract class CaseCode {

    public abstract MTipo<?> createForm(PacoteBuilder pb);
}
