package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;


public class CaseInputCoreBasicPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");
        tipoMyForm.addCampoCNPJ("cnpj")
                .as(AtrBasic.class).label("CNPJ");
        tipoMyForm.addCampoCPF("cpf")
                .as(AtrBasic.class).label("CPF");
        tipoMyForm.addCampoCEP("cep")
                .as(AtrBasic.class).label("CEP");
        tipoMyForm.addCampoEmail("email")
                .as(AtrBasic.class).label("E-mail");
        tipoMyForm.addCampoString("descricao")
                .as(AtrBasic.class).label("Descrição");
        tipoMyForm.addCampo("telefone", STypeTelefoneNacional.class)
                .as(AtrBasic.class).label("Telefone");
        super.carregarDefinicoes(pb);
    }
}
