package br.net.mirante.singular.showcase.component.input.core.search;


import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.provider.FilteredPagedProvider;
import br.net.mirante.singular.form.mform.provider.filter.FilterDefinitionBuilder;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioProvider implements FilteredPagedProvider<Funcionario> {

    private static final long serialVersionUID = 5385665582164936063L;

    private static final List<Funcionario> FUNCIONARIOS;

    static {
        FUNCIONARIOS = new ArrayList<>();
        FUNCIONARIOS.add(new Funcionario("João Vitor Mota", "Analista de Sistemas", 30));
        FUNCIONARIOS.add(new Funcionario("Alexandre Guimarães", "Analista de Requisitos", 27));
        FUNCIONARIOS.add(new Funcionario("Romario De Paula", "Analista de Testes", 22));
        FUNCIONARIOS.add(new Funcionario("Alexandra Leite", "Gerente de Projetos", 40));
        FUNCIONARIOS.add(new Funcionario("Alexandra Paes", "Analista de Sistemas", 31));
        FUNCIONARIOS.add(new Funcionario("Sandy Souza", "Analista de Testes", 23));
        FUNCIONARIOS.add(new Funcionario("Julia Camara Guimarães", "Gerente de Projetos", 49));
        FUNCIONARIOS.add(new Funcionario("Fabia Rocha", "Analista de Sistemas", 25));
        FUNCIONARIOS.add(new Funcionario("Lucio Palmeira Machado", "Analista de Requisitos", 23));
        FUNCIONARIOS.add(new Funcionario("Carlos Chaves Cordeiro", "Analista de Suporte Técnico", 19));
        FUNCIONARIOS.add(new Funcionario("Arnaldo Palmeira", "Gerente de Projetos", 35));
        FUNCIONARIOS.add(new Funcionario("Andre Klein Oliveira", "Analista de Sistemas", 33));
        FUNCIONARIOS.add(new Funcionario("Cecilia Moreira Fonseca", "Analista de Suporte Técnico", 32));
        FUNCIONARIOS.add(new Funcionario("Joao Franco", "Gerente de Projetos", 41));
        FUNCIONARIOS.add(new Funcionario("Lucio Krause", "Analista de Sistemas", 40));
        FUNCIONARIOS.add(new Funcionario("Thales Lima Mello", "Analista de Testes", 27));
        FUNCIONARIOS.add(new Funcionario("Joao Vitor Da Silva Mattos", "Analista de Requisitos", 25));
    }

    private List<Funcionario> filtrarFuncionarios(SInstance filter) {
        String  nome   = Value.of(filter, "nome");
        String  funcao = Value.of(filter, "funcao");
        Integer idade  = Value.of(filter, "idade");

        return FUNCIONARIOS.stream().filter((func) -> {
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
    public void defineFilter(FilterDefinitionBuilder builder) {
        builder
                .configureType(f -> {
                    f.addFieldString("nome").asAtr().label("Nome").asAtrBootstrap().colPreference(6);
                    f.addFieldString("funcao").asAtr().label("Função").asAtrBootstrap().colPreference(6);
                    f.addFieldInteger("idade").asAtr().label("Idade").asAtrBootstrap().colPreference(2);
                })
                .addColumn("nome", "Nome")
                .addColumn("funcao", "Função")
                .addColumn("idade", "Idade");
    }

    @Override
    public Long getSize(SInstance rootInstance, SInstance filter) {
        return (long) filtrarFuncionarios(filter).size();
    }

    @Override
    public List<Funcionario> load(SInstance rootInstance, SInstance filter, long first, long count) {
        return filtrarFuncionarios(filter).subList((int) first, (int) (first + count));
    }

}