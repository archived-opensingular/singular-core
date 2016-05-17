/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.search;

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
