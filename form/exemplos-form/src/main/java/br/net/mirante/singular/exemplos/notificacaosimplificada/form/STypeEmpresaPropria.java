package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.country.brazil.STypeCNPJ;

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
