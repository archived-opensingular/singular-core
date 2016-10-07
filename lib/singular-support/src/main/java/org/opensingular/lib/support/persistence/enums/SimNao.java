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

package org.opensingular.lib.support.persistence.enums;

import org.opensingular.lib.support.persistence.util.EnumId;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum SimNao implements EnumId<SimNao, String> {

    @XmlEnumValue("S")
    SIM("S", "Sim"),

    @XmlEnumValue("N")
    NAO("N", "Não");

    public static final String ENUM_CLASS_NAME = "org.opensingular.lib.support.persistence.enums.SimNao";

    private String codigo;
    private String descricao;

    SimNao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    @Override
    public SimNao valueOfEnum(String codigo) {
        for (SimNao tipo : values()) {
            if (tipo.getCodigo().equals(codigo)) {
                return tipo;
            }
        }

        return null;
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

}
