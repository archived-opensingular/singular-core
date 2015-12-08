package br.net.mirante.singular.showcase.layout;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.AtrWicket;

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
                    .as(AtrBasic::new).label("Experiências profissionais").tamanhoInicial(1);
            dtInicioExperiencia
                    .as(AtrBasic::new).label("Data inicial")
                    .as(AtrWicket::new).larguraPref(2);
            dtFimExperiencia
                    .as(AtrBasic::new).label("Data final")
                    .as(AtrWicket::new).larguraPref(2);
            empresa
                    .as(AtrBasic::new).label("Empresa")
                    .as(AtrWicket::new).larguraPref(8);
            cargo
                    .as(AtrBasic::new).label("Cargo");
            atividades
                    .as(AtrBasic::new).label("Atividades Desenvolvidas").multiLinha(true);
        }

    }
}
