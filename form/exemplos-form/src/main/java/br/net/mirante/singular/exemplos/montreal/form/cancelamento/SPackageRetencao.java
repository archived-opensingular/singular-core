/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.montreal.form.cancelamento;

import java.util.Date;

import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDateTime;
import br.net.mirante.singular.form.mform.core.STypeString;

public class SPackageRetencao extends SPackage {

    public static final String PACOTE        = "mform.montreal.atendimento";
    public static final String TIPO          = "Retencao";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackageRetencao() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<SIComposite> retencao = pb.createCompositeType(TIPO);
        retencao.asAtr().displayString("Solicitação de cancelamento de ${associado.nome} (<#list cancelamentos as c>${c.titulo.numero}<#sep>, </#sep></#list>) ");
        retencao.asAtr().label("Cancelamento de título");

        final STypeComposite<SIComposite> associado = retencao.addFieldComposite("associado");
        associado.asAtr().label("Associado");

        {
            final STypeString nome = associado.addFieldString("nome");
            nome.asAtr().label("Associado").enabled(false);

            final STypeDate dataInclusao = associado.addFieldDate("dataInclusao");
            dataInclusao.asAtr().label("Data de inclusão").enabled(false);

            retencao.withInitListener(i -> {
                i.findNearest(nome).ifPresent(n -> n.setValue("Juracy Abrantes Júnior"));
                i.findNearest(dataInclusao).ifPresent(d -> d.setValue(new Date()));
            });

        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> cancelamentos = retencao.addFieldListOfComposite("cancelamentos", "retencao");
        cancelamentos.asAtr().label("Cancelamentos");
        final STypeComposite<SIComposite> cancelamento = cancelamentos.getElementsType();

        {
            final STypeComposite<SIComposite> titulo = cancelamento.addFieldComposite("titulo");
            titulo.asAtr().label("Dados do Título");
            final STypeString numero = titulo.addFieldString("numero");
            numero.asAtr().label("Número");
            final STypeString situacao = titulo.addFieldString("situacao");
            situacao.asAtr().label("Situção");

            final STypeComposite<SIComposite> dadosCancelamento = cancelamento.addFieldComposite("dadosCancelamento");
            dadosCancelamento.asAtr().label("Dados do Cancelamento");
            final STypeString observacoes = dadosCancelamento.addFieldString("observacoes");
            observacoes.asAtr().label("Obsevações");
            final STypeString formaContato = dadosCancelamento.addFieldString("formaContato");
            formaContato.asAtr().label("Forma de contato");
            final STypeDateTime dataInclusao = dadosCancelamento.addFieldDateTime("dataInclusao");
            dataInclusao.asAtr().label("Data de inclusão");
            final STypeString atendente = dadosCancelamento.addFieldString("atendente");
            atendente.asAtr().label("Atendente");

            cancelamentos
                    .withView(new SViewListByMasterDetail()
                            .col(numero, "Número do título")
                            .col(observacoes)
                            .col(atendente)
                    );
        }



    }

    public static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }
}
