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
        
        this.asAtr().label("Projeto Pedagógico");
        
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
        perfilCurso.asAtr().label("Perfil do Curso");
        perfilCurso.addFieldString("justificativaOfertaCurso", true)
            .withTextAreaView().asAtr().label("Justificativa da Oferta do Curso")
            .asAtrBootstrap().colPreference(12);
    }

    private void addAtividadesCurso() {
        final STypeComposite<SIComposite> atividadesCurso = this.addFieldComposite("atividadesCurso");
        atividadesCurso.asAtr().label("Atividades do Curso");
        atividadesCurso.addFieldString("atividadesComplementares")
            .withTextAreaView().asAtr().label("Atividades Complementares")
            .asAtrBootstrap().colPreference(12);
    }

    private void addPerfilEgresso() {
        this.addFieldString("perfilEgresso", true)
            .withTextAreaView().asAtr().label("Perfil do Egresso")
            .asAtrBootstrap().colPreference(12);
    }

    private void addFormaAcessoCurso() {
        this.addFieldString("formaAcessoCurso", true)
            .withTextAreaView().asAtr().label("Forma de Acesso ao Curso")
            .asAtrBootstrap().colPreference(12);
    }

    private void addRepresentacaoGraficaPerfilFormacao() {
        this.addFieldAttachment("representacaoGraficaPerfilFormacao")
            .asAtr().label("Representação Gráfica de um perfil de formação");
    }
    
    private void addSistemaAvaliacaoProcessoEnsinoAprendizagem() {
        this.addFieldString("sistemaAvaliacaoProcessoEnsinoAprendizagem", true)
            .withTextAreaView().asAtr().label("Sistema de Avaliação do processo de ensino e aprendizagem")
            .asAtrBootstrap().colPreference(12);
    }

    private void addSistemaAvaliacaoProjetoCurso() {
        this.addFieldString("sistemaAvaliacaoProjetoCurso", true)
            .withTextAreaView().asAtr().label("Sistema de Avaliação do Projeto de Curso")
            .asAtrBootstrap().colPreference(12);
    }
    
    private void addTrabalhoConclusaoCurso() {
        final STypeComposite<SIComposite> trabalhoConclusaoCurso = this.addFieldComposite("trabalhoConclusaoCurso");
        trabalhoConclusaoCurso.asAtr().label("Atividades de Conclusão de Curso (TCC)");
        trabalhoConclusaoCurso.addFieldString("atividadesConclusaoCurso")
            .withTextAreaView().asAtr().label("Atividades de Conslusão de Curso")
            .asAtrBootstrap().colPreference(12);
    }
    
    private void addAtoAutorizativoCriacao() {
        final STypeComposite<SIComposite> atoAutorizativoCriacao = this.addFieldComposite("atoAutorizativoCriacao");
        atoAutorizativoCriacao.asAtr().label("Ato autorizativo anterior ou ato de criação");
        atoAutorizativoCriacao.addFieldString("tipoDocumento", true)
            .withRadioView().selectionOf("Ata", "Decreto", "Decreto-lei", "Lei", "Medida Provisória", "Parecer", "Portaria", "Resolução")
            .asAtr().label("Tipo de Documento")
            .asAtrBootstrap().colPreference(12);
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
        outrosProjetosPedagogicosCurso.asAtr().label("Outros Projetos Pedagógicos de Curso");
        outrosProjetosPedagogicosCurso.addFieldAttachment("informacoesOutrosProjetosPedagogicosCurso")
            .asAtr().label("Informações sobre outros Projetos Pedagógicos de Curso");
    }
}
