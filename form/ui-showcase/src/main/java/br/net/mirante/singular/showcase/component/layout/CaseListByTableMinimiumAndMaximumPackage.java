package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;

public class CaseListByTableMinimiumAndMaximumPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = testForm.addFieldListOfComposite("certificacoes", "certificacao");
        certificacoes.as(AtrBasic::new).label("Certificações");
        final STypeComposite<?> certificacao = certificacoes.getElementsType();
        final STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        final STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        final STypeDate validadeCertificacao = certificacao.addFieldData("validade");
        final STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                    //@destacar:bloco
                    .withMiniumSizeOf(2)
                    .withMaximumSizeOf(3)
                     //@destacar:fim
                    .withView(SViewListByTable::new)
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
