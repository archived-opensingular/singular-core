package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;


public class CaseInputCoreBasicPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampoCNPJ("cnpj")
                .as(AtrBasic.class).label("CNPJ");
        tipoMyForm.addCampoCPF("cpf")
                .as(AtrBasic.class).label("CPF");
        tipoMyForm.addCampoCEP("cep")
                .as(AtrBasic.class).label("CEP");
        tipoMyForm.addCampoEmail("email")
                .as(AtrBasic.class).label("E-Mail");
        tipoMyForm.addCampoString("descricao")
                .as(AtrBasic.class).label("Descrição");
        tipoMyForm.addCampo("telefone", STypeTelefoneNacional.class)
                .as(AtrBasic.class).label("Telefone");
        super.carregarDefinicoes(pb);
    }
}
