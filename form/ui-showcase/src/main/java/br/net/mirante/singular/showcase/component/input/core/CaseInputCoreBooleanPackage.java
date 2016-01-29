package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PacoteBuilder;

//@formatter:off
public class CaseInputCoreBooleanPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");

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
