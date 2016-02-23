package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;

public class CaseGridTablePackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createTipoComposto("testForm");

        final STypeLista<STypeComposite<SIComposite>, SIComposite> certificacoes = testForm.addCampoListaOfComposto("certificacoes", "certificacao");
        certificacoes.as(AtrBasic::new).label("Certificações");
        final STypeComposite<?> certificacao = certificacoes.getTipoElementos();
        final STypeAnoMes dataCertificacao = certificacao.addCampo("data", STypeAnoMes.class, true);
        final STypeString entidadeCertificacao = certificacao.addCampoString("entidade", true);
        final STypeData validadeCertificacao = certificacao.addCampoData("validade");
        final STypeString nomeCertificacao = certificacao.addCampoString("nome", true);
        {
            certificacoes
                    .withMiniumSizeOf(2)
                    .withMaximumSizeOf(3)
                    .withView(MTableListaView::new)
                    .as(AtrBasic::new).label("Certificações");
            certificacao
                    .as(AtrBasic::new).label("Certificação");
            dataCertificacao
                    .as(AtrBasic::new).label("Data")
                    .as(AtrBootstrap::new).colPreference(2);
            entidadeCertificacao
                    .as(AtrBasic::new).label("Entidade")
                    .as(AtrBootstrap::new).colPreference(4);
            validadeCertificacao
                    .as(AtrBasic::new).label("Validade")
                    .as(AtrBootstrap::new).colPreference(2);
            nomeCertificacao
                    .as(AtrBasic::new).label("Nome")
                    .as(AtrBootstrap::new).colPreference(4);
        }
    }
}
