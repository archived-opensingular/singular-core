package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;

public class CaseMasterDetailButtonsPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> testForm = pb.createTipoComposto("testForm");

        final STypeLista<STypeComposto<SIComposite>, SIComposite> experiencias = testForm.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final STypeComposto<?> experiencia = experiencias.getTipoElementos();
        final STypeAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", STypeAnoMes.class, true);
        final STypeAnoMes dtFimExperiencia = experiencia.addCampo("fim", STypeAnoMes.class);
        final STypeString empresa = experiencia.addCampoString("empresa", true);
        final STypeString cargo = experiencia.addCampoString("cargo", true);
        final STypeString atividades = experiencia.addCampoString("atividades");

        {
            //@destacar:bloco
            experiencias.withView(new MListMasterDetailView()
                    //.disableNew() // o botão de adicionar pode ser desabilitado também.
                    .disableDelete()
                    .disableEdit())
            //@destacar:fim
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

    }
}
