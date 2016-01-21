package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;

//@formatter:off
public class CaseInputCoreBooleanPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        tipoMyForm.addCampoBoolean("aceitaTermos")
            .asAtrBasic().label("Aceito os termos e condições");

        tipoMyForm.addCampoBoolean("receberNotificacoes")
            //@destacar
            .withRadioView()
            .asAtrBasic().label("Receber notificações");

        tipoMyForm.addCampoBoolean("aceitaTermos2")
            //@destacar
            .withRadioView("Aceito", "Rejeito")
            .asAtrBasic().label("Aceito os termos e condições");
    }
}
