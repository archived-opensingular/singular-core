/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewByBlock;

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
        setView(SViewByBlock::new)
            .newBlock("12 Justificativa da Oferta do Curso").add("justificativaOfertaCurso")
            .newBlock("13 Atividades do Curso").add("atividadesComplementares")
            .newBlock("14 Perfil do Egresso").add("perfilEgresso")
            .newBlock("15 Forma de Acesso ao Curso").add("formaAcessoCurso")
            .newBlock("16 Representação Gráfica de um perfil de formação").add("representacaoGraficaPerfilFormacao")
            .newBlock("17 Sistema de Avaliação do processo de ensino e aprendizagem").add("sistemaAvaliacaoProcessoEnsinoAprendizagem")
            .newBlock("18 Sistema de Avaliação do Projeto de Curso").add("sistemaAvaliacaoProjetoCurso")
            .newBlock("19 Atividades de Conclusão de Curso (TCC)").add("atividadesConclusaoCurso")
            .newBlock("20 Ato autorizativo anterior ou ato de criação").add("atoAutorizativoCriacao")
            .newBlock("21 Outros Projetos Pedagógicos de Curso").add("informacoesOutrosProjetosPedagogicosCurso");
    }

    private void addPerfilCurso() {
        this.addFieldString("justificativaOfertaCurso", true)
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }

    private void addAtividadesCurso() {
        this.addFieldString("atividadesComplementares")
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }

    private void addPerfilEgresso() {
        this.addFieldString("perfilEgresso", true)
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }

    private void addFormaAcessoCurso() {
        this.addFieldString("formaAcessoCurso", true)
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }

    private void addRepresentacaoGraficaPerfilFormacao() {
        this.addFieldAttachment("representacaoGraficaPerfilFormacao");
    }
    
    private void addSistemaAvaliacaoProcessoEnsinoAprendizagem() {
        this.addFieldString("sistemaAvaliacaoProcessoEnsinoAprendizagem", true)
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }

    private void addSistemaAvaliacaoProjetoCurso() {
        this.addFieldString("sistemaAvaliacaoProjetoCurso", true)
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }
    
    private void addTrabalhoConclusaoCurso() {
        this.addFieldString("atividadesConclusaoCurso")
            .withTextAreaView().asAtrBootstrap().maxColPreference();
    }
    
    private void addAtoAutorizativoCriacao() {
        final STypeComposite<SIComposite> atoAutorizativoCriacao = this.addFieldComposite("atoAutorizativoCriacao");
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
        this.addFieldAttachment("informacoesOutrosProjetosPedagogicosCurso")
            .asAtr().label("Informações sobre outros Projetos Pedagógicos de Curso");
    }
}
