package br.net.mirante.singular.showcase.custom;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseCustomStringMapperPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoString("nomeCompleto")
                //@destacar
                .withCustomMapper(MaterialDesignInputMapper::new)
                .as(AtrBasic::new).label("Nome Completo");

    }
}
