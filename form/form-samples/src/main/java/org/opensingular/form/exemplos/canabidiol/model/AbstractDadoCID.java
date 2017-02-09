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

package org.opensingular.form.exemplos.canabidiol.model;

import java.io.Serializable;

public abstract class AbstractDadoCID  implements Serializable {

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
