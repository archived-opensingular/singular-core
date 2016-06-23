/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.view.SViewByBlock;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypePDI extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addPerfilInstitucional();
        addProjetoPedagogico();
        addImplantacaoInstituicao();
        addOrganizacaoDidaticopedagogica();
        addPerfilCorpoDocente();
        addOrganizacaoAdministrativa();
        addInfraestruturaInstalacoesAcademicas();
        addAtendimentoPessoasNecessidadesEspeciais();
        addAtoAutorizativoCriacao();
        addDemonstrativoCapacidadeSustentabilidadeFinanceira();
        addOutros();
        
        // cria um bloco por campo
        setView(SViewByBlock::new)
            .newBlock("1 Perfil Institucional").add("perfilInstitucional")
            .newBlock("2 Projeto Pedagógico da Instituição").add("projetoPedagogicoInstituicao")
            .newBlock("3 Implantação de Desenvolvimento da Instituição - Programa de Abertura de Cursos de Pós Graduação").add("implantacaoInstituicao")
            .newBlock("4 Organização didatico-pedagógica da Instituição").add("organizacaoDidaticopedagogicaInstituicao")
            .newBlock("5 Perfil do corpo docente e técnico-administrativo").add("perfilCorpoDocenteETecnicoAdministrativo")
            .newBlock("6 Organização Administrativa da Instituição").add("organizacaoAdministrativa")
            .newBlock("7 Infra-estrutura e Instalações Acadêmicas").add("infraestruturaInstalacoesAcademicas")
            .newBlock("8 Atendimento de Pessoas com Necessidades Especiais").add("atendimentoPessoasNecessidadesEspeciais")
            .newBlock("9 Ato autorizativo anterior ou ato de criação").add("atoAutorizativoCriacao")
            .newBlock("10 Demonstrativo de Capacidade e Sustentabilidade Financeira").add("demonstrativoCapacidadeSustentabilidadeFinanceira")
            .newBlock("11 Outros").add("outros");
    }
    
    private void addPerfilInstitucional() {
        final STypeComposite<SIComposite> perfilInstitucional = this.addFieldComposite("perfilInstitucional");
        perfilInstitucional.addFieldInteger("anoInicioPDI", true).asAtr().label("Ano de Início do PDI");
        perfilInstitucional.addFieldInteger("anoFimPDI", true).asAtr().label("Ano de Fim do PDI");
        perfilInstitucional.addFieldString("historicoDesenvolvimentoInstituicao", true)
            .withTextAreaView().asAtr().label("Histórico e desenvolvimento da Instituição de Ensino");
        perfilInstitucional.addFieldString("missaoObjetivosMetas", true)
            .withTextAreaView().asAtr().label("Missão, objetivos e metas da Instituição, na sua área de atuação");
    }
    
    private void addProjetoPedagogico() {
        this.addFieldString("projetoPedagogicoInstituicao", true)
            .withTextAreaView()
            .asAtrBootstrap().maxColPreference();
    }

    private void addImplantacaoInstituicao() {
        final STypeComposite<SIComposite> implantacaoInstituicao = this.addFieldComposite("implantacaoInstituicao");
        final STypeList<STypeComposite<SIComposite>, SIComposite> cursosPrevistos = implantacaoInstituicao.addFieldListOfComposite("cursosPrevistos", "curso");
        cursosPrevistos.withView(SViewListByMasterDetail::new)
            .asAtr().label("Cursos Previstos").itemLabel("Curso Previsto");
        cursosPrevistos.getElementsType()
            .addFieldString("nome").asAtr().required().label("Nome");
    }
    
    private void addOrganizacaoDidaticopedagogica() {
        this.addFieldString("organizacaoDidaticopedagogicaInstituicao", true)
            .withTextAreaView().asAtr().label("Organização didatico-pedagógica da Instituição")
            .asAtrBootstrap().maxColPreference();
    }

    private void addPerfilCorpoDocente() {
        final STypeComposite<SIComposite> perfilCorpoDocenteETecnicoAdministrativo = this.addFieldComposite("perfilCorpoDocenteETecnicoAdministrativo");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("corpoTecnicoAdministrativo", true)
            .withTextAreaView().asAtr().label("Corpo técnico-administrativo");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("cronogramaExpansaoCorpoTecnicoAdministrativo", true)
            .withTextAreaView().asAtr().label("Crongrama de expansão do corpo técnico-administrativo");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("cronogramaExpansaoCorpoDocente", true)
            .withTextAreaView().asAtr().label("Crongrama de expansão do corpo docente");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("criteriosSelecaoContratacaoProfessores", true)
            .withTextAreaView().asAtr().label("Critérios de seleção e contratação dos professores");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("politicasQualificacaoPlanoCarreira", true)
            .withTextAreaView().asAtr().label("Políticas de qualificação e plano de carreira do corpo docente");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("requisitosTitulacaoExperienciaProfissional", true)
            .withTextAreaView().asAtr().label("Requisitos de titulação e experiência profissional do corpo docente");
        perfilCorpoDocenteETecnicoAdministrativo.addFieldString("regimeTrabalhoProcedimentosSubstituicaoEventualProfessores", true)
            .withTextAreaView().asAtr().label("Regime de trabalho e procedimentos de substituição eventual de professores");
    }

    private void addOrganizacaoAdministrativa() {
        final STypeComposite<SIComposite> organizacaoAdministrativa = this.addFieldComposite("organizacaoAdministrativa");
        organizacaoAdministrativa.addFieldString("estruturaOrganizacionalIES", true)
            .withTextAreaView().asAtr().label("Estrutura OrganizacionalIES");
        organizacaoAdministrativa.addFieldString("procedimentosAtendimentosAlunos", true)
            .withTextAreaView().asAtr().label("Procedimentos de atendimento dos alunos");
        organizacaoAdministrativa.addFieldString("procedimentoAutoavaliacaoInstitucional", true)
            .withTextAreaView().asAtr().label("Procedimento de auto-avaliação Institucional");
    }
    
    private void addInfraestruturaInstalacoesAcademicas() {
        final STypeComposite<SIComposite> infraestruturaInstalacoesAcademicas = this.addFieldComposite("infraestruturaInstalacoesAcademicas");
        
        final STypeList<STypeComposite<SIComposite>, SIComposite> enderecoes = infraestruturaInstalacoesAcademicas.addFieldListOfComposite("enderecoes", "encereco");
        enderecoes.withView(SViewListByMasterDetail::new)
            .asAtr().label("Endereços").itemLabel("Endereço");
        final STypeComposite<SIComposite> endereco = enderecoes.getElementsType();
        endereco.addFieldString("endereco").asAtr().required().label("Endereço");
        endereco.addField("cep", STypeCEP.class);
    }
    
    private void addAtendimentoPessoasNecessidadesEspeciais() {
        this.addFieldString("atendimentoPessoasNecessidadesEspeciais", true)
            .withTextAreaView().asAtr().label("Plano de promoção de acessibilidade e atendimento prioritário, imediato e diferenciado para utilização, "
                + "com segurança e autonomia, total ou assistida, dos espaços, mobiliários e equipamentos urbanos, das edificações, dos serviços de transporte, dos dispositivos, "
                + "sistemas e meios de comunicação e informação, serviços de tradutor e intérprete de Língua Brasileira de Sinais - LIBRAS")
            .asAtrBootstrap().maxColPreference();
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
        atoAutorizativoCriacao.addFieldDate("dataCriacao", true)
            .asAtr().label("Data de Criação")
            .asAtrBootstrap().colPreference(3);
        atoAutorizativoCriacao.addFieldAttachment("atoAutorizativoAnterior").asAtr().label("Ato autorizativo anterior");
    }
    
    private void addDemonstrativoCapacidadeSustentabilidadeFinanceira() {
        final STypeComposite<SIComposite> demonstrativoCapacidadeSustentabilidadeFinanceira = this.addFieldComposite("demonstrativoCapacidadeSustentabilidadeFinanceira");
        
        final STypeList<STypeComposite<SIComposite>, SIComposite> demonstrativos = demonstrativoCapacidadeSustentabilidadeFinanceira.addFieldListOfComposite("demonstrativos", "demonstrativo");
        demonstrativos.withView(SViewListByMasterDetail::new)
            .asAtr().label("Demonstrativos").itemLabel("Demonstrativo");
        final STypeComposite<SIComposite> demonstrativo = demonstrativos.getElementsType();
        demonstrativo.addFieldInteger("ano", true).asAtr().label("Ano");
        demonstrativo.addFieldDecimal("receitas", true).asAtr().label("Receitas");
        demonstrativo.addFieldDecimal("despesas", true).asAtr().label("Despesas");
    }
    
    private void addOutros() {
        this.addFieldString("outros", true)
            .withTextAreaView()
            .asAtrBootstrap().maxColPreference();
    }
    
}
