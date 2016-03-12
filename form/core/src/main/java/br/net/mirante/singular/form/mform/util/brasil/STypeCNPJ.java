package br.net.mirante.singular.form.mform.util.brasil;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.SPackageUtil;
import br.net.mirante.singular.form.validation.ValidationErrorLevel;
import br.net.mirante.singular.form.validation.validator.InstanceValidators;

@SInfoType(name = "CNPJ", spackage = SPackageUtil.class)
public class STypeCNPJ extends STypeString {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addInstanceValidator(ValidationErrorLevel.ERROR, InstanceValidators.cnpj());
        as(AtrBasic.class).label("CNPJ").basicMask("CNPJ").tamanhoMaximo(null);
    }
}
