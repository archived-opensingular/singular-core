package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@MInfoTipo(nome = "CNPJ", pacote = MPacoteUtil.class)
public class MTipoCNPJ extends MTipoString {

    @Override
    protected void onCargaTipo(TipoBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cnpj());
        as(AtrBasic.class).label("CNPJ").basicMask("CNPJ");
    }
}
