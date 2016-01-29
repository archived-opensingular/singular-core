package br.net.mirante.singular.showcase.component.custom;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseCustomStringMapperPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoString("nomeCompleto")
                //@destacar
                .withCustomMapper(MaterialDesignInputMapper::new)
                .as(AtrBasic::new).label("Nome Completo");

    }
}
