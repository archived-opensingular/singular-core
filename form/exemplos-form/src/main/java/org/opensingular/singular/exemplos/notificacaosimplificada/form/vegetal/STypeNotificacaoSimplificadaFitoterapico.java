package org.opensingular.singular.exemplos.notificacaosimplificada.form.vegetal;


import org.opensingular.singular.exemplos.notificacaosimplificada.form.STypeAcondicionamento;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.exemplos.util.PairConverter;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Optional;

@SInfoType(name = "STypeNotificacaoSimplificadaFitoterapico", spackage = SPackageNotificacaoSimplificadaFitoterapico.class)
public class STypeNotificacaoSimplificadaFitoterapico extends STypeComposite<SIComposite> {


    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        asAtr().displayString("${nomenclaturaBotanica.descricao} - ${nomeComercial}");
        asAtr().label("Produto Tradicional Fitoterápico");

        final STypeComposite<SIComposite> nomenclaturaBotanica     = addFieldComposite("nomenclaturaBotanica");
        STypeInteger                      idNomenclaturaBotanica   = nomenclaturaBotanica.addFieldInteger("id");
        STypeString                       descNomenclaturaBotanica = nomenclaturaBotanica.addFieldString("descricao");
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

        STypeList<STypeComposite<SIComposite>, SIComposite> concentracoes     = addFieldListOfComposite("concentracoes", "concentracao");
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
                .visible(i -> Value.notNull(i, idNomenclaturaBotanica))
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
            Integer          idNomenclatura = validatable.getInstance().findNearest(nomenclaturaBotanica).get().findNearest(idNomenclaturaBotanica).get().getValue();
            final BigDecimal value          = validatable.getInstance().getValue();

            final Pair p = dominioService(validatable.getInstance()).rangeConcentracoes(idNomenclatura);
            if (p != null) {
                final BigDecimal min   = (BigDecimal) p.getLeft();
                final BigDecimal max   = (BigDecimal) p.getRight();
                String           faixa = String.format("%s - %s", min, max);
                if (value == null) {

                } else if (value.compareTo(min) < 0) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));
                } else if (value.compareTo(max) > 0) {
                    validatable.error(String.format("O valor está fora da faixa de concentração: %s", faixa));
                }
            }
        });

        addFieldListOfAttachment("formulas", "formula")
                .asAtr()
                .label("Fórmula do produto");


        STypeString nomeComercial = addFieldString("nomeComercial");

        nomeComercial
                .asAtr()
                .required()
                .label("Nome do medicamento");

        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        STypeAcondicionamento                               acondicionamento  = acondicionamentos.getElementsType();
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

        final STypeList<STypeEnsaioControleQualidade, SIComposite> ensaios = addFieldListOf("ensaiosControleQualidade", STypeEnsaioControleQualidade.class);
        final STypeEnsaioControleQualidade                         ensaio  = ensaios.getElementsType();
        ensaios
                .withView(new SViewListByMasterDetail()
                        .col(ensaio.descricaoTipoEnsaio, "Ensaio")
                        .col(ensaio.descricaoTipoReferencia, "Tipo de referência")
                        .disableNew().disableDelete())
                .asAtr().label("Ensaio de Controle de Qualidade");


        withInitListener(ins -> {
            final Optional<SIList<SIComposite>> lista = ins.findNearest(ensaios);

            for (STypeEnsaioControleQualidade.TipoEnsaioControleQualidade tipoEnsaioControleQualidade : STypeEnsaioControleQualidade.TipoEnsaioControleQualidade.values()) {
                final SIComposite siComposite = lista.get().addNew();
                siComposite.findNearest(ensaio.idTipoEnsaio).get().setValue(tipoEnsaioControleQualidade.getId());
                siComposite.findNearest(ensaio.descricaoTipoEnsaio).get().setValue(tipoEnsaioControleQualidade.getDescricao());
            }
        });

    }


}
