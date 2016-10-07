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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author alessandro.leite
 */
@XmlEnum
public enum TipoUnidadeMedida implements EnumId<TipoUnidadeMedida, Character> {

    /**
     * Tipo de unidade de volune.
     */
    @XmlEnumValue("V")
    VOLUME('V', "Volume"),

    /**
     * Massa
     */
    @XmlEnumValue("M")
    MASSA('M', "Massa"),

    /**
     * Energia
     */
    @XmlEnumValue("E")
    ENERGIA('E', "Energia"),

    /**
     * Temperatura
     */
    @XmlEnumValue("T")
    TEMPERATURA('T', "Temperatura");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoUnidadeMedida";

    /**
     * Identificador do tipo de unidade de medida.
     */
    private final Character codigo;

    /**
     * Descrição do tipo de unidade de medida.
     */
    private final String descricao;

    private TipoUnidadeMedida(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public Character getCodigo() {
        return this.codigo;
    }

    @Override
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public TipoUnidadeMedida valueOfEnum(Character id) {
        for (TipoUnidadeMedida tipo : values()) {
            if (tipo.getCodigo().equals(id)) {
                return tipo;
            }
        }
        return null;
    }
}