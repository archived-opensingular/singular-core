package org.opensingular.singular.form.showcase.component.form.core.search;

import java.io.Serializable;

class Funcionario implements Serializable {

    private String nome;
    private String funcao;
    private Integer idade;

    Funcionario(String nome, String funcao, Integer idade) {
        this.nome = nome;
        this.funcao = funcao;
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public Integer getIdade() {
        return idade;
    }
}
