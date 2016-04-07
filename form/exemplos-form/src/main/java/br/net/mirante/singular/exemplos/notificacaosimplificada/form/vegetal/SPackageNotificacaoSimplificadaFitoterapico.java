/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeAttachmentList;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.commons.lang3.tuple.Pair;

@SInfoType(spackage = SPackageNotificacaoSimplificadaFitoterapico.class)
public class SPackageNotificacaoSimplificadaFitoterapico extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.fitoterapico";
    public static final String TIPO          = "MedicamentoProdutoTradicionalFitoterapico";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificadaFitoterapico() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString("${nomenclaturaBotanica.descricao} - (<#list concentracoes as c>${c.descricao} <#sep>, </#sep></#list>)");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Produto Tradicional Fitoterápico");

        final STypeComposite<?> nomenclaturaBotanica     = notificacaoSimplificada.addFieldComposite("nomenclaturaBotanica");
        STypeInteger            idNomenclaturaBotanica   = nomenclaturaBotanica.addFieldInteger("id");
        STypeString             descNomenclaturaBotanica = nomenclaturaBotanica.addFieldString("descricao");
        nomenclaturaBotanica
                .asAtrBasic()
                .required()
                .label("Nomenclatura botânica")
                .asAtrBootstrap()
                .colPreference(4);
        nomenclaturaBotanica
                .withSelectView()
                .withSelectionFromProvider(descNomenclaturaBotanica, (ins, filter) -> {
                    final SIList<?> list = ins.getType().newList();
                    for (Pair p : dominioService(ins).nomenclaturaBotanica(filter)) {
                        final SIComposite c = (SIComposite) list.addNew();
                        c.setValue(idNomenclaturaBotanica, p.getLeft());
                        c.setValue(descNomenclaturaBotanica, p.getRight());
                    }
                    return list;
                });

        STypeList<STypeComposite<SIComposite>, SIComposite> concentracoes = notificacaoSimplificada.addFieldListOfComposite("concentracoes", "concentracao");
        STypeComposite<SIComposite> concentracao = concentracoes.getElementsType();
        SType<?>                planta   = concentracao.addFieldString("planta");
        STypeSimple             valorConcentracao = concentracao.addFieldDecimal("concentracao");
        STypeSimple             unidade = concentracao.addFieldString("unidade");
        planta
                .asAtrBasic().enabled(false).label("Nomenclatura botânica").asAtrBootstrap().colPreference(4);
        valorConcentracao
                .asAtrBasic().label("Concentração").asAtrBootstrap().colPreference(4);
        unidade
                .asAtrBasic().enabled(false).label("Unidade de medida").asAtrBootstrap().colPreference(4);

        concentracoes
                .asAtrBasic()
                .visible( i -> Value.notNull(i, idNomenclaturaBotanica))
                .label("Concentração")
                .dependsOn(nomenclaturaBotanica);
        concentracoes
                .withView(() -> new SViewListByTable().disableNew().disableDelete())
                .withUpdateListener(list -> {
                    String value = Value.of(list, descNomenclaturaBotanica);
                    String[] values = value.split("\\+");
                    for (String plantinha : values) {
                        SIComposite elem = list.addNew();
                        elem.setValue(planta, plantinha);
                        elem.setValue(unidade, "mg");
                    }
                });

        notificacaoSimplificada.addFieldListOfAttachment("formulas", "formula")
        .asAtrBasic()
        .label("Fórmula do produto");


        STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");

        nomeComercial
                .asAtrBasic()
                .required()
                .label("Nome do medicamento");

        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        STypeAcondicionamento acondicionamento = acondicionamentos.getElementsType();
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamento.embalagemPrimaria, "Embalagem primária")
                        .col(acondicionamento.embalagemSecundaria.descricao, "Embalagem secundária")
                        .col(acondicionamento.estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamento.prazoValidade))
                .asAtrBasic().label("Acondicionamento");
        acondicionamento.quantidade.asAtrBasic().visible(false);
        acondicionamento.unidadeMedida.asAtrBasic().visible(false);
        acondicionamento.layoutsRotulagem.asAtrBasic().visible(false);
        acondicionamento.locaisFabricacao.asAtrBasic().visible(false);

        final STypeList<STypeEnsaioControleQualidade, SIComposite> ensaios = notificacaoSimplificada.addFieldListOf("ensaiosControleQualidade", STypeEnsaioControleQualidade.class);
        ensaios
                .withView(new SViewListByMasterDetail()
                        .col(ensaios.getElementsType().descricaoTipoEnsaio, "Ensaio"))
                .asAtrBasic().label("Ensaio de Controle de Qualidade");

    }

}

