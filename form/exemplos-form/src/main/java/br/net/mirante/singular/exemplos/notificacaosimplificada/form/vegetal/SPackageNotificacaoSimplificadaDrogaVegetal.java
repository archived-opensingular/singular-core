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
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeString;

import org.apache.commons.lang3.tuple.Pair;

@SInfoType(spackage = SPackageNotificacaoSimplificadaDrogaVegetal.class)
public class SPackageNotificacaoSimplificadaDrogaVegetal extends SPackage {

    public static final String PACOTE        = "mform.peticao.notificacaosimplificada.vegetal";
    public static final String TIPO          = "MedicamentoDrogaVegetal";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    public SPackageNotificacaoSimplificadaDrogaVegetal() {
        super(PACOTE);
    }


    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.getDictionary().loadPackage(SPackageNotificacaoSimplificada.class);

        final STypeComposite<?> notificacaoSimplificada = pb.createCompositeType(TIPO);
        notificacaoSimplificada.asAtrBasic().displayString("${nomenclaturaBotanica.descricao} - ${concentracao.descricao}");
        notificacaoSimplificada.asAtrBasic().label("Notificação Simplificada - Droga Vegetal");

        final STypeComposite<?> nomenclaturaBotanica     = notificacaoSimplificada.addFieldComposite("nomenclaturaBotanica");
        SType<?>                idNomenclaturaBotanica   = nomenclaturaBotanica.addFieldInteger("id");
        STypeSimple             descNomenclaturaBotanica = nomenclaturaBotanica.addFieldString("descricao");
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

        final STypeComposite<?> concentracao     = notificacaoSimplificada.addFieldComposite("concentracao");
        SType<?>                idConcentracao   = concentracao.addFieldInteger("id");
        STypeSimple             descConcentracao = concentracao.addFieldString("descricao");
        concentracao
                .asAtrBasic()
                .required()
                .label("Concentração/Unidade de medida")
                .asAtrBootstrap()
                .colPreference(4);
        concentracao
                .withSelectView()
                .withSelectionFromProvider(descConcentracao, (ins, filter) -> {
                    final SIList<?> list = ins.getType().newList();
                    for (Pair p : dominioService(ins).concentracao(filter)) {
                        final SIComposite c = (SIComposite) list.addNew();
                        c.setValue(idConcentracao, p.getLeft());
                        c.setValue(descConcentracao, p.getRight());
                    }
                    return list;
                });

        STypeList<STypeComposite<SIComposite>, SIComposite> nomePopulares = notificacaoSimplificada.addFieldListOfComposite("nomesPopulares", "nomePopular");
        STypeComposite<SIComposite> nomePopular = nomePopulares.getElementsType();
        STypeString nome = nomePopular.addFieldString("nome");
        nome.asAtrBasic().label("Nome Popular");

        nomePopulares.asAtrBasic().label("Nome Popular");

        final STypeList<STypeAcondicionamento, SIComposite> acondicionamentos = notificacaoSimplificada.addFieldListOf("acondicionamentos", STypeAcondicionamento.class);
        STypeAcondicionamento acondicionamento = acondicionamentos.getElementsType();
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamento.embalagemPrimaria.descricao, "Embalagem primária")
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
                .withView(SViewListByMasterDetail::new)
                .asAtrBasic().label("Ensaio de Controle de Qualidade");

    }

}

