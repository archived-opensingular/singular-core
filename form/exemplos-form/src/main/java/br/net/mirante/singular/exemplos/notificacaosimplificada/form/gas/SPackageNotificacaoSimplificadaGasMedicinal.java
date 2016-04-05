/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.Farmacopeia;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.baixorisco.SPackageNotificacaoSimplificadaBaixoRisco.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificadaGasMedicinal.class)
public class SPackageNotificacaoSimplificadaGasMedicinal extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.gas";
    public static final String TIPO = "MedicamentoGasMedicinal";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private STypeString descricao, gas, concentracao;
    private STypeComposite<SIComposite> informacoesFarmacopeicas;
    private STypeList<STypeAcondicionamento, SIComposite> acondicionamentos;
    private STypeAttachmentList listaAnexos;
    private STypeString nomeComercial;

    public SPackageNotificacaoSimplificadaGasMedicinal() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString(" ${nomeComercial} ${descricao} - ${concentracao}");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Gás Medicinal");

        addDescricao(notificacaoSimplificada);
        addConcentracao(notificacaoSimplificada);
        addNomeComercial(notificacaoSimplificada);

        addInformacoesFarmacopeicas(notificacaoSimplificada);

        addAnexos(notificacaoSimplificada);

        addAcondicionamentos(notificacaoSimplificada);

    }

    private void addDescricao(STypeComposite<?> notificacaoSimplificada) {
        descricao = notificacaoSimplificada.addFieldString("descricao");
        descricao.asAtrBasic().label("Descrição");
        descricao.asAtrBootstrap().colPreference(2);
        descricao.withSelectionOf("TRATAMENTO RESPIRATÓRIO E REANIMAÇÃO", "ANESTESIA", "TRATAMENTO DA DOR");
    }

    private void addConcentracao(STypeComposite<?> notificacaoSimplificada) {
        concentracao = notificacaoSimplificada.addFieldString("concentracao");
        concentracao.asAtrBasic().label("Concentração/Unidade de medida");
        concentracao.withSelectionOf("50mg + 30mg", "45mg + 60mg", "55mg + 90mg");
        concentracao.asAtrBasic()
                .dependsOn(descricao)
                .enabled((x) -> x.findNearestValue(descricao).isPresent())
        ;
        concentracao.asAtrBootstrap().colPreference(3);
    }

    private void addNomeComercial(STypeComposite<?> notificacaoSimplificada) {
        nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");
        nomeComercial
                .asAtrBasic()
                .label("Nome Comercial")
                .asAtrBootstrap()
                .newRow().colPreference(4);

    }

    private void addInformacoesFarmacopeicas(STypeComposite<?> notificacaoSimplificada) {
        informacoesFarmacopeicas = notificacaoSimplificada.addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtrBasic().label("Informações farmacopeicas");

        STypeComposite farmacopeia = informacoesFarmacopeicas.addFieldComposite("farmacopeia");
        farmacopeia.asAtrBasic().label("Nome");
        STypeInteger id = farmacopeia.addFieldInteger("id");
        STypeString descricao = farmacopeia.addFieldString("descricao");
        farmacopeia.withSelectionFromProvider(descricao, (ins, filter) -> {
            final SIList<?> list = ins.getType().newList();
            for (Farmacopeia f : dominioService(ins).listFarmacopeias()) {
                final SIComposite c = (SIComposite) list.addNew();
                c.setValue(id, f.getId());
                c.setValue(descricao, f.getDescricao());
            }
            return list;
        });
        farmacopeia.withView(SViewSelectionBySearchModal::new);

        informacoesFarmacopeicas.addFieldString("edicao")
                .asAtrBasic().label("Ediçao")
                .asAtrBootstrap().colPreference(2);
        informacoesFarmacopeicas.addFieldInteger("pagina")
                .asAtrBasic().label("Página")
                .asAtrBootstrap().colPreference(2);
    }

    private STypeAttachmentList addAnexos(STypeComposite<?> notificacaoSimplificada) {
        listaAnexos = notificacaoSimplificada.addFieldListOfAttachment("listaDeAnexosDeMetodologiaEEspecificacao", "AnexoDeMetodologiaEEspecificacao");
        listaAnexos.asAtrBasic().label("Metodologia e especificação");
        return listaAnexos;
    }

    private void addAcondicionamentos(STypeComposite<?> notificacaoSimplificada) {
        acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos.getElementsType().embalagemSecundaria.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().quantidade.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().unidadeMedida.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().estudosEstabilidade.asAtrBasic().visible(false);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria.descricao, "Embalagem primária")
                        .col(acondicionamentos.getElementsType().prazoValidade))
                .asAtrBasic().label("Acondicionamento");
    }

}

