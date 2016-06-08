/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vegetal;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.SPackageNotificacaoSimplificada;
import br.net.mirante.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.exemplos.util.PairConverter;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.view.SViewListByTable;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Optional;

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
    protected void onLoadPackage(PackageBuilder pb) {
        pb.loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtr().displayString("${nomenclaturaBotanica.descricao} - ${nomeComercial}");
        notificacaoSimplificada.asAtr().label("Produto Tradicional Fitoterápico");

        final STypeComposite<SIComposite> nomenclaturaBotanica     = notificacaoSimplificada.addFieldComposite("nomenclaturaBotanica");
        STypeInteger            idNomenclaturaBotanica   = nomenclaturaBotanica.addFieldInteger("id");
        STypeString             descNomenclaturaBotanica = nomenclaturaBotanica.addFieldString("descricao");
        nomenclaturaBotanica
                .asAtr()
                .required()
                .label("Nomenclatura botânica")
                .asAtrBootstrap()
                .colPreference(4);

        nomenclaturaBotanica.selectionOf(Pair.class)
                .id("${left}")
                .display("${right}")
                .converter(new PairConverter(idNomenclaturaBotanica, descNomenclaturaBotanica))
                .simpleProvider(ins -> dominioService(ins).nomenclaturaBotanica());

        STypeList<STypeComposite<SIComposite>, SIComposite> concentracoes     = notificacaoSimplificada.addFieldListOfComposite("concentracoes", "concentracao");
        STypeComposite<SIComposite>                         concentracao      = concentracoes.getElementsType();
        SType<?>                                            planta            = concentracao.addFieldString("planta");
        STypeDecimal                                        valorConcentracao = concentracao.addFieldDecimal("concentracao");
        STypeSimple                                         unidade           = concentracao.addFieldString("unidade");
        planta
                .asAtr().enabled(false).label("Nomenclatura botânica").asAtrBootstrap().colPreference(4);
        valorConcentracao
                .asAtr().label("Concentração").asAtrBootstrap().colPreference(4);
        unidade
                .asAtr().enabled(false).label("Unidade de medida").asAtrBootstrap().colPreference(4);

        concentracoes
                .asAtr()
                .visible( i -> Value.notNull(i, idNomenclaturaBotanica))
                .label("Concentração")
                .dependsOn(nomenclaturaBotanica);
        concentracoes
                .withView(() -> new SViewListByTable().disableNew().disableDelete())
                .withUpdateListener(list -> {
                    String value = Value.of(list, descNomenclaturaBotanica);
                    if (value != null) {
                        String[] values = value.split("\\+");
                        for (String plantinha : values) {
                            SIComposite elem = list.addNew();
                            elem.setValue(planta, plantinha);
                            elem.setValue(unidade, "mg");
                        }
                    }
                });

        valorConcentracao.addInstanceValidator(validatable -> {
            Integer idNomenclatura = validatable.getInstance().findNearest(nomenclaturaBotanica).get().findNearest(idNomenclaturaBotanica).get().getValue();
            final BigDecimal value = validatable.getInstance().getValue();

            final Pair p = dominioService(validatable.getInstance()).rangeConcentracoes(idNomenclatura);
            if (p != null) {
                final BigDecimal min = (BigDecimal) p.getLeft();
                final BigDecimal max = (BigDecimal) p.getRight();
                String faixa = String.format("%s - %s", min, max);
                if (value == null) {

                } else if (value.compareTo(min) < 0 ) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));
                } else if (value.compareTo(max) > 0 ) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));
                }
            }
        });

        notificacaoSimplificada.addFieldListOfAttachment("formulas", "formula")
        .asAtr()
        .label("Fórmula do produto");


        STypeString nomeComercial = notificacaoSimplificada.addFieldString("nomeComercial");

        nomeComercial
                .asAtr()
                .required()
                .label("Nome do medicamento");

        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        STypeAcondicionamento acondicionamento = acondicionamentos.getElementsType();
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamento.embalagemPrimaria, "Embalagem primária")
                        .col(acondicionamento.embalagemSecundaria.descricao, "Embalagem secundária")
                        .col(acondicionamento.estudosEstabilidade, "Estudo de estabilidade")
                        .col(acondicionamento.prazoValidade))
                .asAtr().label("Acondicionamento");
        acondicionamento.quantidade.asAtr().visible(false);
        acondicionamento.unidadeMedida.asAtr().visible(false);
        acondicionamento.layoutsRotulagem.asAtr().visible(false);
        acondicionamento.locaisFabricacao.asAtr().visible(false);

        final STypeList<STypeEnsaioControleQualidade, SIComposite> ensaios = notificacaoSimplificada.addFieldListOf("ensaiosControleQualidade", STypeEnsaioControleQualidade.class);
        final STypeEnsaioControleQualidade ensaio = ensaios.getElementsType();
        ensaios
                .withView(new SViewListByMasterDetail()
                        .col(ensaio.descricaoTipoEnsaio, "Ensaio")
                        .col(ensaio.descricaoTipoReferencia, "Tipo de referência")
                        .disableNew().disableDelete())
                .asAtr().label("Ensaio de Controle de Qualidade");


        notificacaoSimplificada.withInitListener(ins -> {
            final Optional<SIList<SIComposite>> lista = ins.findNearest(ensaios);

            for (STypeEnsaioControleQualidade.TipoEnsaioControleQualidade tipoEnsaioControleQualidade : STypeEnsaioControleQualidade.TipoEnsaioControleQualidade.values()) {
                final SIComposite siComposite = lista.get().addNew();
                siComposite.findNearest(ensaio.idTipoEnsaio).get().setValue(tipoEnsaioControleQualidade.getId());
                siComposite.findNearest(ensaio.descricaoTipoEnsaio).get().setValue(tipoEnsaioControleQualidade.getDescricao());
            }
        });

    }

}

