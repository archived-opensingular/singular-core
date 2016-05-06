/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.montreal.form.cancelamento;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.net.mirante.singular.exemplos.montreal.domain.Titulo;
import br.net.mirante.singular.exemplos.montreal.service.DominioMontrealService;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewTextArea;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDateTime;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.SSimpleProvider;

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
            final STypeInteger id = associado.addFieldInteger("id");
            id.asAtr().visible(false);

            final STypeString nome = associado.addFieldString("nome");
            nome
                    .asAtr().label("Associado").enabled(false)
                    .asAtrBootstrap().colPreference(2);

            final STypeDate dataInclusao = associado.addFieldDate("dataInclusao");
            dataInclusao
                    .asAtr().label("Data de inclusão").enabled(false)
                    .asAtrBootstrap().colPreference(2);

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

            final STypeString id = titulo.addFieldString("id");
            final STypeString numero = titulo.addFieldString("numero");
            final STypeString situacao = titulo.addFieldString("situacao");
//            numero.asAtr().label("Número")
//                    .asAtrBootstrap().colPreference(2);

            titulo.setView(SViewAutoComplete::new);
            titulo.selection()
                    .id(id)
                    .display("${numero} - ${situacao}")
                    .simpleProvider((SSimpleProvider) builder -> {
                        for (Titulo t : dominioService(builder.getCurrentInstance()).titulos()) {
                            builder.add()
                                    .set(id, t.getId())
                                    .set(numero, t.getNumero())
                                    .set(situacao, t.getSituacao())
                            ;
                        }
                    });

            final STypeComposite<SIComposite> dadosCancelamento = cancelamento.addFieldComposite("dadosCancelamento");
            final STypeString centralReservas = dadosCancelamento.addFieldString("centralReservas");
            centralReservas
                    .withSelectView()
                    .selectionOf(getOpcoesCentralReserva())
                    .asAtr().label("Central de Reservas")
                    .asAtrBootstrap().colPreference(4);

            final STypeString observacoes = dadosCancelamento.addFieldString("observacoes");
            observacoes
                    .asAtr().label("Obsevações").tamanhoMaximo(1000)
                    .getTipo().withView(SViewTextArea::new);
            final STypeString formaContato = dadosCancelamento.addFieldString("formaContato");
            formaContato.asAtr().label("Forma de contato")
                    .asAtrBootstrap().colPreference(2);
            final STypeDateTime dataInclusao = dadosCancelamento.addFieldDateTime("dataInclusao");
            dataInclusao.asAtr().label("Data de inclusão")
                    .asAtrBootstrap().colPreference(2);
            final STypeString atendente = dadosCancelamento.addFieldString("atendente");
            atendente.asAtr().label("Atendente")
                    .asAtrBootstrap().colPreference(4);

            cancelamentos
                    .withView(new SViewListByMasterDetail()
                            .col(numero, "Número do título")
                            .col(observacoes)
                            .col(atendente)
                    );
        }

    }

    public static DominioMontrealService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioMontrealService.class);
    }

    public List<Titulo> titulos() {
        return Arrays.asList(
                new Titulo(1L, "167.981"),
                new Titulo(2L, "167.982"),
                new Titulo(3L, "167.983"),
                new Titulo(4L, "167.984"),
                new Titulo(5L, "167.985")
        );
    }

    public String[] getOpcoesCentralReserva() {
        return new String[] {
                "01. Não conseguiu vaga no hotel desejado",
                "02. Não conseguiu vaga nos hotéis conveniados",
                "03. Hotéis lotados no primeiro dia de marcação",
                "04. Sistema Telefônico deficiente",
                "05. Cobrança de no-show por perda de prazo",
                "06. Cobrança d eno-show por falha funcional",
                "07. Dificuldade recorrente na marcação de reserva",
                "08. Erro na marcação de reserva: falha funcional"
        };
    }
}
