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
import javax.xml.bind.annotation.XmlType;

@XmlEnum
@XmlType(namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
public enum TipoObrigatoriedade {

    @XmlEnumValue("0")
    NAO_APLICAVEL('1', "Não aplicável"),

    @XmlEnumValue("1")
    OBRIGATORIO('1', "Obrigatório"),

    @XmlEnumValue("2")
    NAO_OBRIGATORIO('2', "Não obrigatório");

    public static final String ENUM_CLASS_NAME = "org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoObrigatoriedade";

    private Character codigo;
    private String    descricao;

    private TipoObrigatoriedade(Character codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Character getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static TipoObrigatoriedade valueOfEnum(Character codigo) {

        TipoObrigatoriedade status[] = TipoObrigatoriedade.values();

        for (TipoObrigatoriedade st : status) {
            if (st.getCodigo().equals(codigo)) {
                return st;
            }
        }
        return null;
    }
}
