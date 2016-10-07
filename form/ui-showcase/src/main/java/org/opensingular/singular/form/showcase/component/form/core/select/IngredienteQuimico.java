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

package org.opensingular.singular.form.showcase.component.form.core.select;

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