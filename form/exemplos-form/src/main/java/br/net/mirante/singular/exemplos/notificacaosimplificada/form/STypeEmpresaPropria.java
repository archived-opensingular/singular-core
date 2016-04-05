package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
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
                .asAtrBasic()
                .label("Razão Social");
        (cnpj = addFieldCNPJ("cnpj"))
                .asAtrBasic().label("CNPJ");
        (endereco = addFieldString("endereco"))
                .asAtrBasic().label("Endereço");
    }

}
