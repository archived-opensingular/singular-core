/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.internal.xml.ConversorToolkit;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypePDI extends STypeComposite<SIComposite>{

    private static final List<String> TIPOS_RECEITA = Arrays.asList(
        "Anuidade / Mensalidade(+)", "Bolsas(-)", "Diversos(+)", 
        "Financiamentos(+)", "Inadimplência(-)", "Serviços(+)", "Taxas(+)");
    private static final List<String> TIPOS_DESPESA = Arrays.asList(
        "Acervo Bibliográfico(-)", "Aluguel(-)", "Despesas Administrativas(-)", 
        "Encargos(-)", "Equipamentos(-)", "Eventos(-)", 
        "Investimento (compra de imóvel)(-)", "Manutenção(-)", "Mobiliário(-)",
        "Pagamento Pessoal Administrativo(-)", "Pagamento Professores(-)",
        "Pesquisa e Extensão(-)", "Treinamento(-)");

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
        implantacaoInstituicao.addFieldListOf("cursosPrevistos", STypeCurso.class)
            .withView(SViewListByMasterDetail::new)
            .asAtr().label("Cursos Previstos").itemLabel("Curso Previsto");
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
        final STypeComposite<SIComposite> demonstrativo = demonstrativos.getElementsType();
        
        final STypeInteger ano = demonstrativo.addFieldInteger("ano");
        ano.asAtr().label("Demonstrativo Financeiro").enabled(false);
        
        final STypeList<STypeComposite<SIComposite>, SIComposite> receitas = demonstrativo.addFieldListOfComposite("receitas", "receita");
        receitas.setView(SViewListByTable::new).disableNew().disableDelete();
        receitas.asAtr().label("Receitas");
        receitas.getElementsType().addFieldString("tipo").asAtr().enabled(false);
        receitas.getElementsType().addFieldMonetary("valor");

        final STypeList<STypeComposite<SIComposite>, SIComposite> despesas = demonstrativo.addFieldListOfComposite("despesas", "despesa");
        despesas.setView(SViewListByTable::new).disableNew().disableDelete();
        despesas.asAtr().label("Despesas");
        despesas.getElementsType().addFieldString("tipo").asAtr().enabled(false);
        despesas.getElementsType().addFieldMonetary("valor");
        
        demonstrativoCapacidadeSustentabilidadeFinanceira.withInitListener(ins -> {
            final Optional<SIList<SIComposite>> lista = ins.findNearest(demonstrativos);
            for (int i = 0; i < 5; i++) {
                final SIComposite siComposite = lista.get().addNew();
                siComposite.findNearest(ano).get().setValue(LocalDate.now().getYear() + i);
                final SIList<SIComposite> receitas_ = siComposite.findNearest(receitas).get();
                final SIList<SIComposite> despesas_ = siComposite.findNearest(despesas).get();
                TIPOS_RECEITA.stream().forEach(tipo -> receitas_.addNew().setValue("tipo", tipo));
                TIPOS_DESPESA.stream().forEach(tipo -> despesas_.addNew().setValue("tipo", tipo));
            }
        });
        
        demonstrativos.withView(
            new SViewListByMasterDetail()
                .col(ano)
                .col("Receitas", calcularTotal("receitas"))
                .col("Despesas", calcularTotal("despesas"))
                .col("Total Geral", calcularTotal())
                .disableNew().disableDelete());
    }
    
    @SuppressWarnings("unchecked")
    private static IFunction<SInstance, String> calcularTotal(){
        return ins -> {
            BigDecimal total = BigDecimal.ZERO;
            SIList<SIComposite> lista = (SIList<SIComposite>) ((SIComposite)ins).getField("receitas");
            if(lista != null){
                total = lista.stream().map(siComposite -> ((BigDecimal)Value.of(siComposite, "valor")))
                    .filter(Objects::nonNull)
                    .reduce(total, BigDecimal::add);
            }
            lista = (SIList<SIComposite>) ((SIComposite)ins).getField("despesas");
            if(lista != null){
                total = lista.stream().map(siComposite -> ((BigDecimal)Value.of(siComposite, "valor")))
                    .filter(Objects::nonNull)
                    .reduce(total, BigDecimal::subtract);
            }
            
            return ConversorToolkit.printNumber(total, 2);
        };
    }
    
    @SuppressWarnings("unchecked")
    private static IFunction<SInstance, String> calcularTotal(String nomeLista){
        return ins -> {
            BigDecimal total = BigDecimal.ZERO;
            final SIList<SIComposite> lista = (SIList<SIComposite>) ((SIComposite)ins).getField(nomeLista);
            if(lista != null){
                total = lista.stream().map(siComposite -> (BigDecimal)Value.of(siComposite, "valor"))
                    .filter(Objects::nonNull)
                    .reduce(total, BigDecimal::add);
            }
            return ConversorToolkit.printNumber(total, 2);
        };
    }
    
    private void addOutros() {
        this.addFieldString("outros", true)
            .withTextAreaView()
            .asAtrBootstrap().maxColPreference();
    }
    
}
