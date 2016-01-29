package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

public class CaseTabsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> testForm = pb.createTipoComposto("testForm");

        STypeString nome;
        STypeInteger idade;
        STypeEMail email;
        (nome = testForm.addCampoString("nome"))
                .as(AtrBasic.class).label("Nome");
        (idade = testForm.addCampoInteger("idade"))
                .as(AtrBasic.class).label("Idade");
        (email = testForm.addCampoEmail("email"))
                .as(AtrBasic.class).label("E-mail");

        final STypeLista<STypeComposto<SIComposite>, SIComposite> experiencias = testForm.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final STypeComposto<?> experiencia = experiencias.getTipoElementos();
        final STypeAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", STypeAnoMes.class, true);
        final STypeAnoMes dtFimExperiencia = experiencia.addCampo("fim", STypeAnoMes.class);
        final STypeString empresa = experiencia.addCampoString("empresa", true);
        final STypeString cargo = experiencia.addCampoString("cargo", true);
        final STypeString atividades = experiencia.addCampoString("atividades");

        {
            experiencias.withView(MListMasterDetailView::new)
                    .as(AtrBasic::new).label("Experiências profissionais");
            dtInicioExperiencia
                    .as(AtrBasic::new).label("Data inicial")
                    .as(AtrBootstrap::new).colPreference(2);
            dtFimExperiencia
                    .as(AtrBasic::new).label("Data final")
                    .as(AtrBootstrap::new).colPreference(2);
            empresa
                    .as(AtrBasic::new).label("Empresa")
                    .as(AtrBootstrap::new).colPreference(8);
            cargo
                    .as(AtrBasic::new).label("Cargo");
            atividades
                    .withTextAreaView()
                    .as(AtrBasic::new).label("Atividades Desenvolvidas");
        }

        //@destacar:bloco
        MTabView tabbed = new MTabView();
        tabbed.addTab("informacoes", "Informações pessoais")
                .add(nome)
                .add(email)
                .add(idade);
        tabbed.addTab(experiencias);
        testForm.withView(tabbed);
        //@destacar:fim
    }
}
