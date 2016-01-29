package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@MInfoTipo(nome = "TelefoneNacional", pacote = SPackageUtil.class)
public class STypeTelefoneNacional extends STypeString {

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.telefoneNacional());
        as(AtrBasic.class).tamanhoMaximo(15);
    }
}
