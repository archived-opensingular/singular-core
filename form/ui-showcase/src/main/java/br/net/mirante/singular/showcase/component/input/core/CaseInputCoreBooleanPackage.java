package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;

public class CaseInputCoreBooleanPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampoBoolean("aceitaTermos")
                .as(AtrBasic.class).label("Aceito os termos e condições");
        super.carregarDefinicoes(pb);
    }

}
