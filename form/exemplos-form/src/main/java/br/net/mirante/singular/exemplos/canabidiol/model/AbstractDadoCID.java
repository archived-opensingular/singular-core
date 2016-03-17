/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol.model;

public abstract class AbstractDadoCID {

    private String id;

    private Character letraInicial;

    private Character letraFinal;

    private Integer numInicial;

    private Integer numFinal;

    private String descricao;

    private String descricaoAbreviada;

    public Character getLetraInicial() {
        return letraInicial;
    }

    public void setLetraInicial(Character letraInicial) {
        this.letraInicial = letraInicial;
    }

    public Character getLetraFinal() {
        return letraFinal;
    }

    public void setLetraFinal(Character letraFinal) {
        this.letraFinal = letraFinal;
    }

    public Integer getNumInicial() {
        return numInicial;
    }

    public void setNumInicial(Integer numInicial) {
        this.numInicial = numInicial;
    }

    public Integer getNumFinal() {
        return numFinal;
    }

    public void setNumFinal(Integer numFinal) {
        this.numFinal = numFinal;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoAbreviada() {
        return descricaoAbreviada;
    }

    public void setDescricaoAbreviada(String descricaoAbreviada) {
        this.descricaoAbreviada = descricaoAbreviada;
    }

    public String getId() {
        if (id == null) {
            id = this.getClass().getSimpleName()
                    + this.getLetraInicial()
                    + this.getNumInicial()
                    + this.getLetraFinal()
                    + this.getNumFinal();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
