/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.select;

import java.io.Serializable;

public class IngredienteQuimico implements Serializable {

    private String nome;
    private String formulaQuimica;

    public IngredienteQuimico() {
    }

    public IngredienteQuimico(String nome, String formulaQuimica) {
        this.nome = nome;
        this.formulaQuimica = formulaQuimica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFormulaQuimica() {
        return formulaQuimica;
    }

    public void setFormulaQuimica(String formulaQuimica) {
        this.formulaQuimica = formulaQuimica;
    }

}