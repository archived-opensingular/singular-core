package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@MInfoTipo(nome = "EMail", pacote = SPackageUtil.class)
public class STypeEMail extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.email());
    }
}
