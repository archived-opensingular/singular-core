/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.montreal.form.cancelamento;

import br.net.mirante.singular.exemplos.montreal.domain.Titulo;
import br.net.mirante.singular.exemplos.montreal.service.DominioMontrealService;
import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeDate;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewTextArea;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SPackageRetencao extends SPackage {

    public static final String PACOTE = "mform.montreal.atendimento";
    public static final String TIPO = "Retencao";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    public SPackageRetencao() {
        super(PACOTE);
    }

    public static DominioMontrealService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioMontrealService.class);
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
                i.findNearest(nome).ifPresent(n -> n.setValue(" Romeu Ambrósio"));
                i.findNearest(dataInclusao).ifPresent(d -> d.setValue(new Date()));
            });

        }

        final STypeList<STypeComposite<SIComposite>, SIComposite> cancelamentos = retencao.addFieldListOfComposite("cancelamentos", "cancelamento");
        cancelamentos.withMiniumSizeOf(1).asAtr().label("Cancelamentos");

        final STypeComposite<SIComposite> cancelamento = cancelamentos.getElementsType();

        {

            final STypeList<STypeComposite<SIComposite>, SIComposite> titulos = cancelamento.addFieldListOfComposite("titulos", "titulo");
            titulos.asAtr().label("Títulos").required();
            titulos.asAtrBootstrap().colPreference(6);
            final STypeComposite<SIComposite> titulo = titulos.getElementsType();

            final STypeString id = titulo.addFieldString("id");
            final STypeString numero = titulo.addFieldString("numero");
            final STypeString situacao = titulo.addFieldString("situacao");

            titulos.selection()
                    .id(id)
                    .display("${numero} - ${situacao}")
                    .simpleProvider(builder -> {
                        for (Titulo t : dominioService(builder.getCurrentInstance()).titulos()) {
                            builder.add()
                                    .set(id, t.getId())
                                    .set(numero, t.getNumero())
                                    .set(situacao, t.getSituacao())
                            ;
                        }
                    });
            titulos.setView(SMultiSelectionByPicklistView::new);


            final STypeList<STypeString, SIString> motivosCancelamento = cancelamento.addFieldListOf("motivosCancelamento", STypeString.class);
            motivosCancelamento
                    .selectionOf(getOpcoesCentralReserva())
                    .asAtr().label("Motivos do cancelamento").required()
                    .asAtrBootstrap().colPreference(4);
            motivosCancelamento.setView(SMultiSelectionByPicklistView::new);



            final STypeString observacoes = cancelamento.addFieldString("observacoes");
            observacoes
                    .asAtr().label("Obsevações").tamanhoMaximo(1000)
                    .getTipo().withView(SViewTextArea::new);

//        final STypeDate dataInclusao = dadosCancelamento.addFieldDate("dataInclusao");
//        dataInclusao.asAtr().label("Data de inclusão").required()
//                .asAtrBootstrap().colPreference(3);
//        final STypeString atendente = dadosCancelamento.addFieldString("atendente");
//        atendente.asAtr().label("Atendente").required()
//                .asAtrBootstrap().colPreference(3);

            cancelamentos.withView(SViewListByForm::new);

        }
        final STypeString formaContato = retencao.addFieldString("formaContato");
        formaContato.asAtr().label("Forma de contato").required()
                .asAtrBootstrap().colPreference(3);

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
        return new String[]{
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
