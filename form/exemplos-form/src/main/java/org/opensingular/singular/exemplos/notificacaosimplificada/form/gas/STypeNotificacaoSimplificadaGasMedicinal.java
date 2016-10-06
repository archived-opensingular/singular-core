package org.opensingular.singular.exemplos.notificacaosimplificada.form.gas;

import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.STypeList;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewListByMasterDetail;

@SInfoType(name = "STypeNotificacaoSimplificadaGasMedicinal", spackage = SPackageNotificacaoSimplificadaGasMedicinal.class)
public class STypeNotificacaoSimplificadaGasMedicinal extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr().displayString(" ${nomeComercial} - ${descricao} ");
        asAtr().label("Gás Medicinal");

        addDescricao();
        addNomeComercial();
        addInformacoesFarmacopeicas();
        addAcondicionamentos();
    }

    private STypeString                                      descricao;
    private STypeComposite<SIComposite>                      informacoesFarmacopeicas;
    private STypeList<STypeAcondicionamentoGAS, SIComposite> acondicionamentos;
    private STypeString                                      nomeComercial;


    private void addDescricao() {
        descricao = addFieldString("descricao");
        descricao.asAtr().label("Descrição").required();
        descricao.withSelectView();
        descricao.asAtrBootstrap().colPreference(6);
        descricao.selectionOf("Ciclopropano  99,5%", "Óxido nitroso (NO2) 70%", "Ar comprimido medicinal 79% N2 + 21% O2 ");
    }

    private void addNomeComercial() {
        nomeComercial = addFieldString("nomeComercial");
        nomeComercial
                .asAtr()
                .label("Nome do gás")
                .asAtrBootstrap()
                .newRow().colPreference(4);

    }

    private void addInformacoesFarmacopeicas() {
        informacoesFarmacopeicas = addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtr().label("Informações farmacopeicas");

        STypeFarmacopeiaReferencia farmacopeia = informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);
    }

    private void addAcondicionamentos() {
        acondicionamentos = addFieldListOf("acondicionamentos", STypeAcondicionamentoGAS.class);
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria, "Embalagem primária"))
                .asAtr().label("Acondicionamento");
    }

}
