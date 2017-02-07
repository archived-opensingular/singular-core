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

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * @author allysson.cavalcante
 */
@XmlEnum
public enum TipoDesenhoExperimental {

    /**
     * Tratamento.
     */
    @XmlEnumValue("F")
    FATORIAL('F', "Fatorial"),

    /**
     * Prevencao
     */
    @XmlEnumValue("P")
    PARALELO('P', "Paralelo"),

    /**
     * Auxiliar diagnostico
     */
    @XmlEnumValue("C")
    CRUZADO('C', "Cruzado"),

    /**
     * Diagnostico
     */
    @XmlEnumValue("A")
    ADAPTATIVOS('A', "Adaptativos");

    /**
     * Identificador do tipo de unidade de medida.
     */
    private final Character codigo;

    /**
     * Descricao do tipo de unidade de medida.
     */
    private final String descricao;

    private TipoDesenhoExperimental(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return this.codigo;
    }

    public String getDescricao() {
        return this.descricao;
    }

    /**
     * @param id
     * @return
     */
    public static TipoDesenhoExperimental valueOf(Character codigo) {
        TipoDesenhoExperimental tipos[] = TipoDesenhoExperimental.values();

        for (TipoDesenhoExperimental tipo : tipos) {
            if (tipo != null && tipo.getCodigo().charValue() == codigo.charValue()) {
                return tipo;
            }
        }
        return null;
    }
}