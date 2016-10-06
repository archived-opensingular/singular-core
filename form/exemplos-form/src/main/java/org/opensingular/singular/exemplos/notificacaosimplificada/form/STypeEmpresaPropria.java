package org.opensingular.singular.exemplos.notificacaosimplificada.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.country.brazil.STypeCNPJ;

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
