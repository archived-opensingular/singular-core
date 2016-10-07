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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.enums;

import org.opensingular.lib.support.persistence.util.EnumId;

public enum TipoSolventeResidual implements EnumId<TipoSolventeResidual, Character> {

    CLASSE_1('1', "Classe 1 - Solventes que devem ser evitados"),
    CLASSE_2('2', "Classe 2 - Solventes que devem ser limitados"),
    CLASSE_3('3', "Classe 3 - Solventes com baixo potencial tóxico"),
    CLASSE_4('4', "Classe 4 - Solventes sem avaliação do ICH");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoSolventeResidual";

    private Character codigo;
    private String    descricao;


    private TipoSolventeResidual(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }


    public String getDescricao() {
        return descricao;
    }


    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    public Character getCodigo() {
        return codigo;
    }


    public void setCodigo(Character codigo) {
        this.codigo = codigo;
    }

    @Override
    public TipoSolventeResidual valueOfEnum(Character codigo) {
        TipoSolventeResidual status[] = TipoSolventeResidual.values();

        for (TipoSolventeResidual st : status) {
            if (codigo != null && st.getCodigo().charValue() == codigo.charValue()) {
                return st;
            }
        }
        return null;
    }


}