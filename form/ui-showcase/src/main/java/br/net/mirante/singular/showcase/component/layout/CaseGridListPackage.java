package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MTextAreaView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class CaseGridListPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> experiencias = testForm.addCampoListaOfComposto("experienciasProfissionais", "experiencia");
        final MTipoComposto<?> experiencia = experiencias.getTipoElementos();
        final MTipoAnoMes dtInicioExperiencia = experiencia.addCampo("inicio", MTipoAnoMes.class, true);
        final MTipoAnoMes dtFimExperiencia = experiencia.addCampo("fim", MTipoAnoMes.class);
        final MTipoString empresa = experiencia.addCampoString("empresa", true);
        final MTipoString cargo = experiencia.addCampoString("cargo", true);
        final MTipoString atividades = experiencia.addCampoString("atividades");

        {
            experiencias.withView(MPanelListaView::new)
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
                    .withView(MTextAreaView::new)
                    .as(AtrBasic::new).label("Atividades Desenvolvidas");
        }

    }
}
