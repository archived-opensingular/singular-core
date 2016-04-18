/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeString;

public class SPackageNotificacaoSimplificadaGasMedicinal extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.gas";
    public static final String TIPO = "MedicamentoGasMedicinal";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private STypeString descricao;
    private STypeComposite<SIComposite> informacoesFarmacopeicas;
    private STypeList<STypeAcondicionamentoGAS, SIComposite> acondicionamentos;
    private STypeString nomeComercial;

    public SPackageNotificacaoSimplificadaGasMedicinal() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString(" ${nomeComercial} - ${descricao} ");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Gás Medicinal");

        addDescricao(notificacaoSimplificada);
        addNomeComercial(notificacaoSimplificada);
        addInformacoesFarmacopeicas(notificacaoSimplificada);
        addAcondicionamentos(notificacaoSimplificada);

    }

    private void addDescricao(STypeComposite<?> notificacaoSimplificada) {
        descricao = notificacaoSimplificada.addFieldString("descricao");
        descricao.asAtrBasic().label("Descrição").required();
        descricao.withSelectView();
        descricao.asAtrBootstrap().colPreference(6);
        descricao.withSelectionOf("Ciclopropano  99,5%", "Óxido nitroso (NO2) 70%", "Ar comprimido medicinal 79% N2 + 21% O2 ");
    }

    private void addNomeComercial(STypeComposite<?> notificacaoSimplificada) {
        nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");
        nomeComercial
                .asAtrBasic()
                .label("Nome do gás")
                .asAtrBootstrap()
                .newRow().colPreference(4);

    }

    private void addInformacoesFarmacopeicas(STypeComposite<?> notificacaoSimplificada) {
        informacoesFarmacopeicas = notificacaoSimplificada.addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtrBasic().label("Informações farmacopeicas");

        STypeFarmacopeiaReferencia farmacopeia = informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);
    }

    private void addAcondicionamentos(STypeComposite<?> notificacaoSimplificada) {
        acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamentoGAS.class);
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria, "Embalagem primária"))
                .asAtrBasic().label("Acondicionamento");
    }

}

