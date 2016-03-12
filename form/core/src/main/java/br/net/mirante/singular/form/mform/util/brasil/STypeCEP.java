package br.net.mirante.singular.form.mform.util.brasil;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.SPackageUtil;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CEP", spackage = SPackageUtil.class)
public class STypeCEP extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cep());
        as(AtrBasic.class).label("CEP").basicMask("CEP").tamanhoMaximo(null);
    }
}
