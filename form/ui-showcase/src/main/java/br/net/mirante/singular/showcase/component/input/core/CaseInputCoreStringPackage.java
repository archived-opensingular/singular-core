package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreStringPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoString("nomeCompleto")
                .as(AtrBasic::new).label("Nome Completo")
                .as(AtrBasic::new).tamanhoMaximo(100);

        tipoMyForm.addCampoString("endereco")
                .as(AtrBasic::new).label("Endereço")
                .as(AtrBasic::new).tamanhoMaximo(250);

    }
}
