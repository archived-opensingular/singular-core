package br.net.mirante.singular.showcase.component.input.core.search;


import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioProvider implements FilteredPagedProvider<Funcionario> {

    private List<Funcionario> funcionarios;

    {
        funcionarios = new ArrayList<>();
        funcionarios.add(new Funcionario("João Vitor Mota", "Analista de Sistemas", 30));
        funcionarios.add(new Funcionario("Alexandre Guimarães", "Analista de Requisitos", 27));
        funcionarios.add(new Funcionario("Romario De Paula", "Analista de Testes", 22));
        funcionarios.add(new Funcionario("Alexandra Leite", "Gerente de Projetos", 40));
        funcionarios.add(new Funcionario("Alexandra Paes", "Analista de Sistemas", 31));
        funcionarios.add(new Funcionario("Sandy Souza", "Analista de Testes", 23));
        funcionarios.add(new Funcionario("Julia Camara Guimarães", "Gerente de Projetos", 49));
        funcionarios.add(new Funcionario("Fabia Rocha", "Analista de Sistemas", 25));
        funcionarios.add(new Funcionario("Lucio Palmeira Machado", "Analista de Requisitos", 23));
        funcionarios.add(new Funcionario("Carlos Chaves Cordeiro", "Analista de Suporte Técnico", 19));
        funcionarios.add(new Funcionario("Arnaldo Palmeira", "Gerente de Projetos", 35));
        funcionarios.add(new Funcionario("Andre Klein Oliveira", "Analista de Sistemas", 33));
        funcionarios.add(new Funcionario("Cecilia Moreira Fonseca", "Analista de Suporte Técnico", 32));
        funcionarios.add(new Funcionario("Joao Franco", "Gerente de Projetos", 41));
        funcionarios.add(new Funcionario("Lucio Krause", "Analista de Sistemas", 40));
        funcionarios.add(new Funcionario("Thales Lima Mello", "Analista de Testes", 27));
        funcionarios.add(new Funcionario("Joao Vitor Da Silva Mattos", "Analista de Requisitos", 25));
    }

    @Override
    public void loadFilterDefinition(STypeComposite<?> filter) {
        filter.addFieldString("nome").asAtrBasic().label("Nome").asAtrBootstrap().colPreference(6);
        filter.addFieldString("funcao").asAtrBasic().label("Função").asAtrBootstrap().colPreference(6);
        filter.addFieldInteger("idade").asAtrBasic().label("Idade").asAtrBootstrap().colPreference(2);
    }

    private List<Funcionario> filtrarFuncionarios(SInstance filter) {
        String  nome   = Value.of(filter, "nome");
        String  funcao = Value.of(filter, "funcao");
        Integer idade  = Value.of(filter, "idade");

        return funcionarios.stream().filter((func) -> {
            boolean contains = true;
            if (nome != null) {
                contains = func.getNome().toUpperCase().contains(nome.toUpperCase());
            }
            if (funcao != null) {
                contains = func.getFuncao().toUpperCase().contains(funcao.toUpperCase());
            }
            if (idade != null) {
                contains = func.getIdade().equals(idade);
            }
            return contains;
        }).collect(Collectors.toList());
    }

    @Override
    public Long getSize(SInstance rootInstance, SInstance filter) {
        return (long) filtrarFuncionarios(filter).size();
    }

    @Override
    public List<Funcionario> load(SInstance rootInstance, SInstance filter, long first, long count) {
        return filtrarFuncionarios(filter).subList((int) first, (int) (first + count));
    }

    @Override
    public List<Column> getColumns() {
        return Arrays.asList(Column.of("nome", "Nome"), Column.of("funcao", "Função"), Column.of("idade", "Idade"));
    }
}
