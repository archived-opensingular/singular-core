/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

public class SPackageNotificacaoSimplificadaGasMedicinal extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.gas";
    public static final String TIPO = "MedicamentoGasMedicinal";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private STypeString                                      descricao;
    private STypeComposite<SIComposite>                      informacoesFarmacopeicas;
    private STypeList<STypeAcondicionamentoGAS, SIComposite> acondicionamentos;
    private STypeString                                      nomeComercial;

    public SPackageNotificacaoSimplificadaGasMedicinal() {
        super(PACOTE);
    }

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtr().displayString(" ${nomeComercial} - ${descricao} ");
        notificacaoSimplificada.asAtr().label("Gás Medicinal");

        addDescricao(notificacaoSimplificada);
        addNomeComercial(notificacaoSimplificada);
        addInformacoesFarmacopeicas(notificacaoSimplificada);
        addAcondicionamentos(notificacaoSimplificada);

    }

    private void addDescricao(STypeComposite<?> notificacaoSimplificada) {
        descricao = notificacaoSimplificada.addFieldString("descricao");
        descricao.asAtr().label("Descrição").required();
        descricao.withSelectView();
        descricao.asAtrBootstrap().colPreference(6);
        descricao.selectionOf("Ciclopropano  99,5%", "Óxido nitroso (NO2) 70%", "Ar comprimido medicinal 79% N2 + 21% O2 ");
    }

    private void addNomeComercial(STypeComposite<?> notificacaoSimplificada) {
        nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");
        nomeComercial
                .asAtr()
                .label("Nome do gás")
                .asAtrBootstrap()
                .newRow().colPreference(4);

    }

    private void addInformacoesFarmacopeicas(STypeComposite<?> notificacaoSimplificada) {
        informacoesFarmacopeicas = notificacaoSimplificada.addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtr().label("Informações farmacopeicas");

        STypeFarmacopeiaReferencia farmacopeia = informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);
    }

    private void addAcondicionamentos(STypeComposite<?> notificacaoSimplificada) {
        acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamentoGAS.class);
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria, "Embalagem primária"))
                .asAtr().label("Acondicionamento");
    }

}

