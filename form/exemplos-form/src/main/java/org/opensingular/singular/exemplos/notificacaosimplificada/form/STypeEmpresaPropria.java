package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.type.country.brazil.STypeCNPJ;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaPropria extends STypeComposite<SIComposite> {

    public STypeString razaoSocial;
    public STypeCNPJ cnpj;
    public STypeString endereco;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        razaoSocial = addFieldString("razaoSocial");
        razaoSocial
                .asAtr()
                .label("Razão Social")
                .enabled(false);
        (cnpj = addField("cnpj", STypeCNPJ.class))
                .asAtr()
                .enabled(false);
        (endereco = addFieldString("endereco"))
                .asAtr().label("Endereço")
                .enabled(false);
    }

}
