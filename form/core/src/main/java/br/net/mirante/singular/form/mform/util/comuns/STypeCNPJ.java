package br.net.mirante.singular.form.mform.util.comuns;

import br.net.mirante.singular.form.mform.MInfoTipo;
import br.net.mirante.singular.form.mform.TipoBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@MInfoTipo(nome = "CNPJ", pacote = SPackageUtil.class)
public class STypeCNPJ extends STypeString {

    @Override
    protected void onLoadType(TipoBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cnpj());
        as(AtrBasic.class).label("CNPJ").basicMask("CNPJ").tamanhoMaximo(null);
    }
}
