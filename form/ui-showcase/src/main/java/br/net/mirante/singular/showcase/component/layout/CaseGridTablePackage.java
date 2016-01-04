package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class CaseGridTablePackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> testForm = pb.createTipoComposto("testForm");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> certificacoes = testForm.addCampoListaOfComposto("certificacoes", "certificacao");
        certificacoes.as(AtrBasic::new).label("Certificações");
        final MTipoComposto<?> certificacao = certificacoes.getTipoElementos();
        final MTipoAnoMes dataCertificacao = certificacao.addCampo("data", MTipoAnoMes.class, true);
        final MTipoString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final MTipoData validadeCertificacao = certificacao.addCampoData("validade");
        final MTipoString nomeCertificacao = certificacao.addCampoString("nome", true);
        {
            certificacoes
                    .withView(MTableListaView::new)
                    .as(AtrBasic::new).label("Certificações");
            certificacao
                    .as(AtrBasic::new).label("Certificação");
            dataCertificacao
                    .as(AtrBasic::new).label("Data")
                    .as(AtrBootstrap::new).colPreference(2);
            entidadeCertificacao
                    .as(AtrBasic::new).label("Entidade")
                    .as(AtrBootstrap::new).colPreference(10);
            validadeCertificacao
                    .as(AtrBasic::new).label("Validade")
                    .as(AtrBootstrap::new).colPreference(2);
            nomeCertificacao
                    .as(AtrBasic::new).label("Nome")
                    .as(AtrBootstrap::new).colPreference(10);
        }
    }
}
