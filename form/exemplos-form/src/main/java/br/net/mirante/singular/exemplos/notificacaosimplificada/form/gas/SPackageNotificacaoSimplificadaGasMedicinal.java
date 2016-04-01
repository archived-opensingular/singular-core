/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.gas;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySearchModal;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

import java.util.Optional;

@SInfoType(spackage = SPackageNotificacaoSimplificadaGasMedicinal.class)
public class SPackageNotificacaoSimplificadaGasMedicinal extends SPackage {

    public static final String PACOTE = "mform.peticao.notificacaosimplificada.gas";
    public static final String TIPO = "MedicamentoGasMedicinal";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private STypeString descricao, gas, concentracao;
    private STypeComposite<SIComposite> informacoesFarmacopeicas;
    private STypeList<STypeAcondicionamento, SIComposite> acondicionamentos;
    private STypeAttachmentList listaAnexos, rotulagens;

    public SPackageNotificacaoSimplificadaGasMedicinal() {
        super(PACOTE);
    }

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Gás Medicinal");

        addDescricao(notificacaoSimplificada);
        addGas(notificacaoSimplificada);
        addConcentracao(notificacaoSimplificada);

        addInformacoesFarmacopeicas(notificacaoSimplificada);

        addAnexos(notificacaoSimplificada);

        addAcondicionamentos(notificacaoSimplificada);

        addRotulagens(notificacaoSimplificada);

        SViewTab tabbed = notificacaoSimplificada.setView(SViewTab::new);
        tabbed.addTab("medicamento", "Gás")
                .add(descricao)
                .add(gas)
                .add(concentracao)
                .add(informacoesFarmacopeicas)
                .add(listaAnexos)
        ;
        tabbed.addTab("acondicionamento", "Acondicionamento")
                .add(acondicionamentos);
        tabbed.addTab("layoutsRotulagem", "Rotulagem")
                .add(rotulagens);
    }

    private void addDescricao(STypeComposite<?> notificacaoSimplificada) {
        descricao = notificacaoSimplificada.addFieldString("descricao");
        descricao.asAtrBasic().label("Descrição");
        descricao.withSelectionOf("TRATAMENTO RESPIRATÓRIO E REANIMAÇÃO", "ANESTESIA", "TRATAMENTO DA DOR");
    }

    private void addGas(STypeComposite<?> notificacaoSimplificada) {
        gas = notificacaoSimplificada.addFieldString("gas");
        gas.asAtrBasic().label("Gás");
        gas.withSelectionFromProvider(new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter) {
                Optional<Object> descValue = optionsInstance.findNearestValue(descricao);
                SIList<?> r = gas.newList();
                if(descValue.isPresent()){
                    String[] options =  {};
                    if(descValue.get().equals("TRATAMENTO RESPIRATÓRIO E REANIMAÇÃO")){
                        options = new String[]{"Oxigênio","VasoKinox","KEOL"};
                    }else if(descValue.get().equals("ANESTESIA")){
                        options = new String[]{"LENOXe", "Protóxido de azoto medicinal"};
                    }else if(descValue.get().equals("TRATAMENTO DA DOR")){
                        options = new String[]{"Kalinox"};
                    }
                    for(String c : options){
                        r.addNew().setValue(c);
                    }
                }
                return r;
            }
        });
        gas.asAtrBasic()
                .dependsOn(descricao)
                .enabled((x) -> x.findNearestValue(descricao).isPresent())
        ;

        gas.asAtrBootstrap().colPreference(2);
    }

    private void addConcentracao(STypeComposite<?> notificacaoSimplificada) {
        concentracao = notificacaoSimplificada.addFieldString("concentracao");
        concentracao.asAtrBasic().label("Concentração/Unidade de medida");
        concentracao.withSelectionOf("50mg + 30mg","45mg + 60mg","55mg + 90mg");
        concentracao.asAtrBasic()
                .dependsOn(descricao)
                .enabled((x) -> x.findNearestValue(descricao).isPresent())
        ;
        concentracao.asAtrBootstrap().colPreference(2);
    }

    private void addInformacoesFarmacopeicas(STypeComposite<?> notificacaoSimplificada) {
        informacoesFarmacopeicas = notificacaoSimplificada.addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtrBasic().label("Informações farmacopeicas");

        STypeString farmacopeia = informacoesFarmacopeicas.addFieldString("farmacopeia");
        farmacopeia.asAtrBasic().label("Nome");
        farmacopeia.withSelectionOf("Farmacopeia 1", "Farmacopeia 2", "Farmacopeia 3", "Farmacopeia 4");
        farmacopeia.withView(SViewSelectionBySearchModal::new);

        informacoesFarmacopeicas.addFieldInteger("pagina")
                .asAtrBasic().label("Página")
                .asAtrBootstrap().colPreference(2);
        informacoesFarmacopeicas.addFieldString("edicao")
                .asAtrBasic().label("Ediçao")
                .asAtrBootstrap().colPreference(2);
    }

    private STypeAttachmentList addAnexos(STypeComposite<?> notificacaoSimplificada) {
        listaAnexos = notificacaoSimplificada.addFieldListOfAttachment("listaDeAnexosDeMetodologiaEEspecificacao", "AnexoDeMetodologiaEEspecificacao");
        listaAnexos.asAtrBasic().label("Metodologia e especificação");
        return listaAnexos;
    }

    private void addAcondicionamentos(STypeComposite<?> notificacaoSimplificada) {
        acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        acondicionamentos.getElementsType().embalagemSecundaria.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().quantidade.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().unidadeMedida.asAtrBasic().visible(false);
        acondicionamentos.getElementsType().estudosEstabilidade.asAtrBasic().visible(false);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria.descricaoEmbalagemPrimaria, "Embalagem primária")
                        .col(acondicionamentos.getElementsType().prazoValidade))
                .asAtrBasic().label("Acondicionamento");
    }

    private STypeAttachmentList addRotulagens(STypeComposite<?> notificacaoSimplificada) {
        rotulagens = notificacaoSimplificada.addFieldListOfAttachment("rotulagens", "rotulagem");
        rotulagens.asAtrBasic().label("Layout de Rotulagem");
        return rotulagens;
    }

}

