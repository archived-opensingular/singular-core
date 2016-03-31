package br.net.mirante.singular.exemplos.notificacaosimplificada.baixocusto;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.STypeString;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaPropria extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        STypeString razaoSocialPropria = addFieldString("razaoSocial");
        razaoSocialPropria
                .asAtrBasic()
                .label("Razão Social");
        addFieldCNPJ("cnpj")
                .asAtrBasic().label("CNPJ");
        addFieldString("endereco")
                .asAtrBasic().label("Endereço");
    }

}
