/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypePDIProjetoPedagogico extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addPerfilCurso();
        addAtividadesCurso();
        addPerfilEgresso();
        addFormaAcessoCurso();
        addRepresentacaoGraficaPerfilFormacao();
        addSistemaAvaliacaoProcessoEnsinoAprendizagem();
        addSistemaAvaliacaoProjetoCurso();
        addTrabalhoConclusaoCurso();
        addAtoAutorizativoCriacao();
        addOutrosProjetosPedagogicosCurso();
        
        // cria um bloco por campo
        setView(SViewByBlock::new).newBlockPerType(getFieldsLocal());
    }

    private void addPerfilCurso() {
        final STypeComposite<SIComposite> perfilCurso = this.addFieldComposite("perfilCurso");
        perfilCurso.asAtr().label("12 Perfil do Curso");
        perfilCurso.addFieldString("justificativaOfertaCurso", true)
            .withTextAreaView().asAtr().label("Justificativa da Oferta do Curso")
            .asAtrBootstrap().maxColPreference();
    }

    private void addAtividadesCurso() {
        final STypeComposite<SIComposite> atividadesCurso = this.addFieldComposite("atividadesCurso");
        atividadesCurso.asAtr().label("13 Atividades do Curso");
        atividadesCurso.addFieldString("atividadesComplementares")
            .withTextAreaView().asAtr().label("Atividades Complementares")
            .asAtrBootstrap().maxColPreference();
    }

    private void addPerfilEgresso() {
        this.addFieldString("perfilEgresso", true)
            .withTextAreaView().asAtr().label("14 Perfil do Egresso")
            .asAtrBootstrap().maxColPreference();
    }

    private void addFormaAcessoCurso() {
        this.addFieldString("formaAcessoCurso", true)
            .withTextAreaView().asAtr().label("15 Forma de Acesso ao Curso")
            .asAtrBootstrap().maxColPreference();
    }

    private void addRepresentacaoGraficaPerfilFormacao() {
        this.addFieldAttachment("representacaoGraficaPerfilFormacao")
            .asAtr().label("16 Representação Gráfica de um perfil de formação");
    }
    
    private void addSistemaAvaliacaoProcessoEnsinoAprendizagem() {
        this.addFieldString("sistemaAvaliacaoProcessoEnsinoAprendizagem", true)
            .withTextAreaView().asAtr().label("17 Sistema de Avaliação do processo de ensino e aprendizagem")
            .asAtrBootstrap().maxColPreference();
    }

    private void addSistemaAvaliacaoProjetoCurso() {
        this.addFieldString("sistemaAvaliacaoProjetoCurso", true)
            .withTextAreaView().asAtr().label("18 Sistema de Avaliação do Projeto de Curso")
            .asAtrBootstrap().maxColPreference();
    }
    
    private void addTrabalhoConclusaoCurso() {
        final STypeComposite<SIComposite> trabalhoConclusaoCurso = this.addFieldComposite("trabalhoConclusaoCurso");
        trabalhoConclusaoCurso.asAtr().label("19 Atividades de Conclusão de Curso (TCC)");
        trabalhoConclusaoCurso.addFieldString("atividadesConclusaoCurso")
            .withTextAreaView().asAtr().label("Atividades de Conslusão de Curso")
            .asAtrBootstrap().maxColPreference();
    }
    
    private void addAtoAutorizativoCriacao() {
        final STypeComposite<SIComposite> atoAutorizativoCriacao = this.addFieldComposite("atoAutorizativoCriacao");
        atoAutorizativoCriacao.asAtr().label("20 Ato autorizativo anterior ou ato de criação");
        atoAutorizativoCriacao.addFieldString("tipoDocumento", true)
            .withRadioView().selectionOf("Ata", "Decreto", "Decreto-lei", "Lei", "Medida Provisória", "Parecer", "Portaria", "Resolução")
            .asAtr().label("Tipo de Documento")
            .asAtrBootstrap().maxColPreference();
        atoAutorizativoCriacao.addFieldInteger("numeroDocumento", true)
            .asAtr().label("Nº do Documento")
            .asAtrBootstrap().colPreference(3);
        atoAutorizativoCriacao.addFieldDate("dataDocumento", true)
            .asAtr().label("Data do Documento")
            .asAtrBootstrap().colPreference(3);
        atoAutorizativoCriacao.addFieldDate("dataPublicacao", true)
            .asAtr().label("Data de Publicação")
            .asAtrBootstrap().colPreference(3);
        atoAutorizativoCriacao.addFieldDate("dataCriacaoCurso")
            .asAtr().label("Data de Criação do Curso")
            .asAtrBootstrap().colPreference(3);
        atoAutorizativoCriacao.addFieldAttachment("atoAutorizativoAnterior").asAtr().label("Ato autorizativo anterior");
    }

    private void addOutrosProjetosPedagogicosCurso() {
        final STypeComposite<SIComposite> outrosProjetosPedagogicosCurso = this.addFieldComposite("outrosProjetosPedagogicosCurso");
        outrosProjetosPedagogicosCurso.asAtr().label("21 Outros Projetos Pedagógicos de Curso");
        outrosProjetosPedagogicosCurso.addFieldAttachment("informacoesOutrosProjetosPedagogicosCurso")
            .asAtr().label("Informações sobre outros Projetos Pedagógicos de Curso");
    }
}
