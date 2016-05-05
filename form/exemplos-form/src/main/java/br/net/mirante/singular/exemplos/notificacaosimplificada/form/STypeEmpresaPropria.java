package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaPropria extends STypeComposite<SIComposite> {

    public STypeString razaoSocialPropria;
    public STypeCNPJ cnpj;
    public STypeString endereco;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        razaoSocialPropria = addFieldString("razaoSocial");
        razaoSocialPropria
                .asAtr()
                .label("Razão Social")
                .enabled(false);
        (cnpj = addFieldCNPJ("cnpj"))
                .asAtr().label("CNPJ")
                .enabled(false);
        (endereco = addFieldString("endereco"))
                .asAtr().label("Endereço")
                .enabled(false);
    }

}
