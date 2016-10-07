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

public enum TipoEnsaio implements EnumId<TipoEnsaio, Character> {
    /**
     * Ensaio de embalagem
     */
    EMBALAGEM('1', "Controle de Qualidade de Embalagem"),

    /**
     * Ensaio de produto
     */
    PRODUTO('2', "Controle de Qualidade do Produto");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoEnsaio";

    private TipoEnsaio(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    private Character codigo;

    private String descricao;

    @Override
    public Character getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public TipoEnsaio valueOfEnum(Character id) {
        for (TipoEnsaio tipo : values()) {
            if (tipo.getCodigo().equals(id)) {
                return tipo;
            }
        }
        return null;
    }
}