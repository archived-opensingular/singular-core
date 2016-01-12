package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@MInfoTipo(nome = "EMail", pacote = MPacoteUtil.class)
public class MTipoEMail extends MTipoString {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.email());
    }
}
